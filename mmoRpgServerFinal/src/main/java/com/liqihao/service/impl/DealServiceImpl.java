package com.liqihao.service.impl;

import com.liqihao.pojo.bean.roleBean.MmoSimpleRole;
import com.liqihao.protobufObject.EmailModel;
import com.liqihao.service.DealService;

/**
 * 交易模块
 * @author lqhao
 */
public class DealServiceImpl implements DealService {
    @Override
    public void askDealRequest(EmailModel.EmailModelMessage myMessage, MmoSimpleRole mmoSimpleRole) {
        
    }

    @Override
    public void agreeDealRequest(EmailModel.EmailModelMessage myMessage, MmoSimpleRole mmoSimpleRole) {

    }

    @Override
    public void refuseDealRequest(EmailModel.EmailModelMessage myMessage, MmoSimpleRole mmoSimpleRole) {

    }

    @Override
    public void confirmDealRequest(EmailModel.EmailModelMessage myMessage, MmoSimpleRole mmoSimpleRole) {

    }

    @Override
    public void cancelDealRequest(EmailModel.EmailModelMessage myMessage, MmoSimpleRole mmoSimpleRole) {

    }
}
