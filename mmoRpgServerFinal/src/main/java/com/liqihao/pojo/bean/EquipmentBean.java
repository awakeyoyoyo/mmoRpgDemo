package com.liqihao.pojo.bean;

import com.liqihao.Cache.MmoCache;
import com.liqihao.pojo.baseMessage.EquipmentMessage;
import org.apache.ibatis.annotations.Param;

/**
 * @author Administrator
 */
public class EquipmentBean extends EquipmentMessage implements Article{
    private Integer nowDurability;
    private Integer quantity;
    private Integer articleId;//缓存中背包id
    private Integer bagId;//背包数据库 数据库行记录id
    private Integer equipmentId; //装备数据库id
    private Integer equipmentBagId;//装备栏数据库id

    public Integer getEquipmentBagId() {
        return equipmentBagId;
    }

    public void setEquipmentBagId(Integer equipmentBagId) {
        this.equipmentBagId = equipmentBagId;
    }

    public Integer getEquipmentId() {
        return equipmentId;
    }

    public void setEquipmentId(Integer equipmentId) {
        this.equipmentId = equipmentId;
    }

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
        Integer num= MmoCache.getInstance().getBaseDetailMessage().getReduceDurability();
        if (nowDurability>num){
            nowDurability-=num;
            return true;
        }
        return false;
    }

    /**
     * 修复
     * @return
     */
    public Integer changeDurability(int number){
        nowDurability+=number;
        if (nowDurability>getDurability()) {
            nowDurability=getDurability();
        }
        return nowDurability;
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
