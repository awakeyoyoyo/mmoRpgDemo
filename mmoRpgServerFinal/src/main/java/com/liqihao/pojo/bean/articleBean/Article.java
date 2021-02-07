package com.liqihao.pojo.bean.articleBean;

import com.liqihao.Dbitem.Iitem;
import com.liqihao.commons.RpgServerException;
import com.liqihao.pojo.bean.BackPackManager;
import com.liqihao.pojo.bean.dealBankBean.DealBankArticleBean;
import com.liqihao.pojo.bean.dealBean.DealArticleBean;
import com.liqihao.pojo.bean.guildBean.WareHouseManager;
import com.liqihao.pojo.bean.roleBean.MmoSimpleRole;
import com.liqihao.pojo.dto.ArticleDto;
import com.liqihao.util.DbUtil;
import com.liqihao.util.ScheduledThreadPoolUtil;

/**
 * 背包物品接口
 * @author lqhao
 */
public abstract class Article extends Iitem {
    /**
     * 数量
     */
    private Integer quantity;
    /**
     * 缓存中背包id
     */
    private Integer articleId;
    /**
     * 物品信息id
     */
    private Integer articleMessageId;

    /**
     * 背包数据库 数据库行记录id
     */
    private Integer bagId;

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




    public Integer getArticleMessageId() {
        return articleMessageId;
    }

    public void setArticleMessageId(Integer articleMessageId) {
        this.articleMessageId = articleMessageId;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Integer getArticleId() {
        return articleId;
    }

    public void setArticleId(Integer articleId) {
        this.articleId = articleId;
    }

    public Integer getBagId() {
        return bagId;
    }

    public void setBagId(Integer bagId) {
        this.bagId = bagId;
    }

    public Integer getFloorIndex() {
        return floorIndex;
    }

    public void setFloorIndex(Integer floorIndex) {
        this.floorIndex = floorIndex;
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

    public Integer getDealArticleId() {
        return dealArticleId;
    }

    public void setDealArticleId(Integer dealArticleId) {
        this.dealArticleId = dealArticleId;
    }

    /**
     * description 物品减少或者删除
     * @param number
     * @param backPackManager
     * @param roleId
     * @return {@link Article }
     * @author lqhao
     * @createTime 2021/1/25 11:07
     */
    public abstract Article useOrAbandon(Integer number, BackPackManager backPackManager,Integer roleId);

    /**
     * description 获取物品type
     * @return {@link Integer }
     * @author lqhao
     * @createTime 2021/1/29 16:12
     */

    public abstract Integer getArticleTypeCode();
    /**
     * description 物品转化为物品dto
     * @return {@link ArticleDto }
     * @author lqhao
     * @createTime 2021/1/25 11:07
     */
    public abstract ArticleDto getArticleMessage();
  
    /**
     * description 获取其类型
     * @return {@link T }
     * @author lqhao
     * @createTime 2021/1/25 11:07
     */
    public  abstract <T extends Article> T getArticle();
  
    /**
     * description 放入背包中
     * @param backPackManager  
     * @param roleId
     * @return {@link boolean }
     * @author lqhao
     * @createTime 2021/1/25 11:07
     */
    public abstract  boolean put(BackPackManager backPackManager,Integer roleId);
    
    /**
     * description 物品整理
     * @param backPackManager  
     * @param roleId
     * @return void
     * @author lqhao
     * @createTime 2021/1/25 11:07
     */
    public abstract void clearPut(BackPackManager backPackManager,Integer roleId);

    /**
     * description 检查是否可以放入
     * @param backPackManager
     * @return {@link boolean }
     * @author lqhao
     * @createTime 2021/1/25 11:08
     */
    public abstract boolean checkCanPut(BackPackManager backPackManager);

    /**
     * description 物品被使用
     * @param backpackManager
     * @param mmoSimpleRole
     * @return {@link boolean }
     * @author lqhao
     * @createTime 2021/1/25 11:04
     */
    public abstract boolean use(BackPackManager backpackManager, MmoSimpleRole mmoSimpleRole);

    /**
     * description 按规则放入仓库
     * @param wareHouseManager
     * @param guildId
     * @return {@link null }
     * @author lqhao
     * @createTime 2021/1/29 16:13
     */
    public abstract void clearPutWareHouse(WareHouseManager wareHouseManager, Integer guildId);

    /**
     * description 是否可放入仓库
     * @param wareHouseManager
     * @return {@link boolean }
     * @author lqhao
     * @createTime 2021/1/29 16:13
     */
    public abstract boolean checkCanPutWareHouse(WareHouseManager wareHouseManager);

    /**
     * description 放入仓库
     * @param wareHouseManager
     * @param guildId
     * @return {@link boolean }
     * @author lqhao
     * @createTime 2021/1/29 16:13
     */
    public abstract boolean putWareHouse(WareHouseManager wareHouseManager, Integer guildId);

    /**
     * description 减少仓库中某物品数量
     * @param number
     * @param wareHouseManager
     * @param guildId
     * @return {@link Article }
     * @author lqhao
     * @createTime 2021/1/29 16:13
     */
    public abstract Article useOrAbandonWareHouse(Integer number, WareHouseManager wareHouseManager,Integer guildId);

    /**
     * description 放入仓库
     * @param dealArticleBean
     * @return {@link boolean }
     * @author lqhao
     * @createTime 2021/1/29 16:14
     */
    public abstract boolean putDealBean(DealArticleBean dealArticleBean);

    /**
     * description 减少交易栏中某物品数量
     * @param number
     * @param dealArticleBean
     * @return {@link Article }
     * @author lqhao
     * @createTime 2021/1/29 16:14
     */
    public abstract Article abandonDealBean(Integer number,DealArticleBean dealArticleBean);

    /**
     * description 转化为交易栏bean
     * @return {@link DealBankArticleBean }
     * @author lqhao
     * @createTime 2021/1/29 16:14
     */
    public abstract DealBankArticleBean convertDealBankArticleBean();

    /**
     * 背包数据库更新
     * @param id
     */
    @Override
    public void updateItem(Integer id) {
        if (!getChangeFlag()) {
            setChangeFlag(true);
            Article article=this;
            if (getBagId()!=null) {
                ScheduledThreadPoolUtil.addTask(() -> DbUtil.updateBagPojo(article, id));
            }else if (getWareHouseDBId()!=null){
                //公会
                ScheduledThreadPoolUtil.addTask(()->DbUtil.updateWareHousePojo(article,id));
            }
        }
    }
}
