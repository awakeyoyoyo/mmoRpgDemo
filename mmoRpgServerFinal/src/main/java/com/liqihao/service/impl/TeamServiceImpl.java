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
import com.liqihao.protobufObject.TeamModel;
import com.liqihao.provider.TeamServiceProvider;
import com.liqihao.service.TeamService;
import com.liqihao.util.CommonsUtil;
import io.netty.channel.Channel;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;


/**
 * 队伍模块
 * @author lqhao
 */
@Service
@HandlerServiceTag(protobufModel = "TeamModel$TeamModelMessage")
public class TeamServiceImpl implements TeamService {
    @Override
    @HandlerCmdTag(cmd = ConstantValue.APPLY_FOR_TEAM_REQUEST,module = ConstantValue.TEAM_MODULE)
    public void applyForTeamRequest(TeamModel.TeamModelMessage myMessage, Channel channel) throws InvalidProtocolBufferException {

        Integer teamId=myMessage.getApplyForTeamRequest().getTeamId();
        if (teamId==0){
            NettyResponse errotResponse=new NettyResponse(StateCode.FAIL, ConstantValue.FAIL_RESPONSE,"请输入参数".getBytes());
            channel.writeAndFlush(errotResponse);
            return;
        }
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
        TeamApplyOrInviteBean teamApplyBean=teamBean.applyTeam(mmoSimpleRole);
        TeamModel.ApplyInviteBeanDto.Builder applyBeanBuilder=TeamModel.ApplyInviteBeanDto.newBuilder();
        applyBeanBuilder.setTeamId(teamApplyBean.getTeamId()).setCreateTime(teamApplyBean.getCreateTime())
                .setEndTime(teamApplyBean.getEndTime()).setRoleId(teamApplyBean.getRoleId())
                .setTeamName(teamBean.getTeamName()).setType(teamApplyBean.getType());
        TeamModel.ApplyForTeamResponse ApplyForTeamResponse=TeamModel.ApplyForTeamResponse.newBuilder().setApplyInviteBeanDtos(applyBeanBuilder.build()).build();
        TeamModel.TeamModelMessage.Builder teamMessageBuilder=TeamModel.TeamModelMessage.newBuilder();
        teamMessageBuilder.setDataType(TeamModel.TeamModelMessage.DateType.ApplyForTeamResponse);
        teamMessageBuilder.setApplyForTeamResponse(ApplyForTeamResponse);
        NettyResponse nettyResponse=new NettyResponse();
        nettyResponse.setStateCode(StateCode.SUCCESS);
        nettyResponse.setCmd(ConstantValue.APPLY_FOR_TEAM_RESPONSE);
        nettyResponse.setData(teamMessageBuilder.build().toByteArray());
        //发给队长
        c.writeAndFlush(nettyResponse);
    }


    @Override
    @HandlerCmdTag(cmd = ConstantValue.CREATE_TEAM_REQUEST,module = ConstantValue.TEAM_MODULE)
    public void createTeamRequest(TeamModel.TeamModelMessage myMessage, Channel channel) throws InvalidProtocolBufferException {
        //判断是否在线 并且返回玩家对象

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
    @HandlerCmdTag(cmd = ConstantValue.BAN_PEOPLE_REQUEST,module = ConstantValue.TEAM_MODULE)
    public void banPeopleRequest(TeamModel.TeamModelMessage myMessage, Channel channel) throws InvalidProtocolBufferException {
        Integer roleId=myMessage.getBanPeopleRequest().getRoleId();
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
        if (player.getId().equals(mmoSimpleRole.getId())){
            NettyResponse errotResponse=new NettyResponse(StateCode.FAIL, ConstantValue.FAIL_RESPONSE,"不能自己踢除自己".getBytes());
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
    @HandlerCmdTag(cmd = ConstantValue.DELETE_TEAM_REQUEST,module = ConstantValue.TEAM_MODULE)
    public void deleteTeamRequest(TeamModel.TeamModelMessage myMessage, Channel channel) throws InvalidProtocolBufferException {
        //判断是否在线 并且返回玩家对象
        MmoSimpleRole mmoSimpleRole= CommonsUtil.checkLogin(channel);
        if (mmoSimpleRole==null) {
            return;
        }
        //判断玩家是否已经有队伍
        Integer teamId=mmoSimpleRole.getTeamId();
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
    @HandlerCmdTag(cmd = ConstantValue.ENTRY_PEOPLE_REQUEST,module = ConstantValue.TEAM_MODULE)
    public void entryPeopleRequest(TeamModel.TeamModelMessage myMessage, Channel channel) throws InvalidProtocolBufferException {
        /**
         *  若当前channle的role与传入的roleId相等,则表示用户接受了该队伍的邀请
         *  若当前channle的role与传入的roleId不相等，则是队长同意该用户的申请，需要判断当前channel role是否为队长
         */

        Integer roleId=myMessage.getEntryPeopleRequest().getRoleId();
        Integer teamId=myMessage.getEntryPeopleRequest().getTeamId();
        //判断是否在线 并且返回玩家对象
        MmoSimpleRole mmoSimpleRole= CommonsUtil.checkLogin(channel);
        if (mmoSimpleRole==null) {
            return;
        }
        if (roleId.equals(mmoSimpleRole.getId())){
            TeamBean teamBean=TeamServiceProvider.getTeamBeanByTeamId(teamId);
            if (teamBean==null){
                NettyResponse errotResponse=new NettyResponse(StateCode.FAIL, ConstantValue.FAIL_RESPONSE,"没有该队伍 ".getBytes());
                channel.writeAndFlush(errotResponse);
                return;
            }
            //玩家角色
            if (mmoSimpleRole.getTeamId()!=null){
                NettyResponse errotResponse=new NettyResponse(StateCode.FAIL, ConstantValue.FAIL_RESPONSE,"已经有队伍了".getBytes());
                channel.writeAndFlush(errotResponse);
                return;
            }
            //用户接受队伍邀请
            //查看人物中是否有该请求
            TeamApplyOrInviteBean applyOrInviteBean=mmoSimpleRole.constainsInvite(teamId);
            if (applyOrInviteBean==null){
                NettyResponse errotResponse=new NettyResponse(StateCode.FAIL, ConstantValue.FAIL_RESPONSE,"没有该邀请或者邀请已经过期".getBytes());
                channel.writeAndFlush(errotResponse);
                return;
            }

            // 此时传入的channel是用户的，mmo是玩家
            teamBean.addRole(mmoSimpleRole,channel);
            //用户删除该邀请
            mmoSimpleRole.getTeamApplyOrInviteBeans().remove(applyOrInviteBean);
        }else{
            //队长同意该用户进队伍
            TeamBean teamBean=TeamServiceProvider.getTeamBeanByTeamId(teamId);
            if (teamBean==null){
                NettyResponse errotResponse=new NettyResponse(StateCode.FAIL, ConstantValue.FAIL_RESPONSE,"请先创建队伍".getBytes());
                channel.writeAndFlush(errotResponse);
                return;
            }
            //获取玩家
            mmoSimpleRole=OnlineRoleMessageCache.getInstance().get(roleId);
            if (mmoSimpleRole.getTeamId()!=null){
                NettyResponse errotResponse=new NettyResponse(StateCode.FAIL, ConstantValue.FAIL_RESPONSE,"已经有队伍了".getBytes());
                channel.writeAndFlush(errotResponse);
                return;
            }
            TeamApplyOrInviteBean inviteBean=teamBean.constainsInvite(roleId);
            if (inviteBean==null){
                NettyResponse errotResponse=new NettyResponse(StateCode.FAIL, ConstantValue.FAIL_RESPONSE,"该申请已经过期了".getBytes());
                channel.writeAndFlush(errotResponse);
                return;
            }
            // 此时传入的channel是队长，mmorole是玩家
            teamBean.addRole(mmoSimpleRole,channel);
            //队伍删除该申请
            teamBean.getTeamApplyOrInviteBeans().remove(inviteBean);
        }
    }

    @Override
    @HandlerCmdTag(cmd = ConstantValue.EXIT_TEAM_REQUEST,module = ConstantValue.TEAM_MODULE)
    public void exitTeamRequest(TeamModel.TeamModelMessage myMessage, Channel channel) throws InvalidProtocolBufferException {
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
    @HandlerCmdTag(cmd = ConstantValue.INVITE_PEOPLE_REQUEST,module = ConstantValue.TEAM_MODULE)
    public void invitePeopleRequest(TeamModel.TeamModelMessage myMessage, Channel channel) throws InvalidProtocolBufferException {

        Integer roleId=myMessage.getInvitePeopleRequest().getRoleId();
        if (roleId==0){
            NettyResponse errotResponse=new NettyResponse(StateCode.FAIL, ConstantValue.FAIL_RESPONSE,"请输入参数".getBytes());
            channel.writeAndFlush(errotResponse);
            return;
        }
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
        TeamApplyOrInviteBean inviteBean=teamBean.invitePeople(inviteRole);
        Channel c=ChannelMessageCache.getInstance().get(roleId);
        //发送邀请给玩家
        TeamModel.ApplyInviteBeanDto.Builder inviteBeanBuilder=TeamModel.ApplyInviteBeanDto.newBuilder();
        inviteBeanBuilder.setTeamId(inviteBean.getTeamId()).setCreateTime(inviteBean.getCreateTime())
                .setEndTime(inviteBean.getEndTime()).setRoleId(inviteBean.getRoleId())
                .setTeamName(teamBean.getTeamName()).setType(inviteBean.getType());
        TeamModel.InvitePeopleResponse invitePeopleResponse=TeamModel.InvitePeopleResponse.newBuilder().setApplyInviteBeanDtos(inviteBeanBuilder.build()).build();
        TeamModel.TeamModelMessage.Builder teamMessageBuilder=TeamModel.TeamModelMessage.newBuilder();
        teamMessageBuilder.setDataType(TeamModel.TeamModelMessage.DateType.InvitePeopleResponse);
        teamMessageBuilder.setInvitePeopleResponse(invitePeopleResponse);
        NettyResponse nettyResponse=new NettyResponse();
        nettyResponse.setStateCode(StateCode.SUCCESS);
        nettyResponse.setCmd(ConstantValue.INVITE_PEOPLE_RESPONSE);
        nettyResponse.setData(teamMessageBuilder.build().toByteArray());
        //发给玩家
        c.writeAndFlush(nettyResponse);
    }

    @Override
    @HandlerCmdTag(cmd = ConstantValue.REFUSE_APPLY_REQUEST,module = ConstantValue.TEAM_MODULE)
    public void refuseApplyRequest(TeamModel.TeamModelMessage myMessage, Channel channel) throws InvalidProtocolBufferException {

        //teamId和roleId和teamApplyId给队长
        //判断是否在线 并且返回玩家对象
        Integer roleId=myMessage.getRefuseApplyRequest().getRoleId();
        MmoSimpleRole mmoSimpleRole= CommonsUtil.checkLogin(channel);
        if (mmoSimpleRole==null) {
            return;
        }
        Integer teamId=mmoSimpleRole.getTeamId();
        if (teamId==null){
            NettyResponse errotResponse=new NettyResponse(StateCode.FAIL, ConstantValue.FAIL_RESPONSE,("你根本无队伍").getBytes());
            channel.writeAndFlush(errotResponse);
            return;
        }
        TeamBean teamBean=TeamServiceProvider.getTeamBeanByTeamId(mmoSimpleRole.getTeamId());
        TeamApplyOrInviteBean bean=teamBean.refuseApply(roleId);
        if (bean==null) {
            return;
        }
        TeamModel.TeamModelMessage.Builder teamMessageBuilder=TeamModel.TeamModelMessage.newBuilder();
        teamMessageBuilder.setDataType(TeamModel.TeamModelMessage.DateType.RefuseApplyResponse);
        teamMessageBuilder.setRefuseApplyResponse(TeamModel.RefuseApplyResponse.newBuilder().setTeamName(teamBean.getTeamName()));
        NettyResponse nettyResponse=new NettyResponse();
        nettyResponse.setStateCode(StateCode.SUCCESS);
        nettyResponse.setCmd(ConstantValue.REFUSE_APPLY_RESPONSE);
        nettyResponse.setData(teamMessageBuilder.build().toByteArray());
        //发给玩家
        Channel c=ChannelMessageCache.getInstance().get(roleId);
        if (c==null) {
            return;
        }
        c.writeAndFlush(nettyResponse);
    }

    @Override
    @HandlerCmdTag(cmd = ConstantValue.REFUSE_INVITE_REQUEST,module = ConstantValue.TEAM_MODULE)

    public void refuseInviteRequest(TeamModel.TeamModelMessage myMessage, Channel channel) throws InvalidProtocolBufferException {
        //teamId和roleId和teamApplyId给队长

        //判断是否在线 并且返回玩家对象
        Integer teamId=myMessage.getRefuseInviteRequest().getTeamId();
        MmoSimpleRole mmoSimpleRole= CommonsUtil.checkLogin(channel);
        if (mmoSimpleRole==null) {
            return;
        }
        TeamApplyOrInviteBean bean=mmoSimpleRole.refuseInvite(teamId);
        if (bean==null){
            return;
        }
        TeamBean teamBean=TeamServiceProvider.getTeamBeanByTeamId(teamId);
        TeamModel.TeamModelMessage.Builder teamMessageBuilder=TeamModel.TeamModelMessage.newBuilder();
        teamMessageBuilder.setDataType(TeamModel.TeamModelMessage.DateType.RefuseInviteResponse);
        teamMessageBuilder.setRefuseInviteResponse(TeamModel.RefuseInviteResponse.newBuilder().setRoleName(mmoSimpleRole.getName()));
        NettyResponse nettyResponse=new NettyResponse();
        nettyResponse.setStateCode(StateCode.SUCCESS);
        nettyResponse.setCmd(ConstantValue.REFUSE_INVITE_RESPONSE);
        nettyResponse.setData(teamMessageBuilder.build().toByteArray());
        //发给队长
        Channel c=ChannelMessageCache.getInstance().get(teamBean.getLeaderId());
        if (c==null) {
            return;
        }
        c.writeAndFlush(nettyResponse);
    }

    @Override
    @HandlerCmdTag(cmd = ConstantValue.APPLY_MESSAGE_REQUEST,module = ConstantValue.TEAM_MODULE)
    public void applyMessageRequest(TeamModel.TeamModelMessage myMessage, Channel channel) throws InvalidProtocolBufferException {
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
        //发送给角色
        List<TeamModel.ApplyInviteBeanDto> applyInviteBeanDtos=new ArrayList<>();
        for (TeamApplyOrInviteBean e:applyOrInviteBeans) {
            TeamModel.ApplyInviteBeanDto.Builder applyBeanBuilder=TeamModel.ApplyInviteBeanDto.newBuilder();
            applyBeanBuilder.setTeamId(e.getTeamId()).setCreateTime(e.getCreateTime())
                    .setEndTime(e.getEndTime()).setRoleId(e.getRoleId())
                    .setTeamName(teamBean.getTeamName()).setType(e.getType());
            applyInviteBeanDtos.add(applyBeanBuilder.build());
        }

        TeamModel.ApplyMessageResponse applyMessageResponse=TeamModel.ApplyMessageResponse.newBuilder().addAllApplyInviteBeanDtos(applyInviteBeanDtos).build();
        TeamModel.TeamModelMessage.Builder teamMessageBuilder=TeamModel.TeamModelMessage.newBuilder();
        teamMessageBuilder.setDataType(TeamModel.TeamModelMessage.DateType.ApplyMessageResponse);
        teamMessageBuilder.setApplyMessageResponse(applyMessageResponse);
        NettyResponse nettyResponse=new NettyResponse();
        nettyResponse.setStateCode(StateCode.SUCCESS);
        nettyResponse.setCmd(ConstantValue.APPLY_MESSAGE_RESPONSE);
        nettyResponse.setData(teamMessageBuilder.build().toByteArray());
        //发给队长
        channel.writeAndFlush(nettyResponse);
    }

    @Override
    @HandlerCmdTag(cmd = ConstantValue.INVITE_MESSAGE_REQUEST,module = ConstantValue.TEAM_MODULE)
    public void inviteMessage(TeamModel.TeamModelMessage myMessage, Channel channel) throws InvalidProtocolBufferException {
        MmoSimpleRole mmoSimpleRole= CommonsUtil.checkLogin(channel);
        if (mmoSimpleRole==null) {
            return;
        }
        List<TeamApplyOrInviteBean> inviteBeanList=mmoSimpleRole.getInviteBeans();
        //发送给角色
        List<TeamModel.ApplyInviteBeanDto> inviteBeanDtos=new ArrayList<>();
        for (TeamApplyOrInviteBean e:inviteBeanList) {
            TeamModel.ApplyInviteBeanDto.Builder inviteBeanBuilder=TeamModel.ApplyInviteBeanDto.newBuilder();
            TeamBean teamBean=TeamServiceProvider.getTeamBeanByTeamId(e.getTeamId());
            inviteBeanBuilder.setTeamId(e.getTeamId()).setCreateTime(e.getCreateTime())
                    .setEndTime(e.getEndTime()).setRoleId(e.getRoleId())
                    .setTeamName(teamBean.getTeamName()).setType(e.getType());
            inviteBeanDtos.add(inviteBeanBuilder.build());
        }

        TeamModel.InviteMessageResponse inviteMessageResponse=TeamModel.InviteMessageResponse.newBuilder().addAllApplyInviteBeanDtos(inviteBeanDtos).build();
        TeamModel.TeamModelMessage.Builder teamMessageBuilder=TeamModel.TeamModelMessage.newBuilder();
        teamMessageBuilder.setDataType(TeamModel.TeamModelMessage.DateType.InviteMessageResponse);
        teamMessageBuilder.setInviteMessageResponse(inviteMessageResponse);
        NettyResponse nettyResponse=new NettyResponse();
        nettyResponse.setStateCode(StateCode.SUCCESS);
        nettyResponse.setCmd(ConstantValue.INVITE_MESSAGE_RESPONSE);
        nettyResponse.setData(teamMessageBuilder.build().toByteArray());
        //发给队长
        channel.writeAndFlush(nettyResponse);
    }

    @Override
    @HandlerCmdTag(cmd = ConstantValue.TEAM_MESSAGE_REQUEST,module = ConstantValue.TEAM_MODULE)
    public void teamMessageRequest(TeamModel.TeamModelMessage myMessage, Channel channel) throws InvalidProtocolBufferException {
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
