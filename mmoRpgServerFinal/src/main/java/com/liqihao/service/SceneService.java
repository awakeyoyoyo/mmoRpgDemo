package com.liqihao.service;

import com.google.protobuf.InvalidProtocolBufferException;
import com.liqihao.commons.NettyRequest;
import com.liqihao.commons.NettyResponse;
import io.netty.channel.Channel;

public interface SceneService {
    NettyResponse askCanRequest(NettyRequest nettyRequest,Channel channel) throws InvalidProtocolBufferException;

    NettyResponse wentRequest(NettyRequest nettyRequest,Channel channel) throws InvalidProtocolBufferException;

    NettyResponse findAllRolesRequest(NettyRequest nettyRequest, Channel channel) throws InvalidProtocolBufferException;
}
