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


    @Override
    public Integer getTheId() {
        return getId();
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Integer getBlood() {
        return blood;
    }

    public Integer getMp() {
        return mp;
    }

    public String getSkillIds() {
        return skillIds;
    }

    public Integer getAttack() {
        return attack;
    }

    public double getDamageAdd() {
        return damageAdd;
    }
}
