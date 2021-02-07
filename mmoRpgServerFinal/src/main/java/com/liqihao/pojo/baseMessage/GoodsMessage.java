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

    public Integer getArticleMessageId() {
        return articleMessageId;
    }

    public Integer getArticleTypeId() {
        return articleTypeId;
    }

    public Integer getNum() {
        return num;
    }

    public Integer getPrice() {
        return price;
    }
}
