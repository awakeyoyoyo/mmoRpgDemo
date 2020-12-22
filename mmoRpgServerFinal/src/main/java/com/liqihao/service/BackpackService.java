package com.liqihao.service;

import com.google.protobuf.InvalidProtocolBufferException;
import com.liqihao.commons.NettyRequest;
import com.liqihao.commons.NettyResponse;
import io.netty.channel.Channel;

/**
 * 背包模块
 * @author lqhao
 */
public interface BackpackService {
    /**
     * 丢弃物品
     * @param nettyRequest
     * @param channel
     * @throws InvalidProtocolBufferException
     */
    void abandonRquest(NettyRequest nettyRequest, Channel channel) throws InvalidProtocolBufferException;

    /**
     * 查看背包信息
     * @param nettyRequest
     * @param channel
     * @throws InvalidProtocolBufferException
     */
    void backPackMsgRequest(NettyRequest nettyRequest, Channel channel) throws InvalidProtocolBufferException;

    /**
     * 使用物品
     * @param nettyRequest
     * @param channel
     * @throws InvalidProtocolBufferException
     */
    void useRequest(NettyRequest nettyRequest, Channel channel) throws InvalidProtocolBufferException;

    /**
     * 放入背包
     * @param nettyRequest
     * @param channel
     * @throws InvalidProtocolBufferException
     */
    void addArticleRequest(NettyRequest nettyRequest, Channel channel) throws InvalidProtocolBufferException;

}
