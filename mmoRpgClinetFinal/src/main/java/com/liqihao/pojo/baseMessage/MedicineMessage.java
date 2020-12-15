package com.liqihao.pojo.baseMessage;

public class MedicineMessage {
    private Integer id;
    private String name;
    private boolean singleFlag;
    private Integer articleType;
    private String description;
    private Integer damageType;
    private Integer damageValue;
    private Integer lastTime;
    private Integer secondValue;
    private Integer medicineType;
    private Integer cd;

    public Integer getLastTime() {
        return lastTime;
    }

    public void setLastTime(Integer lastTime) {
        this.lastTime = lastTime;
    }

    public Integer getSecondValue() {
        return secondValue;
    }

    public void setSecondValue(Integer secondValue) {
        this.secondValue = secondValue;
    }

    public Integer getMedicineType() {
        return medicineType;
    }

    public void setMedicineType(Integer medicineType) {
        this.medicineType = medicineType;
    }

    public Integer getCd() {
        return cd;
    }

    public void setCd(Integer cd) {
        this.cd = cd;
    }

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
