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

    private void setDamageAdd(double damageAdd) {
        this.damageAdd = damageAdd;
    }

    public Integer getAttack() {
        return attack;
    }

    private void setAttack(Integer attack) {
        this.attack = attack;
    }

    public Integer getHp() {
        return hp;
    }

    private void setHp(Integer hp) {
        this.hp = hp;
    }

    public Integer getMp() {
        return mp;
    }

    private void setMp(Integer mp) {
        this.mp = mp;
    }
}
