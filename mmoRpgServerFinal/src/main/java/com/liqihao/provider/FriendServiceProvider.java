package com.liqihao.provider;

import com.googlecode.protobuf.format.JsonFormat;
import com.liqihao.cache.OnlineRoleMessageCache;
import com.liqihao.cache.RoleMessageCache;
import com.liqihao.commons.ConstantValue;
import com.liqihao.commons.NettyResponse;
import com.liqihao.commons.StateCode;
import com.liqihao.commons.enums.RoleOnStatusCode;
import com.liqihao.commons.enums.TaskTargetTypeCode;
import com.liqihao.pojo.MmoRolePOJO;
import com.liqihao.pojo.bean.friendBean.FriendApplyBean;
import com.liqihao.pojo.bean.friendBean.FriendBean;
import com.liqihao.pojo.bean.roleBean.MmoSimpleRole;
import com.liqihao.pojo.bean.taskBean.firstFriendTask.FriendFirstAction;
import com.liqihao.protobufObject.FriendModel;
import com.liqihao.util.CommonsUtil;
import com.liqihao.util.DbUtil;
import com.liqihao.util.NotificationUtil;
import io.netty.channel.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * @Classname FriendServiceProvider
 * @Description 好友服务提供
 * @Author lqhao
 * @Date 2021/1/27 10:03
 * @Version 1.0
 */
@Component
public class FriendServiceProvider {
    private final Logger log = LoggerFactory.getLogger(FriendServiceProvider.class);

    /**
     * description 申请添加朋友
     * @param role
     * @param roleId
     * @return {@link null }
     * @author lqhao
     * @createTime 2021/1/27 10:05
     */
    public static void addApplyFriend(MmoSimpleRole role,Integer roleId){
        FriendApplyBean friendApplyBean=new FriendApplyBean();
        MmoSimpleRole simpleRole=OnlineRoleMessageCache.getInstance().get(roleId);
        friendApplyBean.setId(simpleRole.getFriendApplyIdAuto().incrementAndGet());
        friendApplyBean.setRoleId(simpleRole.getId());
        friendApplyBean.setName(simpleRole.getName());
        role.getFriendApplyBeanHashMap().put(friendApplyBean.getId(),friendApplyBean);
    }
    /**
     * description 通过申请
     * @param role
     * @param applyId
     * @return {@link null }
     * @author lqhao
     * @createTime 2021/1/27 10:05
     */
    public static void agreeApplyFriend(MmoSimpleRole role,Integer applyId){
        FriendApplyBean friendApplyBean=role.getFriendApplyBeanHashMap().get(applyId);
        role.getFriendApplyBeanHashMap().remove(applyId);
        role.getFriends().add(friendApplyBean.getRoleId());
        role.updateItem(role.getId());
        //加好友事件
        FriendFirstAction firstAction=new FriendFirstAction();
        firstAction.setTaskTargetType(TaskTargetTypeCode.FIRST_TIME_FRIEND.getCode());
        role.getTaskManager().handler(firstAction,role);
        MmoSimpleRole mmoSimpleRole= OnlineRoleMessageCache.getInstance().get(friendApplyBean.getRoleId());
        if (mmoSimpleRole!=null){
            mmoSimpleRole.getFriends().add(role.getId());
            mmoSimpleRole.updateItem(mmoSimpleRole.getId());
            //加好友事件
            FriendFirstAction friendFirstAction=new FriendFirstAction();
            friendFirstAction.setTaskTargetType(TaskTargetTypeCode.FIRST_TIME_FRIEND.getCode());
            mmoSimpleRole.getTaskManager().handler(friendFirstAction,mmoSimpleRole);
        }else{
            MmoRolePOJO mmoRolePOJO= RoleMessageCache.getInstance().get(friendApplyBean.getRoleId());
            List<Integer> friendIds= CommonsUtil.split(mmoRolePOJO.getFriendIds());
            friendIds.add(role.getId());
            DbUtil.updateRolePOJO(mmoRolePOJO);
        }
        // 发送给双方有新的好友
        //protobuf
        FriendModel.FriendModelMessage messageData= FriendModel.FriendModelMessage.newBuilder()
                .setDataType( FriendModel.FriendModelMessage.DateType.HasNewFriendsResponse)
                .setHasNewFriendsResponse(FriendModel.HasNewFriendsResponse.newBuilder().build())
                .build();
        NettyResponse nettyResponse=new NettyResponse();
        nettyResponse.setCmd(ConstantValue.HAS_NEW_FRIENDS_RESPONSE);
        nettyResponse.setStateCode(StateCode.SUCCESS);
        nettyResponse.setData(messageData.toByteArray());
        List<MmoSimpleRole> roles=new ArrayList<>();
        roles.add(mmoSimpleRole);
        roles.add(role);
        //send
        NotificationUtil.sendRolesMessage(nettyResponse,roles,messageData);
    }
    /**
     * description 拒绝申请
     * @param role
     * @param applyId
     * @return {@link null }
     * @author lqhao
     * @createTime 2021/1/27 10:05
     */
    public static void refuseApplyFriend(MmoSimpleRole role,Integer applyId){
        FriendApplyBean friendApplyBean=role.getFriendApplyBeanHashMap().get(applyId);
        role.getFriendApplyBeanHashMap().remove(applyId);
        //发送给对方你已被拒绝
        Integer roleId=friendApplyBean.getRoleId();
        MmoSimpleRole applyRole=OnlineRoleMessageCache.getInstance().get(roleId);
        if (applyRole!=null){
            //protobuf
            FriendModel.FriendModelMessage messageData= FriendModel.FriendModelMessage.newBuilder()
                    .setDataType( FriendModel.FriendModelMessage.DateType.BeRefuseResponse)
                    .setBeRefuseResponse(FriendModel.BeRefuseResponse.newBuilder().build())
                    .build();
            NettyResponse nettyResponse=new NettyResponse();
            nettyResponse.setCmd(ConstantValue.BE_REFUSE_RESPONSE);
            nettyResponse.setStateCode(StateCode.SUCCESS);
            nettyResponse.setData(messageData.toByteArray());
            //send
            Channel channel=applyRole.getChannel();
            if (channel!=null) {
                NotificationUtil.sendMessage(channel,nettyResponse,messageData);
            }
        }
    }
    /**
     * description 删除朋友
     * @param role
     * @param roleId
     * @return {@link null }
     * @author lqhao
     * @createTime 2021/1/27 10:06
     */
    public static void reduceFriend(MmoSimpleRole role,Integer roleId){
        List<Integer> friendsIds = role.getFriends();
        friendsIds.remove(roleId);
        role.updateItem(role.getId());
    }

    /**
     * description 好友列表
     * @param role
     * @return {@link List< FriendBean> }
     * @author lqhao
     * @createTime 2021/1/27 10:27
     */
    public static List<FriendBean> friendsList(MmoSimpleRole role){
        List<Integer> friendIds=role.getFriends();
        List<FriendBean> friendBeans=new ArrayList<>();
        for (Integer friendId : friendIds) {
            MmoSimpleRole mmoSimpleRole= OnlineRoleMessageCache.getInstance().get(friendId);
            FriendBean friendBean=new FriendBean();
            if (mmoSimpleRole!=null){
                friendBean.setName(mmoSimpleRole.getName());
                friendBean.setRoleId(mmoSimpleRole.getId());
                friendBean.setProfessionId(mmoSimpleRole.getProfessionId());
                friendBean.setOnStatus(mmoSimpleRole.getOnStatus());
            }else{
                MmoRolePOJO mmoRolePOJO= RoleMessageCache.getInstance().get(friendId);
                friendBean.setName(mmoRolePOJO.getName());
                friendBean.setRoleId(mmoRolePOJO.getId());
                friendBean.setProfessionId(mmoRolePOJO.getProfessionId());
                friendBean.setOnStatus(RoleOnStatusCode.EXIT.getCode());
            }
            friendBeans.add(friendBean);
        }
        return friendBeans;
    }
}
