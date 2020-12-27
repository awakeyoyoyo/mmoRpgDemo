package com.liqihao.service;

import com.google.protobuf.InvalidProtocolBufferException;
import com.liqihao.protobufObject.TeamModel;
import io.netty.channel.Channel;

/**
 * 队伍模块
 * @author lqhao
 */
public interface TeamService {
    /**
     * 申请入队伍的请求
     * @param myMessage
     * @param channel
     * @throws InvalidProtocolBufferException
     */
    void applyForTeamRequest(TeamModel.TeamModelMessage myMessage, Channel channel) throws InvalidProtocolBufferException;
    /**
     * 踢除玩家请求
     * @param myMessage
     * @param channel
     * @throws InvalidProtocolBufferException
     */
    void banPeopleRequest(TeamModel.TeamModelMessage myMessage, Channel channel) throws InvalidProtocolBufferException;
    /**
     * 创建队伍请求
     * @param myMessage
     * @param channel
     * @throws InvalidProtocolBufferException
     */
    void createTeamRequest(TeamModel.TeamModelMessage myMessage, Channel channel) throws InvalidProtocolBufferException;
    /**
     * 解散队伍请求
     * @param myMessage
     * @param channel
     * @throws InvalidProtocolBufferException
     */
    void deleteTeamRequest(TeamModel.TeamModelMessage myMessage, Channel channel) throws InvalidProtocolBufferException;
    /**
     * 用户接受邀请or 队长同意玩家申请请求
     * @param myMessage
     * @param channel
     * @throws InvalidProtocolBufferException
     */
    void entryPeopleRequest(TeamModel.TeamModelMessage myMessage, Channel channel) throws InvalidProtocolBufferException;
    /**
     * 退出队伍请求
     * @param myMessage
     * @param channel
     * @throws InvalidProtocolBufferException
     */
    void exitTeamRequest(TeamModel.TeamModelMessage myMessage, Channel channel) throws InvalidProtocolBufferException;
    /**
     * 邀请玩家入队伍请求
     * @param myMessage
     * @param channel
     * @throws InvalidProtocolBufferException
     */
    void invitePeopleRequest(TeamModel.TeamModelMessage myMessage, Channel channel) throws InvalidProtocolBufferException;
    /**
     * 拒绝玩家队伍申请
     * @param myMessage
     * @param channel
     * @throws InvalidProtocolBufferException
     */
    void refuseApplyRequest(TeamModel.TeamModelMessage myMessage, Channel channel) throws InvalidProtocolBufferException;
    /**
     * 拒绝玩家队伍邀请
     * @param myMessage
     * @param channel
     * @throws InvalidProtocolBufferException
     */
    void refuseInviteRequest(TeamModel.TeamModelMessage myMessage, Channel channel) throws InvalidProtocolBufferException;

    /**
     * 队伍申请的详细信息
     * @param myMessage
     * @param channel
     * @throws InvalidProtocolBufferException
     */
    void applyMessageRequest(TeamModel.TeamModelMessage myMessage, Channel channel) throws InvalidProtocolBufferException;

    /**
     * 角色被邀请的信息
     * @param myMessage
     * @param channel
     * @throws InvalidProtocolBufferException
     */
    void inviteMessage(TeamModel.TeamModelMessage myMessage, Channel channel) throws InvalidProtocolBufferException;
    /**
     * 获取队伍信息
     * @param myMessage
     * @param channel
     * @throws InvalidProtocolBufferException
     */
    void teamMessageRequest(TeamModel.TeamModelMessage myMessage, Channel channel) throws InvalidProtocolBufferException;
}
