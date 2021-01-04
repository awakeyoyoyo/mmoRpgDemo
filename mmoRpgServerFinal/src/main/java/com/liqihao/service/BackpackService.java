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
    void addArticleRequest(BackPackModel.BackPackModelMessage myMessage, MmoSimpleRole mmoSimpleRole) throws InvalidProtocolBufferException;

}
