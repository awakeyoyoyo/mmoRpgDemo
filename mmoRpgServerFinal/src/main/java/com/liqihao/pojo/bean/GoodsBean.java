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
    private Integer articleMessageId;
    /**
     * 商品类型
     */
    private Integer articleTypeId;
    /**
     * 数量
     */
    private Integer num;
    /**
     * 剩余数量
     */
    private volatile Integer nowNum;
    /**
     * 价格
     */
    private Integer price;


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getArticleMessageId() {
        return articleMessageId;
    }

    public void setArticleMessageId(Integer articleMessageId) {
        this.articleMessageId = articleMessageId;
    }

    public Integer getArticleTypeId() {
        return articleTypeId;
    }

    public void setArticleTypeId(Integer articleTypeId) {
        this.articleTypeId = articleTypeId;
    }

    public Integer getNum() {
        return num;
    }

    public void setNum(Integer num) {
        this.num = num;
    }

    public Integer getNowNum() {
        return nowNum;
    }

    public void setNowNum(Integer nowNum) {
        this.nowNum = nowNum;
    }

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }
}
