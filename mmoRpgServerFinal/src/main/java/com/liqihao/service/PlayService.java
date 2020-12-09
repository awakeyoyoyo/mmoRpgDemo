package com.liqihao.service;

import com.google.protobuf.InvalidProtocolBufferException;

import com.liqihao.commons.NettyRequest;
import com.liqihao.commons.NettyResponse;
import io.netty.channel.Channel;
public interface PlayService {
    NettyResponse registerRequest(NettyRequest nettyRequest, Channel channel) throws InvalidProtocolBufferException;
    NettyResponse loginRequest(NettyRequest nettyRequest,Channel channel) throws InvalidProtocolBufferException;
    NettyResponse logoutRequest(NettyRequest nettyRequest,Channel channel) throws InvalidProtocolBufferException;
    NettyResponse useSkillRequest(NettyRequest nettyRequest,Channel channel) throws InvalidProtocolBufferException;
}
