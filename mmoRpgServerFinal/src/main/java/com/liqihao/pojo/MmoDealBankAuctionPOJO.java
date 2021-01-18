package com.liqihao.pojo;

public class MmoDealBankAuctionPOJO {
    private Integer id;

    private Integer dealBankArticleId;

    private Integer money;

    private Integer fromRoleId;

    private Long createTime;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getDealBankArticleId() {
        return dealBankArticleId;
    }

    public void setDealBankArticleId(Integer dealBankArticleId) {
        this.dealBankArticleId = dealBankArticleId;
    }

    public Integer getMoney() {
        return money;
    }

    public void setMoney(Integer money) {
        this.money = money;
    }

    public Integer getFromRoleId() {
        return fromRoleId;
    }

    public void setFromRoleId(Integer fromRoleId) {
        this.fromRoleId = fromRoleId;
    }

    public Long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }
}