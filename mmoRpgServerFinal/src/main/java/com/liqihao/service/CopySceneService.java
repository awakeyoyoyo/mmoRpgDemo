package com.liqihao.service;

import com.liqihao.protobufObject.TeamModel;
import io.netty.channel.Channel;

/**
 * 副本模块
 * @author lqhao
 */
public interface CopySceneService {
    /**
     * 询问可以进入的副本信息
     * @param myMessage
     * @param channel
     */
    void askCanCopySceneRequest(TeamModel.TeamModelMessage myMessage, Channel channel);

    /**
     * 副本的详细信息
     * @param myMessage
     * @param channel
     */
    void copySceneMessageRequest(TeamModel.TeamModelMessage myMessage, Channel channel);

    /**
     * 进入副本请求
     * @param myMessage
     * @param channel
     */
    void enterCopySceneRequest(TeamModel.TeamModelMessage myMessage, Channel channel);

    /**
     * 离开副本请求
     * @param myMessage
     * @param channel
     */
    void exitCopySceneRequest(TeamModel.TeamModelMessage myMessage, Channel channel);

    /**
     * 创建副本
     * @param myMessage
     * @param channel
     */
    void createCopySceneBeanRequest(TeamModel.TeamModelMessage myMessage, Channel channel);
}
