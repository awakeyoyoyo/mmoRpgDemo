package com.liqihao.service;

import com.google.protobuf.InvalidProtocolBufferException;
import com.liqihao.commons.NettyResponse;

/**
 * 任务系统
 * @author lqhao
 */
public interface TaskService {
    /**
     * 查看自身任务 and 成就列表
     * @throws InvalidProtocolBufferException
     * @param nettyResponse
     */
    void getPeopleTaskRequest(NettyResponse nettyResponse) throws InvalidProtocolBufferException;

    /**
     * 查看可接受的任务
     * @throws InvalidProtocolBufferException
     * @param nettyResponse
     */
    void getCanAcceptTaskRequest(NettyResponse nettyResponse) throws InvalidProtocolBufferException;

    /**
     * 接收任务
     * @throws InvalidProtocolBufferException
     * @param nettyResponse
     */
    void acceptTaskRequest(NettyResponse nettyResponse) throws InvalidProtocolBufferException;

    /**
     * 放弃任务
     * @throws InvalidProtocolBufferException
     * @param nettyResponse
     */
    void abandonTaskRequest(NettyResponse nettyResponse) throws InvalidProtocolBufferException;

    /**
     * 任务完成
     * @throws InvalidProtocolBufferException
     * @param nettyResponse
     */
    void finishTaskRequest(NettyResponse nettyResponse) throws InvalidProtocolBufferException;
}
