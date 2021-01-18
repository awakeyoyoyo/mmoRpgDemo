package com.liqihao.service;

import com.google.protobuf.InvalidProtocolBufferException;
import com.liqihao.commons.NettyResponse;

public interface EmailService {
    void getEmailMessageResponse(NettyResponse nettyResponse) throws InvalidProtocolBufferException;

    void getEmailArticleResponse(NettyResponse nettyResponse) throws InvalidProtocolBufferException;

    void acceptEmailListResponse(NettyResponse nettyResponse) throws InvalidProtocolBufferException;

    void isSendEmailListResponse(NettyResponse nettyResponse) throws InvalidProtocolBufferException;

    void sendEmailResponse(NettyResponse nettyResponse) throws InvalidProtocolBufferException;

    void deleteAcceptEmailResponse(NettyResponse nettyResponse) throws InvalidProtocolBufferException;

    void deleteSendEmailResponse(NettyResponse nettyResponse) throws InvalidProtocolBufferException;

    void getEmailMoneyResponse(NettyResponse nettyResponse) throws InvalidProtocolBufferException;
}
