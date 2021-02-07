package com.liqihao.pojo.baseMessage;

/**
 *装备信息
 * @author lqhao
 */
public class MedicineMessage extends BaseMessage{
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
    @Override
    public Integer getTheId() {
        return getId();
    }
    public Integer getLastTime() {
        return lastTime;
    }

    private void setLastTime(Integer lastTime) {
        this.lastTime = lastTime;
    }

    public Integer getSecondValue() {
        return secondValue;
    }

    private void setSecondValue(Integer secondValue) {
        this.secondValue = secondValue;
    }

    public Integer getMedicineType() {
        return medicineType;
    }

    private void setMedicineType(Integer medicineType) {
        this.medicineType = medicineType;
    }

    public Integer getCd() {
        return cd;
    }

    private void setCd(Integer cd) {
        this.cd = cd;
    }

    public Integer getDamageType() {
        return damageType;
    }

    private void setDamageType(Integer damageType) {
        this.damageType = damageType;
    }

    public Integer getDamageValue() {
        return damageValue;
    }

    private void setDamageValue(Integer damageValue) {
        this.damageValue = damageValue;
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
}
