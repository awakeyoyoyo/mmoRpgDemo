package com.liqihao.service;

import com.liqihao.commons.RpgServerException;
import com.liqihao.pojo.bean.roleBean.MmoSimpleRole;
import com.liqihao.protobufObject.ChatModel;
import com.liqihao.protobufObject.CopySceneModel;
import com.liqihao.protobufObject.DealBankModel;

/**
 * 拍卖行模块
 * @author lqhao
 */
public interface DealBankService {
    /**
     * 上架商品
     * @param myMessage
     * @param mmoSimpleRole
     * @throws RpgServerException
     */
    void addSellArticleRequest(DealBankModel.DealBankModelMessage myMessage, MmoSimpleRole mmoSimpleRole) throws RpgServerException;
    /**
     * 下架商品
     * @param myMessage
     * @param mmoSimpleRole
     * @throws RpgServerException
     */
    void reduceSellArticleRequest(DealBankModel.DealBankModelMessage myMessage, MmoSimpleRole mmoSimpleRole) throws RpgServerException;
    /**
     * 购买一口价物品
     * @param myMessage
     * @param mmoSimpleRole
     * @throws RpgServerException
     */
    void buyArticleRequest(DealBankModel.DealBankModelMessage myMessage, MmoSimpleRole mmoSimpleRole) throws RpgServerException;
    /**
     * 拍卖物品
     * @param myMessage
     * @param mmoSimpleRole
     * @throws RpgServerException
     */
    void auctionArticleRequest(DealBankModel.DealBankModelMessage myMessage, MmoSimpleRole mmoSimpleRole) throws RpgServerException;
    /**
     * 获取所有商品物品
     * @param myMessage
     * @param mmoSimpleRole
     * @throws RpgServerException
     */
    void getArticleRequest(DealBankModel.DealBankModelMessage myMessage, MmoSimpleRole mmoSimpleRole) throws RpgServerException;

}
