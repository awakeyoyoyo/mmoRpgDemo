package com.liqihao.service;

import com.google.protobuf.InvalidProtocolBufferException;
import com.liqihao.annotation.HandlerCmdTag;
import com.liqihao.annotation.HandlerServiceTag;
import com.liqihao.commons.ConstantValue;
import com.liqihao.commons.NettyRequest;
import com.liqihao.commons.NettyResponse;
import io.netty.channel.Channel;

/**
 * 场景模块
 * @author lqhao
 */
public interface SceneService {
    /**
     * 查看能前往的场景
     * @param nettyRequest
     * @param channel
     * @throws InvalidProtocolBufferException
     */
    void askCanRequest(NettyRequest nettyRequest,Channel channel) throws InvalidProtocolBufferException;

    /**
     * 前往场景
     * @param nettyRequest
     * @param channel
     * @throws InvalidProtocolBufferException
     */
    void wentRequest(NettyRequest nettyRequest,Channel channel) throws InvalidProtocolBufferException;

    /**
     * 查找当前场景所有角色
     * @param nettyRequest
     * @param channel
     * @throws InvalidProtocolBufferException
     */
    void findAllRolesRequest(NettyRequest nettyRequest, Channel channel) throws InvalidProtocolBufferException;

    /**
     * 与npc聊天
     * @param nettyRequest
     * @param channel
     * @throws InvalidProtocolBufferException
     */
    void talkNpcRequest(NettyRequest nettyRequest, Channel channel) throws InvalidProtocolBufferException;
}
