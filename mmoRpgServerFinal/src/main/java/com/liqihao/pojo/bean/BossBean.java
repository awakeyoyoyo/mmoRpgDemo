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
public class BossBean extends Role{
    private String skillIds;
    private String medicines;
    private String equipmentIds;
    private Integer money;

    public Integer getMoney() {
        return money;
    }

    public void setMoney(Integer money) {
        this.money = money;
    }

    /**
     * 仇恨
     */
    private ConcurrentHashMap<Integer,Integer> hatredMap;
    /**
     * 技能&cd
     */
    private List<SkillBean> skillBeans;
    private HashMap<Integer, Long> cdMap;
    private Integer BossBeanId;

    public String getSkillIds() {
        return skillIds;
    }

    public void setSkillIds(String skillIds) {
        this.skillIds = skillIds;
    }

    public String getMedicines() {
        return medicines;
    }

    public void setMedicines(String medicines) {
        this.medicines = medicines;
    }

    public String getEquipmentIds() {
        return equipmentIds;
    }

    public void setEquipmentIds(String equipmentIds) {
        this.equipmentIds = equipmentIds;
    }

    public Integer getBossBeanId() {
        return BossBeanId;
    }

    public void setBossBeanId(Integer bossBeanId) {
        BossBeanId = bossBeanId;
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



}
