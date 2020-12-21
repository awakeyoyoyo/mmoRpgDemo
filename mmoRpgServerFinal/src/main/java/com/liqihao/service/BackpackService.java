package com.liqihao.service;

import com.google.protobuf.InvalidProtocolBufferException;
import com.liqihao.commons.NettyRequest;
import com.liqihao.commons.NettyResponse;
import io.netty.channel.Channel;

public interface BackpackService {
    void abandonRquest(NettyRequest nettyRequest, Channel channel) throws InvalidProtocolBufferException;
    void backPackMsgRequest(NettyRequest nettyRequest, Channel channel) throws InvalidProtocolBufferException;
    void useRequest(NettyRequest nettyRequest, Channel channel) throws InvalidProtocolBufferException;
    void addArticleRequest(NettyRequest nettyRequest, Channel channel) throws InvalidProtocolBufferException;

}
