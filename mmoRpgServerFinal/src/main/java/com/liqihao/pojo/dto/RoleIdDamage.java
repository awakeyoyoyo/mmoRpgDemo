package com.liqihao.pojo.dto;

public class RoleIdDamage {
    Integer fromRoleId;

    Integer toRoleId;

    Integer bufferId;

    Integer skillId;

    Integer attackStyle; //buff造成、攻击造成、使用技能、自动恢复

    //伤害类型 扣血 扣蓝
    Integer damageType;

    Integer damage;
    //boss当前血量或者角色血量
    Integer nowblood;

    Integer mp;

    Integer State;

    public Integer getBufferId() {
        return bufferId;
    }

    public void setBufferId(Integer bufferId) {
        this.bufferId = bufferId;
    }

    public Integer getSkillId() {
        return skillId;
    }

    public void setSkillId(Integer skillId) {
        this.skillId = skillId;
    }

    public Integer getFromRoleId() {
        return fromRoleId;
    }

    public void setFromRoleId(Integer fromRoleId) {
        this.fromRoleId = fromRoleId;
    }

    public Integer getToRoleId() {
        return toRoleId;
    }

    public void setToRoleId(Integer toRoleId) {
        this.toRoleId = toRoleId;
    }

    public Integer getAttackStyle() {
        return attackStyle;
    }

    public void setAttackStyle(Integer attackStyle) {
        this.attackStyle = attackStyle;
    }

    public Integer getMp() {
        return mp;
    }

    public void setMp(Integer mp) {
        this.mp = mp;
    }

    public Integer getNowblood() {
        return nowblood;
    }

    public void setNowblood(Integer nowblood) {
        this.nowblood = nowblood;
    }

    public Integer getState() {
        return State;
    }

    public void setState(Integer state) {
        State = state;
    }


    public Integer getDamageType() {
        return damageType;
    }

    public void setDamageType(Integer damageType) {
        this.damageType = damageType;
    }

    public Integer getDamage() {
        return damage;
    }

    public void setDamage(Integer damage) {
        this.damage = damage;
    }
}
