package com.liqihao.service;

import com.google.protobuf.InvalidProtocolBufferException;
import com.liqihao.commons.NettyRequest;
import com.liqihao.commons.NettyResponse;
import io.netty.channel.Channel;

public interface EquipmentService {
    NettyResponse addEquipmentRequest(NettyRequest nettyRequest, Channel channel) throws InvalidProtocolBufferException;
    NettyResponse equipmentMasRequest(NettyRequest nettyRequest, Channel channel) throws InvalidProtocolBufferException;
    NettyResponse reduceEquipmentRequest(NettyRequest nettyRequest, Channel channel) throws InvalidProtocolBufferException;
}
