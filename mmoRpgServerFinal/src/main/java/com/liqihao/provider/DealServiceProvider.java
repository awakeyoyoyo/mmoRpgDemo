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
import com.liqihao.pojo.bean.taskBean.teamFirstTask.TeamTaskAction;
import com.liqihao.protobufObject.DealModel;
import com.liqihao.util.CommonsUtil;
import io.netty.channel.Channel;

import java.util.ArrayList;
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
     *
     * @param role1
     * @param role2
     * @return
     * @throws RpgServerException
     */
    public static DealBean createDeal(MmoSimpleRole role1, MmoSimpleRole role2) throws RpgServerException {
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
        role1.setOnDeal(true);
        role2.setOnDeal(true);
        role1.setDealBeanId(dealBean.getId());
        role2.setDealBeanId(dealBean.getId());
        dealBeans.put(dealBean.getId(), dealBean);
        return dealBean;
    }

    /**
     * 开始交易
     *
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
        return dealBean;
    }

    /**
     * 拒绝交易
     *
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
            dealBeans.remove(dealBean.getId());
            dealBean.getFirstRole().setOnDeal(false);
            dealBean.getFirstRole().setDealBeanId(null);
            dealBean.getSecondRole().setOnDeal(false);
            dealBean.getSecondRole().setDealBeanId(null);
        }
        return dealBean;
    }

    /**
     * 确认交易
     *
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
                //交换金币
                role1.setMoney(role1.getMoney() + dealArticleBean02.getMoney());
                role2.setMoney(role2.getMoney() + dealArticleBean01.getMoney());
                //物品
                if (dealArticleBean02.getArticles().size() > 0) {
                    for (Article a : dealArticleBean02.getArticles()) {
                        role1.getBackpackManager().put(a, role1.getId());
                    }
                }
                if (dealArticleBean01.getArticles().size() > 0) {
                    for (Article a : dealArticleBean01.getArticles()) {
                        role2.getBackpackManager().put(a, role2.getId());
                    }
                }
                dealBean.setStatus(DealStatusCode.FINISH.getCode());
                dealBeans.remove(dealBean.getId());
                role1.setOnDeal(false);
                role1.setDealBeanId(null);
                role2.setOnDeal(false);
                role2.setDealBeanId(null);
                //发送消息交易完成
                sendDealSuccessMessage(dealBean);
                //任务条件触发
                DealTaskAction dealTaskAction01=new DealTaskAction();
                dealTaskAction01.setTaskTargetType(TaskTargetTypeCode.FIRST_TIME_DEAL.getCode());
                role1.getTaskManager().handler(dealTaskAction01,role1);
                DealTaskAction dealTaskAction02=new DealTaskAction();
                dealTaskAction02.setTaskTargetType(TaskTargetTypeCode.FIRST_TIME_DEAL.getCode());
                role2.getTaskManager().handler(dealTaskAction02,role2);
            }
        }
    }

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
     * 取消交易
     *
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
            //交换金币
            role1.setMoney(role1.getMoney() + dealArticleBean01.getMoney());
            role2.setMoney(role2.getMoney() + dealArticleBean02.getMoney());
            //物品
            if (dealArticleBean01.getArticles().size() > 0) {
                for (Article a : dealArticleBean01.getArticles()) {
                    role1.getBackpackManager().put(a, role1.getId());
                }
            }
            if (dealArticleBean02.getArticles().size() > 0) {
                for (Article a : dealArticleBean02.getArticles()) {
                    role2.getBackpackManager().put(a, role2.getId());
                }
            }
            dealBean.setStatus(DealStatusCode.FINISH.getCode());
            dealBeans.remove(dealBean.getId());
            role1.setOnDeal(false);
            role1.setDealBeanId(null);
            role2.setOnDeal(false);
            role2.setDealBeanId(null);
        }
        return dealBean;
    }

    /**
     * 修改金钱
     *
     * @param role
     * @return
     * @throws RpgServerException
     */
    public static DealBean setMoneyDeal(MmoSimpleRole role, Integer money) throws RpgServerException {
        if (role.getDealBeanId() == null) {
            throw new RpgServerException(StateCode.FAIL, "该玩家并没有被邀请交易");
        }
        DealBean dealBean = dealBeans.get(role.getDealBeanId());
        if (!dealBean.getStatus().equals(DealStatusCode.ON_DEAL.getCode())) {
            throw new RpgServerException(StateCode.FAIL, "该交易还没开始，无法再修改金币");
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
        role.moneyLock.writeLock().lock();
        try {
            Integer endMoney = dealArticleBean01.getMoney() - money;
            role.setMoney(role.getMoney() + endMoney);
            dealArticleBean01.setMoney(dealArticleBean01.getMoney() + money);
        } finally {
            role.moneyLock.writeLock().unlock();
        }
    }

    /**
     * 添加物品
     *
     * @throws RpgServerException
     */
    public static Article addArticleDeal(Integer articleId, Integer num, MmoSimpleRole role) throws RpgServerException {
        if (role.getDealBeanId() == null) {
            throw new RpgServerException(StateCode.FAIL, "该玩家并没有被邀请交易");
        }
        DealBean dealBean = dealBeans.get(role.getDealBeanId());
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

    /**
     * 移除物品
     *
     * @throws RpgServerException
     */
    public static Article reduceArticleDeal(Integer dealArticleId, Integer num, MmoSimpleRole role) throws RpgServerException {
        if (role.getDealBeanId() == null) {
            throw new RpgServerException(StateCode.FAIL, "该玩家并没有被邀请交易");
        }
        DealBean dealBean = dealBeans.get(role.getDealBeanId());
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

    public static DealBean getDealBean(Integer dealBeanId) {
        return dealBeans.get(dealBeanId);
    }
}
