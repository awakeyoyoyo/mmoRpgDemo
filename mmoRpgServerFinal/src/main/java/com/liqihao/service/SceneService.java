package com.liqihao.service;

import com.google.protobuf.InvalidProtocolBufferException;
import com.liqihao.commons.NettyRequest;
import com.liqihao.commons.NettyResponse;

public interface SceneService {
    NettyResponse askCanRequest(NettyRequest nettyRequest) throws InvalidProtocolBufferException;

    NettyResponse wentRequest(NettyRequest nettyRequest) throws InvalidProtocolBufferException;

    NettyResponse whereRequest(NettyRequest nettyRequest) throws InvalidProtocolBufferException;

    NettyResponse findAllRolesRequest(NettyRequest nettyRequest) throws InvalidProtocolBufferException;
}
