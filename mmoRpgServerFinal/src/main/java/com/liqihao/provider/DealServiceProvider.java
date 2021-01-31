package com.liqihao.provider;

import com.liqihao.Cache.ChannelMessageCache;
import com.liqihao.Cache.MedicineMessageCache;
import com.liqihao.commons.ConstantValue;
import com.liqihao.commons.NettyResponse;
import com.liqihao.commons.RpgServerException;
import com.liqihao.commons.StateCode;
import com.liqihao.commons.enums.ArticleTypeCode;
import com.liqihao.commons.enums.DealStatusCode;
import com.liqihao.commons.enums.TaskTargetTypeCode;
import com.liqihao.pojo.baseMessage.MedicineMessage;
import com.liqihao.pojo.bean.articleBean.Article;
import com.liqihao.pojo.bean.articleBean.MedicineBean;
import com.liqihao.pojo.bean.dealBean.DealArticleBean;
import com.liqihao.pojo.bean.dealBean.DealBean;
import com.liqihao.pojo.bean.roleBean.MmoSimpleRole;
import com.liqihao.pojo.bean.taskBean.dealFirstTask.DealTaskAction;
import com.liqihao.protobufObject.DealModel;
import com.liqihao.service.impl.DealServiceImpl;
import com.liqihao.util.CommonsUtil;
import com.liqihao.util.LogicThreadPool;
import io.netty.channel.Channel;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 交易服务提供者
 *
 * @author lqhao
 */
public class DealServiceProvider {
    private static ConcurrentHashMap<Integer, DealBean> dealBeans = new ConcurrentHashMap<>();
    private static AtomicInteger dealBeanIdAuto = new AtomicInteger(0);

    /**
     * 发起交易
     * @param role1
     * @param role2
     * @throws RpgServerException
     */
    public static void createDeal(MmoSimpleRole role1, MmoSimpleRole role2) throws RpgServerException {
        deleteUnSetDeal();
        if (role1.getOnDeal()) {
            throw new RpgServerException(StateCode.FAIL, "你处于交易状态，无法再发起交易请求");
        }
        if (role2.getOnDeal()) {
            throw new RpgServerException(StateCode.FAIL, "对方处于交易状态，无法再发起交易请求");
        }
        DealBean dealBean = new DealBean();
        //初始化交易bean
        dealBean.setId(dealBeanIdAuto.incrementAndGet());
        dealBean.setFirstRole(role1);
        dealBean.setSecondRole(role2);
        //三分钟后该交易还没进入交易状态就删除
        dealBean.setEndTime(System.currentTimeMillis()+60*60*1000*3);
        DealArticleBean dealArticleBean01 = new DealArticleBean();
        dealArticleBean01.setRole(role1);
        dealArticleBean01.setArticles(new ArrayList<>());
        dealArticleBean01.setMoney(0);
        dealArticleBean01.setConfirm(false);

        dealBean.setFirstDealArticleBean(dealArticleBean01);
        DealArticleBean dealArticleBean02 = new DealArticleBean();
        dealArticleBean02.setRole(role2);
        dealArticleBean02.setArticles(new ArrayList<>());
        dealArticleBean02.setMoney(0);
        dealArticleBean02.setConfirm(false);

        dealBean.setSecondDealArticleBean(dealArticleBean02);
        dealBean.setStatus(DealStatusCode.WAIT.getCode());
        //玩家进入交易状态
        //放到各自的线程做
        role1.setDealBeanId(dealBean.getId());
        role2.setDealBeanId(dealBean.getId());
        synchronized (dealBeans) {
            dealBeans.put(dealBean.getId(), dealBean);
        }
    }

    /**
     * 开始交易
     * @param role
     * @return
     * @throws RpgServerException
     */
    public static DealBean beginDeal(MmoSimpleRole role) throws RpgServerException {
        if (role.getDealBeanId() == null) {
            throw new RpgServerException(StateCode.FAIL, "该玩家并没有被邀请交易");
        }
        DealBean dealBean = dealBeans.get(role.getDealBeanId());
        synchronized (dealBean.lock) {
            if (!role.getId().equals(dealBean.getSecondRole().getId())) {
                throw new RpgServerException(StateCode.FAIL, "请耐心等待对方接收交易邀请");
            }
            if (!dealBean.getStatus().equals(DealStatusCode.WAIT.getCode())) {
                throw new RpgServerException(StateCode.FAIL, "该交易已经开始了，请勿重复开始");
            }
            dealBean.setStatus(DealStatusCode.ON_DEAL.getCode());
        }
        //删除超时交易
        deleteUnSetDeal();
        return dealBean;
    }

    /**
     * 拒绝交易
     * @param role
     * @return
     * @throws RpgServerException
     */
    public static DealBean refuseDeal(MmoSimpleRole role) throws RpgServerException {
        if (role.getDealBeanId() == null) {
            throw new RpgServerException(StateCode.FAIL, "该玩家并没有被邀请交易");
        }
        DealBean dealBean = dealBeans.get(role.getDealBeanId());
        synchronized (dealBean.lock) {
            if (!role.getId().equals(dealBean.getSecondRole().getId())) {
                throw new RpgServerException(StateCode.FAIL, "请耐心等待对方处理交易邀请");
            }
            if (!dealBean.getStatus().equals(DealStatusCode.WAIT.getCode())) {
                throw new RpgServerException(StateCode.FAIL, "该交易已经开始了，请勿重复拒绝");
            }
            synchronized (dealBeans) {
                dealBeans.remove(dealBean.getId());
            }
            //放入各个玩家线程进行
            Integer index01 = CommonsUtil.getIndexByChannel(dealBean.getFirstRole().getChannel());
            LogicThreadPool.getInstance().execute(() -> setRoleStatus(dealBean.getFirstRole(),false,null),index01);
            Integer index02 = CommonsUtil.getIndexByChannel(dealBean.getSecondRole().getChannel());
            LogicThreadPool.getInstance().execute(() -> setRoleStatus(dealBean.getSecondRole(),false,null),index02);
        }
        //删除超时交易
        deleteUnSetDeal();
        return dealBean;
    }

    /**
     * 确认交易
     * @param role
     * @return
     * @throws RpgServerException
     */
    public static void confirmDeal(MmoSimpleRole role) throws RpgServerException {
        if (role.getDealBeanId() == null) {
            throw new RpgServerException(StateCode.FAIL, "该玩家并没有被邀请交易");
        }
        DealBean dealBean = dealBeans.get(role.getDealBeanId());
        synchronized (dealBean.lock) {
            if (!dealBean.getStatus().equals(DealStatusCode.ON_DEAL.getCode())) {
                throw new RpgServerException(StateCode.FAIL, "该交易还没开始，无法确认");
            }
            DealArticleBean dealArticleBean01 = dealBean.getFirstDealArticleBean();
            DealArticleBean dealArticleBean02 = dealBean.getSecondDealArticleBean();
            if (dealArticleBean01.getRole().getId().equals(role.getId())) {
                if (dealArticleBean01.getConfirm()) {
                    throw new RpgServerException(StateCode.FAIL, "已经确认过该交易");
                }
                dealArticleBean01.setConfirm(true);
                //发信息 某某确认了
                sendConfirmMessage(dealBean, dealArticleBean01.getRole());
            } else {
                if (dealArticleBean02.getConfirm()) {
                    throw new RpgServerException(StateCode.FAIL, "已经确认过该交易");
                }
                dealArticleBean02.setConfirm(true);
                //发信息 某某确认了
                sendConfirmMessage(dealBean, dealArticleBean02.getRole());
            }
            //判断是否双方已经确认，确认则交易物品
            if (dealArticleBean01.getConfirm() && dealArticleBean02.getConfirm()) {
                //交易完成 双方交换
                MmoSimpleRole role1 = dealBean.getFirstRole();
                MmoSimpleRole role2 = dealBean.getSecondRole();
                //交换金币 物品
                //根据channel计算index
                Integer index01 = CommonsUtil.getIndexByChannel(role1.getChannel());
                LogicThreadPool.getInstance().execute(() -> exchangeThing(dealArticleBean02.getArticles(),role1,dealArticleBean02.getMoney()), index01);
                //放到各自线程中执行
                Integer index02 = CommonsUtil.getIndexByChannel(role2.getChannel());
                LogicThreadPool.getInstance().execute(() -> exchangeThing(dealArticleBean01.getArticles(),role2,dealArticleBean01.getMoney()), index02);
                dealBean.setStatus(DealStatusCode.FINISH.getCode());
                synchronized (dealBeans) {
                    dealBeans.remove(dealBean.getId());
                }
                //发送消息交易完成
                sendDealSuccessMessage(dealBean);
            }
        }
        //删除超时交易
        deleteUnSetDeal();
    }


    /**
     * 取消交易
     * @param role
     * @return
     * @throws RpgServerException
     */
    public static DealBean cancelDeal(MmoSimpleRole role) throws RpgServerException {
        if (role.getDealBeanId() == null) {
            throw new RpgServerException(StateCode.FAIL, "该玩家并没有被邀请交易");
        }
        DealBean dealBean = dealBeans.get(role.getDealBeanId());
        synchronized (dealBean.lock) {
            DealArticleBean dealArticleBean01 = dealBean.getFirstDealArticleBean();
            DealArticleBean dealArticleBean02 = dealBean.getSecondDealArticleBean();
            //交易取消 物品还原
            MmoSimpleRole role1 = dealBean.getFirstRole();
            MmoSimpleRole role2 = dealBean.getSecondRole();
            //交换金币 物品
            //根据channel计算index
            Integer index01 = CommonsUtil.getIndexByChannel(role1.getChannel());
            LogicThreadPool.getInstance().execute(() -> exchangeThing(dealArticleBean01.getArticles(),role1,dealArticleBean01.getMoney()), index01);
            //放到各自线程中执行
            Integer index02 = CommonsUtil.getIndexByChannel(role2.getChannel());
            LogicThreadPool.getInstance().execute(() -> exchangeThing(dealArticleBean02.getArticles(),role2,dealArticleBean02.getMoney()), index02);
            dealBean.setStatus(DealStatusCode.FINISH.getCode());
            synchronized (dealBeans) {
                dealBeans.remove(dealBean.getId());
            }
            //放入各个玩家线程进行
            LogicThreadPool.getInstance().execute(() -> setRoleStatus(dealBean.getFirstRole(),false,null),index01);
            LogicThreadPool.getInstance().execute(() -> setRoleStatus(dealBean.getSecondRole(),false,null),index02);
        }
        //删除超时交易
        deleteUnSetDeal();
        return dealBean;
    }

    /**
     * 修改金钱
     * @param role
     * @return
     * @throws RpgServerException
     */
    public static DealBean setMoneyDeal(MmoSimpleRole role, Integer money) throws RpgServerException {
        //删除超时交易
        deleteUnSetDeal();
        if (role.getDealBeanId() == null) {
            throw new RpgServerException(StateCode.FAIL, "该玩家并没有被邀请交易");
        }
        DealBean dealBean = dealBeans.get(role.getDealBeanId());
        synchronized (dealBean.lock) {
            if (!dealBean.getStatus().equals(DealStatusCode.ON_DEAL.getCode())) {
                throw new RpgServerException(StateCode.FAIL, "该交易还没开始，无法再修改金币");
            }
            boolean dealConfirm;
            if (dealBean.getFirstDealArticleBean().getRole().getId().equals(role.getId())){
                dealConfirm=dealBean.getFirstDealArticleBean().getConfirm();
            }else{
                dealConfirm=dealBean.getSecondDealArticleBean().getConfirm();
            }
            if (dealConfirm){
                throw new RpgServerException(StateCode.FAIL, "你已经确认交易无法再进一步操作");
            }
            DealArticleBean dealArticleBean01 = dealBean.getFirstDealArticleBean();
            DealArticleBean dealArticleBean02 = dealBean.getSecondDealArticleBean();
            if (dealArticleBean01.getRole().getId().equals(role.getId())) {
                setMoney(dealArticleBean01, role, money);
            } else {
                setMoney(dealArticleBean02, role, money);
            }
            return dealBean;
        }

    }


    /**
     * 添加物品
     * @throws RpgServerException
     */
    public static Article addArticleDeal(Integer articleId, Integer num, MmoSimpleRole role) throws RpgServerException {
        //删除超时交易
        deleteUnSetDeal();
        if (role.getDealBeanId() == null) {
            throw new RpgServerException(StateCode.FAIL, "该玩家并没有被邀请交易");
        }
        DealBean dealBean = dealBeans.get(role.getDealBeanId());
        synchronized (dealBean.lock) {
            if (!dealBean.getStatus().equals(DealStatusCode.ON_DEAL.getCode())) {
                throw new RpgServerException(StateCode.FAIL, "该交易还没开始，无法放入物品");
            }
            DealArticleBean dealArticleBean01 = dealBean.getFirstDealArticleBean();
            DealArticleBean dealArticleBean02 = dealBean.getSecondDealArticleBean();
            if (dealArticleBean01.getRole().getId().equals(role.getId())) {
                if (dealArticleBean01.getConfirm()) {
                    throw new RpgServerException(StateCode.FAIL, "已经确认过该交易，无法再放入");
                }
                Article article = role.getBackpackManager().useOrAbandonArticle(articleId, num, role.getId());
                if (article == null) {
                    throw new RpgServerException(StateCode.FAIL, "背包中物品数量不足");
                }
                if (article.getArticleTypeCode().equals(ArticleTypeCode.MEDICINE.getCode())) {
                    MedicineMessage medicineMessage = MedicineMessageCache.getInstance().get(article.getArticleMessage().getId());
                    if (medicineMessage == null) {
                        throw new RpgServerException(StateCode.FAIL, "存入错误物品id");
                    }
                    MedicineBean medicineBean = CommonsUtil.medicineMessageToMedicineBean(medicineMessage);
                    medicineBean.setQuantity(num);
                    article = medicineBean;
                }
                dealArticleBean01.put(article);
                return article;
            } else {
                if (dealArticleBean02.getConfirm()) {
                    throw new RpgServerException(StateCode.FAIL, "已经确认过该交易，无法再放入");
                }
                Article article = role.getBackpackManager().useOrAbandonArticle(articleId, num, role.getId());
                if (article == null) {
                    throw new RpgServerException(StateCode.FAIL, "背包中物品数量不足");
                }
                if (article.getArticleTypeCode().equals(ArticleTypeCode.MEDICINE.getCode())) {
                    MedicineMessage medicineMessage = MedicineMessageCache.getInstance().get(article.getArticleMessage().getId());
                    if (medicineMessage == null) {
                        throw new RpgServerException(StateCode.FAIL, "存入错误物品id");
                    }
                    MedicineBean medicineBean = CommonsUtil.medicineMessageToMedicineBean(medicineMessage);
                    medicineBean.setQuantity(num);
                    article = medicineBean;
                }
                dealArticleBean02.put(article);
                return article;
            }
        }
    }

    /**
     * 移除物品
     * @throws RpgServerException
     */
    public static Article reduceArticleDeal(Integer dealArticleId, Integer num, MmoSimpleRole role) throws RpgServerException {
        //删除超时交易
        deleteUnSetDeal();
        if (role.getDealBeanId() == null) {
            throw new RpgServerException(StateCode.FAIL, "该玩家并没有被邀请交易");
        }
        DealBean dealBean = dealBeans.get(role.getDealBeanId());
        synchronized (dealBean.lock) {
            if (!dealBean.getStatus().equals(DealStatusCode.ON_DEAL.getCode())) {
                throw new RpgServerException(StateCode.FAIL, "该交易还没开始，无法移除物品");
            }
            DealArticleBean dealArticleBean01 = dealBean.getFirstDealArticleBean();
            DealArticleBean dealArticleBean02 = dealBean.getSecondDealArticleBean();
            if (dealArticleBean01.getRole().getId().equals(role.getId())) {
                if (dealArticleBean01.getConfirm()) {
                    throw new RpgServerException(StateCode.FAIL, "已经确认过该交易，无法再移除");
                }
                //从交易栏中找出物品
                Article article = dealArticleBean01.abandon(dealArticleId, num);
                if (article == null) {
                    throw new RpgServerException(StateCode.FAIL, "交易栏中物品数量不足");
                }
                if (article.getArticleTypeCode().equals(ArticleTypeCode.MEDICINE.getCode())) {
                    MedicineMessage medicineMessage = MedicineMessageCache.getInstance().get(article.getArticleMessage().getId());
                    if (medicineMessage == null) {
                        throw new RpgServerException(StateCode.FAIL, "存入错误物品id");
                    }
                    MedicineBean medicineBean = CommonsUtil.medicineMessageToMedicineBean(medicineMessage);
                    medicineBean.setQuantity(num);
                    article = medicineBean;
                }
                //放入背包
                boolean flag = role.getBackpackManager().put(article, role.getId());
                if (!flag) {
                    throw new RpgServerException(StateCode.FAIL, "背包已经满了");
                }
                if (article.getArticleTypeCode().equals(ArticleTypeCode.MEDICINE.getCode())) {
                    MedicineMessage medicineMessage = MedicineMessageCache.getInstance().get(article.getArticleMessage().getId());
                    if (medicineMessage == null) {
                        throw new RpgServerException(StateCode.FAIL, "存入错误物品id");
                    }
                    MedicineBean medicineBean = CommonsUtil.medicineMessageToMedicineBean(medicineMessage);
                    medicineBean.setQuantity(num);
                    article = medicineBean;
                }
                return article;
            } else {
                if (dealArticleBean02.getConfirm()) {
                    throw new RpgServerException(StateCode.FAIL, "已经确认过该交易，无法再移除");
                }
                //从交易栏中找出物品
                Article article = dealArticleBean02.abandon(dealArticleId, num);
                if (article == null) {
                    throw new RpgServerException(StateCode.FAIL, "交易栏中物品数量不足");
                }
                if (article.getArticleTypeCode().equals(ArticleTypeCode.MEDICINE.getCode())) {
                    MedicineMessage medicineMessage = MedicineMessageCache.getInstance().get(article.getArticleMessage().getId());
                    if (medicineMessage == null) {
                        throw new RpgServerException(StateCode.FAIL, "存入错误物品id");
                    }
                    MedicineBean medicineBean = CommonsUtil.medicineMessageToMedicineBean(medicineMessage);
                    medicineBean.setQuantity(num);
                    article = medicineBean;
                }
                //放入背包
                boolean flag = role.getBackpackManager().put(article, role.getId());
                if (!flag) {
                    throw new RpgServerException(StateCode.FAIL, "背包已经满了");
                }
                return article;
            }
        }
    }


    public static DealBean getDealBean(Integer dealBeanId) {
        return dealBeans.get(dealBeanId);
    }

    /**
     * 修改金币
     *
     * @param dealArticleBean01
     * @param role
     * @param money
     * @throws RpgServerException
     */
    private static void setMoney(DealArticleBean dealArticleBean01, MmoSimpleRole role, Integer money) throws RpgServerException {
        if (dealArticleBean01.getConfirm()) {
            throw new RpgServerException(StateCode.FAIL, "已经确认过该交易，无法再添加");
        }
        if (role.getMoney() < money) {
            throw new RpgServerException(StateCode.FAIL, "当前没有如此之多的金币");
        }
        Integer endMoney = dealArticleBean01.getMoney() - money;
        role.setMoney(role.getMoney() + endMoney);
        dealArticleBean01.setMoney(dealArticleBean01.getMoney() + money);
    }

    /**
     * 人物状态
     * @throws RpgServerException
     */
    private static void setRoleStatus(MmoSimpleRole role,boolean onDeal,Integer dealBeanId) {
        role.setOnDeal(onDeal);
        role.setDealBeanId(dealBeanId);
    }

    /**
     * 发送交易成功消息
     * @param dealBean
     */
    private static void sendDealSuccessMessage(DealBean dealBean) {
        //返回数据包
        NettyResponse nettyResponse = new NettyResponse();
        nettyResponse.setCmd(ConstantValue.DEAL_SUCCESS_RESPONSE);
        nettyResponse.setStateCode(StateCode.SUCCESS);
        //protobuf 生成registerResponse
        DealModel.DealModelMessage.Builder messageData = DealModel.DealModelMessage.newBuilder();
        messageData.setDataType(DealModel.DealModelMessage.DateType.DealSuccessResponse);
        DealModel.DealSuccessResponse.Builder dealSuccessResponseBuilder = DealModel.DealSuccessResponse.newBuilder();
        messageData.setDealSuccessResponse(dealSuccessResponseBuilder.build());
        nettyResponse.setData(messageData.build().toByteArray());
        Channel channel = ChannelMessageCache.getInstance().get(dealBean.getFirstRole().getId());
        if (channel != null) {
            channel.writeAndFlush(nettyResponse);
        }
        Channel channel02 = dealBean.getSecondRole().getChannel();
        if (channel02 != null) {
            channel02.writeAndFlush(nettyResponse);
        }
    }
    /**
     * 发送确认交易消息
     * @param dealBean
     * @param role
     */
    private static void sendConfirmMessage(DealBean dealBean, MmoSimpleRole role) {
        //返回数据包
        NettyResponse nettyResponse = new NettyResponse();
        nettyResponse.setCmd(ConstantValue.CONFIRM_DEAL_RESPONSE);
        nettyResponse.setStateCode(StateCode.SUCCESS);
        //protobuf 生成registerResponse
        DealModel.DealModelMessage.Builder messageData = DealModel.DealModelMessage.newBuilder();
        messageData.setDataType(DealModel.DealModelMessage.DateType.ConfirmDealResponse);
        DealModel.ConfirmDealResponse.Builder confirmDealResponseBuilder = DealModel.ConfirmDealResponse.newBuilder()
                .setRoleId(role.getId()).setRoleName(role.getName());
        messageData.setConfirmDealResponse(confirmDealResponseBuilder.build());
        DealServiceImpl.sendResponseEachOther(dealBean, nettyResponse, messageData);
    }

    /**
     * 交换物品
     * @param articles
     * @param role
     * @param money
     */
    public static void exchangeThing(List<Article> articles, MmoSimpleRole role, Integer money){
        if (articles.size()>0) {
            for (Article a : articles) {
                role.getBackpackManager().put(a, role.getId());
            }
        }
        role.setMoney(role.getMoney() +money);
        role.setOnDeal(false);
        role.setDealBeanId(null);
        //任务
        DealTaskAction dealTaskAction = new DealTaskAction();
        dealTaskAction.setTaskTargetType(TaskTargetTypeCode.FIRST_TIME_DEAL.getCode());
        role.getTaskManager().handler(dealTaskAction, role);
    }

    /**
     * 取消无用交易
     */
    public static void deleteUnSetDeal(){
        synchronized (dealBeans){
            Iterator iterator=dealBeans.values().iterator();
            while(iterator.hasNext()){
                DealBean dealBean= (DealBean) iterator.next();
                //非交易中的状态 且 超时 交易bean
                if (dealBean.getStatus().equals(DealStatusCode.WAIT.getCode())&&dealBean.getEndTime()<=System.currentTimeMillis()){
                    dealBeans.remove(dealBean.getId());
                }
            }
        }
    }
}
