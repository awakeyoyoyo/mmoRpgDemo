package com.liqihao.service;

import com.google.protobuf.InvalidProtocolBufferException;
import com.liqihao.commons.NettyRequest;
import com.liqihao.commons.NettyResponse;
import com.liqihao.protobufObject.BackPackModel;
import io.netty.channel.Channel;

/**
 * 背包模块
 * @author lqhao
 */
public interface BackpackService {
    /**
     * 丢弃物品
     * @param myMessage
     * @param channel
     * @throws InvalidProtocolBufferException
     */
    void abandonRquest(BackPackModel.BackPackModelMessage myMessage, Channel channel) throws InvalidProtocolBufferException;

    /**
     * 查看背包信息
     * @param myMessage
     * @param channel
     * @throws InvalidProtocolBufferException
     */
    void backPackMsgRequest(BackPackModel.BackPackModelMessage myMessage, Channel channel) throws InvalidProtocolBufferException;

    /**
     * 使用物品
     * @param myMessage
     * @param channel
     * @throws InvalidProtocolBufferException
     */
    void useRequest(BackPackModel.BackPackModelMessage myMessage, Channel channel) throws InvalidProtocolBufferException;

    /**
     * 放入背包
     * @param myMessage
     * @param channel
     * @throws InvalidProtocolBufferException
     */
    void addArticleRequest(BackPackModel.BackPackModelMessage myMessage, Channel channel) throws InvalidProtocolBufferException;

}
