package com.liqihao.pojo.bean;

import com.liqihao.pojo.baseMessage.EquipmentMessage;

/**
 * @author Administrator
 */
public class EquipmentBean extends EquipmentMessage implements Article{
    private Integer nowDurability;
    private Integer quantity;
    private Integer articleId;
    private Integer bagId;//数据库行记录id

    public Integer getBagId() {
        return bagId;
    }

    public void setBagId(Integer bagId) {
        this.bagId = bagId;
    }

    /**
     * 减耐久度
     * @return
     */
    public boolean reduceDurability(){
        //todo
        return false;
    }

    /**
     * 修复
     * @return
     */
    public boolean changeDurability(){
        //todo
        return false;
    }

    public Integer getArticleId() {
        return articleId;
    }

    public void setArticleId(Integer articleId) {
        this.articleId = articleId;
    }
    public Integer getNowDurability() {
        return nowDurability;
    }

    public void setNowDurability(Integer nowDurability) {
        this.nowDurability = nowDurability;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    @Override
    public Integer getArticleTypeCode() {
        return getArticleType();
    }
}
