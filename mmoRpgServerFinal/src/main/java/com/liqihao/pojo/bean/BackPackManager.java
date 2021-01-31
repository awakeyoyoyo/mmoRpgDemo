package com.liqihao.pojo.bean;

import com.liqihao.commons.enums.ArticleTypeCode;
import com.liqihao.pojo.bean.articleBean.Article;
import com.liqihao.pojo.bean.articleBean.EquipmentBean;
import com.liqihao.pojo.bean.articleBean.MedicineBean;
import com.liqihao.pojo.dto.ArticleDto;
import com.liqihao.util.DbUtil;
import com.liqihao.util.ScheduledThreadPoolUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 背包管理类
 * @author lqhao
 */
public class BackPackManager {
    private CopyOnWriteArrayList<Article> backpacks;
    private Integer size;
    private Integer nowSize = 0;
    private Integer articleId = 0;
    private List<Integer> needDeleteBagId = new ArrayList<>();

    public List<Integer> getNeedDeleteBagId() {
        return needDeleteBagId;
    }

    public void setNeedDeleteBagId(List<Integer> needDeleteBagId) {
        this.needDeleteBagId = needDeleteBagId;
    }

    public BackPackManager() {
    }

    public BackPackManager(Integer size) {
        backpacks = new CopyOnWriteArrayList<Article>();
        this.size = size;
    }

    public Integer getArticleId() {
        return articleId;
    }

    public void setArticleId(Integer articleId) {
        this.articleId = articleId;
    }

    public Integer getNewArticleId() {
        return ++articleId;
    }

    public CopyOnWriteArrayList<Article> getBackpacks() {
        return backpacks;
    }

    /**
     * 背包格子是否足够
     */
    public boolean canPutArticle(Integer articleMessageId,Integer articleType,Integer num) {
        Article article;
        if (articleType.equals(ArticleTypeCode.EQUIPMENT.getCode())){
            article=new MedicineBean();

        }else if (articleType.equals(ArticleTypeCode.MEDICINE.getCode())){
            article=new EquipmentBean();
        }else{
            return false;
        }
        article.setQuantity(num);
        article.setArticleMessageId(articleMessageId);
        //判断物品类型
        return article.checkCanPut(this);
    }

    /**
     * 整理背包
     */
    public  void clearBackPack(Integer roleId){
        CopyOnWriteArrayList<Article> newBackPack=new CopyOnWriteArrayList<>();
        CopyOnWriteArrayList<Article> oldBackPack=getBackpacks();
        setBackpacks(newBackPack);
        articleId=0;
        for (Article a:oldBackPack) {
            a.clearPut(this,roleId);
        }
    }
    /**
     * 背包放入东西
     */
    public  boolean put(Article article,Integer roleId) {
        //判断物品类型
        return article.put(this,roleId);
    }

    /**
     * 背包放入东西 按照数据库格式来存放
     */
    public  void putOnDatabase(Article article) {
        if (article.getArticleTypeCode().equals(ArticleTypeCode.MEDICINE.getCode())) {
            MedicineBean medicineBean = article.getArticle();
            medicineBean.setArticleId(getNewArticleId());
            getBackpacks().add(medicineBean);
            nowSize++;
        } else if ((article.getArticleTypeCode().equals(ArticleTypeCode.EQUIPMENT.getCode()))) {
            //判断背包大小
            EquipmentBean equipmentBean = article.getArticle();
            //设置背包物品id
            equipmentBean.setArticleId(getNewArticleId());
            nowSize++;
            getBackpacks().add(equipmentBean);
        }

    }

    /**
     * 判断背包是否存在某样东西
     */
    public  boolean contains(Article a) {
        return getBackpacks().contains(a);
    }

    /**
     * 减少某样物品数量/丢弃装备
     */
    public  Article useOrAbandonArticle(Integer articleId, Integer number,Integer roleId) {
        for (Article a : getBackpacks()) {
            if (a.getArticleId().equals(articleId)) {
                return a.useOrAbandon(number,this,roleId);
            }
        }
        return null;
    }


    public Integer getNowSize() {
        return nowSize;
    }

    /**
     * 获取背包依存放空间
     */
    public void setNowSize(Integer nowSize) {
        this.nowSize = nowSize;
    }

    /**
     * 获取背包的大小
     */
    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    /**
     * 根据articleId获取物品信息
     */
    public  Article getArticleByArticleId(Integer articleId) {
        for (Article article : getBackpacks()) {
            if (articleId.equals(article.getArticleId())) {
                return article;
            }
        }
        return null;
    }

    /**
     * 获取背包内物品信息
     */
    public  ArrayList<ArticleDto> getBackpacksMessage() {
        ArrayList<ArticleDto> articleDtos = new ArrayList<>();
        for (Article article : getBackpacks()) {
            ArticleDto articleDto = article.getArticleMessage();
            articleDtos.add(articleDto);
        }
        return articleDtos;
    }

    public void setBackpacks(CopyOnWriteArrayList<Article> backpacks) {
        this.backpacks = backpacks;
    }

    public static void main(String[] args) {
    }
}
