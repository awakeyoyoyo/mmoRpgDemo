package com.liqihao.service;

import com.google.protobuf.InvalidProtocolBufferException;
import com.liqihao.commons.NettyResponse;

public interface DealService {
    void askDealResponse(NettyResponse nettyResponse) throws InvalidProtocolBufferException;

    void agreeDealResponse(NettyResponse nettyResponse) throws InvalidProtocolBufferException;

    void refuseDealResponse(NettyResponse nettyResponse) throws InvalidProtocolBufferException;

    void confirmDealResponse(NettyResponse nettyResponse) throws InvalidProtocolBufferException;

    void cancelDealResponse(NettyResponse nettyResponse) throws InvalidProtocolBufferException;

    void getDealMessageResponse(NettyResponse nettyResponse) throws InvalidProtocolBufferException;

    void setDealMoneyResponse(NettyResponse nettyResponse) throws InvalidProtocolBufferException;

    void addDealArticleResponse(NettyResponse nettyResponse) throws InvalidProtocolBufferException;

    void abandonDealArticleResponse(NettyResponse nettyResponse) throws InvalidProtocolBufferException;

    void dealSuccessResponse(NettyResponse nettyResponse) throws InvalidProtocolBufferException;
}
