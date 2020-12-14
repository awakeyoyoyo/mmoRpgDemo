package com.liqihao.pojo.baseMessage;

public class MedicineMessage {
    private Integer id;
    private String name;
    private boolean singleFlag;
    private Integer articleType;
    private String description;
    private Integer damageType;
    private Integer damageValue;

    public Integer getDamageType() {
        return damageType;
    }

    public void setDamageType(Integer damageType) {
        this.damageType = damageType;
    }

    public Integer getDamageValue() {
        return damageValue;
    }

    public void setDamageValue(Integer damageValue) {
        this.damageValue = damageValue;
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

    public boolean getSingleFlag() {
        return singleFlag;
    }

    public void setSingleFlag(boolean singleFlag) {
        this.singleFlag = singleFlag;
    }

    public Integer getArticleType() {
        return articleType;
    }

    public void setArticleType(Integer articleType) {
        this.articleType = articleType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
