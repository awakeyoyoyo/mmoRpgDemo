package com.liqihao.service;

import com.google.protobuf.InvalidProtocolBufferException;
import com.liqihao.commons.NettyResponse;

public interface CopySceneService {
    void askCanCopySceneResponse(NettyResponse nettyResponse) throws InvalidProtocolBufferException;

    void copySceneMessageResponse(NettyResponse nettyResponse)throws InvalidProtocolBufferException;

    void enterCopySceneResponse(NettyResponse nettyResponse)throws InvalidProtocolBufferException;

    void exitCopySceneResponse(NettyResponse nettyResponse)throws InvalidProtocolBufferException;

    void createCopySceneResponse(NettyResponse nettyResponse)throws InvalidProtocolBufferException;
}
