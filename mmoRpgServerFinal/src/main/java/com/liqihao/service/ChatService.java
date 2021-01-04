package com.liqihao.service;

import com.liqihao.protobufObject.ChatModel;
import com.liqihao.protobufObject.CopySceneModel;
import io.netty.channel.Channel;

/**
 * 聊天模块
 * @author lqhao
 */
public interface ChatService {
    /**
     * 发送给全服
     * @param myMessage
     * @param channel
     */
    void sendToAllRequest(ChatModel.ChatModelMessage myMessage, Channel channel);

    /**
     * 私聊
     * @param myMessage
     * @param channel
     */
    void sendToOneRequest(ChatModel.ChatModelMessage myMessage, Channel channel);
    /**
     * 队伍聊天
     * @param myMessage
     * @param channel
     */
    void sendToTeamRequest(ChatModel.ChatModelMessage myMessage, Channel channel) throws Exception;
    /**
     * 场景聊天
     * @param myMessage
     * @param channel
     */
    void sendToSceneRequest(ChatModel.ChatModelMessage myMessage, Channel channel) throws Exception;
}
