package com.liqihao.service.impl;

import com.google.protobuf.InvalidProtocolBufferException;
import com.googlecode.protobuf.format.JsonFormat;
import com.liqihao.Cache.OnlineRoleMessageCache;
import com.liqihao.annotation.HandlerCmdTag;
import com.liqihao.annotation.HandlerServiceTag;
import com.liqihao.commons.ConstantValue;
import com.liqihao.commons.NettyResponse;
import com.liqihao.commons.RpgServerException;
import com.liqihao.commons.StateCode;
import com.liqihao.pojo.bean.friendBean.FriendApplyBean;
import com.liqihao.pojo.bean.friendBean.FriendBean;
import com.liqihao.pojo.bean.roleBean.MmoSimpleRole;
import com.liqihao.protobufObject.FriendModel;
import com.liqihao.provider.FriendServiceProvider;
import com.liqihao.service.FriendService;
import com.liqihao.util.NotificationUtil;
import io.netty.channel.Channel;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Classname FriendServiceImpl
 * @Description 好友服务
 * @Author lqhao
 * @Date 2021/1/27 10:41
 * @Version 1.0
 */
@Service
@HandlerServiceTag(protobufModel = "FriendModel$FriendModelMessage")
public class FriendServiceImpl implements FriendService {
    @Override
    @HandlerCmdTag(cmd = ConstantValue.APPLY_FRIEND_REQUEST, module = ConstantValue.FRIEND_MODULE)
    public void applyFriendRequest(FriendModel.FriendModelMessage myMessage, MmoSimpleRole mmoSimpleRole) throws InvalidProtocolBufferException, RpgServerException {
        Integer roleId=myMessage.getApplyFriendRequest().getRoleId();
        MmoSimpleRole role= OnlineRoleMessageCache.getInstance().get(roleId);
        if (role==null){
            throw new RpgServerException(StateCode.FAIL,"该用户不存在亦或者不在线");
        }
        if (mmoSimpleRole.getFriends().contains(roleId)){
            throw new RpgServerException(StateCode.FAIL,"已经有该玩家好友");
        }
        FriendServiceProvider.addApplyFriend(role,mmoSimpleRole.getId());
        //protobuf
        FriendModel.FriendModelMessage messageData= FriendModel.FriendModelMessage.newBuilder()
                .setDataType( FriendModel.FriendModelMessage.DateType.ApplyFriendResponse)
                .setApplyFriendResponse(FriendModel.ApplyFriendResponse.newBuilder().build())
                .build();
        NettyResponse nettyResponse=new NettyResponse();
        nettyResponse.setCmd(ConstantValue.APPLY_FRIEND_RESPONSE);
        nettyResponse.setStateCode(StateCode.SUCCESS);
        nettyResponse.setData(messageData.toByteArray());
        Channel channel=mmoSimpleRole.getChannel();
        if (channel!=null) {
            String json= JsonFormat.printToString(messageData);
            NotificationUtil.sendMessage(channel,nettyResponse,json);
        }
    }

    @Override
    @HandlerCmdTag(cmd = ConstantValue.AGREE_FRIEND_REQUEST, module = ConstantValue.FRIEND_MODULE)
    public void agreeFriendRequest(FriendModel.FriendModelMessage myMessage, MmoSimpleRole mmoSimpleRole) throws InvalidProtocolBufferException, RpgServerException {
        Integer applyId=myMessage.getAgreeFriendRequest().getApplyId();
        if (!mmoSimpleRole.getFriendApplyBeanHashMap().containsKey(applyId)){
            throw new RpgServerException(StateCode.FAIL,"不存在该好友申请");
        }
        if (mmoSimpleRole.getFriends().contains(mmoSimpleRole.getFriendApplyBeanHashMap().get(applyId).getRoleId())){
            throw new RpgServerException(StateCode.FAIL,"已经有该玩家好友");
        }
        FriendServiceProvider.agreeApplyFriend(mmoSimpleRole,applyId);
        //protobuf
        FriendModel.FriendModelMessage messageData= FriendModel.FriendModelMessage.newBuilder()
                .setDataType( FriendModel.FriendModelMessage.DateType.AgreeFriendResponse)
                .setAgreeFriendResponse(FriendModel.AgreeFriendResponse.newBuilder().build())
                .build();
        NettyResponse nettyResponse=new NettyResponse();
        nettyResponse.setCmd(ConstantValue.AGREE_FRIEND_RESPONSE);
        nettyResponse.setStateCode(StateCode.SUCCESS);
        nettyResponse.setData(messageData.toByteArray());
        Channel channel=mmoSimpleRole.getChannel();
        if (channel!=null) {
            String json= JsonFormat.printToString(messageData);
            NotificationUtil.sendMessage(channel,nettyResponse,json);
        }
    }

    @Override
    @HandlerCmdTag(cmd = ConstantValue.REFUSE_FRIEND_REQUEST, module = ConstantValue.FRIEND_MODULE)
    public void refuseFriendRequest(FriendModel.FriendModelMessage myMessage, MmoSimpleRole mmoSimpleRole) throws InvalidProtocolBufferException, RpgServerException {
        Integer applyId=myMessage.getRefuseFriendRequest().getApplyId();
        if (!mmoSimpleRole.getFriendApplyBeanHashMap().containsKey(applyId)){
            throw new RpgServerException(StateCode.FAIL,"不存在该好友申请");
        }
        FriendServiceProvider.refuseApplyFriend(mmoSimpleRole,applyId);
        //protobuf
        FriendModel.FriendModelMessage messageData= FriendModel.FriendModelMessage.newBuilder()
                .setDataType( FriendModel.FriendModelMessage.DateType.RefuseFriendResponse)
                .setRefuseFriendResponse(FriendModel.RefuseFriendResponse.newBuilder().build())
                .build();
        NettyResponse nettyResponse=new NettyResponse();
        nettyResponse.setCmd(ConstantValue.REFUSE_FRIEND_RESPONSE);
        nettyResponse.setStateCode(StateCode.SUCCESS);
        nettyResponse.setData(messageData.toByteArray());
        Channel channel=mmoSimpleRole.getChannel();
        if (channel!=null) {
            String json= JsonFormat.printToString(messageData);
            NotificationUtil.sendMessage(channel,nettyResponse,json);
        }
    }

    @Override
    @HandlerCmdTag(cmd = ConstantValue.REDUCE_FRIEND_REQUEST, module = ConstantValue.FRIEND_MODULE)
    public void reduceFriendRequest(FriendModel.FriendModelMessage myMessage, MmoSimpleRole mmoSimpleRole) throws InvalidProtocolBufferException, RpgServerException {
        Integer roleId=myMessage.getReduceFriendRequest().getRoleId();
        if (!mmoSimpleRole.getFriends().contains(roleId)){
            throw new RpgServerException(StateCode.FAIL,"不存在该好友");
        }
        FriendServiceProvider.reduceFriend(mmoSimpleRole,roleId);
        //protobuf
        FriendModel.FriendModelMessage messageData= FriendModel.FriendModelMessage.newBuilder()
                .setDataType( FriendModel.FriendModelMessage.DateType.ReduceFriendResponse)
                .setReduceFriendResponse(FriendModel.ReduceFriendResponse.newBuilder().build())
                .build();
        NettyResponse nettyResponse=new NettyResponse();
        nettyResponse.setCmd(ConstantValue.REDUCE_FRIEND_RESPONSE);
        nettyResponse.setStateCode(StateCode.SUCCESS);
        nettyResponse.setData(messageData.toByteArray());
        Channel channel=mmoSimpleRole.getChannel();
        if (channel!=null) {
            String json= JsonFormat.printToString(messageData);
            NotificationUtil.sendMessage(channel,nettyResponse,json);
        }
    }

    @Override
    @HandlerCmdTag(cmd = ConstantValue.GET_FRIENDS_REQUEST, module = ConstantValue.FRIEND_MODULE)
    public void getFriendsRequest(FriendModel.FriendModelMessage myMessage, MmoSimpleRole mmoSimpleRole) throws InvalidProtocolBufferException, RpgServerException {
        List<FriendBean> friendBeans=FriendServiceProvider.friendsList(mmoSimpleRole);
        List<FriendModel.FriendBean> friendBeanList=new ArrayList<>();
        for (FriendBean friendBean : friendBeans) {
            FriendModel.FriendBean bean= FriendModel.FriendBean.newBuilder()
                    .setName(friendBean.getName()).setProfessionId(friendBean.getProfessionId())
                    .setRoleId(friendBean.getRoleId()).setOnStatus(friendBean.getOnStatus())
                    .build();
            friendBeanList.add(bean);
        }
        //protobuf
        FriendModel.FriendModelMessage messageData= FriendModel.FriendModelMessage.newBuilder()
                .setDataType( FriendModel.FriendModelMessage.DateType.GetFriendsResponse)
                .setGetFriendsResponse(FriendModel.GetFriendsResponse.newBuilder()
                        .addAllFriends(friendBeanList).build())
                .build();
        NettyResponse nettyResponse=new NettyResponse();
        nettyResponse.setCmd(ConstantValue.GET_FRIENDS_RESPONSE);
        nettyResponse.setStateCode(StateCode.SUCCESS);
        nettyResponse.setData(messageData.toByteArray());
        Channel channel=mmoSimpleRole.getChannel();
        if (channel!=null) {
            String json= JsonFormat.printToString(messageData);
            NotificationUtil.sendMessage(channel,nettyResponse,json);
        }
    }

    @Override
    @HandlerCmdTag(cmd = ConstantValue.FRIEND_APPLY_LIST_REQUEST, module = ConstantValue.FRIEND_MODULE)
    public void friendApplyListRequest(FriendModel.FriendModelMessage myMessage, MmoSimpleRole mmoSimpleRole) throws InvalidProtocolBufferException, RpgServerException {
        List<FriendApplyBean> friendApplyBeans= new ArrayList<>(mmoSimpleRole.getFriendApplyBeanHashMap().values());
        List<FriendModel.FriendApplyBean> friendApplyBeanList=new ArrayList<>();
        for (FriendApplyBean friendApplyBean : friendApplyBeans) {
            FriendModel.FriendApplyBean bean= FriendModel.FriendApplyBean.newBuilder()
                    .setId(friendApplyBean.getId()).setName(friendApplyBean.getName()).setRoleId(friendApplyBean.getRoleId())
                    .build();
            friendApplyBeanList.add(bean);
        }
        //protobuf
        FriendModel.FriendModelMessage messageData= FriendModel.FriendModelMessage.newBuilder()
                .setDataType( FriendModel.FriendModelMessage.DateType.FriendApplyListResponse)
                .setFriendApplyListResponse(FriendModel.FriendApplyListResponse.newBuilder()
                        .addAllFriendApplyBeans(friendApplyBeanList).build())
                .build();
        NettyResponse nettyResponse=new NettyResponse();
        nettyResponse.setCmd(ConstantValue.FRIEND_APPLY_LIST_RESPONSE);
        nettyResponse.setStateCode(StateCode.SUCCESS);
        nettyResponse.setData(messageData.toByteArray());
        Channel channel=mmoSimpleRole.getChannel();
        if (channel!=null) {
            String json= JsonFormat.printToString(messageData);
            NotificationUtil.sendMessage(channel,nettyResponse,json);
        }
    }
}
