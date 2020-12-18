package com.liqihao.service;

import com.google.protobuf.InvalidProtocolBufferException;
import com.liqihao.commons.NettyResponse;

public interface EquipmentService {
    void equipmentMsgResponse(NettyResponse nettyResponse) throws InvalidProtocolBufferException;

    void reduceEquipmentResponse(NettyResponse nettyResponse) throws InvalidProtocolBufferException;

    void addEquipmentResponse(NettyResponse nettyResponse) throws InvalidProtocolBufferException;

    void fixEquipmentResponse(NettyResponse nettyResponse) throws InvalidProtocolBufferException;
}
