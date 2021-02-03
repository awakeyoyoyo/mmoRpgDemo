package com.liqihao.pojo.bean.guildBean;

import com.liqihao.commons.enums.ArticleTypeCode;
import com.liqihao.pojo.bean.articleBean.Article;
import com.liqihao.pojo.bean.articleBean.EquipmentBean;
import com.liqihao.pojo.bean.articleBean.MedicineBean;
import com.liqihao.pojo.dto.ArticleDto;

import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * 仓库管理类
 *
 * @author lqhao
 */
public class WareHouseManager {
    /**
     * 仓库集合
     */
    private CopyOnWriteArrayList<Article> backpacks = new CopyOnWriteArrayList<>();
    /**
     * 仓库大小
     */
    private Integer size = 50;
    /**
     * 当前已占用格子数
     */
    private AtomicInteger nowSize = new AtomicInteger(0);
    /**
     * 自增的仓库格子id
     */
    private AtomicInteger wareHouseId = new AtomicInteger(0);
    /**
     * 仓库读写锁
     */
    public final ReadWriteLock wareHouseWrLock = new ReentrantReadWriteLock();

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

    public int getNowSize() {
        return nowSize.get();
    }

    public int addAndReturnNowSize() {
        return nowSize.incrementAndGet();
    }

    public int reduceAndReturnNowSize() {
        return nowSize.decrementAndGet();
    }

    public int getWareHouseId() {
        return wareHouseId.get();
    }

    public int addAndReturnWareHouseId() {
        return wareHouseId.incrementAndGet();
    }

    public int reduceAndReturnWareHouseId() {
        return nowSize.decrementAndGet();
    }

    /**
     * 背包格子是否足够
     */
    public boolean canPutArticle(Article article) {
        //判断物品类型
        return article.checkCanPutWareHouse(this);
    }


    /**
     * 放入仓库
     * @param article
     * @param guildId
     * @return
     */
    public boolean putWareHouse(Article article, Integer guildId) {
        wareHouseWrLock.writeLock().lock();
        try {
            if (!canPutArticle(article)){
                //背包空间不足
                return false;
            }
            article.putWareHouse(this, guildId);
            return true;
        } finally {
            wareHouseWrLock.writeLock().unlock();
        }
    }

    /**
     * 仓库放入东西 按照数据库格式来存放
     */
    public void putFromDatabase(Article article) {
        wareHouseWrLock.writeLock().lock();
        try {
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
        } finally {
            wareHouseWrLock.writeLock().unlock();
        }
    }

    /**
     * 减少某样物品数量
     */
    public Article useOrAbandonArticle(Integer wareHouseId, Integer number, Integer guildId) {
        wareHouseWrLock.writeLock().lock();
        try {
            for (Article a : getBackpacks()) {
                if (a.getWareHouseId().equals(wareHouseId)) {
                    return a.useOrAbandonWareHouse(number, this, guildId);
                }
            }
            return null;
        } finally {
            wareHouseWrLock.writeLock().unlock();
        }
    }

    /**
     * 根据wareHouseId获取物品信息
     */
    public Article getArticleByArticleId(Integer wareHouseId) {
        wareHouseWrLock.readLock().lock();
        try {
            for (Article article : getBackpacks()) {
                if (wareHouseId.equals(article.getWareHouseId())) {
                    return article;
                }
            }
            return null;
        } finally {
            wareHouseWrLock.readLock().unlock();
        }
    }

    /**
     * 获取仓库内物品信息
     */
    public ArrayList<ArticleDto> getBackpacksMessage() {
        wareHouseWrLock.readLock().lock();
        try {
            ArrayList<ArticleDto> articleDtos = new ArrayList<>();
            for (Article article : getBackpacks()) {
                ArticleDto articleDto = article.getArticleMessage();
                articleDtos.add(articleDto);
            }
            return articleDtos;
        } finally {
            wareHouseWrLock.readLock().unlock();
        }
    }

    /**
     * 整理仓库
     */
    public void clearBackPack(Integer guildId) {
        wareHouseWrLock.writeLock().lock();
        try {
            CopyOnWriteArrayList<Article> newBackPack = new CopyOnWriteArrayList<>();
            CopyOnWriteArrayList<Article> oldBackPack = getBackpacks();
            setBackpacks(newBackPack);
            wareHouseId = new AtomicInteger(0);
            for (Article a : oldBackPack) {
                a.clearPutWareHouse(this, guildId);
            }
        } finally {
            wareHouseWrLock.writeLock().unlock();
        }
    }
}
