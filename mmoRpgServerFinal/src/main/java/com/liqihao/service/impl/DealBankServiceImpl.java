package com.liqihao.service.impl;

import com.liqihao.commons.RpgServerException;
import com.liqihao.pojo.bean.roleBean.MmoSimpleRole;
import com.liqihao.protobufObject.CopySceneModel;
import com.liqihao.provider.DealBankServiceProvider;
import com.liqihao.service.DealBankService;

/**
 * 拍卖行模块
 * @author lqhao
 */
public class DealBankServiceImpl implements DealBankService {
    @Override
    public void addSellArticleRequest(CopySceneModel.CopySceneModelMessage myMessage, MmoSimpleRole mmoSimpleRole) throws RpgServerException {


//        DealBankServiceProvider.addSellArticleToDealBank();
    }

    @Override
    public void addAuctionArticleRequest(CopySceneModel.CopySceneModelMessage myMessage, MmoSimpleRole mmoSimpleRole) throws RpgServerException {

    }

    @Override
    public void reduceSellArticleRequest(CopySceneModel.CopySceneModelMessage myMessage, MmoSimpleRole mmoSimpleRole) throws RpgServerException {

    }

    @Override
    public void reduceAuctionArticleRequest(CopySceneModel.CopySceneModelMessage myMessage, MmoSimpleRole mmoSimpleRole) throws RpgServerException {

    }

    @Override
    public void buyArticleRequest(CopySceneModel.CopySceneModelMessage myMessage, MmoSimpleRole mmoSimpleRole) throws RpgServerException {

    }

    @Override
    public void auctionArticleRequest(CopySceneModel.CopySceneModelMessage myMessage, MmoSimpleRole mmoSimpleRole) throws RpgServerException {

    }
}
