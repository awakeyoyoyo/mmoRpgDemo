package com.liqihao.provider;

import com.liqihao.dao.MmoDealBankArticlePOJOMapper;
import com.liqihao.dao.MmoDealBankAuctionPOJOMapper;
import com.liqihao.dao.MmoEmailPOJOMapper;
import com.liqihao.dao.MmoUserPOJOMapper;
import com.liqihao.pojo.MmoDealBankArticlePOJO;
import com.liqihao.pojo.MmoDealBankAuctionPOJO;
import com.liqihao.pojo.bean.articleBean.Article;
import com.liqihao.pojo.bean.dealBankBean.DealBankArticleBean;
import com.liqihao.pojo.bean.dealBankBean.DealBankAuctionBean;
import com.liqihao.pojo.bean.dealBean.DealBean;
import com.liqihao.pojo.bean.roleBean.MmoSimpleRole;
import com.liqihao.util.CommonsUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.Lifecycle;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * 交易行服务提供者
 * @author lqhao
 */
public class DealBankServiceProvider implements ApplicationContextAware {
    private final Logger log = LoggerFactory.getLogger(DealBankServiceProvider.class);
    /**
     * 交易行上物品
     */
    private static ConcurrentHashMap<Integer, DealBankArticleBean> dealBankArticleBeans =new ConcurrentHashMap<>();
    /**
     * 交易行上物品的读写锁
     */
    public final ReadWriteLock dealBankArticleBeansRwLock = new ReentrantReadWriteLock();
    /**
     * 拍卖纪录物品的读写锁
     */
    public final ReadWriteLock dealBankAuctionBeansRwLock = new ReentrantReadWriteLock();
    /**
     * 交易行上物品id
     */
    private static AtomicInteger dealBankArticleBeanIdAuto=new AtomicInteger(0);
    /**
     * 交易行上物品DB Id
     */
    private static AtomicInteger dealBankArticleBeanDBIdAuto;
    /**
     * 拍卖纪录DB Id TODO 是否放入
     */
    private static AtomicInteger dealBankAuctionBeanDBIdAuto=new AtomicInteger(0);;
    private static MmoDealBankArticlePOJOMapper mmoDealBankArticlePOJOMapper;
    private static MmoDealBankAuctionPOJOMapper mmoDealBankAuctionPOJOMapper;
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        mmoDealBankArticlePOJOMapper=(MmoDealBankArticlePOJOMapper)applicationContext.getBean("mmoDealBankArticlePOJOMapper");
        mmoDealBankAuctionPOJOMapper=(MmoDealBankAuctionPOJOMapper)applicationContext.getBean("mmoDealBankAuctionPOJOMapper");
        dealBankArticleBeanDBIdAuto= new AtomicInteger(mmoDealBankArticlePOJOMapper.selectNextIndex()-1);
        dealBankAuctionBeanDBIdAuto=new AtomicInteger(mmoDealBankAuctionPOJOMapper.selectNextIndex()-1);
        //获取数据库中的交易数据放入内存中
        init();
    }

    private void init() {
        List<MmoDealBankArticlePOJO> dealBankArticlePOJOS=mmoDealBankArticlePOJOMapper.selectAll();
        for (MmoDealBankArticlePOJO dealBankArticlePOJO : dealBankArticlePOJOS) {
            DealBankArticleBean d= CommonsUtil.dealBankArticlePOJOToDealBankArticleBean(dealBankArticlePOJO);
            d.setDealBeanArticleBeanId(dealBankArticleBeanDBIdAuto.incrementAndGet());
            dealBankArticleBeans.put(d.getDealBeanArticleBeanId(),d);
        }
    }

    public static ConcurrentHashMap<Integer, DealBankArticleBean> getDealBankArticleBeans() {
        return dealBankArticleBeans;
    }

    public static void setDealBankArticleBeans(ConcurrentHashMap<Integer, DealBankArticleBean> dealBankArticleBeans) {
        DealBankServiceProvider.dealBankArticleBeans = dealBankArticleBeans;
    }

    /**
     * 上架一口价物品
     */
    public void addSellArticleToDealBank(Article article, MmoSimpleRole role){

    }
    /**
     * 下架一口价物品
     */
    public void reduceSellArticleToDealBank(Article article, MmoSimpleRole role){

    }
    /**
     * 购买一口价物品
     */
    public void buySellArticleToDealBank(Integer dealBankArticleBeanId, MmoSimpleRole role){

    }

    /**
     * 上架拍卖物品
     */
    public void addAuctionArticleToDealBank(Article article, MmoSimpleRole role){

    }
    /**
     * 下架拍卖物品
     */
    public void reduceAuctionArticleToDealBank(Article article, MmoSimpleRole role){

    }
    /**
     * 购买拍卖物品
     */
    public void buyAuctionArticleToDealBank(Integer dealBankArticleBeanId, MmoSimpleRole role){

    }

    /**
     * 获取交易物品列表
     */
    public List<DealBankArticleBean> getSellArticleToDealBank(){
        dealBankArticleBeansRwLock.readLock().lock();
        try {
            return new ArrayList<>(dealBankArticleBeans.values());
        }finally {
            dealBankArticleBeansRwLock.readLock().unlock();
        }
    }
}
