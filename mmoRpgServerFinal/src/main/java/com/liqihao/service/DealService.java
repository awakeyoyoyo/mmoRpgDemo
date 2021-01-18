package com.liqihao.service;

import com.liqihao.commons.RpgServerException;
import com.liqihao.pojo.bean.roleBean.MmoSimpleRole;
import com.liqihao.protobufObject.DealModel;
import com.liqihao.protobufObject.EmailModel;

/**
 * 交易模块
 * @author lqhao
 */
public interface DealService {
    /**
     *  发起交易请求
     * @param myMessage
     * @param mmoSimpleRole
     */
    void askDealRequest(DealModel.DealModelMessage myMessage, MmoSimpleRole mmoSimpleRole) throws RpgServerException;

    /**
     *  接收交易请求
     * @param myMessage
     * @param mmoSimpleRole
     */
    void agreeDealRequest(DealModel.DealModelMessage myMessage, MmoSimpleRole mmoSimpleRole) throws RpgServerException;

    /**
     *  拒绝交易请求
     * @param myMessage
     * @param mmoSimpleRole
     */
    void refuseDealRequest(DealModel.DealModelMessage myMessage, MmoSimpleRole mmoSimpleRole) throws RpgServerException;

    /**
     *  确认交易请求
     * @param myMessage
     * @param mmoSimpleRole
     */
    void confirmDealRequest(DealModel.DealModelMessage myMessage, MmoSimpleRole mmoSimpleRole) throws RpgServerException;

    /**
     *  取消交易请求
     * @param myMessage
     * @param mmoSimpleRole
     */
    void cancelDealRequest(DealModel.DealModelMessage myMessage, MmoSimpleRole mmoSimpleRole) throws RpgServerException;

    /**
     *  获取交易信息请求
     * @param myMessage
     * @param mmoSimpleRole
     */
    void getDealMessageRequest(DealModel.DealModelMessage myMessage, MmoSimpleRole mmoSimpleRole) throws RpgServerException;

    /**
     *  设置金币请求
     * @param myMessage
     * @param mmoSimpleRole
     */
    void setDealMoneyRequest(DealModel.DealModelMessage myMessage, MmoSimpleRole mmoSimpleRole) throws RpgServerException;

    /**
     * 添加物品请求
     * @param myMessage
     * @param mmoSimpleRole
     */
    void addDealArticleRequest(DealModel.DealModelMessage myMessage, MmoSimpleRole mmoSimpleRole) throws RpgServerException;

    /**
     * 拿回物品请求
     * @param myMessage
     * @param mmoSimpleRole
     */
    void abandonDealArticleRequest(DealModel.DealModelMessage myMessage, MmoSimpleRole mmoSimpleRole) throws RpgServerException;

}
