package com.liqihao.service;

import com.google.protobuf.InvalidProtocolBufferException;
import com.liqihao.commons.NettyResponse;

public interface SceneService {
    void askCanResponse(NettyResponse nettyResponse) throws InvalidProtocolBufferException;

    void wentResponse(NettyResponse nettyResponse) throws InvalidProtocolBufferException;

    void findAllRolesResponse(NettyResponse nettyResponse) throws InvalidProtocolBufferException;

    void talkNPCResponse(NettyResponse nettyResponse) throws InvalidProtocolBufferException;
}
