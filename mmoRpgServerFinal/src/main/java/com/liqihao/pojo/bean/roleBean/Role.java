package com.liqihao.pojo.bean.roleBean;

import com.liqihao.Dbitem.Item;
import com.liqihao.commons.enums.RoleTypeCode;
import com.liqihao.commons.enums.TaskTargetTypeCode;
import com.liqihao.pojo.bean.SkillBean;
import com.liqihao.pojo.bean.buffBean.BaseBuffBean;
import com.liqihao.pojo.bean.taskBean.equipmentLevelTask.EquipmentTaskLevelAction;
import com.liqihao.pojo.bean.taskBean.roleLevelTask.RoleLevelAction;
import com.liqihao.protobufObject.PlayModel;
import com.liqihao.util.CommonsUtil;

import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * 所有角色的父类
 * @author lqhao
 */
public abstract class Role  extends Item {
    /** roleId */
    private Integer id;
    /** 姓名 */
    private String name;
    /** hp */
    private Integer hp;
    /** mp */
    private Integer mp;
    /** 攻击 */
    private Integer attack;
    /** 技能伤害加成 */
    private double damageAdd;
    /** 类型 */
    private Integer type;
    /** 状态 */
    private Integer status;
    /** 在线离线状态 */
    private Integer onStatus;
    /** 携带buffs */
    private CopyOnWriteArrayList<BaseBuffBean> bufferBeans;
    /** 当前hp */
    private volatile Integer nowHp;
    /** 当前mp */
    private volatile Integer nowMp;
    /** 副本id */
    private Integer copySceneBeanId;
    /** 场景id */
    private Integer mmoSceneId;
    /** 队伍id */
    private Integer teamId;
    /** 等级 */
    private Integer level=0;
    /** 经验 */
    private Integer exp;
    /** 装备等级 */
    private Integer equipmentLevel=0;
    /**锁*/
    public final ReentrantReadWriteLock hpRwLock = new ReentrantReadWriteLock();
    public final ReentrantReadWriteLock mpRwLock = new ReentrantReadWriteLock();

    public Integer getEquipmentLevel() {
        return equipmentLevel;
    }

    public void setEquipmentLevel(Integer equipmentLevel) {
        this.equipmentLevel = equipmentLevel;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public Integer getExp() {
        return exp;
    }

    public void setExp(Integer exp) {
        this.exp = exp;
    }

    public Integer getTeamId() {
        return teamId;
    }

    public void setTeamId(Integer teamId) {
        this.teamId = teamId;
    }

    public Integer getMmoSceneId() {
        return mmoSceneId;
    }

    public void setMmoSceneId(Integer mmoSceneId) {
        this.mmoSceneId = mmoSceneId;
    }

    public Integer getCopySceneBeanId() {
        return copySceneBeanId;
    }

    public void setCopySceneBeanId(Integer copySceneBeanId) {
        this.copySceneBeanId = copySceneBeanId;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getHp() {
        return hp;
    }

    public void setHp(Integer hp) {
        this.hp = hp;
    }

    public Integer getMp() {
        return mp;
    }

    public void setMp(Integer mp) {
        this.mp = mp;
    }

    public Integer getAttack() {
        return attack;
    }

    public void setAttack(Integer attack) {
        this.attack = attack;
    }

    public double getDamageAdd() {
        return damageAdd;
    }

    public void setDamageAdd(double damageAdd) {
        this.damageAdd = damageAdd;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getOnStatus() {
        return onStatus;
    }

    public void setOnStatus(Integer onStatus) {
        this.onStatus = onStatus;
    }

    public CopyOnWriteArrayList<BaseBuffBean> getBufferBeans() {
        return bufferBeans;
    }

    public void setBufferBeans(CopyOnWriteArrayList<BaseBuffBean> bufferBeans) {
        this.bufferBeans = bufferBeans;
    }

    public Integer getNowHp() {
        return nowHp;
    }

    public void setNowHp(Integer nowHp) {
        this.nowHp = nowHp;
    }

    public Integer getNowMp() {
        return nowMp;
    }

    public void setNowMp(Integer nowMp) {
        this.nowMp = nowMp;
    }

    /**
     * description 加经验
     * @param num
     * @return {@link null }
     * @author lqhao
     * @createTime 2021/1/26 20:29
     */
    public void addExp(Integer num){
        Integer nowExp = getExp() + num;
        setExp(nowExp);
        Integer nowLevel = nowExp / 10;
        upLevel(nowLevel);
        MmoSimpleRole mmoSimpleRole = (MmoSimpleRole) this;
        mmoSimpleRole.updateItem(mmoSimpleRole.getId());
    }

    /**
     * description 升级
     * @param level
     * @return {@link null }
     * @author lqhao
     * @createTime 2021/1/26 20:28
     */
    public void upLevel(Integer level){
        if (level>getLevel()){
            Integer addLevel=level-getLevel();
            setLevel(level);
            if(getType().equals(RoleTypeCode.PLAYER.getCode())){
                //发送给所有人有人升级
                CommonsUtil.sendUpLevelAllRoles(addLevel,this);
                //抛出升级事件
                RoleLevelAction roleLevelAction=new RoleLevelAction();
                roleLevelAction.setLevel(getLevel());
                roleLevelAction.setTaskTargetType(TaskTargetTypeCode.UP_LEVEL.getCode());
                MmoSimpleRole mmoSimpleRole= (MmoSimpleRole) this;
                mmoSimpleRole.getTaskManager().handler(roleLevelAction,mmoSimpleRole);
            }
        }
    }

    /**
     * description 改变人物装备星级
     * @param equipmentLevel
     * @return {@link null }
     * @author lqhao
     * @createTime 2021/2/1 15:11
     */
    public void changeEquipmentLevel(Integer equipmentLevel){
        setEquipmentLevel(equipmentLevel);
        // 抛出装备星级事件
        if(getType().equals(RoleTypeCode.PLAYER.getCode())){
            EquipmentTaskLevelAction equipmentTaskLevelAction=new EquipmentTaskLevelAction();
            equipmentTaskLevelAction.setChangeLevel(getEquipmentLevel());
            equipmentTaskLevelAction.setTaskTargetType(TaskTargetTypeCode.EQUIPMENT_LEVEL.getCode());
            MmoSimpleRole mmoSimpleRole= (MmoSimpleRole) this;
            mmoSimpleRole.getTaskManager().handler(equipmentTaskLevelAction,mmoSimpleRole);
        }
    }

    /**
     * 改变蓝量
     * @param number
     * @param damageU
     */
    public abstract void changeMp(int number, PlayModel.RoleIdDamage.Builder damageU) ;

    /**
     * 改变当前血量
     * @param number
     * @param damageU
     * @param type
     */
    public abstract void changeNowBlood(int number, PlayModel.RoleIdDamage.Builder damageU, int type) ;

    /**
     * buffer影响
     * @param bufferBean
     */
    public abstract void effectByBuffer(BaseBuffBean bufferBean,Role fromRole);

    /**
     * 角色被攻击调用
     * @param skillBean
     * @param fromRole
     */
    public abstract void beAttack(SkillBean skillBean, Role fromRole) ;


    /**
     * 死角色死亡调用
     * @param fromRole
     */
    public abstract void die(Role fromRole);
}
