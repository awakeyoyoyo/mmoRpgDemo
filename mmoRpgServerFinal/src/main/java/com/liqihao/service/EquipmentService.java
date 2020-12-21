package com.liqihao.service;

import com.google.protobuf.InvalidProtocolBufferException;
import com.liqihao.commons.NettyRequest;
import com.liqihao.commons.NettyResponse;
import io.netty.channel.Channel;

public interface EquipmentService {
    void addEquipmentRequest(NettyRequest nettyRequest, Channel channel) throws InvalidProtocolBufferException;
    void equipmentMasRequest(NettyRequest nettyRequest, Channel channel) throws InvalidProtocolBufferException;
    void reduceEquipmentRequest(NettyRequest nettyRequest, Channel channel) throws InvalidProtocolBufferException;
    void fixEquipmentRequest(NettyRequest nettyRequest, Channel channel) throws InvalidProtocolBufferException;
}
