package com.liqihao.pojo.bean.articleBean;

import com.liqihao.pojo.bean.BackPackManager;
import com.liqihao.pojo.bean.guildBean.WareHouseManager;
import com.liqihao.pojo.bean.roleBean.MmoSimpleRole;
import com.liqihao.pojo.dto.ArticleDto;

/**
 * 背包物品接口
 * @author lqhao
 */
public interface Article {
    /**
     * 获取物品类型code
     * @return
     */
    Integer getArticleTypeCode();
    /**
     * 获取物品背包的id
     */
    Integer getArticleIdCode();
    /**
     * 获取物品仓库的id
     */
    Integer getWareHouseIdCode();
    /**
     * 物品减少或者删除
     */
    Article useOrAbandon(Integer number, BackPackManager backPackManager,Integer roleId);

    /**
     * 物品转化为物品dto
     * @return
     */
    ArticleDto getArticleMessage();
    /**
     * 获取其类型
     */
    <T extends Article> T getArticle();
    /**
     * 放入背包中
     */
    boolean put(BackPackManager backPackManager,Integer roleId);

    /**
     * 物品整理
     * @param backPackManager
     */
    void clearPut(BackPackManager backPackManager,Integer roleId);
    /**
     * 检查是否可以放入
     */
    boolean checkCanPut(BackPackManager backPackManager);
    /**
     * 检查是否可以放入
     */
    boolean use(BackPackManager backpackManager, MmoSimpleRole mmoSimpleRole);

    void clearPutWareHouse(WareHouseManager wareHouseManager, Integer guildId);

    boolean checkCanPutWareHouse(WareHouseManager wareHouseManager);

    boolean putWareHouse(WareHouseManager wareHouseManager, Integer guildId);

    Article useOrAbandonWareHouse(Integer number, WareHouseManager wareHouseManager, Integer roleId);
}
