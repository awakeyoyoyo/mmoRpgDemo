package com.liqihao.pojo.baseMessage;

/**
 * 基础角色信息
 * @author lqhao
 */
public class RoleBaseMessage {
    private Integer hp;
    private Integer mp;
    private Integer attack;
    private double damageAdd;

    public double getDamageAdd() {
        return damageAdd;
    }

    public Integer getAttack() {
        return attack;
    }

    public Integer getHp() {
        return hp;
    }

    public Integer getMp() {
        return mp;
    }
}
