package com.liqihao.pojo.bean.articleBean;

import com.liqihao.Cache.EquipmentMessageCache;
import com.liqihao.Cache.MmoBaseMessageCache;
import com.liqihao.pojo.baseMessage.EquipmentMessage;
import com.liqihao.pojo.bean.BackPackManager;
import com.liqihao.pojo.bean.roleBean.MmoSimpleRole;
import com.liqihao.pojo.dto.ArticleDto;

/**
 * Equipment Bean
 * @author Administrator
 */
public class EquipmentBean implements Article{
    /**
     * 装备信息Id
     */
    private Integer equipmentMessageId;
    private Integer nowDurability;
    private Integer quantity;
    /**
     * 缓存中背包id
     */
    private Integer articleId;
    /**
     * 背包数据库 数据库行记录id
     */
    private Integer bagId;
    /**
     * 装备数据库id
     */
    private Integer equipmentId;
    /**
     * 装备栏 数据库id
     */
    private Integer equipmentBagId;

    /**
     *
     *地面物品的下标
     */
    private Integer floorIndex;

    public Integer getEquipmentMessageId() {
        return equipmentMessageId;
    }

    public void setEquipmentMessageId(Integer equipmentMessageId) {
        this.equipmentMessageId = equipmentMessageId;
    }

    public Integer getFloorIndex() {
        return floorIndex;
    }

    public void setFloorIndex(Integer floorIndex) {
        this.floorIndex = floorIndex;
    }

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
        Integer num= MmoBaseMessageCache.getInstance().getBaseDetailMessage().getReduceDurability();
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
    public Integer fixDurability(){
        EquipmentMessage equipmentMessage= EquipmentMessageCache.getInstance().get(getEquipmentMessageId());
        nowDurability=equipmentMessage.getDurability();
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

    /**
     * 获取类型
     * @return
     */
    @Override
    public Integer getArticleTypeCode() {
        EquipmentMessage equipmentMessage= EquipmentMessageCache.getInstance().get(getEquipmentMessageId());
        return equipmentMessage.getArticleType();
    }

    /**
     * 获取背包id
     * @return
     */
    @Override
    public Integer getArticleIdCode() {
        return getArticleId();
    }

    /**
     * 丢弃或者使用装备
     * @param number
     * @return
     */
    @Override
    public Article useOrAbandon(Integer number, BackPackManager backPackManager) {
        //需要删除数据库的记录
        backPackManager.getNeedDeleteBagId().add(getBagId());
        setBagId(null);
        backPackManager.getBackpacks().remove(this);
        backPackManager.setNowSize(backPackManager.getNowSize()-1);
        return this;
    }

    @Override
    public ArticleDto getArticleMessage() {
        ArticleDto articleDto = new ArticleDto();
        articleDto.setArticleId(getArticleId());
        articleDto.setId(getEquipmentMessageId());
        articleDto.setArticleType(getArticleTypeCode());
        articleDto.setQuantity(getQuantity());
        articleDto.setBagId(getBagId());
        articleDto.setNowDurability(getNowDurability());
        articleDto.setEquipmentId(getEquipmentId());
        return articleDto;
    }

    @Override
    public <T extends Article> T getArticle() {
        return (T)this;
    }

    @Override
    public boolean put(BackPackManager backPackManager) {
        //判断背包大小
        if ((backPackManager.getSize() - backPackManager.getNowSize()) <= 0) {
            //背包一个格子的空间都没有 无法存放
            return false;
        } else {
            //设置背包物品id
            setArticleId(backPackManager.getNewArticleId());
            backPackManager.setNowSize(backPackManager.getNowSize()+1);
            backPackManager.getBackpacks().add(this);
            return true;
        }
    }

    @Override
    public void clearPut(BackPackManager backPackManager) {
        if (getBagId()!=null){
            backPackManager.getNeedDeleteBagId().add(getBagId());
        }
        //设置背包物品id
        setArticleId(backPackManager.getNewArticleId());
        setBagId(null);
        backPackManager.put(this);
    }

    @Override
    public boolean checkCanPut(BackPackManager backPackManager) {
        if (backPackManager.getNowSize() > backPackManager.getSize()) {
            return false;
        }
        return true;
    }
    /**
     *  穿装备 or替换装备
     */

    @Override
    public boolean use(BackPackManager backpackManager, MmoSimpleRole mmoSimpleRole) {
        EquipmentMessage equipmentMessage= EquipmentMessageCache.getInstance().get(getEquipmentMessageId());
        //判断该位置是否有装备
        EquipmentBean oldBean = mmoSimpleRole.getEquipmentBeanHashMap().get(equipmentMessage.getPosition());
        synchronized (backpackManager) {
            if (oldBean != null) {
                //放回背包内
                //背包新增数据
                //修改人物属性
                mmoSimpleRole.setAttack(mmoSimpleRole.getAttack() - equipmentMessage.getAttackAdd());
                mmoSimpleRole.setDamageAdd(mmoSimpleRole.getDamageAdd() - equipmentMessage.getDamageAdd());
                mmoSimpleRole.getNeedDeleteEquipmentIds().add(oldBean.getEquipmentBagId());
                backpackManager.put(oldBean);
            }
            //背包减少装备
            backpackManager.useOrAbandonArticle(getArticleId(), 1);
            //装备栏增加装备
            mmoSimpleRole.getEquipmentBeanHashMap().put(equipmentMessage.getPosition(), this);
            //人物属性
            mmoSimpleRole.setAttack(mmoSimpleRole.getAttack() + equipmentMessage.getAttackAdd());
            mmoSimpleRole.setDamageAdd(mmoSimpleRole.getDamageAdd() + equipmentMessage.getDamageAdd());
            return true;
        }
    }
}
