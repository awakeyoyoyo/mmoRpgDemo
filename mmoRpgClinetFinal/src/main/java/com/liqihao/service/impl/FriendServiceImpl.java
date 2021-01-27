package com.liqihao.service.impl;

import com.google.protobuf.InvalidProtocolBufferException;
import com.liqihao.commons.MmoCacheCilent;
import com.liqihao.commons.NettyResponse;
import com.liqihao.commons.enums.RoleOnStatusCode;
import com.liqihao.pojo.baseMessage.ProfessionMessage;
import com.liqihao.protobufObject.FriendModel;
import com.liqihao.protobufObject.GameSystemModel;
import com.liqihao.service.FriendService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Classname FriendServiceImpl
 * @Description 好友模块
 * @Author lqhao
 * @Date 2021/1/27 12:07
 * @Version 1.0
 */
@Service
public class FriendServiceImpl implements FriendService {
    @Override
    public void applyFriendResponse(NettyResponse nettyResponse) throws InvalidProtocolBufferException {
        byte[] data=nettyResponse.getData();
        FriendModel.FriendModelMessage myMessage;
        myMessage=FriendModel.FriendModelMessage.parseFrom(data);
        System.out.println("[-]--------------------------------------------------------");
        System.out.println("[-]好友申请已发送");
        System.out.println("[-]--------------------------------------------------------");
    }

    @Override
    public void agreeFriendResponse(NettyResponse nettyResponse) throws InvalidProtocolBufferException {
        byte[] data=nettyResponse.getData();
        FriendModel.FriendModelMessage myMessage;
        myMessage=FriendModel.FriendModelMessage.parseFrom(data);
        System.out.println("[-]--------------------------------------------------------");
        System.out.println("[-]已同意该好友申请");
        System.out.println("[-]--------------------------------------------------------");
    }

    @Override
    public void refuseFriendResponse(NettyResponse nettyResponse) throws InvalidProtocolBufferException {
        byte[] data=nettyResponse.getData();
        FriendModel.FriendModelMessage myMessage;
        myMessage=FriendModel.FriendModelMessage.parseFrom(data);
        System.out.println("[-]--------------------------------------------------------");
        System.out.println("[-]已拒绝该好友申请");
        System.out.println("[-]--------------------------------------------------------");
    }

    @Override
    public void hasNewFriendsResponse(NettyResponse nettyResponse) throws InvalidProtocolBufferException {
        byte[] data=nettyResponse.getData();
        FriendModel.FriendModelMessage myMessage;
        myMessage=FriendModel.FriendModelMessage.parseFrom(data);
        System.out.println("[-]--------------------------------------------------------");
        System.out.println("[-]有新的好友");
        System.out.println("[-]--------------------------------------------------------");
    }

    @Override
    public void beRefuseResponse(NettyResponse nettyResponse) throws InvalidProtocolBufferException {
        byte[] data=nettyResponse.getData();
        FriendModel.FriendModelMessage myMessage;
        myMessage=FriendModel.FriendModelMessage.parseFrom(data);
        System.out.println("[-]--------------------------------------------------------");
        System.out.println("[-]已被拒绝好友申请");
        System.out.println("[-]--------------------------------------------------------");
    }

    @Override
    public void getFriendsResponse(NettyResponse nettyResponse) throws InvalidProtocolBufferException {
        byte[] data=nettyResponse.getData();
        FriendModel.FriendModelMessage myMessage;
        myMessage=FriendModel.FriendModelMessage.parseFrom(data);
        List<FriendModel.FriendBean> friendBeanList=myMessage.getGetFriendsResponse().getFriendsList();
        System.out.println("[-]--------------------------------------------------------");
        System.out.println("[-]好友列表：");
        for (FriendModel.FriendBean friendBean : friendBeanList) {
            ProfessionMessage professionMessage= MmoCacheCilent.getInstance().getProfessionMessageConcurrentHashMap().get(friendBean.getProfessionId());
            System.out.println("[-]角色id："+friendBean.getRoleId()+"角色姓名："+friendBean.getName()+"角色职业:"+professionMessage.getName()+"角色状态："+ RoleOnStatusCode.getValue(friendBean.getOnStatus()));
        }
        System.out.println("[-]");
        System.out.println("[-]--------------------------------------------------------");
    }

    @Override
    public void friendApplyListResponse(NettyResponse nettyResponse) throws InvalidProtocolBufferException {
        byte[] data=nettyResponse.getData();
        FriendModel.FriendModelMessage myMessage;
        myMessage=FriendModel.FriendModelMessage.parseFrom(data);
        List<FriendModel.FriendApplyBean> friendApplyBeanList=myMessage.getFriendApplyListResponse().getFriendApplyBeansList();
        System.out.println("[-]--------------------------------------------------------");
        System.out.println("[-]好友申请列表：");
        for (FriendModel.FriendApplyBean friendApplyBean : friendApplyBeanList) {
            System.out.println("[-]申请id："+friendApplyBean.getId()+"申请人id："+friendApplyBean.getRoleId()+"申请人名字："+friendApplyBean.getName());
        }
        System.out.println("[-]--------------------------------------------------------");
    }

    @Override
    public void reduceFriendResponse(NettyResponse nettyResponse) throws InvalidProtocolBufferException {
        byte[] data=nettyResponse.getData();
        FriendModel.FriendModelMessage myMessage;
        myMessage=FriendModel.FriendModelMessage.parseFrom(data);
        System.out.println("[-]--------------------------------------------------------");
        System.out.println("[-]删除成功");
        System.out.println("[-]--------------------------------------------------------");
    }
}
