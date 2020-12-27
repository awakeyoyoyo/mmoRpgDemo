package com.liqihao.service;

import com.google.protobuf.InvalidProtocolBufferException;
import com.liqihao.commons.NettyRequest;
import com.liqihao.commons.NettyResponse;
import com.liqihao.protobufObject.EquipmentModel;
import io.netty.channel.Channel;

/**
 * 装备模块
 * @author lqhao
 */
public interface EquipmentService {
    /**
     * 穿装备
     * @param myMessage
     * @param channel
     * @throws InvalidProtocolBufferException
     */
    void addEquipmentRequest(EquipmentModel.EquipmentModelMessage myMessage, Channel channel) throws InvalidProtocolBufferException;

    /**
     * 背包信息
     * @param myMessage
     * @param channel
     * @throws InvalidProtocolBufferException
     */
    void equipmentMasRequest(EquipmentModel.EquipmentModelMessage myMessage, Channel channel) throws InvalidProtocolBufferException;

    /**
     * 脱装备
     * @param myMessage
     * @param channel
     * @throws InvalidProtocolBufferException
     */
    void reduceEquipmentRequest(EquipmentModel.EquipmentModelMessage myMessage, Channel channel) throws InvalidProtocolBufferException;

    /**
     * 修复装备
     * @param myMessage
     * @param channel
     * @throws InvalidProtocolBufferException
     */
    void fixEquipmentRequest(EquipmentModel.EquipmentModelMessage myMessage, Channel channel) throws InvalidProtocolBufferException;
}
