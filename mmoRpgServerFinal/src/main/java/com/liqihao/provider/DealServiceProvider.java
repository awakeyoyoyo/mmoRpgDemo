package com.liqihao.provider;

import com.liqihao.commons.RpgServerException;
import com.liqihao.commons.StateCode;
import com.liqihao.commons.enums.DealStatusCode;
import com.liqihao.pojo.bean.articleBean.Article;
import com.liqihao.pojo.bean.dealBean.DealArticleBean;
import com.liqihao.pojo.bean.dealBean.DealBean;
import com.liqihao.pojo.bean.roleBean.MmoSimpleRole;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 交易服务提供者
 * @author lqhao
 */
public class DealServiceProvider {
    private static ConcurrentHashMap<Integer, DealBean> dealBeans =new ConcurrentHashMap<>();
    private static AtomicInteger dealBeanIdAuto=new AtomicInteger(0);

    public static DealBean createDeal(MmoSimpleRole role1,MmoSimpleRole role2) throws RpgServerException {
        if (role1.getOnDeal()){
            throw new RpgServerException(StateCode.FAIL,"你处于交易状态，无法再发起交易请求");
        }
        if (role2.getOnDeal()){
            throw new RpgServerException(StateCode.FAIL,"对方处于交易状态，无法再发起交易请求");
        }
        DealBean dealBean=new DealBean();
        //初始化交易bean
        dealBean.setId(dealBeanIdAuto.incrementAndGet());
        dealBean.setFirstRole(role1);
        dealBean.setSecondRole(role2);

        DealArticleBean dealArticleBean01=new DealArticleBean();
        dealArticleBean01.setRole(role1);
        dealArticleBean01.setArticles(new ArrayList<>());
        dealArticleBean01.setMoney(0);
        dealArticleBean01.setConfirm(false);

        dealBean.setFirstDealArticleBean(dealArticleBean01);
        DealArticleBean dealArticleBean02=new DealArticleBean();
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
        //发信息 todo
        return dealBean;
    }

    /**
     * 开始交易
     * @param role
     * @return
     * @throws RpgServerException
     */
    public static DealBean beginDeal(MmoSimpleRole role) throws RpgServerException {
        if (role.getDealBeanId()==null){
            throw new RpgServerException(StateCode.FAIL,"该玩家并没有被邀请交易");
        }
        DealBean dealBean= dealBeans.get(role.getDealBeanId());
        if (!dealBean.getStatus().equals(DealStatusCode.WAIT.getCode())){
            throw new RpgServerException(StateCode.FAIL,"该交易已经开始了，请勿重复开始");
        }

        dealBean.setStatus(DealStatusCode.ON_DEAL.getCode());
        //发信息 todo
        return dealBean;
    }

    /**
     * 确认交易
     * @param role
     * @return
     * @throws RpgServerException
     */
    public static void confirmDeal(MmoSimpleRole role) throws RpgServerException {
        if (role.getDealBeanId()==null){
            throw new RpgServerException(StateCode.FAIL,"该玩家并没有被邀请交易");
        }
        DealBean dealBean= dealBeans.get(role.getDealBeanId());
        if (!dealBean.getStatus().equals(DealStatusCode.ON_DEAL.getCode())){
            throw new RpgServerException(StateCode.FAIL,"该交易还没开始，无法确认");
        }
        DealArticleBean dealArticleBean01=dealBean.getFirstDealArticleBean();
        DealArticleBean dealArticleBean02=dealBean.getSecondDealArticleBean();
        if (dealArticleBean01.getRole().getId().equals(role.getId())){
            if (dealArticleBean01.getConfirm()){
                throw new RpgServerException(StateCode.FAIL,"已经确认过该交易");
            }
            dealArticleBean01.setConfirm(true);
            //发信息 某某确认了
        }else{
            if (dealArticleBean02.getConfirm()){
                throw new RpgServerException(StateCode.FAIL,"已经确认过该交易");
            }
            dealArticleBean02.setConfirm(true);
            //发信息 某某确认了
        }
        //判断是否双方已经完成交易
        if (dealArticleBean01.getConfirm()&&dealArticleBean02.getConfirm()){
            //交易完成 双方交换
            MmoSimpleRole role1=dealBean.getFirstRole();
            MmoSimpleRole role2=dealBean.getSecondRole();
            //交换金币
            role1.setMoney(role1.getMoney()+dealArticleBean02.getMoney());
            role2.setMoney(role2.getMoney()+dealArticleBean01.getMoney());
            //物品
            if (dealArticleBean02.getArticles().size()>0) {
                for (Article a : dealArticleBean02.getArticles()) {
                    role1.getBackpackManager().put(a,role1.getId());
                }
            }
            if (dealArticleBean01.getArticles().size()>0) {
                for (Article a : dealArticleBean01.getArticles()) {
                    role2.getBackpackManager().put(a,role2.getId());
                }
            }
            dealBean.setStatus(DealStatusCode.FINISH.getCode());
            dealBeans.remove(dealBean.getId());
            role1.setOnDeal(false);
            role1.setDealBeanId(null);
            role2.setOnDeal(false);
            role2.setDealBeanId(null);
            //发送消息交易完成 todo
        }
    }

    /**
     * 取消交易
     * @param role
     * @return
     * @throws RpgServerException
     */
    public static void cancelDeal(MmoSimpleRole role) throws RpgServerException {
        if (role.getDealBeanId()==null){
            throw new RpgServerException(StateCode.FAIL,"该玩家并没有被邀请交易");
        }
        DealBean dealBean= dealBeans.get(role.getDealBeanId());
        if (!dealBean.getStatus().equals(DealStatusCode.ON_DEAL.getCode())){
            throw new RpgServerException(StateCode.FAIL,"该交易还没开始，无法取消");
        }
        DealArticleBean dealArticleBean01=dealBean.getFirstDealArticleBean();
        DealArticleBean dealArticleBean02=dealBean.getSecondDealArticleBean();
        if (dealArticleBean01.getRole().getId().equals(role.getId())){
            if (dealArticleBean01.getConfirm()){
                throw new RpgServerException(StateCode.FAIL,"已经确认过该交易，无法再取消");
            }
        }else{
            if (dealArticleBean02.getConfirm()){
                throw new RpgServerException(StateCode.FAIL,"已经确认过该交易，无法再取消");
            }
        }
        //交易取消 物品还原
        MmoSimpleRole role1=dealBean.getFirstRole();
        MmoSimpleRole role2=dealBean.getSecondRole();
        //交换金币
        role1.setMoney(role1.getMoney()+dealArticleBean01.getMoney());
        role2.setMoney(role2.getMoney()+dealArticleBean02.getMoney());
            //物品
        if (dealArticleBean01.getArticles().size()>0) {
            for (Article a : dealArticleBean01.getArticles()) {
                    role1.getBackpackManager().put(a,role1.getId());
            }
        }
        if (dealArticleBean02.getArticles().size()>0) {
            for (Article a : dealArticleBean02.getArticles()) {
                role2.getBackpackManager().put(a,role2.getId());
            }
        }
        dealBean.setStatus(DealStatusCode.FINISH.getCode());
        dealBeans.remove(dealBean.getId());
        role1.setOnDeal(false);
        role1.setDealBeanId(null);
        role2.setOnDeal(false);
        role2.setDealBeanId(null);
        //发送消息交易取消 todo
    }

    /**
     * 添加金钱
     * @param role
     * @return
     * @throws RpgServerException
     */
    public static void addMoneyDeal(MmoSimpleRole role,Integer money) throws RpgServerException {
        if (role.getDealBeanId()==null){
            throw new RpgServerException(StateCode.FAIL,"该玩家并没有被邀请交易");
        }
        DealBean dealBean= dealBeans.get(role.getDealBeanId());
        if (!dealBean.getStatus().equals(DealStatusCode.ON_DEAL.getCode())){
            throw new RpgServerException(StateCode.FAIL,"该交易还没开始，无法放入金币");
        }
        DealArticleBean dealArticleBean01=dealBean.getFirstDealArticleBean();
        DealArticleBean dealArticleBean02=dealBean.getSecondDealArticleBean();
        if (dealArticleBean01.getRole().getId().equals(role.getId())){
            addMoney(dealArticleBean01,role,money);
        }else{
            addMoney(dealArticleBean02,role,money);
        }
        //发消息给双方加钱了 todo
    }

    /**
     *  加钱
     * @param dealArticleBean01
     * @param role
     * @param money
     * @throws RpgServerException
     */
    private static void addMoney(DealArticleBean dealArticleBean01, MmoSimpleRole role, Integer money) throws RpgServerException {
        if (dealArticleBean01.getConfirm()){
            throw new RpgServerException(StateCode.FAIL,"已经确认过该交易，无法再添加");
        }
        if (role.getMoney()<money){
            throw new RpgServerException(StateCode.FAIL,"当前没有如此之多的金币");
        }
        role.setMoney(role.getMoney()-money);
        dealArticleBean01.setMoney(dealArticleBean01.getMoney()+money);
    }

    /**
     * 添加物品
     * @throws RpgServerException
     */
    public static void addArticleDeal(Integer articleId,Integer num,MmoSimpleRole role) throws RpgServerException {
        if (role.getDealBeanId()==null){
            throw new RpgServerException(StateCode.FAIL,"该玩家并没有被邀请交易");
        }
        DealBean dealBean= dealBeans.get(role.getDealBeanId());
        if (!dealBean.getStatus().equals(DealStatusCode.ON_DEAL.getCode())){
            throw new RpgServerException(StateCode.FAIL,"该交易还没开始，无法放入物品");
        }
        DealArticleBean dealArticleBean01=dealBean.getFirstDealArticleBean();
        DealArticleBean dealArticleBean02=dealBean.getSecondDealArticleBean();
        if (dealArticleBean01.getRole().getId().equals(role.getId())){
            if (dealArticleBean01.getConfirm()){
                throw new RpgServerException(StateCode.FAIL,"已经确认过该交易，无法再放入");
            }
            Article article=role.getBackpackManager().useOrAbandonArticle(articleId,num,role.getId());
            if (article==null){
                throw new RpgServerException(StateCode.FAIL,"背包中物品数量不足");
            }
            dealArticleBean01.put(article);
        }else{
            if (dealArticleBean02.getConfirm()){
                throw new RpgServerException(StateCode.FAIL,"已经确认过该交易，无法再放入");
            }
            Article article=role.getBackpackManager().useOrAbandonArticle(articleId,num,role.getId());
            if (article==null){
                throw new RpgServerException(StateCode.FAIL,"背包中物品数量不足");
            }
            dealArticleBean02.put(article);
        }
        //发送消息给双方 交易中增加了什么 todo
    }
    /**
     * 移除物品
     * @throws RpgServerException
     */
    public static void reduceArticleDeal(Integer dealArticleId,Integer num,MmoSimpleRole role) throws RpgServerException {
        if (role.getDealBeanId()==null){
            throw new RpgServerException(StateCode.FAIL,"该玩家并没有被邀请交易");
        }
        DealBean dealBean= dealBeans.get(role.getDealBeanId());
        if (!dealBean.getStatus().equals(DealStatusCode.ON_DEAL.getCode())){
            throw new RpgServerException(StateCode.FAIL,"该交易还没开始，无法移除物品");
        }
        DealArticleBean dealArticleBean01=dealBean.getFirstDealArticleBean();
        DealArticleBean dealArticleBean02=dealBean.getSecondDealArticleBean();
        //发送消息给双方 交易中增加了什么 todo
    }
    /**
     * 减少金币
     * @throws RpgServerException
     */
    public static void reduceArticleDeal(Integer money,MmoSimpleRole role) throws RpgServerException {
        if (role.getDealBeanId()==null){
            throw new RpgServerException(StateCode.FAIL,"该玩家并没有被邀请交易");
        }
        DealBean dealBean= dealBeans.get(role.getDealBeanId());
        if (!dealBean.getStatus().equals(DealStatusCode.ON_DEAL.getCode())){
            throw new RpgServerException(StateCode.FAIL,"该交易还没开始，无法移除金币");
        }
        DealArticleBean dealArticleBean01=dealBean.getFirstDealArticleBean();
        DealArticleBean dealArticleBean02=dealBean.getSecondDealArticleBean();
        //发送消息给双方 交易中增加了什么 todo
    }
}
