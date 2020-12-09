package com.liqihao.pojo.dto;

public class RoleIdDamage {
    Integer roleId;
    //扣血 扣蓝
    Integer damageType;
    Integer damage;
    //boss当前血量或者角色血量
    Integer nowblood;
    Integer State;

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

    public Integer getRoleId() {
        return roleId;
    }

    public void setRoleId(Integer roleId) {
        this.roleId = roleId;
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
