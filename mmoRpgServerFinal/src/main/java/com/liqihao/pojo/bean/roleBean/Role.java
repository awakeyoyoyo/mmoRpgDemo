package com.liqihao.pojo.bean.roleBean;

import com.liqihao.pojo.bean.SkillBean;
import com.liqihao.pojo.bean.bufferBean.BaseBufferBean;
import com.liqihao.protobufObject.PlayModel;

import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * 所有角色的父类
 * @author lqhao
 */
public class Role {
    private Integer id;
    private String name;
    private Integer hp;
    private Integer mp;
    private Integer attack;
    private double damageAdd;
    private Integer type;
    private Integer status;
    private Integer onStatus;
    private CopyOnWriteArrayList<BaseBufferBean> bufferBeans;
    private volatile Integer nowHp;
    private volatile Integer nowMp;
    private Integer copySceneBeanId;
    private Integer mmoSceneId;
    private Integer teamId;
    /**
     * 锁
     */
    public final ReentrantReadWriteLock hpRwLock = new ReentrantReadWriteLock();
    public final ReentrantReadWriteLock mpRwLock = new ReentrantReadWriteLock();

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
    public void changeMp(int number, PlayModel.RoleIdDamage.Builder damageU) {
    }
    public void changeNowBlood(int number, PlayModel.RoleIdDamage.Builder damageU, int type) {
    }
    public void effectByBuffer(BaseBufferBean bufferBean){
    }
    public void beAttack(SkillBean skillBean, Role fromRole) {
    }

    public void die(){
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

    public CopyOnWriteArrayList<BaseBufferBean> getBufferBeans() {
        return bufferBeans;
    }

    public void setBufferBeans(CopyOnWriteArrayList<BaseBufferBean> bufferBeans) {
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

}
