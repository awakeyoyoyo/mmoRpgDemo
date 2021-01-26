package com.liqihao.pojo.baseMessage;

/**
 * boss基本信息类
 * @author lqhao
 */
public class BossMessage  extends BaseMessage{
    private Integer id;
    private String name;
    private Integer blood;
    private Integer mp;
    private String skillIds;
    private Integer attack;
    private double damageAdd;
    private Integer addExp;

    public Integer getAddExp() {
        return addExp;
    }

    public void setAddExp(Integer addExp) {
        this.addExp = addExp;
    }

    @Override
    public Integer getTheId() {
        return getId();
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

    public Integer getBlood() {
        return blood;
    }

    public void setBlood(Integer blood) {
        this.blood = blood;
    }

    public Integer getMp() {
        return mp;
    }

    public void setMp(Integer mp) {
        this.mp = mp;
    }

    public String getSkillIds() {
        return skillIds;
    }

    public void setSkillIds(String skillIds) {
        this.skillIds = skillIds;
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
}
