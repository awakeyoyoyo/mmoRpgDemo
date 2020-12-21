package com.liqihao.service;

import com.google.protobuf.InvalidProtocolBufferException;

import com.liqihao.commons.NettyRequest;
import com.liqihao.commons.NettyResponse;
import io.netty.channel.Channel;
public interface PlayService {
    void registerRequest(NettyRequest nettyRequest, Channel channel) throws InvalidProtocolBufferException;
    void loginRequest(NettyRequest nettyRequest,Channel channel) throws InvalidProtocolBufferException;
    void logoutRequest(NettyRequest nettyRequest,Channel channel) throws InvalidProtocolBufferException;
    void useSkillRequest(NettyRequest nettyRequest,Channel channel) throws InvalidProtocolBufferException;
}
