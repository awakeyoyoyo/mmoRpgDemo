package com.liqihao.service;

import com.google.protobuf.InvalidProtocolBufferException;
import com.liqihao.commons.RpgServerException;
import com.liqihao.pojo.baseMessage.TaskMessage;
import com.liqihao.pojo.bean.roleBean.MmoSimpleRole;
import com.liqihao.protobufObject.TaskModel;
import com.liqihao.protobufObject.TeamModel;

/**
 * 任务系统
 * @author lqhao
 */
public interface TaskService {
    /**
     * 查看自身任务 and 成就列表
     * @param myMessage
     * @param mmoSimpleRole
     * @throws InvalidProtocolBufferException
     * @throws RpgServerException
     */
    void getPeopleTaskRequest(TaskModel.TaskModelMessage myMessage, MmoSimpleRole mmoSimpleRole) throws InvalidProtocolBufferException, RpgServerException;

    /**
     * 查看可接受的任务
     * @param myMessage
     * @param mmoSimpleRole
     * @throws InvalidProtocolBufferException
     * @throws RpgServerException
     */
    void getCanAcceptTaskRequest(TaskModel.TaskModelMessage myMessage, MmoSimpleRole mmoSimpleRole) throws InvalidProtocolBufferException, RpgServerException;

    /**
     * 接收任务
     * @param myMessage
     * @param mmoSimpleRole
     * @throws InvalidProtocolBufferException
     * @throws RpgServerException
     */
    void acceptTaskRequest(TaskModel.TaskModelMessage myMessage, MmoSimpleRole mmoSimpleRole) throws InvalidProtocolBufferException, RpgServerException;

    /**
     * 放弃任务
     * @param myMessage
     * @param mmoSimpleRole
     * @throws InvalidProtocolBufferException
     * @throws RpgServerException
     */
    void abandonTaskRequest(TaskModel.TaskModelMessage myMessage, MmoSimpleRole mmoSimpleRole) throws InvalidProtocolBufferException, RpgServerException;
}
