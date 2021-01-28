package com.liqihao.pojo.bean.articleBean;

import com.liqihao.Cache.EquipmentMessageCache;
import com.liqihao.Cache.MmoBaseMessageCache;
import com.liqihao.commons.enums.TaskTargetTypeCode;
import com.liqihao.pojo.baseMessage.EquipmentMessage;
import com.liqihao.pojo.bean.BackPackManager;
import com.liqihao.pojo.bean.dealBankBean.DealBankArticleBean;
import com.liqihao.pojo.bean.dealBean.DealArticleBean;
import com.liqihao.pojo.bean.guildBean.WareHouseManager;
import com.liqihao.pojo.bean.roleBean.MmoSimpleRole;
import com.liqihao.pojo.bean.taskBean.oneBestEquipmentTask.OneBestEquipmentAction;
import com.liqihao.pojo.dto.ArticleDto;
import com.liqihao.util.DbUtil;
import com.liqihao.util.ScheduledThreadPoolUtil;

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
     *地面物品的下标
     */
    private Integer floorIndex;

    /**
     * 仓库id
     */
    private Integer wareHouseId;

    /**
     * 仓库 数据库id
     */
    private Integer wareHouseDBId;
    /**
     * 交易栏id
     */
    private Integer dealArticleId;

    public Integer getDealArticleId() {
        return dealArticleId;
    }

    public void setDealArticleId(Integer dealArticleId) {
        this.dealArticleId = dealArticleId;
    }

    public Integer getWareHouseId() {
        return wareHouseId;
    }

    public void setWareHouseId(Integer wareHouseId) {
        this.wareHouseId = wareHouseId;
    }

    public Integer getWareHouseDBId() {
        return wareHouseDBId;
    }

    public void setWareHouseDBId(Integer wareHouseDBId) {
        this.wareHouseDBId = wareHouseDBId;
    }

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

    @Override
    public Integer getDealArticleIdCode() {
        return getDealArticleId();
    }

    @Override
    public Integer getWareHouseIdCode() {
        return getWareHouseId();
    }

    /**
     * 丢弃或者使用装备
     * @param number
     * @return
     */
    @Override
    public Article useOrAbandon(Integer number, BackPackManager backPackManager,Integer roleId) {
        //需要删除数据库的记录
        Integer bagId=getBagId();
        setBagId(null);
        backPackManager.getBackpacks().remove(this);
        backPackManager.setNowSize(backPackManager.getNowSize()-1);
        //数据库
        DbUtil.deleteBagById(bagId);
        return this;
    }

    /**
     * 物品信息
     * @return
     */
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
        articleDto.setWareHouseId(getWareHouseId());
        articleDto.setWareHouseDBId(getWareHouseDBId());
        articleDto.setDealArticleId(getDealArticleId());
        return articleDto;
    }

    /**
     * 返回子类
     * @param <T>
     * @return
     */
    @Override
    public <T extends Article> T getArticle() {
        return (T)this;
    }

    /**
     * 放入背包
     * @param backPackManager
     * @param roleId
     * @return
     */
    @Override
    public boolean put(BackPackManager backPackManager,Integer roleId) {
        //判断背包大小
        if ((backPackManager.getSize() - backPackManager.getNowSize()) <= 0) {
            //背包一个格子的空间都没有 无法存放
            return false;
        } else {
            //设置背包物品id
            setArticleId(backPackManager.getNewArticleId());
            backPackManager.setNowSize(backPackManager.getNowSize()+1);
            backPackManager.getBackpacks().add(this);
            setBagId(DbUtil.getBagPojoNextIndex());
            //数据库
            ArticleDto articleDto=new ArticleDto();
            articleDto.setQuantity(getQuantity());
            articleDto.setEquipmentId(getEquipmentId());
            articleDto.setArticleType(getArticleTypeCode());
            articleDto.setBagId(getBagId());
            DbUtil.insertBag(articleDto,roleId);
            return true;
        }
    }

    /**
     * 整理背包从新放入
     * @param backPackManager
     * @param roleId
     */
    @Override
    public void clearPut(BackPackManager backPackManager,Integer roleId) {
//        if (getBagId()!=null){
//            backPackManager.getNeedDeleteBagId().add(getBagId());
//        }
        Integer oldBagId=getBagId();
        //设置背包物品id
        setArticleId(backPackManager.getNewArticleId());
        backPackManager.put(this,roleId);
        //数据库
        ArticleDto articleDto=new ArticleDto();
        articleDto.setQuantity(getQuantity());
        articleDto.setEquipmentId(getEquipmentId());
        articleDto.setArticleType(getArticleTypeCode());
        articleDto.setBagId(getBagId());

        DbUtil.deleteBagById(oldBagId);

    }

    /**
     * 检查是否可放入背包
     * @param backPackManager
     * @return
     */
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
//                mmoSimpleRole.getNeedDeleteEquipmentIds().add(oldBean.getEquipmentBagId());
                Integer oldEquipmentBagId=oldBean.getEquipmentBagId();
                oldBean.setEquipmentBagId(null);
                //入库
                DbUtil.deleteEquipmentBagById(oldEquipmentBagId);
                backpackManager.put(oldBean,mmoSimpleRole.getId());
            }
            //背包减少装备
            backpackManager.useOrAbandonArticle(getArticleId(), 1,mmoSimpleRole.getId());
            //装备栏增加装备
            mmoSimpleRole.getEquipmentBeanHashMap().put(equipmentMessage.getPosition(), this);
            equipmentBagId=DbUtil.getEquipmentBagNextIndex();
            //修改人物装备星数
            Integer olderEquipmentLevel=mmoSimpleRole.getEquipmentLevel();
            if (oldBean!=null) {
                EquipmentMessage oldEquipmentMessage = EquipmentMessageCache.getInstance().get(oldBean.getEquipmentMessageId());
                olderEquipmentLevel=olderEquipmentLevel+equipmentMessage.getEquipmentLevel()-oldEquipmentMessage.getEquipmentLevel();
            }else{
                olderEquipmentLevel=olderEquipmentLevel+equipmentMessage.getEquipmentLevel();
            }
            mmoSimpleRole.changeEquipmentLevel(olderEquipmentLevel);
            //穿极品装备
            OneBestEquipmentAction oneBestEquipmentAction=new OneBestEquipmentAction();
            oneBestEquipmentAction.setTaskTargetType(TaskTargetTypeCode.BEST_EQUIPMENT.getCode());
            oneBestEquipmentAction.setEquipmentLevel(equipmentMessage.getEquipmentLevel());
            mmoSimpleRole.getTaskManager().handler(oneBestEquipmentAction,mmoSimpleRole);
            //插入数据库
            EquipmentBean equipmentBean=this;
            Integer roleId=mmoSimpleRole.getId();
             DbUtil.addEquipmentBagPOJO(equipmentBean,roleId);
            //人物属性
            mmoSimpleRole.setAttack(mmoSimpleRole.getAttack() + equipmentMessage.getAttackAdd());
            mmoSimpleRole.setDamageAdd(mmoSimpleRole.getDamageAdd() + equipmentMessage.getDamageAdd());
            return true;
        }
    }

    /**
     * 整理放入仓库中
     * @param wareHouseManager
     * @param guildId
     */
    @Override
    public void clearPutWareHouse(WareHouseManager wareHouseManager, Integer guildId) {
        Integer wareHouseDBId=getWareHouseDBId();
        wareHouseManager.putWareHouse(this,guildId);
        //数据库
        DbUtil.deleteWareHouseById(wareHouseDBId);

    }

    /**
     * 检测是否可放入仓库中
     * @param wareHouseManager
     * @return
     */
    @Override
    public boolean checkCanPutWareHouse(WareHouseManager wareHouseManager) {
        if (wareHouseManager.getNowSize() > wareHouseManager.getSize()) {
            return false;
        }
        return true;
    }

    /**
     * 放入仓库
     * @param wareHouseManager
     * @param guildId
     * @return
     */
    @Override
    public boolean putWareHouse(WareHouseManager wareHouseManager, Integer guildId) {
        //判断背包大小
        if ((wareHouseManager.getSize() - wareHouseManager.getNowSize()) <= 0) {
            //背包一个格子的空间都没有 无法存放
            return false;
        } else {
            //设置仓库id
            setWareHouseId(wareHouseManager.addAndReturnWareHouseId());
            setWareHouseDBId(DbUtil.getWareHouseIndex());
            //删除背包id
            Integer bagId=getBagId();
            setBagId(null);
            setArticleId(null);
            wareHouseManager.getBackpacks().add(this);
            wareHouseManager.addAndReturnNowSize();
            //数据库
            ArticleDto articleDto=new ArticleDto();
            articleDto.setQuantity(getQuantity());
            articleDto.setEquipmentId(getEquipmentId());
            articleDto.setArticleType(getArticleTypeCode());
            articleDto.setWareHouseDBId(getWareHouseDBId());
            //入库
            DbUtil.insertEquipmentWareHouse(articleDto,guildId);
            DbUtil.deleteBagById(bagId);
            return true;
        }
    }

    @Override
    public Article useOrAbandonWareHouse(Integer number, WareHouseManager wareHouseManager,Integer guildId) {
        //需要删除数据库的记录
        Integer wareHouseDBId=getWareHouseDBId();
        setWareHouseDBId(null);
        wareHouseManager.getBackpacks().remove(this);
        wareHouseManager.reduceAndReturnNowSize();
        //数据库
         DbUtil.deleteWareHouseById(wareHouseDBId);
        return this;
    }

    @Override
    public boolean putDealBean(DealArticleBean dealArticleBean) {
        //判断交易栏大小
        if ((dealArticleBean.getSize() - dealArticleBean.getNowSize()) <= 0) {
            //背包一个格子的空间都没有 无法存放
            return false;
        } else {
            //删除背包id
            setBagId(null);
            setArticleId(null);
            dealArticleBean.getArticles().add(this);
            setDealArticleId(dealArticleBean.addAndReturnDealArticleId());
            dealArticleBean.addAndReturnNowSize();
            return true;
        }
    }

    @Override
    /**
     * 丢弃交易栏物品
     */
    public Article abandonDealBean(Integer number,DealArticleBean dealArticleBean) {
        setDealArticleId(null);
        dealArticleBean.getArticles().remove(this);
        dealArticleBean.reduceAndReturnNowSize();
        return this;
    }

    @Override
    /**
     * 转化为拍卖行物品
     */
    public DealBankArticleBean convertDealBankArticleBean() {
        DealBankArticleBean dealBankArticleBean=new DealBankArticleBean();
        dealBankArticleBean.setArticleMessageId(getEquipmentMessageId());
        dealBankArticleBean.setArticleType(getArticleTypeCode());
        dealBankArticleBean.setEquipmentId(getEquipmentId());
        dealBankArticleBean.setNum(getQuantity());
        return dealBankArticleBean;
    }
}
