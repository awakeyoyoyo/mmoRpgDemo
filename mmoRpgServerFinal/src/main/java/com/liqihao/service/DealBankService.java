package com.liqihao.service;

import com.liqihao.commons.RpgServerException;
import com.liqihao.pojo.bean.roleBean.MmoSimpleRole;
import com.liqihao.protobufObject.ChatModel;
import com.liqihao.protobufObject.CopySceneModel;

/**
 * 拍卖行模块
 * @author lqhao
 */
public interface DealBankService {
    /**
     * 上架一口价商品
     * @param myMessage
     * @param mmoSimpleRole
     * @throws RpgServerException
     */
    void addSellArticleRequest(CopySceneModel.CopySceneModelMessage myMessage, MmoSimpleRole mmoSimpleRole) throws RpgServerException;
    /**
     * 上架拍卖品
     * @param myMessage
     * @param mmoSimpleRole
     * @throws RpgServerException
     */
    void addAuctionArticleRequest(CopySceneModel.CopySceneModelMessage myMessage, MmoSimpleRole mmoSimpleRole) throws RpgServerException;
    /**
     * 下架一口价商品
     * @param myMessage
     * @param mmoSimpleRole
     * @throws RpgServerException
     */
    void reduceSellArticleRequest(CopySceneModel.CopySceneModelMessage myMessage, MmoSimpleRole mmoSimpleRole) throws RpgServerException;

    /**
     * 下架拍卖品
     * @param myMessage
     * @param mmoSimpleRole
     * @throws RpgServerException
     */
    void reduceAuctionArticleRequest(CopySceneModel.CopySceneModelMessage myMessage, MmoSimpleRole mmoSimpleRole) throws RpgServerException;
    /**
     * 购买一口价物品
     * @param myMessage
     * @param mmoSimpleRole
     * @throws RpgServerException
     */
    void buyArticleRequest(CopySceneModel.CopySceneModelMessage myMessage, MmoSimpleRole mmoSimpleRole) throws RpgServerException;
    /**
     * 拍卖物品
     * @param myMessage
     * @param mmoSimpleRole
     * @throws RpgServerException
     */
    void auctionArticleRequest(CopySceneModel.CopySceneModelMessage myMessage, MmoSimpleRole mmoSimpleRole) throws RpgServerException;
    /**
     * 获取所有商品物品
     * @param myMessage
     * @param mmoSimpleRole
     * @throws RpgServerException
     */
    void getArticleRequest(CopySceneModel.CopySceneModelMessage myMessage, MmoSimpleRole mmoSimpleRole) throws RpgServerException;

}
