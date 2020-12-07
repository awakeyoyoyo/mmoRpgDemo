package com.liqihao.service;

import com.google.protobuf.InvalidProtocolBufferException;
import com.liqihao.annotation.HandlerServiceTag;
import com.liqihao.commons.ConstantValue;
import com.liqihao.commons.NettyRequest;
import com.liqihao.commons.NettyResponse;
import io.netty.channel.Channel;

public interface PlayService {
    @HandlerServiceTag(cmd = ConstantValue.REGISTER_REQUEST)
    NettyResponse registerRequest(NettyRequest nettyRequest, Channel channel) throws InvalidProtocolBufferException;
    @HandlerServiceTag(cmd = ConstantValue.LOGIN_REQUEST)
    NettyResponse loginRequest(NettyRequest nettyRequest,Channel channel) throws InvalidProtocolBufferException;
    @HandlerServiceTag(cmd = ConstantValue.LOGOUT_REQUEST)
    NettyResponse logoutRequest(NettyRequest nettyRequest,Channel channel) throws InvalidProtocolBufferException;
}
