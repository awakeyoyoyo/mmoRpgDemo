package com.liqihao.pojo.bean;

/**
 * 商品实体类
 * @author lqhao
 */
public class GoodsBean {
    /**
     * 商品id
     */
    private Integer id;
    /**
     * 商品信息Id
     */
    private Integer goodsMessageId;
    /**
     * 剩余数量
     */
    private volatile Integer nowNum;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getGoodsMessageId() {
        return goodsMessageId;
    }

    public void setGoodsMessageId(Integer goodsMessageId) {
        this.goodsMessageId = goodsMessageId;
    }

    public Integer getNowNum() {
        return nowNum;
    }

    public void setNowNum(Integer nowNum) {
        this.nowNum = nowNum;
    }

}
