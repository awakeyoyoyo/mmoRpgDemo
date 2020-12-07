package com.liqihao.service;

import com.google.protobuf.InvalidProtocolBufferException;
import com.liqihao.annotation.HandlerServiceTag;
import com.liqihao.commons.ConstantValue;
import com.liqihao.commons.NettyRequest;
import com.liqihao.commons.NettyResponse;
import io.netty.channel.Channel;

public interface SceneService {
    @HandlerServiceTag(cmd = ConstantValue.ASK_CAN_REQUEST)
    NettyResponse askCanRequest(NettyRequest nettyRequest,Channel channel) throws InvalidProtocolBufferException;
    @HandlerServiceTag(cmd = ConstantValue.WENT_REQUEST)
    NettyResponse wentRequest(NettyRequest nettyRequest,Channel channel) throws InvalidProtocolBufferException;
    @HandlerServiceTag(cmd = ConstantValue.FIND_ALL_ROLES_REQUEST)
    NettyResponse findAllRolesRequest(NettyRequest nettyRequest, Channel channel) throws InvalidProtocolBufferException;
}
