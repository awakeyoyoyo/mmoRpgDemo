package com.liqihao.service;

import com.google.protobuf.InvalidProtocolBufferException;
import com.liqihao.commons.RpgServerException;
import com.liqihao.pojo.bean.roleBean.MmoSimpleRole;
import com.liqihao.protobufObject.EquipmentModel;
import com.liqihao.protobufObject.FriendModel;

/**
 * 好友模块
 * @author lqhao
 */
public interface FriendService {
    /**
     * 申请加好友
     * @param myMessage
     * @param mmoSimpleRole
     * @throws InvalidProtocolBufferException
     * @throws RpgServerException
     */
    void applyFriendRequest(FriendModel.FriendModelMessage myMessage, MmoSimpleRole mmoSimpleRole) throws InvalidProtocolBufferException, RpgServerException;
    /**
     * 通过申请加好友
     * @param myMessage
     * @param mmoSimpleRole
     * @throws InvalidProtocolBufferException
     * @throws RpgServerException
     */
    void agreeFriendRequest(FriendModel.FriendModelMessage myMessage, MmoSimpleRole mmoSimpleRole) throws InvalidProtocolBufferException, RpgServerException;
    /**
     * 拒绝加好友
     * @param myMessage
     * @param mmoSimpleRole
     * @throws InvalidProtocolBufferException
     * @throws RpgServerException
     */
    void refuseFriendRequest(FriendModel.FriendModelMessage myMessage, MmoSimpleRole mmoSimpleRole) throws InvalidProtocolBufferException, RpgServerException;

    /**
     * 删除好友
     * @param myMessage
     * @param mmoSimpleRole
     * @throws InvalidProtocolBufferException
     * @throws RpgServerException
     */
    void reduceFriendRequest(FriendModel.FriendModelMessage myMessage, MmoSimpleRole mmoSimpleRole) throws InvalidProtocolBufferException, RpgServerException;

    /**
     * 好友列表
     * @param myMessage
     * @param mmoSimpleRole
     * @throws InvalidProtocolBufferException
     * @throws RpgServerException
     */
    void getFriendsRequest(FriendModel.FriendModelMessage myMessage, MmoSimpleRole mmoSimpleRole) throws InvalidProtocolBufferException, RpgServerException;
    /**
     * 好友申请列表
     * @param myMessage
     * @param mmoSimpleRole
     * @throws InvalidProtocolBufferException
     * @throws RpgServerException
     */
    void friendApplyListRequest(FriendModel.FriendModelMessage myMessage, MmoSimpleRole mmoSimpleRole) throws InvalidProtocolBufferException, RpgServerException;
}
