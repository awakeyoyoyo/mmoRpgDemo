package com.liqihao.provider;

import com.liqihao.commons.RpgServerException;
import com.liqihao.commons.StateCode;
import com.liqihao.commons.enums.ArticleTypeCode;
import com.liqihao.commons.enums.DealBankArticleTypeCode;
import com.liqihao.dao.MmoDealBankArticlePOJOMapper;
import com.liqihao.dao.MmoDealBankAuctionPOJOMapper;
import com.liqihao.dao.MmoEmailPOJOMapper;
import com.liqihao.dao.MmoUserPOJOMapper;
import com.liqihao.pojo.MmoDealBankArticlePOJO;
import com.liqihao.pojo.MmoDealBankAuctionPOJO;
import com.liqihao.pojo.bean.articleBean.Article;
import com.liqihao.pojo.bean.articleBean.EquipmentBean;
import com.liqihao.pojo.bean.articleBean.MedicineBean;
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
import java.util.stream.Collectors;

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

    private static MmoDealBankArticlePOJOMapper mmoDealBankArticlePOJOMapper;
//    private static MmoDealBankAuctionPOJOMapper mmoDealBankAuctionPOJOMapper;
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        mmoDealBankArticlePOJOMapper=(MmoDealBankArticlePOJOMapper)applicationContext.getBean("mmoDealBankArticlePOJOMapper");
//        mmoDealBankAuctionPOJOMapper=(MmoDealBankAuctionPOJOMapper)applicationContext.getBean("mmoDealBankAuctionPOJOMapper");
        dealBankArticleBeanDBIdAuto= new AtomicInteger(mmoDealBankArticlePOJOMapper.selectNextIndex()-1);
//        dealBankAuctionBeanDBIdAuto=new AtomicInteger(mmoDealBankAuctionPOJOMapper.selectNextIndex()-1);
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
     * 上架物品
     */
    public void addSellArticleToDealBank(Article article, MmoSimpleRole role,int price,int type){
        DealBankArticleBean dealBankArticleBean=article.convertDealBankArticleBean();
        dealBankArticleBean.setCreateTime(System.currentTimeMillis());
        dealBankArticleBean.setDealBankArticleDbId(dealBankArticleBeanDBIdAuto.incrementAndGet());
        dealBankArticleBean.setFromRoleId(role.getId());
        dealBankArticleBean.setType(type);
        dealBankArticleBean.setPrice(price);
        dealBankArticleBean.setEndTime(dealBankArticleBean.getCreateTime()+60*2*1000);//2分钟
        dealBankArticleBean.setDealBeanArticleBeanId(dealBankArticleBeanIdAuto.incrementAndGet());
        dealBankArticleBeans.put(dealBankArticleBean.getDealBeanArticleBeanId(),dealBankArticleBean);
    }
    /**
     * 下架物品
     */
    public void reduceSellArticleToDealBank(Integer dealBeanArticleBeanId, MmoSimpleRole role) throws RpgServerException {
        DealBankArticleBean dealBankArticleBean=dealBankArticleBeans.get(dealBeanArticleBeanId);
        if (dealBankArticleBean==null){
            throw new RpgServerException(StateCode.FAIL,"该物品已经交易完成或不存在");
        }
        Article article;
        if (dealBankArticleBean.getArticleType().equals(ArticleTypeCode.MEDICINE.getCode())){
            MedicineBean medicineBean=new MedicineBean();
            medicineBean.setMedicineMessageId(dealBankArticleBean.getArticleMessageId());
            medicineBean.setQuantity(dealBankArticleBean.getNum());
            article=medicineBean;
        }else{
            EquipmentBean equipmentBean=new EquipmentBean();
            equipmentBean.setEquipmentId(equipmentBean.getEquipmentId());
            equipmentBean.setEquipmentMessageId(equipmentBean.getEquipmentMessageId());
            //todo 武器耐久度
            equipmentBean.setQuantity(1);
            article=equipmentBean;
        }
        //放回去背包中
        role.getBackpackManager().put(article,role.getId());
        //判断是否是拍卖品
        if (dealBankArticleBean.getType().equals(DealBankArticleTypeCode.AUCTION.getCode())){
            //todo 把拍卖纪录中的金币通过邮件退换
        }
    }
    /**
     * 购买物品
     */
    public void buySellArticleToDealBank(Integer dealBankArticleBeanId, MmoSimpleRole role,Integer money) throws RpgServerException {
        DealBankArticleBean dealBankArticleBean=dealBankArticleBeans.get(dealBankArticleBeanId);
        if (dealBankArticleBean==null){
            throw new RpgServerException(StateCode.FAIL,"该物品已经交易完成或不存在");
        }
        //判断是否是拍卖品
        if (dealBankArticleBean.getType().equals(DealBankArticleTypeCode.AUCTION.getCode())){
            //todo 生成拍卖记录 扣除金币
        }else{
            //TODO 扣除金币放入背包
        }
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

    /**
     * 获取自身上架交易物品列表
     */
    public List<DealBankArticleBean> getSellArticleToDealBankByMySelf(Integer roleId){
        dealBankArticleBeansRwLock.readLock().lock();
        try {
            return dealBankArticleBeans.values().stream().filter(e->e.getFromRoleId().equals(roleId)).collect(Collectors.toList());
        }finally {
            dealBankArticleBeansRwLock.readLock().unlock();
        }
    }
}
