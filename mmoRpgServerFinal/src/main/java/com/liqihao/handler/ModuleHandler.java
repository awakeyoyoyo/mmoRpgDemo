package com.liqihao.handler;

import com.google.protobuf.InvalidProtocolBufferException;
import com.liqihao.commons.NettyRequest;
import com.liqihao.commons.NettyResponse;
import io.netty.channel.Channel;

public interface ModuleHandler {
    NettyResponse handler(NettyRequest nettyRequest, Channel channel) throws InvalidProtocolBufferException;
}
