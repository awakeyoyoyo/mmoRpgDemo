package com.liqihao.service;

import com.google.protobuf.InvalidProtocolBufferException;
import com.liqihao.commons.NettyRequest;
import com.liqihao.commons.NettyResponse;
import io.netty.channel.Channel;

/**
 * 装备模块
 * @author lqhao
 */
public interface EquipmentService {
    /**
     * 穿装备
     * @param nettyRequest
     * @param channel
     * @throws InvalidProtocolBufferException
     */
    void addEquipmentRequest(NettyRequest nettyRequest, Channel channel) throws InvalidProtocolBufferException;

    /**
     * 背包信息
     * @param nettyRequest
     * @param channel
     * @throws InvalidProtocolBufferException
     */
    void equipmentMasRequest(NettyRequest nettyRequest, Channel channel) throws InvalidProtocolBufferException;

    /**
     * 脱装备
     * @param nettyRequest
     * @param channel
     * @throws InvalidProtocolBufferException
     */
    void reduceEquipmentRequest(NettyRequest nettyRequest, Channel channel) throws InvalidProtocolBufferException;

    /**
     * 修复装备
     * @param nettyRequest
     * @param channel
     * @throws InvalidProtocolBufferException
     */
    void fixEquipmentRequest(NettyRequest nettyRequest, Channel channel) throws InvalidProtocolBufferException;
}
