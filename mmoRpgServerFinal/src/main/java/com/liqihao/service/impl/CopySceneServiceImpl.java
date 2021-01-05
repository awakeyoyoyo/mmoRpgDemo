package com.liqihao.service.impl;

import com.liqihao.Cache.ChannelMessageCache;
import com.liqihao.annotation.HandlerCmdTag;
import com.liqihao.annotation.HandlerServiceTag;
import com.liqihao.commons.ConstantValue;
import com.liqihao.commons.NettyResponse;
import com.liqihao.commons.StateCode;
import com.liqihao.commons.enums.RoleTypeCode;
import com.liqihao.pojo.bean.*;
import com.liqihao.protobufObject.CopySceneModel;
import com.liqihao.provider.CopySceneProvider;
import com.liqihao.provider.TeamServiceProvider;
import com.liqihao.service.CopySceneService;
import com.liqihao.util.CommonsUtil;
import io.netty.channel.Channel;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;

/**
 * 副本模块
 * @author lqhao
 */
@Service
@HandlerServiceTag(protobufModel = "CopySceneModel$CopySceneModelMessage")
public class CopySceneServiceImpl implements CopySceneService {
    @Override
    @HandlerCmdTag(cmd = ConstantValue.COPY_SCENE_MESSAGE_REQUEST,module = ConstantValue.COPY_MODULE)
    public void copySceneMessageRequest(CopySceneModel.CopySceneModelMessage myMessage, MmoSimpleRole mmoSimpleRole) {
        //copySceneBeanId
        //判断是否在线 并且返回玩家对象
        Channel channel= ChannelMessageCache.getInstance().get(mmoSimpleRole.getId());
        Integer teamId=mmoSimpleRole.getTeamId();
        if (teamId==null){
            NettyResponse errorResponse=new NettyResponse(StateCode.FAIL, ConstantValue.FAIL_RESPONSE,"当前角色还没是组队状态".getBytes());
            channel.writeAndFlush(errorResponse);
            return;
        }
        //判断是否已经有进入副本
        Integer copySceneId=mmoSimpleRole.getCopySceneId();
        if (copySceneId==null){
            NettyResponse errorResponse=new NettyResponse(StateCode.FAIL, ConstantValue.FAIL_RESPONSE,"当前角色还未进入副本".getBytes());
            channel.writeAndFlush(errorResponse);
            return;
        }
        Integer copySceneBeanId=TeamServiceProvider.getTeamBeanByTeamId(teamId).getCopySceneBeanId();
        CopySceneBean copySceneBean=CopySceneProvider.getCopySceneBeanById(copySceneBeanId);
        // 返回
        sendCopyMessage(copySceneBean, channel);
    }

    @Override
    @HandlerCmdTag(cmd = ConstantValue.ENTER_COPY_SCENE_REQUEST,module = ConstantValue.COPY_MODULE)
    public void enterCopySceneRequest(CopySceneModel.CopySceneModelMessage myMessage, MmoSimpleRole mmoSimpleRole) {
        //copySceneId
        Integer copySceneId=myMessage.getEnterCopySceneRequest().getCopySceneId();
        Channel channel= ChannelMessageCache.getInstance().get(mmoSimpleRole.getId());
        //判断是否在组队状态
        Integer teamId=mmoSimpleRole.getTeamId();
        if (teamId==null){
            NettyResponse errorResponse=new NettyResponse(StateCode.FAIL, ConstantValue.FAIL_RESPONSE,"非组队状态不能进去".getBytes());
            channel.writeAndFlush(errorResponse);
            return;
        }
        //判断人物是否已经在副本
        if (mmoSimpleRole.getCopySceneId()!=null){
            NettyResponse errorResponse=new NettyResponse(StateCode.FAIL, ConstantValue.FAIL_RESPONSE,"人物已经在副本中，请退出当前你副本再尝试".getBytes());
            channel.writeAndFlush(errorResponse);
            return;
        }
        //判断队伍是否关联了副本
        TeamBean teamBean= TeamServiceProvider.getTeamBeanByTeamId(teamId);
        if (teamBean.getCopySceneBeanId()==null||!teamBean.getCopySceneId().equals(copySceneId)){
            NettyResponse errorResponse=new NettyResponse(StateCode.FAIL, ConstantValue.FAIL_RESPONSE,"当前队伍不在副本中或者队伍不是关联该副本".getBytes());
            channel.writeAndFlush(errorResponse);
            return;
        }
        CopySceneBean copySceneBean=CopySceneProvider.getCopySceneBeanById(teamBean.getCopySceneBeanId());
        mmoSimpleRole.wentCopyScene(copySceneBean);

        // 广播有人进入了副本
        CopySceneModel.RoleDto roleDto=CommonsUtil.mmoSimpleRolesToCopyScneRoleDto(mmoSimpleRole);
        CopySceneModel.CopySceneModelMessage messageData=CopySceneModel.CopySceneModelMessage.newBuilder()
                .setDataType(CopySceneModel.CopySceneModelMessage.DateType.EnterCopySceneResponse)
                .setEnterCopySceneResponse(CopySceneModel.EnterCopySceneResponse.newBuilder().setRoleDto(roleDto).build()).build();
        NettyResponse nettyResponse=new NettyResponse();
        nettyResponse.setCmd(ConstantValue.ENTER_COPY_SCENE_RESPONSE);
        nettyResponse.setStateCode(StateCode.SUCCESS);
        nettyResponse.setData(messageData.toByteArray());
        for (Role m:copySceneBean.getRoles()) {
            if (m.getType().equals(RoleTypeCode.PLAYER.getCode())) {
                Channel c = ChannelMessageCache.getInstance().get(m.getId());
                if (c != null) {
                    c.writeAndFlush(nettyResponse);
                }
            }
        }
    }

    @Override
    @HandlerCmdTag(cmd = ConstantValue.EXIT_COPY_SCENE_REQUEST,module = ConstantValue.COPY_MODULE)
    public void exitCopySceneRequest(CopySceneModel.CopySceneModelMessage myMessage, MmoSimpleRole mmoSimpleRole) {
        Channel channel= ChannelMessageCache.getInstance().get(mmoSimpleRole.getId());
        //判断玩家是否在副本中
        if (mmoSimpleRole.getCopySceneId()==null){
            NettyResponse errorResponse=new NettyResponse(StateCode.FAIL, ConstantValue.FAIL_RESPONSE,"当前玩家不在副本中".getBytes());
            channel.writeAndFlush(errorResponse);
            return;
        }
        TeamBean teamBean=TeamServiceProvider.getTeamBeanByTeamId(mmoSimpleRole.getTeamId());
        Integer copySceneBeanId=teamBean.getCopySceneBeanId();
        CopySceneBean copySceneBean=CopySceneProvider.getCopySceneBeanById(copySceneBeanId);
        copySceneBean.peopleExit(mmoSimpleRole.getId());
        // 发送信息
        CopySceneModel.RoleDto roleDto=CommonsUtil.mmoSimpleRolesToCopyScneRoleDto(mmoSimpleRole);
        CopySceneModel.CopySceneModelMessage messageData=CopySceneModel.CopySceneModelMessage.newBuilder()
                .setDataType(CopySceneModel.CopySceneModelMessage.DateType.ExitCopySceneResponse)
                .setExitCopySceneResponse(CopySceneModel.ExitCopySceneResponse.newBuilder().setRoleDto(roleDto).build()).build();
        NettyResponse nettyResponse=new NettyResponse();
        nettyResponse.setCmd(ConstantValue.EXIT_COPY_SCENE_RESPONSE);
        nettyResponse.setStateCode(StateCode.SUCCESS);
        nettyResponse.setData(messageData.toByteArray());
        for (MmoSimpleRole m:teamBean.getMmoSimpleRoles()) {
            Channel c= ChannelMessageCache.getInstance().get(m.getId());
            if (c!=null) {
                c.writeAndFlush(nettyResponse);
            }
        }

    }

    @Override
    @HandlerCmdTag(cmd = ConstantValue.CREATE_COPY_SCENE_REQUEST,module = ConstantValue.COPY_MODULE)
    public void createCopySceneBeanRequest(CopySceneModel.CopySceneModelMessage myMessage, MmoSimpleRole mmoSimpleRole) {
        Integer copySceneId=myMessage.getCreateCopySceneRequest().getCopySceneId();
        Channel channel= ChannelMessageCache.getInstance().get(mmoSimpleRole.getId());
        //判断是否在组队状态
        if (mmoSimpleRole.getTeamId()==null){
            NettyResponse errorResponse=new NettyResponse(StateCode.FAIL, ConstantValue.FAIL_RESPONSE,"请先组队".getBytes());
            channel.writeAndFlush(errorResponse);
            return;
        }
        TeamBean teamBean=TeamServiceProvider.getTeamBeanByTeamId(mmoSimpleRole.getTeamId());
        //判断是否是队长
        if (!teamBean.getLeaderId().equals(mmoSimpleRole.getId())){
            NettyResponse errorResponse=new NettyResponse(StateCode.FAIL, ConstantValue.FAIL_RESPONSE,"你不是队长没有该权利".getBytes());
            channel.writeAndFlush(errorResponse);
            return;
        }
        //判断队伍是否绑定了副本
        if (teamBean.getCopySceneBeanId()!=null){
            NettyResponse errorResponse=new NettyResponse(StateCode.FAIL, ConstantValue.FAIL_RESPONSE,"该队伍已经绑定副本，无法在创建".getBytes());
            channel.writeAndFlush(errorResponse);
            return;
        }
        CopySceneBean copySceneBean=CopySceneProvider.createNewCopyScene(copySceneId,teamBean);

        teamBean.setCopySceneBeanId(copySceneBean.getCopySceneBeanId());
        teamBean.setCopySceneId(copySceneBean.getId());
        // 创建成功 对队伍的人广播
        for (MmoSimpleRole m:teamBean.getMmoSimpleRoles()) {
            Channel c= ChannelMessageCache.getInstance().get(m.getId());
            if (c!=null) {
                sendSuccessCopyMessage(copySceneBean,c );
            }
        }

    }

    private void sendSuccessCopyMessage(CopySceneBean copySceneBean,Channel channel){
        CopySceneModel.CopySceneBeanDto.Builder copySceneBeanDtoBuilder=CopySceneModel.CopySceneBeanDto.newBuilder();
        List<CopySceneModel.BossBeanDto> bossBeanDtos=new ArrayList<>();
        List<CopySceneModel.RoleDto> roleDtos=new ArrayList<>();
        //bossDto
        for (BossBean boss:copySceneBean.getBossBeans()){
            CopySceneModel.BossBeanDto bossBeanDto=CommonsUtil.bossBeanToBossBeanDto(boss);
            bossBeanDtos.add(bossBeanDto);
        }
        //roleDto
        for (Role role:copySceneBean.getRoles()){
            if (role.getType().equals(RoleTypeCode.PLAYER.getCode())) {
                CopySceneModel.RoleDto roleDto = CommonsUtil.mmoSimpleRolesToCopyScneRoleDto((MmoSimpleRole) role);
                roleDtos.add(roleDto);
            }
        }
        CopySceneModel.BossBeanDto bossBeanDto=null;

        if (copySceneBean.getNowBoss()!=null) {
            bossBeanDto = CommonsUtil.bossBeanToBossBeanDto(copySceneBean.getNowBoss());
        }else {
            bossBeanDto = CopySceneModel.BossBeanDto.newBuilder().setId(-1).build();
        }
        CopySceneModel.CopySceneModelMessage messageData=CopySceneModel.CopySceneModelMessage.newBuilder()
                .setDataType(CopySceneModel.CopySceneModelMessage.DateType.CreateCopySceneResponse)
                .setCreateCopySceneResponse(CopySceneModel.CreateCopySceneResponse.newBuilder()
                        .setCopySceneBeanDto(copySceneBeanDtoBuilder
                                .addAllRoleDto(roleDtos).addAllBossBeans(bossBeanDtos)
                                .setNowBoss(bossBeanDto)
                                .setCopySceneId(copySceneBean.getId()).setCopySceneBeanId(copySceneBean.getCopySceneBeanId())
                                .setStatus(copySceneBean.getStatus())
                                .setCreateTime(copySceneBean.getCreateTime())
                                .setEndTime(copySceneBean.getEndTime())
                                .build()).build())
                .build();
        NettyResponse nettyResponse=new NettyResponse();
        nettyResponse.setCmd(ConstantValue.CREATE_COPY_SCENE_RESPONSE);
        nettyResponse.setStateCode(StateCode.SUCCESS);
        nettyResponse.setData(messageData.toByteArray());
        channel.writeAndFlush(nettyResponse);
    }

    private void sendCopyMessage(CopySceneBean copySceneBean,Channel channel){
        CopySceneModel.CopySceneBeanDto.Builder copySceneBeanDtoBuilder=CopySceneModel.CopySceneBeanDto.newBuilder();
        List<CopySceneModel.BossBeanDto> bossBeanDtos=new ArrayList<>();
        List<CopySceneModel.RoleDto> roleDtos=new ArrayList<>();
        //bossDto
        for (BossBean boss:copySceneBean.getBossBeans()){
            CopySceneModel.BossBeanDto bossBeanDto=CommonsUtil.bossBeanToBossBeanDto(boss);
            bossBeanDtos.add(bossBeanDto);
        }
        //roleDto
        for (Role role:copySceneBean.getRoles()){
            if (role.getType().equals(RoleTypeCode.PLAYER.getCode())) {
                CopySceneModel.RoleDto roleDto = CommonsUtil.mmoSimpleRolesToCopyScneRoleDto((MmoSimpleRole) role);
                roleDtos.add(roleDto);
            }
        }
        CopySceneModel.BossBeanDto nowBoss=CommonsUtil.bossBeanToBossBeanDto(copySceneBean.getNowBoss());
        CopySceneModel.CopySceneModelMessage messageData=CopySceneModel.CopySceneModelMessage.newBuilder()
                .setDataType(CopySceneModel.CopySceneModelMessage.DateType.CopySceneMessageResponse)
                .setCopySceneMessageResponse(CopySceneModel.CopySceneMessageResponse.newBuilder()
                        .setCopySceneBeanDto(copySceneBeanDtoBuilder
                                .addAllRoleDto(roleDtos).addAllBossBeans(bossBeanDtos)
                                .setNowBoss(nowBoss)
                                .setCopySceneId(copySceneBean.getId()).setCopySceneBeanId(copySceneBean.getCopySceneBeanId())
                                .setStatus(copySceneBean.getStatus())
                                .setCreateTime(copySceneBean.getCreateTime())
                                .setEndTime(copySceneBean.getEndTime())
                                .build()).build())
                .build();
        NettyResponse nettyResponse=new NettyResponse();
        nettyResponse.setCmd(ConstantValue.COPY_SCENE_MESSAGE_RESPONSE);
        nettyResponse.setStateCode(StateCode.SUCCESS);
        nettyResponse.setData(messageData.toByteArray());
        channel.writeAndFlush(nettyResponse);
    }
}
