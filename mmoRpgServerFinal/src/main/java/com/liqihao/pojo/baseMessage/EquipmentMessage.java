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

    private void setEquipmentLevel(Integer equipmentLevel) {
        this.equipmentLevel = equipmentLevel;
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

    public boolean getSingleFlag() {
        return singleFlag;
    }

    private void setSingleFlag(boolean singleFlag) {
        this.singleFlag = singleFlag;
    }

    public Integer getArticleType() {
        return articleType;
    }

    private void setArticleType(Integer articleType) {
        this.articleType = articleType;
    }

    public String getDescription() {
        return description;
    }

    private void setDescription(String description) {
        this.description = description;
    }

    public Integer getDurability() {
        return durability;
    }

    private void setDurability(Integer durability) {
        this.durability = durability;
    }

    public Integer getAttackAdd() {
        return attackAdd;
    }

    private void setAttackAdd(Integer attackAdd) {
        this.attackAdd = attackAdd;
    }

    public double getDamageAdd() {
        return damageAdd;
    }

    private void setDamageAdd(double damageAdd) {
        this.damageAdd = damageAdd;
    }

    public Integer getPosition() {
        return position;
    }

    private void setPosition(Integer position) {
        this.position = position;
    }
}
