package com.liqihao.pojo.bean.roleBean;

import com.liqihao.Cache.*;
import com.liqihao.commons.ConstantValue;
import com.liqihao.commons.NettyResponse;
import com.liqihao.commons.StateCode;
import com.liqihao.commons.enums.*;
import com.liqihao.pojo.baseMessage.BufferMessage;
import com.liqihao.pojo.baseMessage.NpcMessage;
import com.liqihao.pojo.bean.SceneBean;
import com.liqihao.pojo.bean.SkillBean;
import com.liqihao.pojo.bean.buffBean.BaseBuffBean;
import com.liqihao.pojo.bean.taskBean.killTask.KillTaskAction;
import com.liqihao.pojo.bean.taskBean.skillTask.SkillTaskAction;
import com.liqihao.pojo.bean.taskBean.talkTask.TalkTaskAction;
import com.liqihao.protobufObject.PlayModel;
import com.liqihao.util.ScheduledThreadPoolUtil;
import io.netty.channel.Channel;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;


/**
 * npc
 *
 * @author lqhao
 */
public class MmoSimpleNPC extends Role {


    private String talk;
    private Integer addExp;

    /**
     * 仇恨
     */
    private ConcurrentHashMap<Role, Integer> hatredMap = new ConcurrentHashMap<>();

    public ConcurrentHashMap<Role, Integer> getHatredMap() {
        return hatredMap;
    }

    public void setHatredMap(ConcurrentHashMap<Role, Integer> hatredMap) {
        this.hatredMap = hatredMap;
    }

    public String getTalk() {
        return talk;
    }

    public void setTalk(String talk) {
        this.talk = talk;
    }

    public Integer getAddExp() {
        return addExp;
    }

    public void setAddExp(Integer addExp) {
        this.addExp = addExp;
    }

    /**
     * description 被攻击
     *
     * @param skillBean
     * @param fromRole
     * @return {@link null }
     * @author lqhao
     * @createTime 2021/1/26 17:43
     */
    @Override
    public void beAttack(SkillBean skillBean, Role fromRole) {
        MmoSimpleNPC mmoSimpleNPC = this;
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
                die(fromRole);
            }
            mmoSimpleNPC.setNowHp(hp);
        } finally {
            hpRwLock.writeLock().unlock();
        }
        //增加仇恨
        addHatred(fromRole, reduce);
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
        PlayModel.PlayModelMessage.Builder myMessageBuilder = PlayModel.PlayModelMessage.newBuilder();
        myMessageBuilder.setDataType(PlayModel.PlayModelMessage.DateType.DamagesNoticeResponse);
        PlayModel.DamagesNoticeResponse.Builder damagesBuilder = PlayModel.DamagesNoticeResponse.newBuilder();
        damagesBuilder.setRoleIdDamage(damageR);
        myMessageBuilder.setDamagesNoticeResponse(damagesBuilder.build());
        NettyResponse nettyResponse = new NettyResponse();
        nettyResponse.setCmd(ConstantValue.DAMAGES_NOTICE_RESPONSE);
        nettyResponse.setStateCode(StateCode.SUCCESS);
        nettyResponse.setData(myMessageBuilder.build().toByteArray());
        //广播给所有当前场景
        SceneBean sceneBean = SceneBeanMessageCache.getInstance().get(this.getMmoSceneId());
        List<Integer> roles = sceneBean.getRoles();
        for (Integer id : roles) {
            MmoSimpleRole role = OnlineRoleMessageCache.getInstance().get(id);
            if (role != null) {
                Channel c = ChannelMessageCache.getInstance().get(role.getId());
                if (c != null) {
                    c.writeAndFlush(nettyResponse);
                }
            }
        }
        //怪物攻击本人
        if (!mmoSimpleNPC.getStatus().equals(RoleStatusCode.DIE.getCode())) {
            mmoSimpleNPC.npcAttack();
        }
    }

    @Override
    public void die(Role fromRole) {
        setStatus(RoleStatusCode.DIE.getCode());
        //任务条件触发
        if (fromRole.getType().equals(RoleTypeCode.PLAYER.getCode()) || fromRole.getType().equals(RoleTypeCode.HELPER.getCode())) {
            MmoSimpleRole role = null;
            if (fromRole.getType().equals(RoleTypeCode.HELPER.getCode())) {
                MmoHelperBean mmoHelperBean = (MmoHelperBean) fromRole;
                Integer roleId = mmoHelperBean.getMasterId();
                role = OnlineRoleMessageCache.getInstance().get(roleId);
            } else {
                role = (MmoSimpleRole) fromRole;
            }
            KillTaskAction killTaskAction = new KillTaskAction();
            killTaskAction.setNum(1);
            killTaskAction.setRoleType(RoleTypeCode.ENEMY.getCode());
            killTaskAction.setTargetRoleId(getId());
            killTaskAction.setTaskTargetType(TaskTargetTypeCode.KILL.getCode());
            role.getTaskManager().handler(killTaskAction, role);
            //人物增加经验
            role.addExp(getAddExp());
        }
    }

    //消除仇恨
    public void removeHatred(Role role) {
        if (getHatredMap().containsKey(role)) {
            getHatredMap().remove(role);
        }
    }

    //增加仇恨
    public void addHatred(Role role, Integer number) {
        synchronized (hatredMap) {
            ConcurrentHashMap<Role, Integer> hatredMap = getHatredMap();
            if (hatredMap.containsKey(role)) {
                Integer hart = hatredMap.get(role);
                hart += number;
                hatredMap.put(role, hart);
            } else {
                hatredMap.put(role, number);
            }
        }
    }

    //查找目标
    public Role getTarget() {
        if (getStatus().equals(RoleStatusCode.DIE.getCode())) {
            return null;
        }
        synchronized (hatredMap) {
            ConcurrentHashMap<Role, Integer> hatredMap = getHatredMap();
            //判断是否有嘲讽buffer,则直接攻击嘲讽对象
            Iterator<BaseBuffBean> buffers = getBufferBeans().iterator();
            while (buffers.hasNext()) {
                BaseBuffBean bufferBean = buffers.next();
                BufferMessage bufferMessage = BufferMessageCache.getInstance().get(bufferBean.getBufferMessageId());
                if (bufferMessage.getBuffType().equals(BufferTypeCode.GG_ATTACK.getCode())) {
                    Role role = OnlineRoleMessageCache.getInstance().get(bufferBean.getFromRoleId());
                    if (role.getStatus().equals(RoleStatusCode.ALIVE.getCode())) {
                        return role;
                    }
                }
            }
            if (hatredMap.size() > 0) {
                Role target = null;
                Integer max = 0;
                for (Role role : hatredMap.keySet()) {
                    if (role.getStatus().equals(RoleStatusCode.DIE.getCode())) {
                        //如果以及死了就消除仇恨了
                        removeHatred(role);
                        continue;
                    }
                    if (hatredMap.get(role) > max) {
                        target = role;
                        max = hatredMap.get(role);
                    }
                }
                return target;
            }
        }
        return null;
    }

    public void npcAttack() {
        ScheduledFuture<?> t = ScheduledThreadPoolUtil.getNpcTaskMap().get(getId());
        if (t != null) {
            //代表着该npc已启动攻击线程
        } else {
            synchronized (this) {
                t = ScheduledThreadPoolUtil.getNpcTaskMap().get(getId());
                if (t == null) {
                    ScheduledThreadPoolUtil.NpcAttackTask npcAttackTask = new ScheduledThreadPoolUtil.NpcAttackTask(getId());
                    t = ScheduledThreadPoolUtil.getScheduledExecutorService().scheduleAtFixedRate(npcAttackTask, 0, 3, TimeUnit.SECONDS);
                    ScheduledThreadPoolUtil.getNpcTaskMap().put(getId(), t);
                }
            }
        }
    }


    @Override
    public void changeMp(int number, PlayModel.RoleIdDamage.Builder damageU) {

    }

    @Override
    public void changeNowBlood(int number, PlayModel.RoleIdDamage.Builder damageU, int type) {

    }

    @Override
    public void effectByBuffer(BaseBuffBean bufferBean, Role fromRole) {
        //根据buffer类型扣血扣蓝
        bufferBean.effectToRole(this, fromRole);
    }
}
