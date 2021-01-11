package com.liqihao.pojo.bean.roleBean;

import com.liqihao.Cache.BufferMessageCache;
import com.liqihao.Cache.ChannelMessageCache;
import com.liqihao.Cache.OnlineRoleMessageCache;
import com.liqihao.Cache.SceneBeanMessageCache;
import com.liqihao.commons.ConstantValue;
import com.liqihao.commons.NettyResponse;
import com.liqihao.commons.StateCode;
import com.liqihao.commons.enums.*;
import com.liqihao.pojo.baseMessage.BufferMessage;
import com.liqihao.pojo.bean.SceneBean;
import com.liqihao.pojo.bean.SkillBean;
import com.liqihao.pojo.bean.bufferBean.BaseBufferBean;
import com.liqihao.protobufObject.PlayModel;
import com.liqihao.provider.CopySceneProvider;
import com.liqihao.util.ScheduledThreadPoolUtil;
import io.netty.channel.Channel;

import java.util.List;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;


/**
 * npc
 *
 * @author lqhao
 */
public class MmoSimpleNPC extends Role {


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
    public void beAttack(SkillBean skillBean, Role fromRole) {
        MmoSimpleNPC mmoSimpleNPC=this;
        Integer reduce = 0;
        try {
            hpRwLock.writeLock().lock();
            Integer hp = mmoSimpleNPC.getNowHp();
            if (skillBean.getSkillType().equals(SkillTypeCode.FIX.getCode())) {
                //固伤 只有技能伤害
                reduce = (int) Math.ceil(skillBean.getBaseDamage() * (1 + fromRole.getDamageAdd()));
                hp -= reduce;
            }
            if (skillBean.getSkillType().equals(SkillTypeCode.PERCENTAGE.getCode())) {
                //百分比 按照攻击力比例增加
                Integer damage = skillBean.getBaseDamage();
                damage = (int) Math.ceil(damage + fromRole.getAttack() * skillBean.getAddPerson());
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
        damageR.setFromRoleType(fromRole.getType());
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
        SceneBean sceneBean=SceneBeanMessageCache.getInstance().get(this.getMmoSceneId());
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
            mmoSimpleNPC.npcAttack(fromRole);
        }
    }


    public void npcAttack(Role target) {
        ScheduledFuture<?> t = ScheduledThreadPoolUtil.getNpcTaskMap().get(getId());
        if (t != null) {
            //代表着该npc已启动攻击线程
        } else {
            synchronized (this) {
                t = ScheduledThreadPoolUtil.getNpcTaskMap().get(getId());
                if (t==null) {
                    ScheduledThreadPoolUtil.NpcAttackTask npcAttackTask = new ScheduledThreadPoolUtil.NpcAttackTask(target, getId());
                    t = ScheduledThreadPoolUtil.getScheduledExecutorService().scheduleAtFixedRate(npcAttackTask, 0, 6, TimeUnit.SECONDS);
                    ScheduledThreadPoolUtil.getNpcTaskMap().put(getId(), t);
                }
            }
        }
    }


    @Override
    public void effectByBuffer(BaseBufferBean bufferBean) {
        //根据buffer类型扣血扣蓝
        bufferBean.effectToRole(this);
    }
}