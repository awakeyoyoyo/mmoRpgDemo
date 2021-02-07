package com.liqihao.pojo.baseMessage;

/**
 * 商品基本信息类
 * @author lqhao
 */
public class GoodsMessage extends BaseMessage {
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
     * 价格
     */
    private Integer price;

    @Override
    public Integer getTheId() {
        return getId();
    }
    public Integer getId() {
        return id;
    }

    private void setId(Integer id) {
        this.id = id;
    }

    public Integer getArticleMessageId() {
        return articleMessageId;
    }

    private void setArticleMessageId(Integer articleMessageId) {
        this.articleMessageId = articleMessageId;
    }

    public Integer getArticleTypeId() {
        return articleTypeId;
    }

    private void setArticleTypeId(Integer articleTypeId) {
        this.articleTypeId = articleTypeId;
    }

    public Integer getNum() {
        return num;
    }

    private void setNum(Integer num) {
        this.num = num;
    }

    public Integer getPrice() {
        return price;
    }

    private void setPrice(Integer price) {
        this.price = price;
    }
}
