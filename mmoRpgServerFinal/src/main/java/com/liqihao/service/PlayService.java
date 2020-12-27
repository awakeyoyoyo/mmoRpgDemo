package com.liqihao.service;

import com.google.protobuf.InvalidProtocolBufferException;

import com.liqihao.commons.NettyRequest;
import com.liqihao.commons.NettyResponse;
import com.liqihao.protobufObject.PlayModel;
import io.netty.channel.Channel;
/**
 * 玩家模块
 * @author lqhao
 */
public interface PlayService {
    /**
     * 注册
     * @param myMessage
     * @param channel
     * @throws InvalidProtocolBufferException
     */
    void registerRequest(PlayModel.PlayModelMessage myMessage, Channel channel) throws InvalidProtocolBufferException;

    /**
     * 登录
     * @param myMessage
     * @param channel
     * @throws InvalidProtocolBufferException
     */
    void loginRequest(PlayModel.PlayModelMessage myMessage,Channel channel) throws InvalidProtocolBufferException;

    /**
     * 登出
     * @param myMessage
     * @param channel
     * @throws InvalidProtocolBufferException
     */
    void logoutRequest(PlayModel.PlayModelMessage myMessage,Channel channel) throws InvalidProtocolBufferException;

    /**
     * 使用技能
     * @param myMessage
     * @param channel
     * @throws InvalidProtocolBufferException
     */
    void useSkillRequest(PlayModel.PlayModelMessage myMessage,Channel channel) throws InvalidProtocolBufferException;
}
