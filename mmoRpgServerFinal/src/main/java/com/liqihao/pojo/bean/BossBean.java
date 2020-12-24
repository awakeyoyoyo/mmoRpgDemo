package com.liqihao.pojo.bean;

import com.liqihao.pojo.baseMessage.BossMessage;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * boss bean
 * @author lqhao
 */
public class BossBean extends BossMessage {
    private Integer nowBlood;
    private Integer nowMp;
    private Integer roleStatus;
    private Integer roleType;
    /**
     * 仇恨
     */
    private ConcurrentHashMap<Integer,Integer> hatredMap;
    /**
     * 技能&cd
     */
    private List<SkillBean> skillBeans;
    private HashMap<Integer, Long> cdMap;
    /**
     * buffer
     */
    private CopyOnWriteArrayList<BufferBean> bufferBeans;
    private Integer BossBeanId;

    public Integer getBossBeanId() {
        return BossBeanId;
    }

    public void setBossBeanId(Integer bossBeanId) {
        BossBeanId = bossBeanId;
    }

    public Integer getNowBlood() {
        return nowBlood;
    }

    public void setNowBlood(Integer nowBlood) {
        this.nowBlood = nowBlood;
    }

    public Integer getNowMp() {
        return nowMp;
    }

    public void setNowMp(Integer nowMp) {
        this.nowMp = nowMp;
    }

    public Integer getRoleStatus() {
        return roleStatus;
    }

    public void setRoleStatus(Integer roleStatus) {
        this.roleStatus = roleStatus;
    }

    public Integer getRoleType() {
        return roleType;
    }

    public void setRoleType(Integer roleType) {
        this.roleType = roleType;
    }

    public ConcurrentHashMap<Integer, Integer> getHatredMap() {
        return hatredMap;
    }

    public void setHatredMap(ConcurrentHashMap<Integer, Integer> hatredMap) {
        this.hatredMap = hatredMap;
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

    public CopyOnWriteArrayList<BufferBean> getBufferBeans() {
        return bufferBeans;
    }

    public void setBufferBeans(CopyOnWriteArrayList<BufferBean> bufferBeans) {
        this.bufferBeans = bufferBeans;
    }

}
