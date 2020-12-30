package com.liqihao.pojo.bean;

import com.liqihao.Cache.ChannelMessageCache;
import com.liqihao.Cache.OnlineRoleMessageCache;
import com.liqihao.Cache.SceneBeanMessageCache;
import com.liqihao.commons.ConstantValue;
import com.liqihao.commons.NettyResponse;
import com.liqihao.commons.StateCode;
import com.liqihao.commons.enums.*;
import com.liqihao.pojo.baseMessage.NPCMessage;
import com.liqihao.protobufObject.PlayModel;
import com.liqihao.util.ScheduledThreadPoolUtil;
import io.netty.channel.Channel;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantReadWriteLock;


/**
 * npc
 *
 * @author lqhao
 */
public class MmoSimpleNPC extends Role {


    public final ReentrantReadWriteLock hpRwLock = new ReentrantReadWriteLock();
    public final ReentrantReadWriteLock mpRwLock = new ReentrantReadWriteLock();
    private String talk;

    public String getTalk() {
        return talk;
    }

    public void setTalk(String talk) {
        this.talk = talk;
    }



    /**
     * 判断是否死亡,并且更改状态
     */
    public void checkDie() {
        if (getNowHp() <= 0) {
            super.setStatus(RoleStatusCode.DIE.getCode());
        }
    }

    @Override
    public void beAttack(SkillBean skillBean,Role fromRole) {
        MmoSimpleNPC mmoSimpleNPC=this;
        Integer reduce = 0;
        try {
            hpRwLock.writeLock().lock();
            Integer hp = mmoSimpleNPC.getNowHp();
            if (skillBean.getSkillType().equals(SkillTypeCode.FIED.getCode())) {
                //固伤 只有技能伤害
                reduce = (int) Math.ceil(skillBean.getBaseDamage() * (1 + fromRole.getDamageAdd()));
                hp -= reduce;
            }
            if (skillBean.getSkillType().equals(SkillTypeCode.PERCENTAGE.getCode())) {
                //百分比 按照攻击力比例增加
                Integer damage = skillBean.getBaseDamage();
                damage = (int) Math.ceil(damage + fromRole.getAttack() * skillBean.getAddPercon());
                hp = hp - damage;
                reduce = damage;
            }
            if (hp <= 0) {
                reduce = reduce + hp;
                hp = 0;
                mmoSimpleNPC.setStatus(RoleStatusCode.DIE.getCode());
            }
            mmoSimpleNPC.setNowHp(hp);
        }finally {
            hpRwLock.writeLock().unlock();
        }
        // 扣血伤害
        PlayModel.RoleIdDamage.Builder damageR = PlayModel.RoleIdDamage.newBuilder();
        damageR.setFromRoleId(fromRole.getId());
        damageR.setToRoleType(fromRole.getType());
        damageR.setToRoleId(mmoSimpleNPC.getId());
        damageR.setToRoleType(getType());
        damageR.setAttackStyle(AttackStyleCode.ATTACK.getCode());
        damageR.setBufferId(-1);
        damageR.setDamage(reduce);
        damageR.setDamageType(DamageTypeCode.HP.getCode());
        damageR.setMp(mmoSimpleNPC.getNowMp());
        damageR.setNowblood(mmoSimpleNPC.getNowHp());
        damageR.setSkillId(skillBean.getId());
        damageR.setState(mmoSimpleNPC.getStatus());
        PlayModel.PlayModelMessage.Builder myMessageBuilder=PlayModel.PlayModelMessage.newBuilder();
        myMessageBuilder.setDataType(PlayModel.PlayModelMessage.DateType.DamagesNoticeResponse);
        PlayModel.DamagesNoticeResponse.Builder damagesBuilder=PlayModel.DamagesNoticeResponse.newBuilder();
        damagesBuilder.setRoleIdDamage(damageR);
        myMessageBuilder.setDamagesNoticeResponse(damagesBuilder.build());
        NettyResponse nettyResponse=new NettyResponse();
        nettyResponse.setCmd(ConstantValue.DAMAGES_NOTICE_RESPONSE);
        nettyResponse.setStateCode(StateCode.SUCCESS);
        nettyResponse.setData(myMessageBuilder.build().toByteArray());
        //广播给所有当前场景
        SceneBean sceneBean=SceneBeanMessageCache.getInstance().get(this.getMmosceneid());
        List<Integer> roles=sceneBean.getRoles();
        for (Integer id: roles) {
            MmoSimpleRole role=OnlineRoleMessageCache.getInstance().get(id);
            if (role!=null){
                Channel c=ChannelMessageCache.getInstance().get(role.getId());
                if (c!=null){
                    c.writeAndFlush(nettyResponse);
                }
            }
        }
        //怪物攻击本人
        if (!mmoSimpleNPC.getStatus().equals(RoleStatusCode.DIE.getCode())) {
            mmoSimpleNPC.npcAttack(fromRole.getId());
        }
    }


    public void npcAttack(Integer roleId) {
        ScheduledFuture<?> t = ScheduledThreadPoolUtil.getNpcTaskMap().get(getId());
        if (t != null) {
            //代表着该npc正在攻击一个目标
            return;
        } else {
            ScheduledThreadPoolUtil.NpcAttackTask npcAttackTask = new ScheduledThreadPoolUtil.NpcAttackTask(roleId, getId());
            t = ScheduledThreadPoolUtil.getScheduledExecutorService().scheduleAtFixedRate(npcAttackTask, 0, 6, TimeUnit.SECONDS);
            ScheduledThreadPoolUtil.getNpcTaskMap().put(getId(), t);
        }
    }


    @Override
    public void effectByBuffer(BufferBean bufferBean) {
        //根据buffer类型扣血扣蓝
        if (bufferBean.getBuffType().equals(BufferTypeCode.REDUCEHP.getCode())) {
            hpRwLock.writeLock().lock();
            try {
                Integer hp = getNowHp() - bufferBean.getBuffNum();
                if (hp <= 0) {
                    hp = 0;
                    setStatus(RoleStatusCode.DIE.getCode());
                }
                setNowHp(hp);
            } finally {
                hpRwLock.writeLock().unlock();
            }

        } else if (bufferBean.getBuffType().equals(BufferTypeCode.REDUCEMP.getCode())) {
            mpRwLock.writeLock().lock();
            try {
                Integer mp = getNowMp() - bufferBean.getBuffNum();
                if (mp <= 0) {
                    mp = 0;
                }
                setNowMp(mp);
            } finally {
                mpRwLock.writeLock().unlock();
            }
        }
        //广播信息
        Integer sceneId = OnlineRoleMessageCache.getInstance().get(bufferBean.getFromRoleId()).getMmosceneid();
        List<Integer> players = SceneBeanMessageCache.getInstance().get(sceneId).getRoles();
        //生成数据包
        PlayModel.PlayModelMessage.Builder myMessageBuilder = PlayModel.PlayModelMessage.newBuilder();
        myMessageBuilder.setDataType(PlayModel.PlayModelMessage.DateType.DamagesNoticeResponse);
        PlayModel.DamagesNoticeResponse.Builder damagesNoticeBuilder = PlayModel.DamagesNoticeResponse.newBuilder();
        PlayModel.RoleIdDamage.Builder damageU = PlayModel.RoleIdDamage.newBuilder();
        damageU.setDamageType(DamageTypeCode.HP.getCode()).setAttackStyle(AttackStyleCode.BUFFER.getCode())
                .setDamage(bufferBean.getBuffNum()).setFromRoleId(bufferBean.getFromRoleId()).setToRoleId(bufferBean.getToRoleId())
                .setState(getStatus()).setMp(getNowMp()).setBufferId(bufferBean.getId()).setNowblood(getNowHp());
        damagesNoticeBuilder.setRoleIdDamage(damageU);
        myMessageBuilder.setDamagesNoticeResponse(damagesNoticeBuilder.build());
        NettyResponse nettyResponse = new NettyResponse();
        nettyResponse.setCmd(ConstantValue.DAMAGES_NOTICE_RESPONSE);
        nettyResponse.setStateCode(StateCode.SUCCESS);
        nettyResponse.setData(myMessageBuilder.build().toByteArray());
        for (Integer playerId : players) {
            Channel c = ChannelMessageCache.getInstance().get(playerId);
            if (c != null) {
                c.writeAndFlush(nettyResponse);
            }

        }

    }
}
