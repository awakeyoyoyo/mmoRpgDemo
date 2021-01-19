package com.liqihao.provider;

import com.liqihao.Cache.OnlineRoleMessageCache;
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
import com.liqihao.pojo.bean.MmoEmailBean;
import com.liqihao.pojo.bean.articleBean.Article;
import com.liqihao.pojo.bean.articleBean.EquipmentBean;
import com.liqihao.pojo.bean.articleBean.MedicineBean;
import com.liqihao.pojo.bean.dealBankBean.DealBankArticleBean;
import com.liqihao.pojo.bean.dealBankBean.DealBankAuctionBean;
import com.liqihao.pojo.bean.dealBean.DealBean;
import com.liqihao.pojo.bean.roleBean.MmoSimpleRole;
import com.liqihao.util.CommonsUtil;
import com.liqihao.util.ScheduledThreadPoolUtil;
import com.sun.org.apache.bcel.internal.generic.IF_ACMPEQ;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.Lifecycle;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

/**
 * 交易行服务提供者
 *
 * @author lqhao
 */
public class DealBankServiceProvider implements ApplicationContextAware {
    private final Logger log = LoggerFactory.getLogger(DealBankServiceProvider.class);
    /**
     * 交易行上物品
     */
    private static ConcurrentHashMap<Integer, DealBankArticleBean> dealBankArticleBeans = new ConcurrentHashMap<>();
    /**
     * 交易行上物品的读写锁
     */
    public final static ReadWriteLock dealBankArticleBeansRwLock = new ReentrantReadWriteLock();
    /**
     * 拍卖纪录物品的读写锁
     */
    public final static ReadWriteLock dealBankAuctionBeansRwLock = new ReentrantReadWriteLock();
    /**
     * 交易行上物品id
     */
    private static AtomicInteger dealBankArticleBeanIdAuto = new AtomicInteger(0);
    /**
     * 交易行上物品DB Id
     */
    private static AtomicInteger dealBankArticleBeanDBIdAuto;
    /**
     * 拍卖纪录id
     */
    private static AtomicInteger dealBankAuctionBeanIdAuto = new AtomicInteger(0);
    /**
     * 拍卖纪录DB Id
     */
    private static AtomicInteger dealBankAuctionBeanDBIdAuto;
    private static MmoDealBankAuctionPOJOMapper mmoDealBankAuctionPOJOMapper;
    private static MmoDealBankArticlePOJOMapper mmoDealBankArticlePOJOMapper;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        mmoDealBankArticlePOJOMapper = (MmoDealBankArticlePOJOMapper) applicationContext.getBean("mmoDealBankArticlePOJOMapper");
        mmoDealBankAuctionPOJOMapper = (MmoDealBankAuctionPOJOMapper) applicationContext.getBean("mmoDealBankAuctionPOJOMapper");
        dealBankArticleBeanDBIdAuto = new AtomicInteger(mmoDealBankArticlePOJOMapper.selectNextIndex() - 1);
        dealBankAuctionBeanDBIdAuto = new AtomicInteger(mmoDealBankAuctionPOJOMapper.selectNextIndex() - 1);
        //获取数据库中的交易数据放入内存中
        init();
    }

    private void init() {
        List<MmoDealBankArticlePOJO> dealBankArticlePOJOS = mmoDealBankArticlePOJOMapper.selectAll();
        for (MmoDealBankArticlePOJO dealBankArticlePOJO : dealBankArticlePOJOS) {
            DealBankArticleBean d = CommonsUtil.dealBankArticlePOJOToDealBankArticleBean(dealBankArticlePOJO);
            d.setDealBeanArticleBeanId(dealBankArticleBeanDBIdAuto.incrementAndGet());
            dealBankArticleBeans.put(d.getDealBeanArticleBeanId(), d);
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
    public static void addSellArticleToDealBank(Article article, MmoSimpleRole role, int price, int type,int num) {
        DealBankArticleBean dealBankArticleBean = article.convertDealBankArticleBean();
        dealBankArticleBean.setCreateTime(System.currentTimeMillis());
        dealBankArticleBean.setDealBankArticleDbId(dealBankArticleBeanDBIdAuto.incrementAndGet());
        dealBankArticleBean.setFromRoleId(role.getId());
        dealBankArticleBean.setType(type);
        dealBankArticleBean.setNum(num);
        dealBankArticleBean.setPrice(price);
        //1天
        dealBankArticleBean.setEndTime(dealBankArticleBean.getCreateTime() + 60 * 60 * 24 * 1000);
        dealBankArticleBean.setDealBeanArticleBeanId(dealBankArticleBeanIdAuto.incrementAndGet());
        dealBankArticleBeans.put(dealBankArticleBean.getDealBeanArticleBeanId(), dealBankArticleBean);
        //判断是否是拍卖模式
        //开启定时任务
        if (dealBankArticleBean.getType().equals(DealBankArticleTypeCode.AUCTION.getCode())) {
            //开启延时任务
            ScheduledFuture<?> t = ScheduledThreadPoolUtil.getScheduledExecutorService().schedule(
                    new ScheduledThreadPoolUtil.DealBankOutTimeTask(dealBankArticleBean)
                    , 60 * 24 * 1000, TimeUnit.SECONDS);
            ScheduledThreadPoolUtil.getDealBankTaskMap().put(dealBankArticleBean.getDealBeanArticleBeanId(), t);
        }
        //插入数据库
        ScheduledThreadPoolUtil.addTask(() -> insertDealBankArticleBean(dealBankArticleBean));
    }


    /**
     * 下架物品
     */
    public static void reduceSellArticleToDealBank(Integer dealBeanArticleBeanId,MmoSimpleRole role) throws RpgServerException {
        DealBankArticleBean dealBankArticleBean = dealBankArticleBeans.get(dealBeanArticleBeanId);
        if (dealBankArticleBean == null) {
            throw new RpgServerException(StateCode.FAIL, "该物品已经交易完成或不存在");
        }
        if (!role.getId().equals(dealBankArticleBean.getFromRoleId())){
            throw new RpgServerException(StateCode.FAIL, "非本商品的卖家无法下架");
        }
        //发送给买家
        sendSuccessToSeller(dealBankArticleBean);
        //判断是否是拍卖品
        if (dealBankArticleBean.getType().equals(DealBankArticleTypeCode.AUCTION.getCode())) {
            //消除任务
            ScheduledFuture<?> t = ScheduledThreadPoolUtil.getCopySceneTaskMap().get(dealBankArticleBean.getDealBeanArticleBeanId());
            ScheduledThreadPoolUtil.getCopySceneTaskMap().remove(dealBankArticleBean.getDealBeanArticleBeanId());
            t.cancel(false);
            for (DealBankAuctionBean dealBankAuctionBean : dealBankArticleBean.getDealBankAuctionBeans()) {
                //把金币发给买家
                sendFailToBuyer(dealBankAuctionBean);
                Integer dealBankAuctionDbId = dealBankAuctionBean.getDealBeanAuctionBeanDbId();
                //Db删除
                ScheduledThreadPoolUtil.addTask(() -> DealBankServiceProvider.deleteDealBankAuctionById(dealBankAuctionDbId));
            }
            //db删除
            Integer dealBankArticleDbId = dealBankArticleBean.getDealBankArticleDbId();
            ScheduledThreadPoolUtil.addTask(() -> DealBankServiceProvider.deleteDealBankArticleById(dealBankArticleDbId));
        }

    }

    /**
     * 购买物品
     */
    public static void buySellArticleToDealBank(Integer dealBankArticleBeanId, MmoSimpleRole role, Integer money) throws RpgServerException {
        DealBankArticleBean dealBankArticleBean = dealBankArticleBeans.get(dealBankArticleBeanId);
        if (dealBankArticleBean == null) {
            throw new RpgServerException(StateCode.FAIL, "该物品已经交易完成或不存在");
        }
        if (money==0&&dealBankArticleBean.getType().equals(DealBankArticleTypeCode.AUCTION.getCode())){
            throw new RpgServerException(StateCode.FAIL, "该商品为拍卖模式，请调用拍卖接口");
        }
        synchronized (dealBankArticleBean) {
            //检测金币是否够
            role.moneyLock.readLock().lock();
            try {
            if (dealBankArticleBean.getType().equals(DealBankArticleTypeCode.AUCTION.getCode())) {
                if (role.getMoney() < money) {
                    throw new RpgServerException(StateCode.FAIL, "人物金币不足");
                }
                if (money<dealBankArticleBean.getHighPrice()){
                    throw new RpgServerException(StateCode.FAIL, "当前报价不是最高价");
                }
            }else{
                //判断金币足够
                if (role.getMoney() < dealBankArticleBean.getPrice()) {
                    throw new RpgServerException(StateCode.FAIL, "人物金币不足");
                }
            }
            }finally {
                role.moneyLock.readLock().unlock();
            }
            //判断是否是拍卖品
            if (dealBankArticleBean.getType().equals(DealBankArticleTypeCode.AUCTION.getCode())) {
                //生成拍卖记录
                DealBankAuctionBean dealBankAuctionBean = buildDealBankAuction(dealBankArticleBean, role.getId(), money);
                role.setMoney(role.getMoney() - money);
                dealBankArticleBean.setHighPrice(money);
                dealBankArticleBean.setToRoleId(role.getId());
                //拍卖品 增加拍卖记录
                dealBankArticleBean.getDealBankAuctionBeans().add(dealBankAuctionBean);
                //更新数据库
                ScheduledThreadPoolUtil.addTask(() -> {
                    updateDealBankArticle(dealBankArticleBean);
                    insertDealBankAuction(dealBankAuctionBean);
                });
            } else {
                dealBankArticleBeans.remove(dealBankArticleBeanId);
                dealBankArticleBean.setToRoleId(role.getId());
                //扣除金币
                role.moneyLock.writeLock().lock();
                try {
                    role.setMoney(role.getMoney() - dealBankArticleBean.getPrice());
                }finally {
                    role.moneyLock.writeLock().unlock();
                }
                //发送交易成功给双方
                sendSuccessToBuyer(dealBankArticleBean);
                sendSuccessToSeller(dealBankArticleBean);
                //删除拍卖行中物品
                Integer dealBankArticleDBId=dealBankArticleBean.getDealBankArticleDbId();
                ScheduledThreadPoolUtil.addTask(() -> {
                    deleteDealBankArticleById(dealBankArticleDBId);
                });
            }
        }
    }

    private static void insertDealBankAuction(DealBankAuctionBean dealBankAuctionBean) {
        MmoDealBankAuctionPOJO dealBankAuctionPOJO=new MmoDealBankAuctionPOJO();
        dealBankAuctionPOJO.setDealBankArticleId(dealBankAuctionBean.getDealBeanArticleBeanDbId());
        dealBankAuctionPOJO.setId(dealBankAuctionBean.getDealBeanAuctionBeanDbId());
        dealBankAuctionPOJO.setMoney(dealBankAuctionBean.getMoney());
        dealBankAuctionPOJO.setFromRoleId(dealBankAuctionBean.getFromRoleId());
        dealBankAuctionPOJO.setCreateTime(System.currentTimeMillis());
        mmoDealBankAuctionPOJOMapper.insert(dealBankAuctionPOJO);
    }
    private static void insertDealBankArticleBean(DealBankArticleBean dealBankArticleBean) {
        MmoDealBankArticlePOJO mmoDealBankArticlePOJO=new MmoDealBankArticlePOJO();
        mmoDealBankArticlePOJO.setId(dealBankArticleBean.getDealBankArticleDbId());
        mmoDealBankArticlePOJO.setArticleType(dealBankArticleBean.getArticleType());
        mmoDealBankArticlePOJO.setArticleMessageId(dealBankArticleBean.getArticleMessageId());
        mmoDealBankArticlePOJO.setEquipmentId(dealBankArticleBean.getEquipmentId());
        mmoDealBankArticlePOJO.setFromRoleId(dealBankArticleBean.getFromRoleId());
        mmoDealBankArticlePOJO.setCreateTime(dealBankArticleBean.getCreateTime());
        mmoDealBankArticlePOJO.setEndTime(dealBankArticleBean.getEndTime());
        mmoDealBankArticlePOJO.setType(dealBankArticleBean.getType());
        mmoDealBankArticlePOJO.setHighPrice(dealBankArticleBean.getHighPrice());
        mmoDealBankArticlePOJO.setPrice(dealBankArticleBean.getPrice());
        mmoDealBankArticlePOJO.setToRoleId(dealBankArticleBean.getToRoleId());
        mmoDealBankArticlePOJOMapper.insert(mmoDealBankArticlePOJO);
    }
    private static void updateDealBankArticle(DealBankArticleBean dealBankArticleBean) {
        MmoDealBankArticlePOJO mmoDealBankArticlePOJO=new MmoDealBankArticlePOJO();
        mmoDealBankArticlePOJO.setId(dealBankArticleBean.getDealBankArticleDbId());
        mmoDealBankArticlePOJO.setArticleType(dealBankArticleBean.getArticleType());
        mmoDealBankArticlePOJO.setArticleMessageId(dealBankArticleBean.getArticleMessageId());
        mmoDealBankArticlePOJO.setEquipmentId(dealBankArticleBean.getEquipmentId());
        mmoDealBankArticlePOJO.setFromRoleId(dealBankArticleBean.getFromRoleId());
        mmoDealBankArticlePOJO.setCreateTime(dealBankArticleBean.getCreateTime());
        mmoDealBankArticlePOJO.setEndTime(dealBankArticleBean.getEndTime());
        mmoDealBankArticlePOJO.setType(dealBankArticleBean.getType());
        mmoDealBankArticlePOJO.setHighPrice(dealBankArticleBean.getHighPrice());
        mmoDealBankArticlePOJO.setPrice(dealBankArticleBean.getPrice());
        mmoDealBankArticlePOJO.setToRoleId(dealBankArticleBean.getToRoleId());
        mmoDealBankArticlePOJOMapper.updateByPrimaryKey(mmoDealBankArticlePOJO);
    }

    /**
     * 获取交易物品列表
     */
    public static List<DealBankArticleBean> getSellArticleToDealBank() {
        dealBankArticleBeansRwLock.readLock().lock();
        try {
            return new ArrayList<>(dealBankArticleBeans.values());
        } finally {
            dealBankArticleBeansRwLock.readLock().unlock();
        }
    }

    /**
     * 获取自身上架交易物品列表
     */
    public static List<DealBankArticleBean> getSellArticleToDealBankByMySelf(Integer roleId) {
        dealBankArticleBeansRwLock.readLock().lock();
        try {
            return dealBankArticleBeans.values().stream().filter(e -> e.getFromRoleId().equals(roleId)).collect(Collectors.toList());
        } finally {
            dealBankArticleBeansRwLock.readLock().unlock();
        }
    }

    public static void deleteDealBankAuctionById(Integer dealBankAuctionDbId) {
        mmoDealBankAuctionPOJOMapper.deleteByPrimaryKey(dealBankAuctionDbId);
    }

    public static void deleteDealBankArticleById(Integer dealBankArticleDbId) {
        mmoDealBankArticlePOJOMapper.deleteByPrimaryKey(dealBankArticleDbId);
    }

    public static void sendSuccessToSeller(DealBankArticleBean dealBankArticleBean) throws RpgServerException {
        //把商品发给买家
        MmoEmailBean mmoEmailBean02 = new MmoEmailBean();
        mmoEmailBean02.setContext("恭喜你，你的物品已被拍卖");
        mmoEmailBean02.setTitle("物品出售成功消息");
        mmoEmailBean02.setArticleNum(dealBankArticleBean.getNum());
        mmoEmailBean02.setArticleType(dealBankArticleBean.getArticleType());
        mmoEmailBean02.setArticleMessageId(dealBankArticleBean.getArticleMessageId());
        mmoEmailBean02.setMoney(0);
        mmoEmailBean02.setEquipmentId(dealBankArticleBean.getEquipmentId() == null ? -1 : dealBankArticleBean.getEquipmentId());
        mmoEmailBean02.setToRoleId(dealBankArticleBean.getFromRoleId());
        //GM
        mmoEmailBean02.setFromRoleId(88888);
        MmoSimpleRole fromRole = OnlineRoleMessageCache.getInstance().get(dealBankArticleBean.getFromRoleId());
        EmailServiceProvider.sendArticleEmail(null, fromRole, mmoEmailBean02);
    }

    public static void sendFailToBuyer(DealBankAuctionBean dealBankAuctionBean) throws RpgServerException {
        //把金币发给买家
        MmoEmailBean emailBean = new MmoEmailBean();
        emailBean.setContext("你出价太低了");
        emailBean.setTitle("拍卖失败");
        emailBean.setArticleNum(-1);
        emailBean.setArticleType(-1);
        emailBean.setArticleMessageId(-1);
        emailBean.setMoney(dealBankAuctionBean.getMoney());
        emailBean.setEquipmentId(-1);
        emailBean.setToRoleId(dealBankAuctionBean.getFromRoleId());
        //GM
        emailBean.setFromRoleId(88888);
        MmoSimpleRole player = OnlineRoleMessageCache.getInstance().get(dealBankAuctionBean.getFromRoleId());
        EmailServiceProvider.sendArticleEmail(null, player, emailBean);
    }

    public static void sendFailToSeller(DealBankArticleBean dealBankArticleBean) throws RpgServerException {
        MmoEmailBean mmoEmailBean = new MmoEmailBean();
        mmoEmailBean.setContext("拍卖结束还没有买家购买你的物品");
        mmoEmailBean.setTitle("拍卖失败");
        mmoEmailBean.setArticleNum(dealBankArticleBean.getNum());
        mmoEmailBean.setArticleType(dealBankArticleBean.getArticleType());
        mmoEmailBean.setArticleMessageId(dealBankArticleBean.getArticleMessageId());
        mmoEmailBean.setMoney(0);
        mmoEmailBean.setEquipmentId(dealBankArticleBean.getEquipmentId());
        mmoEmailBean.setToRoleId(dealBankArticleBean.getFromRoleId());
        //GM
        mmoEmailBean.setFromRoleId(88888);
        MmoSimpleRole fromRole = OnlineRoleMessageCache.getInstance().get(dealBankArticleBean.getFromRoleId());

        EmailServiceProvider.sendArticleEmail(null, fromRole, mmoEmailBean);
    }

    public static void sendSuccessToBuyer(DealBankArticleBean dealBankArticleBean) throws RpgServerException {
        //发邮件，把物品给回买家
        MmoEmailBean mmoEmailBean = new MmoEmailBean();
        mmoEmailBean.setContext("恭喜你拍卖成功");
        mmoEmailBean.setTitle("拍卖成功");
        mmoEmailBean.setArticleNum(dealBankArticleBean.getNum());
        mmoEmailBean.setArticleType(dealBankArticleBean.getArticleType());
        mmoEmailBean.setArticleMessageId(dealBankArticleBean.getArticleMessageId());
        mmoEmailBean.setMoney(0);
        mmoEmailBean.setEquipmentId(dealBankArticleBean.getEquipmentId());
        mmoEmailBean.setToRoleId(dealBankArticleBean.getToRoleId());
        //GM
        mmoEmailBean.setFromRoleId(88888);
        MmoSimpleRole toRole = OnlineRoleMessageCache.getInstance().get(dealBankArticleBean.getToRoleId());
        EmailServiceProvider.sendArticleEmail(null, toRole, mmoEmailBean);
    }

    public static DealBankAuctionBean buildDealBankAuction(DealBankArticleBean dealBankArticleBean, Integer fromRoleId, Integer money) {
        DealBankAuctionBean dealBankAuctionBean = new DealBankAuctionBean();
        dealBankAuctionBean.setFromRoleId(fromRoleId);
        dealBankAuctionBean.setCreateTime(System.currentTimeMillis());
        dealBankAuctionBean.setMoney(money);
        dealBankAuctionBean.setDealBeanAuctionBeanId(dealBankAuctionBeanIdAuto.incrementAndGet());
        dealBankAuctionBean.setDealBeanArticleBeanDbId(dealBankArticleBean.getDealBankArticleDbId());
        dealBankAuctionBean.setDealBeanAuctionBeanDbId(dealBankArticleBeanDBIdAuto.incrementAndGet());
        return dealBankAuctionBean;
    }

}
