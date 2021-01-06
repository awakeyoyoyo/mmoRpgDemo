package com.liqihao.service;

import com.google.protobuf.InvalidProtocolBufferException;
import com.liqihao.commons.NettyResponse;

public interface BackPackService {
    void backPackMsgResponse(NettyResponse nettyResponse) throws InvalidProtocolBufferException;

    void useResponse(NettyResponse nettyResponse) throws InvalidProtocolBufferException;

    void abandonResponse(NettyResponse nettyResponse) throws InvalidProtocolBufferException;

    void addArticleResponse(NettyResponse nettyResponse) throws InvalidProtocolBufferException;

    void getArticleFromFloorResponse(NettyResponse nettyResponse) throws InvalidProtocolBufferException;

    void findAllCanResponse(NettyResponse nettyResponse) throws InvalidProtocolBufferException;

    void buyGoodsResponse(NettyResponse nettyResponse) throws InvalidProtocolBufferException;

    void checkMoneyNumberResponse(NettyResponse nettyResponse) throws InvalidProtocolBufferException;

    void findAllGoodsResponse(NettyResponse nettyResponse) throws InvalidProtocolBufferException;
}
