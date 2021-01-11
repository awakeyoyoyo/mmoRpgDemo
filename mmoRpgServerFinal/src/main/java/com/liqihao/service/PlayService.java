package com.liqihao.service;

import com.google.protobuf.InvalidProtocolBufferException;

import com.liqihao.commons.RpgServerException;
import com.liqihao.pojo.bean.roleBean.MmoSimpleRole;
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
    void loginRequest(PlayModel.PlayModelMessage myMessage, Channel channel) throws InvalidProtocolBufferException, RpgServerException;

    /**
     * 登出
     * @param myMessage
     * @param channel
     * @throws InvalidProtocolBufferException
     */
    void logoutRequest(PlayModel.PlayModelMessage myMessage, Channel channel) throws Exception;

    /**
     * 使用技能
     * @param myMessage
     * @param mmoSimpleRole
     * @throws Exception
     */
    void useSkillRequest(PlayModel.PlayModelMessage myMessage, MmoSimpleRole mmoSimpleRole) throws Exception;


//    /**
//     * 解除召唤
//     * @param myMessage
//     * @param mmoSimpleRole
//     * @throws Exception
//     */
//    void reduceCallRequest(PlayModel.PlayModelMessage myMessage, MmoSimpleRole mmoSimpleRole) throws Exception;
}
