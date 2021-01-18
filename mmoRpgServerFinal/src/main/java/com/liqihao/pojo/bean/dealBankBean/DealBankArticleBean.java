package com.liqihao.pojo.bean.dealBankBean;

import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 交易行上物品bean
 * @author lqhao
 */
public class DealBankArticleBean {
    private Integer dealBeanArticleBeanId;
    private Integer dealBankArticleDbId;
    private Integer articleType;
    private Integer articleMessageId;
    private Integer num;
    private Integer price;
    private Integer highPrice;
    private Integer fromRoleId;
    private Integer toRoleId;
    private Integer type;
    private long createTime;
    private long endTime;
    private Integer equipmentId;
    private CopyOnWriteArrayList<DealBankAuctionBean> dealBankAuctionBeans=new CopyOnWriteArrayList<>();

    public CopyOnWriteArrayList<DealBankAuctionBean> getDealBankAuctionBeans() {
        return dealBankAuctionBeans;
    }

    public void setDealBankAuctionBeans(CopyOnWriteArrayList<DealBankAuctionBean> dealBankAuctionBeans) {
        this.dealBankAuctionBeans = dealBankAuctionBeans;
    }

    public Integer getEquipmentId() {
        return equipmentId;
    }

    public void setEquipmentId(Integer equipmentId) {
        this.equipmentId = equipmentId;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Integer getDealBeanArticleBeanId() {
        return dealBeanArticleBeanId;
    }

    public Integer getDealBankArticleDbId() {
        return dealBankArticleDbId;
    }

    public void setDealBankArticleDbId(Integer dealBankArticleDbId) {
        this.dealBankArticleDbId = dealBankArticleDbId;
    }

    public Integer getArticleType() {
        return articleType;
    }

    public void setArticleType(Integer articleType) {
        this.articleType = articleType;
    }

    public Integer getArticleMessageId() {
        return articleMessageId;
    }

    public void setArticleMessageId(Integer articleMessageId) {
        this.articleMessageId = articleMessageId;
    }

    public Integer getNum() {
        return num;
    }

    public void setNum(Integer num) {
        this.num = num;
    }

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    public Integer getHighPrice() {
        return highPrice;
    }

    public void setHighPrice(Integer highPrice) {
        this.highPrice = highPrice;
    }

    public Integer getFromRoleId() {
        return fromRoleId;
    }

    public void setFromRoleId(Integer fromRoleId) {
        this.fromRoleId = fromRoleId;
    }

    public Integer getToRoleId() {
        return toRoleId;
    }

    public void setToRoleId(Integer toRoleId) {
        this.toRoleId = toRoleId;
    }

    public void setDealBeanArticleBeanId(Integer dealBeanArticleBeanId) {
        this.dealBeanArticleBeanId = dealBeanArticleBeanId;
    }
}
