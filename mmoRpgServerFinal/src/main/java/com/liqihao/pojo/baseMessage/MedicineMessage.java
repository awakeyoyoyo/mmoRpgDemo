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

    public Integer getSecondValue() {
        return secondValue;
    }

    public Integer getMedicineType() {
        return medicineType;
    }

    public Integer getCd() {
        return cd;
    }

    public Integer getDamageType() {
        return damageType;
    }

    public Integer getDamageValue() {
        return damageValue;
    }

    public Integer getId() {
        return id;
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
}
