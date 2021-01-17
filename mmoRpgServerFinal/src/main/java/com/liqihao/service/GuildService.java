package com.liqihao.service;

import com.google.protobuf.InvalidProtocolBufferException;
import com.liqihao.commons.RpgServerException;
import com.liqihao.pojo.bean.roleBean.MmoSimpleRole;
import com.liqihao.protobufObject.GuildModel;


/**
 * 工会模块
 * @author lqhao
 */
public interface GuildService {

    /**
     * 创建工会
     *
     * @param myMessage
     * @param mmoSimpleRole
     * @throws InvalidProtocolBufferException
     */
    void createGuild(GuildModel.GuildModelMessage myMessage, MmoSimpleRole mmoSimpleRole) throws InvalidProtocolBufferException, RpgServerException;

    /**
     * 加入工会
     *
     * @param myMessage
     * @param mmoSimpleRole
     * @throws InvalidProtocolBufferException
     */
    void joinGuild(GuildModel.GuildModelMessage myMessage, MmoSimpleRole mmoSimpleRole) throws InvalidProtocolBufferException, RpgServerException;

    /**
     * 设置玩家的职位
     *
     * @param myMessage
     * @param mmoSimpleRole
     * @throws InvalidProtocolBufferException
     */
    void setGuildPosition(GuildModel.GuildModelMessage myMessage, MmoSimpleRole mmoSimpleRole) throws InvalidProtocolBufferException, RpgServerException;

    /**
     * 退出工会
     *
     * @param myMessage
     * @param mmoSimpleRole
     * @throws InvalidProtocolBufferException
     * @throws RpgServerException
     */
    void outGuild(GuildModel.GuildModelMessage myMessage, MmoSimpleRole mmoSimpleRole) throws InvalidProtocolBufferException, RpgServerException;

    /**
     * 捐献金币
     *
     * @param myMessage
     * @param mmoSimpleRole
     * @throws InvalidProtocolBufferException
     */
    void contributeMoney(GuildModel.GuildModelMessage myMessage, MmoSimpleRole mmoSimpleRole) throws InvalidProtocolBufferException, RpgServerException;

    /**
     * 捐赠物品
     *
     * @param myMessage
     * @param mmoSimpleRole
     * @throws InvalidProtocolBufferException
     */
    void contributeArticle(GuildModel.GuildModelMessage myMessage, MmoSimpleRole mmoSimpleRole) throws InvalidProtocolBufferException, RpgServerException;

    /**
     * 取物品
     *
     * @param myMessage
     * @param mmoSimpleRole
     * @throws InvalidProtocolBufferException
     */
    void getArticle(GuildModel.GuildModelMessage myMessage, MmoSimpleRole mmoSimpleRole) throws InvalidProtocolBufferException, RpgServerException;


    /**
     * 取钱
     *
     * @param myMessage
     * @param mmoSimpleRole
     * @throws InvalidProtocolBufferException
     */
    void getMoney(GuildModel.GuildModelMessage myMessage, MmoSimpleRole mmoSimpleRole) throws InvalidProtocolBufferException, RpgServerException;

    /**
     * 获取申请入公会列别
     * @param myMessage
     * @param mmoSimpleRole
     * @throws InvalidProtocolBufferException
     * @throws RpgServerException
     */
    void getGuildApplyList(GuildModel.GuildModelMessage myMessage, MmoSimpleRole mmoSimpleRole) throws InvalidProtocolBufferException, RpgServerException;

    /**
     * 获取公会信息
     * @param myMessage
     * @param mmoSimpleRole
     * @throws InvalidProtocolBufferException
     * @throws RpgServerException
     */
    void getGuildBean(GuildModel.GuildModelMessage myMessage, MmoSimpleRole mmoSimpleRole) throws InvalidProtocolBufferException, RpgServerException;

    /**
     * 接收申请入会
     * @param myMessage
     * @param mmoSimpleRole
     * @throws InvalidProtocolBufferException
     * @throws RpgServerException
     */
    void agreeGuildApply(GuildModel.GuildModelMessage myMessage, MmoSimpleRole mmoSimpleRole) throws InvalidProtocolBufferException, RpgServerException;


    /**
     * 拒绝申请入会
     * @param myMessage
     * @param mmoSimpleRole
     * @throws InvalidProtocolBufferException
     * @throws RpgServerException
     */
    void refuseGuildApply(GuildModel.GuildModelMessage myMessage, MmoSimpleRole mmoSimpleRole) throws InvalidProtocolBufferException, RpgServerException;
    /**
     * 获取公会仓库信息
     * @param myMessage
     * @param mmoSimpleRole
     * @throws InvalidProtocolBufferException
     * @throws RpgServerException
     */
    void getGuildWareHouseMessage(GuildModel.GuildModelMessage myMessage, MmoSimpleRole mmoSimpleRole) throws InvalidProtocolBufferException, RpgServerException;


}