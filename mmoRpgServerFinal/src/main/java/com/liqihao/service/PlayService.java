package com.liqihao.service;

import com.google.protobuf.InvalidProtocolBufferException;

import com.liqihao.commons.NettyRequest;
import com.liqihao.commons.NettyResponse;
import io.netty.channel.Channel;
/**
 * 玩家模块
 * @author lqhao
 */
public interface PlayService {
    /**
     * 注册
     * @param nettyRequest
     * @param channel
     * @throws InvalidProtocolBufferException
     */
    void registerRequest(NettyRequest nettyRequest, Channel channel) throws InvalidProtocolBufferException;

    /**
     * 登录
     * @param nettyRequest
     * @param channel
     * @throws InvalidProtocolBufferException
     */
    void loginRequest(NettyRequest nettyRequest,Channel channel) throws InvalidProtocolBufferException;

    /**
     * 登出
     * @param nettyRequest
     * @param channel
     * @throws InvalidProtocolBufferException
     */
    void logoutRequest(NettyRequest nettyRequest,Channel channel) throws InvalidProtocolBufferException;

    /**
     * 使用技能
     * @param nettyRequest
     * @param channel
     * @throws InvalidProtocolBufferException
     */
    void useSkillRequest(NettyRequest nettyRequest,Channel channel) throws InvalidProtocolBufferException;
}
