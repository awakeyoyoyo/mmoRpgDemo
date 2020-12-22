package com.liqihao.pojo.dto;

/**
 * 传输背包物品dto
 * @author lqhao
 */
public class ArticleDto {
    /**
     * 背包中物品id
     */
    private Integer articleId;
    /**
     *   物品id
     */
    private Integer id;
    /**
     *   数量
     */
    private Integer quantity;
    /**
     * 物品类型
     */
    private Integer articleType;
    /**
     *  数据库行id
     */

    private Integer bagId;
    /**
     *  当前耐久度
     */
    private Integer nowDurability;

    public Integer getNowdurability() {
        return nowDurability;
    }

    public void setNowdurability(Integer nowdurability) {
        nowDurability = nowdurability;
    }

    public Integer getBagId() {
        return bagId;
    }

    public void setBagId(Integer bagId) {
        this.bagId = bagId;
    }

    public Integer getArticleId() {
        return articleId;
    }

    public void setArticleId(Integer articleId) {
        this.articleId = articleId;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Integer getArticleType() {
        return articleType;
    }

    public void setArticleType(Integer articleType) {
        this.articleType = articleType;
    }
}
