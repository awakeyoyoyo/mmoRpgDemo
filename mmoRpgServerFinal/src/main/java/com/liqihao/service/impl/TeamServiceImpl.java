package com.liqihao.service.impl;

import com.google.protobuf.InvalidProtocolBufferException;
import com.liqihao.Cache.ChannelMessageCache;
import com.liqihao.Cache.OnlineRoleMessageCache;
import com.liqihao.annotation.HandlerCmdTag;
import com.liqihao.annotation.HandlerServiceTag;
import com.liqihao.commons.ConstantValue;
import com.liqihao.commons.NettyRequest;
import com.liqihao.commons.NettyResponse;
import com.liqihao.commons.StateCode;
import com.liqihao.pojo.bean.MmoSimpleRole;
import com.liqihao.pojo.bean.TeamApplyOrInviteBean;
import com.liqihao.pojo.bean.TeamBean;
import com.liqihao.protobufObject.SceneModel;
import com.liqihao.protobufObject.TeamModel;
import com.liqihao.provider.TeamServiceProvider;
import com.liqihao.service.TeamService;
import com.liqihao.util.CommonsUtil;
import io.netty.channel.Channel;
import org.springframework.beans.factory.support.ManagedMap;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;


/**
 * 队伍模块
 * @author lqhao
 */
@Service
@HandlerServiceTag
public class TeamServiceImpl implements TeamService {
    @Override
    public void applyForTeamRequest(NettyRequest nettyRequest, Channel channel) throws InvalidProtocolBufferException {
        //todo 获取teamId
        Integer teamId=null;
        //判断是否在线 并且返回玩家对象
        MmoSimpleRole mmoSimpleRole= CommonsUtil.checkLogin(channel);
        if (mmoSimpleRole==null) {
            return;
        }
        //判断玩家是否已经有队伍
        if (mmoSimpleRole.getTeamId()!=null){
            NettyResponse errotResponse=new NettyResponse(StateCode.FAIL, ConstantValue.FAIL_RESPONSE,"已经有队伍了".getBytes());
            channel.writeAndFlush(errotResponse);
            return;
        }
        TeamBean teamBean=TeamServiceProvider.getTeamBeanByTeamId(teamId);
        if (teamBean==null){
            NettyResponse errotResponse=new NettyResponse(StateCode.FAIL, ConstantValue.FAIL_RESPONSE,"该队伍已不存在".getBytes());
            channel.writeAndFlush(errotResponse);
            return;
        }
        Integer leaderId=teamBean.getLeaderId();
        Channel c=ChannelMessageCache.getInstance().get(leaderId);
        if (c==null){
            NettyResponse errotResponse=new NettyResponse(StateCode.FAIL, ConstantValue.FAIL_RESPONSE,"队长已经离线".getBytes());
            channel.writeAndFlush(errotResponse);
            return;
        }
        teamBean.applyTeam(mmoSimpleRole);
        //todo 发送一个申请入队的请求给队长 //发送 teamId和-用户信息-和teamApplyId给队长
//        c.writeAndFlush()
        //todo 申请成功，等待信息
//        channel.writeAndFlush()
    }

    @Override
    public void banPeopleRequest(NettyRequest nettyRequest, Channel channel) throws InvalidProtocolBufferException {
        //todo 获取要t的人的id
        Integer roleId=null;
        MmoSimpleRole player=OnlineRoleMessageCache.getInstance().get(roleId);
        MmoSimpleRole mmoSimpleRole= CommonsUtil.checkLogin(channel);
        if (mmoSimpleRole==null) {
            return;
        }
        //判断玩家是否已经有队伍
        if (mmoSimpleRole.getTeamId()==null){
            NettyResponse errotResponse=new NettyResponse(StateCode.FAIL, ConstantValue.FAIL_RESPONSE,"不在任何队伍中".getBytes());
            channel.writeAndFlush(errotResponse);
            return;
        }
        TeamBean teamBean=TeamServiceProvider.getTeamBeanByTeamId(mmoSimpleRole.getTeamId());
        if (!teamBean.getLeaderId().equals(mmoSimpleRole.getId())){
            NettyResponse errotResponse=new NettyResponse(StateCode.FAIL, ConstantValue.FAIL_RESPONSE,"不是队长没有该权利".getBytes());
            channel.writeAndFlush(errotResponse);
            return;
        }
        //判断玩家是否在队伍里
        if (player.getTeamId()==null||!player.getTeamId().equals(mmoSimpleRole.getTeamId())){
            NettyResponse errotResponse=new NettyResponse(StateCode.FAIL, ConstantValue.FAIL_RESPONSE,"该玩家已不在队伍中".getBytes());
            channel.writeAndFlush(errotResponse);
            return;
        }
        teamBean.banPeople(roleId);
    }

    @Override
    @HandlerCmdTag(cmd = ConstantValue.CREATE_TEAM_REQUEST,module = ConstantValue.TEAM_MODULE)
    public void createTeamRequest(NettyRequest nettyRequest, Channel channel) throws InvalidProtocolBufferException {
        //判断是否在线 并且返回玩家对象
        byte[] data=nettyRequest.getData();
        TeamModel.TeamModelMessage myMessage;
        myMessage=TeamModel.TeamModelMessage.parseFrom(data);
        String teamName=myMessage.getCreateTeamRequest().getName();
        if (teamName==null){
            NettyResponse errotResponse=new NettyResponse(StateCode.FAIL, ConstantValue.FAIL_RESPONSE,"请输入参数".getBytes());
            channel.writeAndFlush(errotResponse);
            return;
        }
        MmoSimpleRole mmoSimpleRole= CommonsUtil.checkLogin(channel);
        if (mmoSimpleRole==null) {
            return;
        }
        //判断玩家是否已经有队伍
        if (mmoSimpleRole.getTeamId()!=null){
            NettyResponse errotResponse=new NettyResponse(StateCode.FAIL, ConstantValue.FAIL_RESPONSE,"已经有队伍了".getBytes());
            channel.writeAndFlush(errotResponse);
            return;
        }
        TeamBean teamBean=TeamServiceProvider.createNewTeamBean(mmoSimpleRole,teamName);
        mmoSimpleRole.setTeamId(teamBean.getTeamId());
        TeamModel.RoleDto role=TeamModel.RoleDto.newBuilder().setId(mmoSimpleRole.getId())
                .setHp(mmoSimpleRole.getBlood()).setMp(mmoSimpleRole.getMp())
                .setName(mmoSimpleRole.getName()).setNowHp(mmoSimpleRole.getNowBlood())
                .setTeamId(mmoSimpleRole.getTeamId())
                .setNowMP(mmoSimpleRole.getNowMp()).build();
        List<TeamModel.RoleDto> roles=new ArrayList<>();
        roles.add(role);
        TeamModel.TeamBeanDto teamBeanDto=TeamModel.TeamBeanDto.newBuilder().addAllRoleDtos(roles)
                .setLeaderId(teamBean.getLeaderId()).setTeamName(teamBean.getTeamName())
                .setTeamId(teamBean.getTeamId()).build();
        TeamModel.TeamMessageResponse teamMessageResponse=TeamModel.TeamMessageResponse.newBuilder().setTeamBeanDto(teamBeanDto).build();
        TeamModel.TeamModelMessage.Builder teamMessageBuilder=TeamModel.TeamModelMessage.newBuilder();
        teamMessageBuilder.setDataType(TeamModel.TeamModelMessage.DateType.TeamMessageResponse);
        teamMessageBuilder.setTeamMessageResponse(teamMessageResponse);
        NettyResponse nettyResponse=new NettyResponse();
        nettyResponse.setStateCode(StateCode.SUCCESS);
        nettyResponse.setCmd(ConstantValue.TEAM_MESSAGE_RESPONSE);
        nettyResponse.setData(teamMessageBuilder.build().toByteArray());
        channel.writeAndFlush(nettyResponse);
    }

    @Override
    public void deleteTeamRequest(NettyRequest nettyRequest, Channel channel) throws InvalidProtocolBufferException {
        //判断是否在线 并且返回玩家对象
        MmoSimpleRole mmoSimpleRole= CommonsUtil.checkLogin(channel);
        if (mmoSimpleRole==null) {
            return;
        }
        //判断玩家是否已经有队伍
        Integer teamId=mmoSimpleRole.getTeamId();
        if (teamId!=null){
            NettyResponse errotResponse=new NettyResponse(StateCode.FAIL, ConstantValue.FAIL_RESPONSE,"已经有队伍了".getBytes());
            channel.writeAndFlush(errotResponse);
            return;
        }
        TeamBean teamBean=TeamServiceProvider.getTeamBeanByTeamId(teamId);
        //判断当前玩家是否有队伍
        if (teamBean==null){
            NettyResponse errotResponse=new NettyResponse(StateCode.FAIL, ConstantValue.FAIL_RESPONSE,"当前角色无队伍".getBytes());
            channel.writeAndFlush(errotResponse);
            return;
        }
        //判断当前玩家是否为队长
        if (!teamBean.getLeaderId().equals(mmoSimpleRole.getId())){
            NettyResponse errotResponse=new NettyResponse(StateCode.FAIL, ConstantValue.FAIL_RESPONSE,"不是队长，无权解散队伍".getBytes());
            channel.writeAndFlush(errotResponse);
            return;
        }
        //解散队伍
        //广播队伍已经解散信息
        teamBean.breakUp();
    }

    @Override
    public void entryPeopleRequest(NettyRequest nettyRequest, Channel channel) throws InvalidProtocolBufferException {
        /**
         *  若当前channle的role与传入的roleId相等,则表示用户接受了该队伍的邀请
         *  若当前channle的role与传入的roleId不相等，则是队长同意该用户的申请，需要判断当前channel role是否为队长
         */
        Integer roleId=null;
        Integer teamId=null;
        Long createTime=null;

        //判断是否在线 并且返回玩家对象
        MmoSimpleRole mmoSimpleRole= CommonsUtil.checkLogin(channel);
        if (mmoSimpleRole==null) {
            return;
        }
        if (mmoSimpleRole.getTeamId()!=null){
            NettyResponse errotResponse=new NettyResponse(StateCode.FAIL, ConstantValue.FAIL_RESPONSE,"已经有队伍了".getBytes());
            channel.writeAndFlush(errotResponse);
            return;
        }
        if (roleId.equals(mmoSimpleRole.getId())){
            //用户接受队伍邀请
            //查看人物中是否有该请求
            TeamApplyOrInviteBean applyOrInviteBean=mmoSimpleRole.constainsInvite(teamId,createTime);
            if (applyOrInviteBean==null){
                NettyResponse errotResponse=new NettyResponse(StateCode.FAIL, ConstantValue.FAIL_RESPONSE,"该邀请已经过期了".getBytes());
                channel.writeAndFlush(errotResponse);
                return;
            }
            TeamBean teamBean=TeamServiceProvider.getTeamBeanByTeamId(teamId);
            // 此时传入的channel是用户的
            teamBean.addRole(mmoSimpleRole,channel);
            //用户删除该邀请
            mmoSimpleRole.getTeamApplyOrInviteBeans().remove(applyOrInviteBean);
        }else{
            TeamBean teamBean=TeamServiceProvider.getTeamBeanByTeamId(teamId);
            //队长同意该用户进队伍
            TeamApplyOrInviteBean inviteBean=teamBean.constainsInvite(roleId,createTime);
            if (inviteBean==null){
                NettyResponse errotResponse=new NettyResponse(StateCode.FAIL, ConstantValue.FAIL_RESPONSE,"该申请已经过期了".getBytes());
                channel.writeAndFlush(errotResponse);
                return;
            }
            // 此时传入的channel是用户的
            teamBean.addRole(mmoSimpleRole,channel);
            //队伍删除该申请
            teamBean.getTeamApplyOrInviteBeans().remove(inviteBean);
        }
    }

    @Override
    public void exitTeamRequest(NettyRequest nettyRequest, Channel channel) throws InvalidProtocolBufferException {
        //判断是否在线 并且返回玩家对象
        MmoSimpleRole mmoSimpleRole= CommonsUtil.checkLogin(channel);
        if (mmoSimpleRole==null) {
            return;
        }
        //判断是否有队伍
        Integer teamId=mmoSimpleRole.getTeamId();
        if (teamId==null){
            NettyResponse errotResponse=new NettyResponse(StateCode.FAIL, ConstantValue.FAIL_RESPONSE,"当前不在队伍中".getBytes());
            channel.writeAndFlush(errotResponse);
            return;
        }
        //离开队伍
        TeamBean teamBean=TeamServiceProvider.getTeamBeanByTeamId(teamId);
        teamBean.exitPeople(mmoSimpleRole.getId());
    }

    @Override
    public void invitePeopleRequest(NettyRequest nettyRequest, Channel channel) throws InvalidProtocolBufferException {
        //TODO 获取roleId
        Integer roleId=null;
        //判断是否在线 并且返回玩家对象
        MmoSimpleRole mmoSimpleRole= CommonsUtil.checkLogin(channel);
        if (mmoSimpleRole==null) {
            return;
        }
        MmoSimpleRole inviteRole=OnlineRoleMessageCache.getInstance().get(roleId);
        if (inviteRole==null){
            NettyResponse errotResponse=new NettyResponse(StateCode.FAIL, ConstantValue.FAIL_RESPONSE,"该用户不在线".getBytes());
            channel.writeAndFlush(errotResponse);
            return;
        }
        //判断是否有队伍
        if (mmoSimpleRole.getTeamId()==null){
            NettyResponse errotResponse=new NettyResponse(StateCode.FAIL, ConstantValue.FAIL_RESPONSE,("请先创建队伍").getBytes());
            channel.writeAndFlush(errotResponse);
            return;
        }
        //判断是否有队伍
        if (inviteRole.getTeamId()!=null){
            NettyResponse errotResponse=new NettyResponse(StateCode.FAIL, ConstantValue.FAIL_RESPONSE,("该用户已经有队伍了").getBytes());
            channel.writeAndFlush(errotResponse);
            return;
        }
        //判断是否是队长
        TeamBean teamBean=TeamServiceProvider.getTeamBeanByTeamId(mmoSimpleRole.getTeamId());
        if (!teamBean.getLeaderId().equals(mmoSimpleRole.getId())){
            NettyResponse errotResponse=new NettyResponse(StateCode.FAIL, ConstantValue.FAIL_RESPONSE,("你不是队长无法邀请玩家进队伍").getBytes());
            channel.writeAndFlush(errotResponse);
            return;
        }
        teamBean.invitePeople(inviteRole);
    }

    @Override
    public void refuseApplyRequest(NettyRequest nettyRequest, Channel channel) throws InvalidProtocolBufferException {
        //teamId和roleId和teamApplyId给队长
        //判断是否在线 并且返回玩家对象
        Integer roleId=null;
        Long createTime=null;
        MmoSimpleRole mmoSimpleRole= CommonsUtil.checkLogin(channel);
        if (mmoSimpleRole==null) {
            return;
        }
        TeamBean teamBean=TeamServiceProvider.getTeamBeanByTeamId(mmoSimpleRole.getTeamId());
        teamBean.refuseApply(roleId,createTime);
    }

    @Override
    public void refuseInviteRequest(NettyRequest nettyRequest, Channel channel) throws InvalidProtocolBufferException {
        //teamId和roleId和teamApplyId给队长
        //判断是否在线 并且返回玩家对象
        Integer teamId=null;
        Long createTime=null;
        MmoSimpleRole mmoSimpleRole= CommonsUtil.checkLogin(channel);
        if (mmoSimpleRole==null) {
            return;
        }
        mmoSimpleRole.refuseInvite(teamId,createTime);
    }

    @Override
    public void applyMessageRequest(NettyRequest nettyRequest, Channel channel) throws InvalidProtocolBufferException {
        MmoSimpleRole mmoSimpleRole= CommonsUtil.checkLogin(channel);
        if (mmoSimpleRole==null) {
            return;
        }
        Integer teamId=mmoSimpleRole.getTeamId();
        if (teamId==null){
            NettyResponse errotResponse=new NettyResponse(StateCode.FAIL, ConstantValue.FAIL_RESPONSE,"当前角色无队伍".getBytes());
            channel.writeAndFlush(errotResponse);
            return;
        }
        TeamBean teamBean=TeamServiceProvider.getTeamBeanByTeamId(teamId);
        if (!teamBean.getLeaderId().equals(mmoSimpleRole.getId())){
            NettyResponse errotResponse=new NettyResponse(StateCode.FAIL, ConstantValue.FAIL_RESPONSE,"不是队长无权限获取队伍申请信息".getBytes());
            channel.writeAndFlush(errotResponse);
            return;
        }
        List<TeamApplyOrInviteBean> applyOrInviteBeans=teamBean.getTeamApplyBean();
        //todo 发送给角色
    }

    @Override
    public void roleInviteMessage(NettyRequest nettyRequest, Channel channel) throws InvalidProtocolBufferException {
        MmoSimpleRole mmoSimpleRole= CommonsUtil.checkLogin(channel);
        if (mmoSimpleRole==null) {
            return;
        }
        List<TeamApplyOrInviteBean> inviteBeanList=mmoSimpleRole.getInviteBeans();
        //todo 发送给角色
    }

    @Override
    @HandlerCmdTag(cmd = ConstantValue.TEAM_MESSAGE_REQUEST,module = ConstantValue.TEAM_MODULE)
    public void teamMessageRequest(NettyRequest nettyRequest, Channel channel) throws InvalidProtocolBufferException {
        MmoSimpleRole mmoSimpleRole= CommonsUtil.checkLogin(channel);
        if (mmoSimpleRole==null) {
            return;
        }
        Integer teamId=mmoSimpleRole.getTeamId();
        if (teamId==null){
            NettyResponse errotResponse=new NettyResponse(StateCode.FAIL, ConstantValue.FAIL_RESPONSE,"没有进入任何队伍".getBytes());
            channel.writeAndFlush(errotResponse);
            return;
        }
        TeamBean teamBean=TeamServiceProvider.getTeamBeanByTeamId(teamId);
        List<TeamModel.RoleDto> roles=new ArrayList<>();
        for (MmoSimpleRole simpleRole:teamBean.getMmoSimpleRoles()) {
            TeamModel.RoleDto role = TeamModel.RoleDto.newBuilder().setId(simpleRole.getId())
                    .setHp(simpleRole.getBlood()).setMp(simpleRole.getMp())
                    .setTeamId(mmoSimpleRole.getTeamId())
                    .setName(simpleRole.getName()).setNowHp(simpleRole.getNowBlood())
                    .setNowMP(simpleRole.getNowMp()).build();
            roles.add(role);
        }
        TeamModel.TeamBeanDto teamBeanDto=TeamModel.TeamBeanDto.newBuilder().addAllRoleDtos(roles)
                .setLeaderId(teamBean.getLeaderId()).setTeamName(teamBean.getTeamName())
                .setTeamId(teamBean.getTeamId()).build();
        TeamModel.TeamMessageResponse teamMessageResponse=TeamModel.TeamMessageResponse.newBuilder().setTeamBeanDto(teamBeanDto).build();
        TeamModel.TeamModelMessage.Builder teamMessageBuilder=TeamModel.TeamModelMessage.newBuilder();
        teamMessageBuilder.setDataType(TeamModel.TeamModelMessage.DateType.TeamMessageResponse);
        teamMessageBuilder.setTeamMessageResponse(teamMessageResponse);
        NettyResponse nettyResponse=new NettyResponse();
        nettyResponse.setStateCode(StateCode.SUCCESS);
        nettyResponse.setCmd(ConstantValue.TEAM_MESSAGE_RESPONSE);
        nettyResponse.setData(teamMessageBuilder.build().toByteArray());
        channel.writeAndFlush(nettyResponse);
    }
}
