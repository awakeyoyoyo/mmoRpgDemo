package com.liqihao.service;

import com.liqihao.commons.RpgServerException;
import com.liqihao.pojo.bean.roleBean.MmoSimpleRole;
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
     * @throws RpgServerException
     */
    void copySceneMessageRequest(CopySceneModel.CopySceneModelMessage myMessage, MmoSimpleRole mmoSimpleRole) throws RpgServerException;

    /**
     * 进入副本请求
     * @param myMessage
     * @param mmoSimpleRole
     * @throws RpgServerException
     */
    void enterCopySceneRequest(CopySceneModel.CopySceneModelMessage myMessage, MmoSimpleRole mmoSimpleRole) throws RpgServerException;

    /**
     * 离开副本请求
     * @param myMessage
     * @param mmoSimpleRole
     * @throws RpgServerException
     */
    void exitCopySceneRequest(CopySceneModel.CopySceneModelMessage myMessage, MmoSimpleRole mmoSimpleRole) throws RpgServerException;

    /**
     * 创建副本
     * @param myMessage
     * @param mmoSimpleRole
     * @throws RpgServerException
     */
    void createCopySceneBeanRequest(CopySceneModel.CopySceneModelMessage myMessage, MmoSimpleRole mmoSimpleRole) throws RpgServerException;
}
