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

    private void setAddExp(Integer addExp) {
        this.addExp = addExp;
    }

    @Override
    public Integer getTheId() {
        return getId();
    }

    public Integer getId() {
        return id;
    }

    private void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    private void setName(String name) {
        this.name = name;
    }

    public Integer getBlood() {
        return blood;
    }

    private void setBlood(Integer blood) {
        this.blood = blood;
    }

    public Integer getMp() {
        return mp;
    }

    private void setMp(Integer mp) {
        this.mp = mp;
    }

    public String getSkillIds() {
        return skillIds;
    }

    private void setSkillIds(String skillIds) {
        this.skillIds = skillIds;
    }

    public Integer getAttack() {
        return attack;
    }

    private void setAttack(Integer attack) {
        this.attack = attack;
    }

    public double getDamageAdd() {
        return damageAdd;
    }

    private void setDamageAdd(double damageAdd) {
        this.damageAdd = damageAdd;
    }
}
