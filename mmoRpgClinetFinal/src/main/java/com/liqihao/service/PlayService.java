package com.liqihao.service;

import com.google.protobuf.InvalidProtocolBufferException;
import com.liqihao.commons.NettyResponse;

public interface PlayService {
    void loginResponse(NettyResponse nettyResponse) throws InvalidProtocolBufferException;

    void registerResponse(NettyResponse nettyResponse) throws InvalidProtocolBufferException;

    void logoutResponse(NettyResponse nettyResponse) throws InvalidProtocolBufferException;

    void useSkillResponse(NettyResponse nettyResponse) throws InvalidProtocolBufferException;

    void damagesNoticeResponse(NettyResponse nettyResponse)throws InvalidProtocolBufferException;

    void upLevelResponse(NettyResponse nettyResponse) throws InvalidProtocolBufferException;
}
