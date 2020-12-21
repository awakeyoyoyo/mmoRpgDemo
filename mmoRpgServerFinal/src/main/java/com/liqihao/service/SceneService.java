package com.liqihao.service;

import com.google.protobuf.InvalidProtocolBufferException;
import com.liqihao.annotation.HandlerCmdTag;
import com.liqihao.annotation.HandlerServiceTag;
import com.liqihao.commons.ConstantValue;
import com.liqihao.commons.NettyRequest;
import com.liqihao.commons.NettyResponse;
import io.netty.channel.Channel;

public interface SceneService {
    void askCanRequest(NettyRequest nettyRequest,Channel channel) throws InvalidProtocolBufferException;
    void wentRequest(NettyRequest nettyRequest,Channel channel) throws InvalidProtocolBufferException;
    void findAllRolesRequest(NettyRequest nettyRequest, Channel channel) throws InvalidProtocolBufferException;
    void talkNpcRequest(NettyRequest nettyRequest, Channel channel) throws InvalidProtocolBufferException;
}
