package com.liqihao.pojo.bean.articleBean;

import com.liqihao.commons.RpgServerException;
import com.liqihao.pojo.bean.BackPackManager;
import com.liqihao.pojo.bean.dealBankBean.DealBankArticleBean;
import com.liqihao.pojo.bean.dealBean.DealArticleBean;
import com.liqihao.pojo.bean.guildBean.WareHouseManager;
import com.liqihao.pojo.bean.roleBean.MmoSimpleRole;
import com.liqihao.pojo.dto.ArticleDto;

/**
 * 背包物品接口
 * @author lqhao
 */
public interface Article {
    
    /**
     * description 获取物品类型code
     * @return {@link Integer }
     * @author lqhao
     * @createTime 2021/1/25 11:04
     */
    Integer getArticleTypeCode();
    
    /**
     * description 获取物品背包的id
     * @return {@link Integer }
     * @author lqhao
     * @createTime 2021/1/25 11:04
     */
    Integer getArticleIdCode();

    /**
     * description 获取物品仓库id
     * @return {@link Integer }
     * @author lqhao
     * @createTime 2021/1/25 11:05
     */
    Integer getWareHouseIdCode();

    /**
     * description 获取交易栏的id
     * @return {@link Integer }
     * @author lqhao
     * @createTime 2021/1/25 11:06
     */
    Integer getDealArticleIdCode();
  
    /**
     * description 物品减少或者删除
     * @param number
     * @param backPackManager
     * @param roleId
     * @return {@link Article }
     * @author lqhao
     * @createTime 2021/1/25 11:07
     */
    Article useOrAbandon(Integer number, BackPackManager backPackManager,Integer roleId);

   
    /**
     * description 物品转化为物品dto
     * @return {@link ArticleDto }
     * @author lqhao
     * @createTime 2021/1/25 11:07
     */
    ArticleDto getArticleMessage();
  
    /**
     * description 获取其类型
     * @return {@link T }
     * @author lqhao
     * @createTime 2021/1/25 11:07
     */
    <T extends Article> T getArticle();
  
    /**
     * description 放入背包中
     * @param backPackManager  
     * @param roleId
     * @return {@link boolean }
     * @author lqhao
     * @createTime 2021/1/25 11:07
     */
    boolean put(BackPackManager backPackManager,Integer roleId);
    
    /**
     * description 物品整理
     * @param backPackManager  
     * @param roleId
     * @return void
     * @author lqhao
     * @createTime 2021/1/25 11:07
     */
    void clearPut(BackPackManager backPackManager,Integer roleId);

    /**
     * description 检查是否可以放入
     * @param backPackManager
     * @return {@link boolean }
     * @author lqhao
     * @createTime 2021/1/25 11:08
     */
    boolean checkCanPut(BackPackManager backPackManager);

    /**
     * description 物品被使用
     * @param backpackManager
     * @param mmoSimpleRole
     * @return {@link boolean }
     * @author lqhao
     * @createTime 2021/1/25 11:04
     */
    boolean use(BackPackManager backpackManager, MmoSimpleRole mmoSimpleRole);

    void clearPutWareHouse(WareHouseManager wareHouseManager, Integer guildId);

    boolean checkCanPutWareHouse(WareHouseManager wareHouseManager);

    boolean putWareHouse(WareHouseManager wareHouseManager, Integer guildId);

    Article useOrAbandonWareHouse(Integer number, WareHouseManager wareHouseManager,Integer guildId);

    boolean putDealBean(DealArticleBean dealArticleBean);

    Article abandonDealBean(Integer number,DealArticleBean dealArticleBean);

    DealBankArticleBean convertDealBankArticleBean();
}
