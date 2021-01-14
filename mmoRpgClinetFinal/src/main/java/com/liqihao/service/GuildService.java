package com.liqihao.service;

import com.google.protobuf.InvalidProtocolBufferException;
import com.liqihao.commons.NettyResponse;
import com.liqihao.protobufObject.GuildModel;

public interface GuildService {
    /**
     * 创建工会
     * @param nettyResponse
     * @throws InvalidProtocolBufferException
     */
    void createGuild(NettyResponse nettyResponse) throws InvalidProtocolBufferException;

    /**
     * 加入工会
     * @param nettyResponse
     * @throws InvalidProtocolBufferException
     */
    void joinGuild(NettyResponse nettyResponse) throws InvalidProtocolBufferException;

    /**
     * 设置玩家的职位
     * @param nettyResponse
     * @throws InvalidProtocolBufferException
     */
    void setGuildPosition(NettyResponse nettyResponse) throws InvalidProtocolBufferException;

    /**
     * 退出工会
     * @param nettyResponse
     * @throws InvalidProtocolBufferException
     */
    void outGuild(NettyResponse nettyResponse) throws InvalidProtocolBufferException;

    /**
     * 捐献金币
     * @param nettyResponse
     * @throws InvalidProtocolBufferException
     */
    void contributeMoney(NettyResponse nettyResponse) throws InvalidProtocolBufferException;

    /**
     * 捐赠物品
     * @param nettyResponse
     * @throws InvalidProtocolBufferException
     */
    void contributeArticle(NettyResponse nettyResponse) throws InvalidProtocolBufferException;

    /**
     * 取物品
     * @param nettyResponse
     * @throws InvalidProtocolBufferException
     */
    void getArticle(NettyResponse nettyResponse) throws InvalidProtocolBufferException;


    /**
     * 取钱
     * @param nettyResponse
     * @throws InvalidProtocolBufferException
     */
    void getMoney(NettyResponse nettyResponse) throws InvalidProtocolBufferException;

    /**
     * 获取申请入公会列表
     * @param nettyResponse
     * @throws InvalidProtocolBufferException
     */
    void getGuildApply(NettyResponse nettyResponse) throws InvalidProtocolBufferException;
    /**
     * 获取公会信息
     * @param nettyResponse
     * @throws InvalidProtocolBufferException
     */
    void getGuildBean(NettyResponse nettyResponse) throws InvalidProtocolBufferException;

    /**
     * 接收申请入会
     * @param nettyResponse
     * @throws InvalidProtocolBufferException
     */
    void agreeGuildApply(NettyResponse nettyResponse) throws InvalidProtocolBufferException;

    /**
     * 拒绝申请入会
     * @param nettyResponse
     * @throws InvalidProtocolBufferException
     */
    void refuseGuildApply(NettyResponse nettyResponse) throws InvalidProtocolBufferException;

    /**
     * 申请处理结果响应
     * @param nettyResponse
     */
    void guildApplyResponse(NettyResponse nettyResponse) throws InvalidProtocolBufferException;
}
