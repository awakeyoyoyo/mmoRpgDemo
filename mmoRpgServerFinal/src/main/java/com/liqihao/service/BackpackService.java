package com.liqihao.service;

import com.google.protobuf.InvalidProtocolBufferException;
import com.liqihao.pojo.bean.MmoSimpleRole;
import com.liqihao.protobufObject.BackPackModel;

/**
 * 背包模块
 * @author lqhao
 */
public interface BackpackService {
    /**
     * 丢弃物品
     * @param myMessage
     * @param mmoSimpleRole
     * @throws InvalidProtocolBufferException
     */
    void abandonRequest(BackPackModel.BackPackModelMessage myMessage, MmoSimpleRole mmoSimpleRole) throws InvalidProtocolBufferException;

    /**
     * 查看背包信息
     * @param myMessage
     * @param mmoSimpleRole
     * @throws InvalidProtocolBufferException
     */
    void backPackMsgRequest(BackPackModel.BackPackModelMessage myMessage, MmoSimpleRole mmoSimpleRole) throws InvalidProtocolBufferException;

    /**
     * 使用物品
     * @param myMessage
     * @param mmoSimpleRole
     * @throws InvalidProtocolBufferException
     */
    void useRequest(BackPackModel.BackPackModelMessage myMessage, MmoSimpleRole mmoSimpleRole) throws InvalidProtocolBufferException;

    /**
     * 放入背包
     * @param myMessage
     * @param mmoSimpleRole
     * @throws InvalidProtocolBufferException
     */
    void addArticleRequest(BackPackModel.BackPackModelMessage myMessage, MmoSimpleRole mmoSimpleRole) throws Exception;
    /**
     *  副本中可捡去的物品
     * @param myMessage
     * @param mmoSimpleRole
     * @throws InvalidProtocolBufferException
     */
    void findAllCanGetRequest(BackPackModel.BackPackModelMessage myMessage, MmoSimpleRole mmoSimpleRole) throws InvalidProtocolBufferException;

    /**
     * 从副本地面上拾取东西
     * @param myMessage
     * @param mmoSimpleRole
     * @throws InvalidProtocolBufferException
     */
    void getArticleFromFloorRequest(BackPackModel.BackPackModelMessage myMessage, MmoSimpleRole mmoSimpleRole) throws Exception;


    /**
     * 查看有多少钱
     * @param myMessage
     * @param mmoSimpleRole
     * @throws InvalidProtocolBufferException
     */
    void checkMoneyNumber(BackPackModel.BackPackModelMessage myMessage, MmoSimpleRole mmoSimpleRole) throws InvalidProtocolBufferException;


    /**
     * 购买商品 需id以及数量
     * @param myMessage
     * @param mmoSimpleRole
     * @throws Exception
     */
    void buyGoods(BackPackModel.BackPackModelMessage myMessage, MmoSimpleRole mmoSimpleRole) throws Exception;

    /**
     * 查看商品列表
     * @param myMessage
     * @param mmoSimpleRole
     * @throws Exception
     */
    void findAllGoods(BackPackModel.BackPackModelMessage myMessage, MmoSimpleRole mmoSimpleRole) throws Exception;


    /**
     * 整理背包
     * @param myMessage
     * @param mmoSimpleRole
     * @throws Exception
     */
    void sortBackPack(BackPackModel.BackPackModelMessage myMessage, MmoSimpleRole mmoSimpleRole) throws Exception;
}
