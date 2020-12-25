package com.liqihao.service.impl;

import com.google.protobuf.InvalidProtocolBufferException;
import com.liqihao.commons.NettyResponse;
import com.liqihao.protobufObject.TeamModel;
import com.liqihao.service.TeamService;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Service
public class TeamServiceImpl implements TeamService {
    @Override
    public void teamMessageResponse(NettyResponse nettyResponse) throws InvalidProtocolBufferException {
        byte[] data = nettyResponse.getData();
        TeamModel.TeamModelMessage myMessage;
        myMessage = TeamModel.TeamModelMessage.parseFrom(data);
        TeamModel.TeamMessageResponse messageResponse = myMessage.getTeamMessageResponse();
        TeamModel.TeamBeanDto teamBeanDto = messageResponse.getTeamBeanDto();
        System.out.println("--------------------------------------------------------");
        System.out.println("队伍的id： " + teamBeanDto.getTeamId() + " 队伍的名称： " + teamBeanDto.getTeamName());
        System.out.println("队长的id： " + teamBeanDto.getLeaderId());
        for (TeamModel.RoleDto r : teamBeanDto.getRoleDtosList()) {
            System.out.println("角色id： " + r.getId() + "角色名称： " + r.getName() + "  Hp:" + r.getNowHp() + "/" + r.getHp()
                    + "  Mp:" + r.getNowMP() + "/" + r.getMp() + " 所在队伍id： " + r.getTeamId()
            );
        }
        System.out.println("--------------------------------------------------------");
    }

    @Override
    public void applyForTeamResponse(NettyResponse nettyResponse) throws InvalidProtocolBufferException {
        byte[] data = nettyResponse.getData();
        TeamModel.TeamModelMessage myMessage;
        myMessage = TeamModel.TeamModelMessage.parseFrom(data);
        TeamModel.ApplyInviteBeanDto applyInviteBeanDto = myMessage.getApplyForTeamResponse().getApplyInviteBeanDtos();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss SSS");
        Date create = new Date(applyInviteBeanDto.getCreateTime());
        String createStr = sdf.format(create);
        Date end = new Date(applyInviteBeanDto.getEndTime());
        String endStr = sdf.format(end);
        System.out.println("--------------------------------------------------------");
        System.out.println("玩家id： " + applyInviteBeanDto.getRoleId() + " 申请加入 " + applyInviteBeanDto.getTeamName() + " 队伍" +
                " 申请时间：" + createStr + " 申请的队伍id： "
                + applyInviteBeanDto.getTeamId() + " 该申请过期时间: " + endStr);
    }

    @Override
    public void invitePeopleResponse(NettyResponse nettyResponse) throws InvalidProtocolBufferException {
        byte[] data = nettyResponse.getData();
        TeamModel.TeamModelMessage myMessage;
        myMessage = TeamModel.TeamModelMessage.parseFrom(data);
        TeamModel.ApplyInviteBeanDto inviteBeanDto = myMessage.getInvitePeopleResponse().getApplyInviteBeanDtos();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss SSS");
        Date create = new Date(inviteBeanDto.getCreateTime());
        String createStr = sdf.format(create);
        Date end = new Date(inviteBeanDto.getEndTime());
        String endStr = sdf.format(end);
        System.out.println("--------------------------------------------------------");
        System.out.println(inviteBeanDto.getTeamName() + " 队伍邀请你加入" + " 你的角色id：" + inviteBeanDto.getRoleId() +
                " 邀请时间：" + createStr + " 邀请的队伍id： "
                + inviteBeanDto.getTeamId() + " 该邀请过期时间: " + endStr);
    }

    @Override
    public void applyMessageResponse(NettyResponse nettyResponse) throws InvalidProtocolBufferException {
        byte[] data = nettyResponse.getData();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss SSS");
        TeamModel.TeamModelMessage myMessage;
        myMessage = TeamModel.TeamModelMessage.parseFrom(data);
        List<TeamModel.ApplyInviteBeanDto> applyInviteBeanDtos = myMessage.getApplyMessageResponse().getApplyInviteBeanDtosList();
        System.out.println("--------------------------------------------------------");
        for (TeamModel.ApplyInviteBeanDto applyInviteBeanDto : applyInviteBeanDtos) {
            Date create = new Date(applyInviteBeanDto.getCreateTime());
            String createStr = sdf.format(create);
            Date end = new Date(applyInviteBeanDto.getEndTime());
            String endStr = sdf.format(end);
            System.out.println("玩家id： " + applyInviteBeanDto.getRoleId() + " 申请加入 " + applyInviteBeanDto.getTeamName() + " 队伍" +
                    " 申请时间：" + createStr + " 申请的队伍id： "
                    + applyInviteBeanDto.getTeamId() + " 该申请过期时间: " + endStr);
            System.out.println("--------------------------------------------------------");
        }
    }

    @Override
    public void inviteMessageResponse(NettyResponse nettyResponse) throws InvalidProtocolBufferException {
        byte[] data = nettyResponse.getData();
        TeamModel.TeamModelMessage myMessage;
        myMessage = TeamModel.TeamModelMessage.parseFrom(data);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss SSS");
        List<TeamModel.ApplyInviteBeanDto> inviteBeanDtos = myMessage.getInviteMessageResponse().getApplyInviteBeanDtosList();
        System.out.println("--------------------------------------------------------");
        for (TeamModel.ApplyInviteBeanDto inviteBeanDto : inviteBeanDtos) {
            Date create = new Date(inviteBeanDto.getCreateTime());
            String createStr = sdf.format(create);
            Date end = new Date(inviteBeanDto.getEndTime());
            String endStr = sdf.format(end);
            System.out.println(inviteBeanDto.getTeamName() + " 队伍邀请你加入" + " 你的角色id：" + inviteBeanDto.getRoleId() +
                    " 邀请时间：" + createStr + " 邀请的队伍id： "
                    + inviteBeanDto.getTeamId() + " 该邀请过期时间: " + endStr);
            System.out.println("--------------------------------------------------------");

        }
    }

    @Override
    public void refuseInviteResponse(NettyResponse nettyResponse) throws InvalidProtocolBufferException {
        byte[] data = nettyResponse.getData();
        TeamModel.TeamModelMessage myMessage;
        myMessage = TeamModel.TeamModelMessage.parseFrom(data);
        String roleName=myMessage.getRefuseInviteResponse().getRoleName();
        System.out.println("--------------------------------------------------------");
        System.out.println("用户： "+roleName+" 拒绝了你的队伍邀请");
        System.out.println("--------------------------------------------------------");

    }

    @Override
    public void refuseApplyResponse(NettyResponse nettyResponse) throws InvalidProtocolBufferException {
        byte[] data = nettyResponse.getData();
        TeamModel.TeamModelMessage myMessage;
        myMessage = TeamModel.TeamModelMessage.parseFrom(data);
        String teamName=myMessage.getRefuseApplyResponse().getTeamName();
        System.out.println("--------------------------------------------------------");
        System.out.println("队伍： "+teamName+" 拒绝了你的队伍申请");
        System.out.println("--------------------------------------------------------");
    }
}
