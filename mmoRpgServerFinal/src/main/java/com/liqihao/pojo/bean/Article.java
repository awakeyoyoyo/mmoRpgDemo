package com.liqihao.pojo.bean;

import com.liqihao.pojo.dto.ArticleDto;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

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
     * 获取物品的id
     */
    Integer getArticleIdCode();
    /**
     * 物品减少或者删除
     */
    Article useOrAbandon(Integer number,BackPackManager backPackManager);

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
    boolean put(BackPackManager backPackManager);

    /**
     * 物品整理
     * @param backPackManager
     */
    void clearPut(BackPackManager backPackManager);
    /**
     * 检查是否可以放入
     */
    boolean checkCanPut(BackPackManager backPackManager);
}
