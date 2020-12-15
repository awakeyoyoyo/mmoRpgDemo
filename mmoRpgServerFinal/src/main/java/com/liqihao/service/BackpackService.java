package com.liqihao.service;

import com.google.protobuf.InvalidProtocolBufferException;
import com.liqihao.commons.NettyRequest;
import com.liqihao.commons.NettyResponse;
import io.netty.channel.Channel;

public interface BackpackService {
    NettyResponse abandonRquest(NettyRequest nettyRequest, Channel channel) throws InvalidProtocolBufferException;
    NettyResponse backPackMsgRequest(NettyRequest nettyRequest, Channel channel) throws InvalidProtocolBufferException;
    NettyResponse useRequest(NettyRequest nettyRequest, Channel channel) throws InvalidProtocolBufferException;

}
