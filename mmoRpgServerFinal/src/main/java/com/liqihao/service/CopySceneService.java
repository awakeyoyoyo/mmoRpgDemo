package com.liqihao.service;

import com.liqihao.protobufObject.CopySceneModel;
import com.liqihao.protobufObject.TeamModel;
import io.netty.channel.Channel;

/**
 * 副本模块
 * @author lqhao
 */
public interface CopySceneService {

    /**
     * 副本的详细信息
     * @param myMessage
     * @param channel
     */
    void copySceneMessageRequest(CopySceneModel.CopySceneModelMessage myMessage, Channel channel);

    /**
     * 进入副本请求
     * @param myMessage
     * @param channel
     */
    void enterCopySceneRequest(CopySceneModel.CopySceneModelMessage myMessage, Channel channel);

    /**
     * 离开副本请求
     * @param myMessage
     * @param channel
     */
    void exitCopySceneRequest(CopySceneModel.CopySceneModelMessage myMessage, Channel channel);

    /**
     * 创建副本
     * @param myMessage
     * @param channel
     */
    void createCopySceneBeanRequest(CopySceneModel.CopySceneModelMessage myMessage, Channel channel);
}
