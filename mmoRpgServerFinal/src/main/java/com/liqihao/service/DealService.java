package com.liqihao.service;

import com.liqihao.pojo.bean.roleBean.MmoSimpleRole;
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
    void askDealRequest(EmailModel.EmailModelMessage myMessage, MmoSimpleRole mmoSimpleRole);

    /**
     *  接收交易请求
     * @param myMessage
     * @param mmoSimpleRole
     */
    void agreeDealRequest(EmailModel.EmailModelMessage myMessage, MmoSimpleRole mmoSimpleRole);

    /**
     *  拒绝交易请求
     * @param myMessage
     * @param mmoSimpleRole
     */
    void refuseDealRequest(EmailModel.EmailModelMessage myMessage, MmoSimpleRole mmoSimpleRole);

    /**
     *  确认交易请求
     * @param myMessage
     * @param mmoSimpleRole
     */
    void confirmDealRequest(EmailModel.EmailModelMessage myMessage, MmoSimpleRole mmoSimpleRole);

    /**
     *  取消交易请求
     * @param myMessage
     * @param mmoSimpleRole
     */
    void cancelDealRequest(EmailModel.EmailModelMessage myMessage, MmoSimpleRole mmoSimpleRole);
}
