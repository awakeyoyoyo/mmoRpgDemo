package com.liqihao.service;

import com.liqihao.pojo.bean.MmoSimpleRole;
import com.liqihao.protobufObject.ChatModel;

/**
 * 聊天模块
 * @author lqhao
 */
public interface ChatService {
    /**
     * 发送给全服
     * @param myMessage
     * @param mmoSimpleRole
     */
    void sendToAllRequest(ChatModel.ChatModelMessage myMessage, MmoSimpleRole mmoSimpleRole);

    /**
     * 私聊
     * @param myMessage
     * @param mmoSimpleRole
     */
    void sendToOneRequest(ChatModel.ChatModelMessage myMessage, MmoSimpleRole mmoSimpleRole);
    /**
     * 队伍聊天
     * @param myMessage
     * @param mmoSimpleRole
     */
    void sendToTeamRequest(ChatModel.ChatModelMessage myMessage, MmoSimpleRole mmoSimpleRole) throws Exception;
    /**
     * 场景聊天
     * @param myMessage
     * @param mmoSimpleRole
     */
    void sendToSceneRequest(ChatModel.ChatModelMessage myMessage, MmoSimpleRole mmoSimpleRole) throws Exception;
}
