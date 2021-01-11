package com.liqihao.pojo.bean;

import com.liqihao.commons.ConstantValue;
import com.liqihao.commons.enums.ArticleTypeCode;
import com.liqihao.pojo.dto.ArticleDto;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

/**
 * 背包管理类
 *
 * @author lqhao
 */
public class BackPackManager {
    private CopyOnWriteArrayList<Article> backpacks;
    private Integer size;
    private volatile Integer nowSize = 0;
    private volatile Integer articleId = 0;
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
    public boolean canPutArticle(Article article) {
        //判断物品类型
        return article.checkCanPut(this);
    }

    //整理背包
    public synchronized void clearBackPack(){
        CopyOnWriteArrayList<Article> newBackPack=new CopyOnWriteArrayList<>();
        CopyOnWriteArrayList<Article> oldBackPack=getBackpacks();
        setBackpacks(newBackPack);
        articleId=0;
        for (Article a:oldBackPack) {
            a.clearPut(this);
        }
    }
    //背包放入东西
    public boolean put(Article article) {
        //判断物品类型
        return article.put(this);
    }

    //背包放入东西 按照数据库格式来存放
    public synchronized void putOnDatabase(Article article) {
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

    //判断背包是否存在某样东西
    public synchronized boolean contains(Article a) {
        return getBackpacks().contains(a);
    }

    //减少某样物品数量/丢弃装备
    public synchronized Article useOrAbandonArticle(Integer articleId, Integer number) {
        for (Article a : getBackpacks()) {
            if (a.getArticleIdCode().equals(articleId)) {
                return a.useOrAbandon(number,this);
            }
        }
        return null;
    }


    public Integer getNowSize() {
        return nowSize;
    }

    //获取背包依存放空间
    public void setNowSize(Integer nowSize) {
        this.nowSize = nowSize;
    }

    //获取背包的大小
    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    //根据articleId获取物品信息
    public synchronized Article getArticleByArticleId(Integer articleId) {
        for (Article article : getBackpacks()) {
            if (articleId.equals(article.getArticleIdCode())) {
                return article;
            }
        }
        return null;
    }

    //获取背包内物品信息
    public synchronized ArrayList<ArticleDto> getBackpacksMessage() {
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
