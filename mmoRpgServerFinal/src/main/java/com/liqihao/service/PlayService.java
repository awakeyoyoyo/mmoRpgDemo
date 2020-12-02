package com.liqihao.service;

import com.google.protobuf.InvalidProtocolBufferException;
import com.liqihao.commons.NettyRequest;
import com.liqihao.commons.NettyResponse;

public interface PlayService {
    NettyResponse registerRequest(NettyRequest nettyRequest) throws InvalidProtocolBufferException;

    NettyResponse loginRequest(NettyRequest nettyRequest) throws InvalidProtocolBufferException;

    NettyResponse logoutRequest(NettyRequest nettyRequest) throws InvalidProtocolBufferException;
}
