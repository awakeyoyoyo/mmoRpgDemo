package com.liqihao.pojo.bean;

import com.liqihao.Cache.BufferMessageCache;
import com.liqihao.Cache.ChannelMessageCache;
import com.liqihao.Cache.OnlineRoleMessageCache;
import com.liqihao.Cache.SceneBeanMessageCache;
import com.liqihao.commons.ConstantValue;
import com.liqihao.commons.NettyResponse;
import com.liqihao.commons.StateCode;
import com.liqihao.commons.enums.*;
import com.liqihao.pojo.baseMessage.BufferMessage;
import com.liqihao.protobufObject.PlayModel;
import com.liqihao.provider.CopySceneProvider;
import com.liqihao.util.ScheduledThreadPoolUtil;
import io.netty.channel.Channel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * 召唤兽实体类
 * @author lqhao
 */
public class MmoHelperBean extends Role{
    /**
     * 锁
     */
    public final ReentrantReadWriteLock hpRwLock = new ReentrantReadWriteLock();
    public final ReentrantReadWriteLock mpRwLock = new ReentrantReadWriteLock();
    /**
     * 主人id
     */
    Integer masterId;
    /**
     * 技能id
     */
    private List<Integer> skillIdList;
    /**
     * 技能实体bean
     */
    private List<SkillBean> skillBeans;
    /**
     * 攻击目标
     */
    private Role target;
    /**
     * CD Map
     */
    private volatile HashMap<Integer, Long> cdMap=new HashMap<>();

    public Role getTarget() {
        return target;
    }

    public void setTarget(Role target) {
        this.target = target;
    }

    public Integer getMasterId() {
        return masterId;
    }

    public void setMasterId(Integer masterId) {
        this.masterId = masterId;
    }

    public List<Integer> getSkillIdList() {
        return skillIdList;
    }

    public void setSkillIdList(List<Integer> skillIdList) {
        this.skillIdList = skillIdList;
    }

    public List<SkillBean> getSkillBeans() {
        return skillBeans;
    }

    public void setSkillBeans(List<SkillBean> skillBeans) {
        this.skillBeans = skillBeans;
    }

    public HashMap<Integer, Long> getCdMap() {
        return cdMap;
    }

    public void setCdMap(HashMap<Integer, Long> cdMap) {
        this.cdMap = cdMap;
    }


    /**
     * 被攻击
     * @param skillBean
     * @param fromRole
     */
    @Override
    public void beAttack(SkillBean skillBean, Role fromRole) {
        MmoHelperBean mmoHelperBean=this;
        Integer reduce = 0;
        try {
            hpRwLock.writeLock().lock();
            Integer hp = mmoHelperBean.getNowHp();
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
                mmoHelperBean.setStatus(RoleStatusCode.DIE.getCode());
                //消失
                MmoSimpleRole role=OnlineRoleMessageCache.getInstance().get(getMasterId());
                role.setMmoHelperBean(null);
                if (mmoHelperBean.getMmoSceneId()!=null){
                    SceneBean sceneBean=SceneBeanMessageCache.getInstance().get(mmoHelperBean.getMmoSceneId());
                    sceneBean.getHelperBeans().remove(mmoHelperBean);
                }
                if (mmoHelperBean.getCopySceneBeanId()!=null){
                    CopySceneProvider.getCopySceneBeanById(mmoHelperBean.getCopySceneBeanId()).getRoles().remove(mmoHelperBean);
                }
            }
            mmoHelperBean.setNowHp(hp);
        }finally {
            hpRwLock.writeLock().unlock();
        }
        // 扣血伤害
        PlayModel.RoleIdDamage.Builder damageR = PlayModel.RoleIdDamage.newBuilder();
        damageR.setFromRoleId(fromRole.getId());
        damageR.setFromRoleType(fromRole.getType());
        damageR.setToRoleId(mmoHelperBean.getId());
        damageR.setToRoleType(getType());
        damageR.setAttackStyle(AttackStyleCode.ATTACK.getCode());
        damageR.setBufferId(-1);
        damageR.setDamage(reduce);
        damageR.setDamageType(DamageTypeCode.HP.getCode());
        damageR.setMp(mmoHelperBean.getNowMp());
        damageR.setNowblood(mmoHelperBean.getNowHp());
        damageR.setSkillId(skillBean.getId());
        damageR.setState(mmoHelperBean.getStatus());
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
        List<Integer> players;
        if (getMmoSceneId()!=null) {
            players = SceneBeanMessageCache.getInstance().get(getMmoSceneId()).getRoles();
            for (Integer playerId:players){
                Channel c= ChannelMessageCache.getInstance().get(playerId);
                if (c!=null){
                    c.writeAndFlush(nettyResponse);
                }
            }

        }else{
            List<Role> roles = CopySceneProvider.getCopySceneBeanById(getCopySceneBeanId()).getRoles();
            for (Role role:roles) {
                if (role.getType().equals(RoleTypeCode.PLAYER.getCode())){
                    Channel c= ChannelMessageCache.getInstance().get(role.getId());
                    if (c!=null){
                        c.writeAndFlush(nettyResponse);
                    }
                }
            }
        }
        //召唤兽
    }

    /**
     * 攻击
     * @param role
     */
    public void npcAttack(Role role) {
        ScheduledFuture<?> t = ScheduledThreadPoolUtil.getHelperTaskMap().get(getId());
        if (getTarget() != null) {
            if (t != null) {
                //代表着该npc正在攻击一个目标
                if (role != getTarget()) {
                    //与npc攻击的不是同一个目标 则切换
                    ScheduledThreadPoolUtil.getHelperTaskMap().remove(getId());
                    t.cancel(false);
                    ScheduledThreadPoolUtil.HeplerAttackTask helperAttackTask =
                            new ScheduledThreadPoolUtil.HeplerAttackTask(this, getSkillBeans(), role);
                    t = ScheduledThreadPoolUtil.getScheduledExecutorService().scheduleAtFixedRate(helperAttackTask, 0, 6, TimeUnit.SECONDS);
                    ScheduledThreadPoolUtil.getHelperTaskMap().put(getId(), t);
                    setTarget(role);
                    return;
                } else {
                    //相同则无需操作 跳过
                    return;
                }
            }
        }
        setTarget(role);
        ScheduledThreadPoolUtil.HeplerAttackTask helperAttackTask =
                new ScheduledThreadPoolUtil.HeplerAttackTask(this, getSkillBeans(), role);
        t = ScheduledThreadPoolUtil.getScheduledExecutorService().scheduleAtFixedRate(helperAttackTask, 0, 6, TimeUnit.SECONDS);
        ScheduledThreadPoolUtil.getHelperTaskMap().put(getId(), t);
    }

    /**
     * 使用技能
     * @param targetRoles
     * @param id
     */
    public void useSkill(List<Role> targetRoles, Integer id) {
        SkillBean skillBean = getSkillBeanBySkillId(id);

        if (skillBean.getConsumeType().equals(ConsumeTypeCode.HP.getCode())) {
            //扣血
            setNowHp(getNowHp() - skillBean.getConsumeNum());
        } else {
            //扣篮
            setNowMp(getNowMp() - skillBean.getConsumeNum());
        }
        List<PlayModel.RoleIdDamage> list = new ArrayList<>();
        // 生成一个角色扣血或者扣篮
        PlayModel.RoleIdDamage.Builder damageU = PlayModel.RoleIdDamage.newBuilder();
        damageU.setFromRoleId(getId());
        damageU.setToRoleId(getId());
        damageU.setToRoleType(RoleTypeCode.BOSS.getCode());
        damageU.setFromRoleType(RoleTypeCode.BOSS.getCode());
        damageU.setArticleId(-1);
        damageU.setArticleType(-1);
        damageU.setAttackStyle(AttackStyleCode.USE_SKILL.getCode());
        damageU.setBufferId(-1);
        damageU.setDamage(skillBean.getConsumeNum());
        damageU.setDamageType(skillBean.getConsumeType());
        damageU.setMp(getNowMp());
        damageU.setNowblood(getNowHp());
        damageU.setSkillId(skillBean.getId());
        damageU.setState(getStatus());
        list.add(damageU.build());
        PlayModel.PlayModelMessage.Builder myMessageBuilder=PlayModel.PlayModelMessage.newBuilder();
        myMessageBuilder.setDataType(PlayModel.PlayModelMessage.DateType.UseSkillResponse);
        PlayModel.UseSkillResponse.Builder useSkillBuilder=PlayModel.UseSkillResponse.newBuilder();
        useSkillBuilder.addAllRoleIdDamages(list);
        myMessageBuilder.setUseSkillResponse(useSkillBuilder.build());
        NettyResponse nettyResponse=new NettyResponse();
        nettyResponse.setCmd(ConstantValue.USE_SKILL_RSPONSE);
        nettyResponse.setStateCode(StateCode.SUCCESS);
        nettyResponse.setData(myMessageBuilder.build().toByteArray());
        //广播
        List<Integer> players;
        if (getMmoSceneId()!=null) {
            players = SceneBeanMessageCache.getInstance().get(getMmoSceneId()).getRoles();
            for (Integer playerId:players){
                Channel c= ChannelMessageCache.getInstance().get(playerId);
                if (c!=null){
                    c.writeAndFlush(nettyResponse);
                }
            }

        }else{
            List<Role> roles = CopySceneProvider.getCopySceneBeanById(getCopySceneBeanId()).getRoles();
            for (Role role:roles) {
                if (role.getType().equals(RoleTypeCode.PLAYER.getCode())){
                    Channel c= ChannelMessageCache.getInstance().get(role.getId());
                    if (c!=null){
                        c.writeAndFlush(nettyResponse);
                    }
                }
            }
        }

        //  被攻击怪物or人物orBoss
        for (Role r :targetRoles) {
            r.beAttack(skillBean,this);
            //buffer
            for (Integer bufferId:skillBean.getBufferIds()) {
                BufferMessage bufferMessage= BufferMessageCache.getInstance().get(bufferId);
                skillBean.bufferToPeople(bufferMessage, this,r);
            }
        }
    }
    /**
     * 根据skillI获取技能
     */
    public SkillBean getSkillBeanBySkillId(Integer skillId) {
        for (SkillBean b : getSkillBeans()) {
            if (b.getId().equals(skillId)) {
                return b;
            }
        }
        return null;
    }
    @Override
    /**
     * BUFFER的影响
     */
    public void effectByBuffer(BufferBean bufferBean) {
        //根据buffer类型扣血扣蓝
        if (bufferBean.getBuffType().equals(BufferTypeCode.REDUCE_HP.getCode())) {
            hpRwLock.writeLock().lock();
            try {
                Integer hp = getNowHp() - bufferBean.getBuffNum();
                if (hp <= 0) {
                    hp = 0;
                    setStatus(RoleStatusCode.DIE.getCode());
                    //消失
                    MmoSimpleRole role=OnlineRoleMessageCache.getInstance().get(getMasterId());
                    role.setMmoHelperBean(null);
                    if (getMmoSceneId()!=null){
                        SceneBean sceneBean=SceneBeanMessageCache.getInstance().get(getMmoSceneId());
                        sceneBean.getHelperBeans().remove(this);
                    }
                    if (getCopySceneBeanId()!=null){
                        CopySceneProvider.getCopySceneBeanById(getCopySceneBeanId()).getRoles().remove(this);
                    }
                }
                setNowHp(hp);
            } finally {
                hpRwLock.writeLock().unlock();
            }

        } else if (bufferBean.getBuffType().equals(BufferTypeCode.REDUCE_MP.getCode())) {
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
        }else if (bufferBean.getBuffType().equals(BufferTypeCode.GG_ATTACK.getCode())){
            PlayModel.RoleIdDamage.Builder damageU = PlayModel.RoleIdDamage.newBuilder();
            damageU.setFromRoleId(bufferBean.getFromRoleId());
            damageU.setFromRoleType(bufferBean.getFromRoleType());
            damageU.setToRoleId(getId());
            damageU.setToRoleType(getType());
            damageU.setBufferId(bufferBean.getId());
            damageU.setDamageType(ConsumeTypeCode.HP.getCode());
            damageU.setSkillId(-1);
            damageU.setAttackStyle(AttackStyleCode.GG_ATTACK.getCode());
            damageU.setMp(getNowMp());
            damageU.setNowblood(getNowHp());
            damageU.setState(getStatus());
            PlayModel.PlayModelMessage.Builder myMessageBuilder = PlayModel.PlayModelMessage.newBuilder();
            myMessageBuilder.setDataType(PlayModel.PlayModelMessage.DateType.DamagesNoticeResponse);
            PlayModel.DamagesNoticeResponse.Builder damagesNoticeBuilder = PlayModel.DamagesNoticeResponse.newBuilder();
            damagesNoticeBuilder.setRoleIdDamage(damageU);
            myMessageBuilder.setDamagesNoticeResponse(damagesNoticeBuilder.build());
            NettyResponse nettyResponse = new NettyResponse();
            nettyResponse.setCmd(ConstantValue.DAMAGES_NOTICE_RESPONSE);
            nettyResponse.setStateCode(StateCode.SUCCESS);
            nettyResponse.setData(myMessageBuilder.build().toByteArray());
            List<Integer> players;
            if (getMmoSceneId()!=null) {
                players = SceneBeanMessageCache.getInstance().get(getMmoSceneId()).getRoles();
                for (Integer playerId:players){
                    Channel c= ChannelMessageCache.getInstance().get(playerId);
                    if (c!=null){
                        c.writeAndFlush(nettyResponse);
                    }
                }

            }else{
                List<Role> roles = CopySceneProvider.getCopySceneBeanById(getCopySceneBeanId()).getRoles();
                for (Role role:roles) {
                    if (role.getType().equals(RoleTypeCode.PLAYER.getCode())){
                        Channel c= ChannelMessageCache.getInstance().get(role.getId());
                        if (c!=null){
                            c.writeAndFlush(nettyResponse);
                        }
                    }
                }
            }
            return;
        }
        //广播信息
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
        List<Integer> players;
        if (getMmoSceneId()!=null) {
            players = SceneBeanMessageCache.getInstance().get(this.getMmoSceneId()).getRoles();
            for (Integer playerId:players){
                Channel c= ChannelMessageCache.getInstance().get(playerId);
                if (c!=null){
                    c.writeAndFlush(nettyResponse);
                }
            }

        }else{
            List<Role> roles = CopySceneProvider.getCopySceneBeanById(getCopySceneBeanId()).getRoles();
            for (Role role:roles) {
                if (role.getType().equals(RoleTypeCode.PLAYER.getCode())){
                    Channel c= ChannelMessageCache.getInstance().get(role.getId());
                    if (c!=null){
                        c.writeAndFlush(nettyResponse);
                    }
                }
            }
        }

    }
}
