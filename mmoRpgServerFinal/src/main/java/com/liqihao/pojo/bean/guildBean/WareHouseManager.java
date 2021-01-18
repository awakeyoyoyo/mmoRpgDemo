package com.liqihao.pojo.bean.guildBean;

import com.liqihao.commons.enums.ArticleTypeCode;
import com.liqihao.pojo.bean.articleBean.Article;
import com.liqihao.pojo.bean.articleBean.EquipmentBean;
import com.liqihao.pojo.bean.articleBean.MedicineBean;
import com.liqihao.pojo.dto.ArticleDto;

import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 仓库管理类
 * @author lqhao
 */
public class WareHouseManager {
    private CopyOnWriteArrayList<Article> backpacks=new CopyOnWriteArrayList<>();
    private Integer size=50;
    private AtomicInteger nowSize = new AtomicInteger(0);
    private AtomicInteger wareHouseId = new AtomicInteger(0);

    public WareHouseManager() {
    }

    public WareHouseManager(Integer size) {
        this.size = size;
    }

    public CopyOnWriteArrayList<Article> getBackpacks() {
        return backpacks;
    }

    public void setBackpacks(CopyOnWriteArrayList<Article> backpacks) {
        this.backpacks = backpacks;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }
    public int getNowSize(){
        return nowSize.get();
    }
    public int addAndReturnNowSize(){
        return  nowSize.incrementAndGet();
    }
    public int reduceAndReturnNowSize(){
        return  nowSize.decrementAndGet();
    }
    public int getWareHouseId(){
        return wareHouseId.get();
    }
    public int addAndReturnWareHouseId(){
        return  wareHouseId.incrementAndGet();
    }
    public int reduceAndReturnWareHouseId(){
        return  nowSize.decrementAndGet();
    }
    /**
     * 背包格子是否足够
     */
    public boolean canPutArticle(Article article) {
        //判断物品类型
        return article.checkCanPutWareHouse(this);
    }

    /**
     * 整理仓库
     */
    public synchronized void clearBackPack(Integer guildId){
        CopyOnWriteArrayList<Article> newBackPack=new CopyOnWriteArrayList<>();
        CopyOnWriteArrayList<Article> oldBackPack=getBackpacks();
        setBackpacks(newBackPack);
        wareHouseId=new AtomicInteger(0);
        for (Article a:oldBackPack) {
            a.clearPutWareHouse(this,guildId);
        }
    }

    /**
     * 放入仓库
     * @param article
     * @param guildId
     * @return
     */
    public synchronized boolean putWareHouse(Article article, Integer guildId) {
        return article.putWareHouse(this,guildId);
    }

    /**
     *   仓库放入东西 按照数据库格式来存放
     */
    public synchronized void putFromDatabase(Article article) {

        if (article.getArticleTypeCode().equals(ArticleTypeCode.MEDICINE.getCode())) {
            MedicineBean medicineBean = article.getArticle();
            medicineBean.setWareHouseId(addAndReturnWareHouseId());
            getBackpacks().add(medicineBean);
            addAndReturnNowSize();
        } else if ((article.getArticleTypeCode().equals(ArticleTypeCode.EQUIPMENT.getCode()))) {
            //判断背包大小
            EquipmentBean equipmentBean = article.getArticle();
            //设置背包物品id
            equipmentBean.setWareHouseId(addAndReturnWareHouseId());
            addAndReturnNowSize();
            getBackpacks().add(equipmentBean);
            addAndReturnNowSize();
        }

    }

    /**
     * 减少某样物品数量
     */
    public synchronized Article useOrAbandonArticle(Integer wareHouseId, Integer number,Integer guildId) {
        for (Article a : getBackpacks()) {
            if (a.getWareHouseIdCode().equals(wareHouseId)) {
                return a.useOrAbandonWareHouse(number,this,guildId);
            }
        }
        return null;
    }

    /**
     * 根据wareHouseId获取物品信息
     */
    public synchronized Article getArticleByArticleId(Integer wareHouseId) {
        for (Article article : getBackpacks()) {
            if (wareHouseId.equals(article.getWareHouseIdCode())) {
                return article;
            }
        }
        return null;
    }

    /**
     * 获取仓库内物品信息
     */
    public synchronized ArrayList<ArticleDto> getBackpacksMessage() {
        ArrayList<ArticleDto> articleDtos = new ArrayList<>();
        for (Article article : getBackpacks()) {
            ArticleDto articleDto = article.getArticleMessage();
            articleDtos.add(articleDto);
        }
        return articleDtos;
    }
}
