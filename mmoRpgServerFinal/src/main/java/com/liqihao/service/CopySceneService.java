package com.liqihao.service;

import com.liqihao.pojo.bean.MmoSimpleRole;
import com.liqihao.protobufObject.CopySceneModel;

/**
 * 副本模块
 * @author lqhao
 */
public interface CopySceneService {

    /**
     * 副本的详细信息
     * @param myMessage
     * @param mmoSimpleRole
     */
    void copySceneMessageRequest(CopySceneModel.CopySceneModelMessage myMessage, MmoSimpleRole mmoSimpleRole);

    /**
     * 进入副本请求
     * @param myMessage
     * @param mmoSimpleRole
     */
    void enterCopySceneRequest(CopySceneModel.CopySceneModelMessage myMessage, MmoSimpleRole mmoSimpleRole);

    /**
     * 离开副本请求
     * @param myMessage
     * @param mmoSimpleRole
     */
    void exitCopySceneRequest(CopySceneModel.CopySceneModelMessage myMessage, MmoSimpleRole mmoSimpleRole);

    /**
     * 创建副本
     * @param myMessage
     * @param mmoSimpleRole
     */
    void createCopySceneBeanRequest(CopySceneModel.CopySceneModelMessage myMessage, MmoSimpleRole mmoSimpleRole);
}
