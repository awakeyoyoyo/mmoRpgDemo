package com.liqihao.pojo.baseMessage;

/**
 * 装备信息
 * @author lqhao
 */
public class EquipmentMessage extends BaseMessage {
    private Integer id;
    private String name;
    private boolean singleFlag;
    private Integer articleType;
    private String description;
    private Integer durability;
    private Integer attackAdd;
    private double damageAdd;
    private Integer position;
    private Integer equipmentLevel;
    @Override
    public Integer getTheId() {
        return getId();
    }
    public Integer getId() {
        return id;
    }

    public Integer getEquipmentLevel() {
        return equipmentLevel;
    }

    public String getName() {
        return name;
    }

    public boolean getSingleFlag() {
        return singleFlag;
    }

    public Integer getArticleType() {
        return articleType;
    }

    public String getDescription() {
        return description;
    }

    public Integer getDurability() {
        return durability;
    }

    public Integer getAttackAdd() {
        return attackAdd;
    }

    public double getDamageAdd() {
        return damageAdd;
    }

    public Integer getPosition() {
        return position;
    }
}
