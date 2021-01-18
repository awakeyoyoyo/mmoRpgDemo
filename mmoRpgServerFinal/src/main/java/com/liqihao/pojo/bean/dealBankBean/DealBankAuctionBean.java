package com.liqihao.pojo.bean.dealBankBean;

/**
 * 拍卖记录bean
 * @author lqhao
 */
public class DealBankAuctionBean {
    private Integer dealBeanAuctionBeanId;
    private Integer dealBeanAuctionBeanDbId;
    private Integer dealBeanArticleBeanDbId;
    private Integer money;
    private Integer fromRoleId;
    private long createTime;

    public Integer getDealBeanArticleBeanDbId() {
        return dealBeanArticleBeanDbId;
    }

    public void setDealBeanArticleBeanDbId(Integer dealBeanArticleBeanDbId) {
        this.dealBeanArticleBeanDbId = dealBeanArticleBeanDbId;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public Integer getDealBeanAuctionBeanDbId() {
        return dealBeanAuctionBeanDbId;
    }

    public void setDealBeanAuctionBeanDbId(Integer dealBeanAuctionBeanDbId) {
        this.dealBeanAuctionBeanDbId = dealBeanAuctionBeanDbId;
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

    public Integer getDealBeanAuctionBeanId() {
        return dealBeanAuctionBeanId;
    }

    public void setDealBeanAuctionBeanId(Integer dealBeanAuctionBeanId) {
        this.dealBeanAuctionBeanId = dealBeanAuctionBeanId;
    }
}
