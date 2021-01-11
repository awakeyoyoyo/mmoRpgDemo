package com.liqihao.service;

import com.google.protobuf.InvalidProtocolBufferException;
import com.liqihao.commons.RpgServerException;
import com.liqihao.pojo.bean.roleBean.MmoSimpleRole;
import com.liqihao.protobufObject.SceneModel;

/**
 * 场景模块
 * @author lqhao
 */
public interface SceneService {
    /**
     * 前往场景
     * @param myMessage
     * @param mmoSimpleRole
     * @throws InvalidProtocolBufferException
     */
    void wentRequest(SceneModel.SceneModelMessage myMessage, MmoSimpleRole mmoSimpleRole) throws InvalidProtocolBufferException, RpgServerException;

    /**
     * 查找当前场景所有角色
     * @param myMessage
     * @param mmoSimpleRole
     * @throws InvalidProtocolBufferException
     */
    void findAllRolesRequest(SceneModel.SceneModelMessage myMessage, MmoSimpleRole mmoSimpleRole) throws InvalidProtocolBufferException;

    /**
     * 与npc聊天
     * @param myMessage
     * @param mmoSimpleRole
     * @throws InvalidProtocolBufferException
     */
    void talkNpcRequest(SceneModel.SceneModelMessage myMessage, MmoSimpleRole mmoSimpleRole) throws InvalidProtocolBufferException, RpgServerException;
}
