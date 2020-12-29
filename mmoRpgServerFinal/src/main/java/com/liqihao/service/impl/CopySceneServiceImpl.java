package com.liqihao.service.impl;

import com.liqihao.Cache.ChannelMessageCache;
import com.liqihao.Cache.CopySceneMessageCache;
import com.liqihao.annotation.HandlerCmdTag;
import com.liqihao.annotation.HandlerServiceTag;
import com.liqihao.commons.ConstantValue;
import com.liqihao.commons.NettyResponse;
import com.liqihao.commons.StateCode;
import com.liqihao.pojo.baseMessage.CopySceneMessage;
import com.liqihao.pojo.bean.BossBean;
import com.liqihao.pojo.bean.CopySceneBean;
import com.liqihao.pojo.bean.MmoSimpleRole;
import com.liqihao.pojo.bean.TeamBean;
import com.liqihao.protobufObject.CopySceneModel;
import com.liqihao.protobufObject.TeamModel;
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
    @HandlerCmdTag(cmd = ConstantValue.ASK_CAN_COPYSCENE_REQUEST,module = ConstantValue.COPY_MODULE)
    public void askCanCopySceneRequest(CopySceneModel.CopySceneModelMessage myMessage, Channel channel) {
        //判断是否在线 并且返回玩家对象
        MmoSimpleRole mmoSimpleRole= CommonsUtil.checkLogin(channel);
        if (mmoSimpleRole==null) {
            return;
        }
        //roleId
        //TODO 增加限制条件
        //暂无限制，可以挑战所有副本
        List<Integer> copySceneId=new ArrayList<>();
        for (CopySceneMessage cMsg:CopySceneMessageCache.getInstance().values()) {
            copySceneId.add(cMsg.getId());
        }
        // 返回
        CopySceneModel.CopySceneModelMessage messageData=CopySceneModel.CopySceneModelMessage.newBuilder()
                .setDataType(CopySceneModel.CopySceneModelMessage.DateType.AskCanCopySceneResponse)
                .setAskCanCopySceneResponse(CopySceneModel.AskCanCopySceneResponse.newBuilder().addAllCopySceneId(copySceneId).build())
                .build();
        NettyResponse nettyResponse=new NettyResponse();
        nettyResponse.setCmd(ConstantValue.ASK_CAN_COPYSCENE_RESPONSE);
        nettyResponse.setStateCode(StateCode.SUCCESS);
        nettyResponse.setData(messageData.toByteArray());
        channel.writeAndFlush(nettyResponse);
    }

    @Override
    @HandlerCmdTag(cmd = ConstantValue.COPYSCENE_MESSAGE_REQUEST,module = ConstantValue.COPY_MODULE)
    public void copySceneMessageRequest(CopySceneModel.CopySceneModelMessage myMessage, Channel channel) {
        //copySceneBeanId
        //判断是否在线 并且返回玩家对象
        MmoSimpleRole mmoSimpleRole= CommonsUtil.checkLogin(channel);
        if (mmoSimpleRole==null) {
            return;
        }
        Integer teamId=mmoSimpleRole.getTeamId();
        if (teamId==null){
            NettyResponse errotResponse=new NettyResponse(StateCode.FAIL, ConstantValue.FAIL_RESPONSE,"当前角色还没是组队状态".getBytes());
            channel.writeAndFlush(errotResponse);
            return;
        }
        //判断是否已经有进入副本
        Integer copySceneId=mmoSimpleRole.getCopySceneId();
        if (copySceneId==null){
            NettyResponse errotResponse=new NettyResponse(StateCode.FAIL, ConstantValue.FAIL_RESPONSE,"当前角色还未进入副本".getBytes());
            channel.writeAndFlush(errotResponse);
            return;
        }
        Integer copySceneBeanId=TeamServiceProvider.getTeamBeanByTeamId(teamId).getCopySceneBeanId();
        CopySceneBean copySceneBean=CopySceneProvider.getCopySceneBeanById(copySceneBeanId);
        // 返回
        sendCopyMessage(copySceneBean,channel);
    }

    @Override
    @HandlerCmdTag(cmd = ConstantValue.ENTER_COPYSCENE_REQUEST,module = ConstantValue.COPY_MODULE)
    public void enterCopySceneRequest(CopySceneModel.CopySceneModelMessage myMessage, Channel channel) {
        //copySceneId
        Integer copySceneId=myMessage.getEnterCopySceneRequest().getCopySceneId();
        MmoSimpleRole mmoSimpleRole= CommonsUtil.checkLogin(channel);
        if (mmoSimpleRole==null) {
            return;
        }
        //判断是否在组队状态
        Integer teamId=mmoSimpleRole.getTeamId();
        if (teamId==null){
            NettyResponse errotResponse=new NettyResponse(StateCode.FAIL, ConstantValue.FAIL_RESPONSE,"非组队状态不能进去".getBytes());
            channel.writeAndFlush(errotResponse);
            return;
        }
        //判断人物是否已经在副本
        if (mmoSimpleRole.getCopySceneId()!=null){
            NettyResponse errotResponse=new NettyResponse(StateCode.FAIL, ConstantValue.FAIL_RESPONSE,"人物已经在副本中，请退出当前你副本再尝试".getBytes());
            channel.writeAndFlush(errotResponse);
            return;
        }
        //判断队伍是否关联了副本
        TeamBean teamBean= TeamServiceProvider.getTeamBeanByTeamId(teamId);
        if (teamBean.getCopySceneBeanId()==null||!teamBean.getCopySceneId().equals(copySceneId)){
            NettyResponse errotResponse=new NettyResponse(StateCode.FAIL, ConstantValue.FAIL_RESPONSE,"当前队伍不在副本中或者队伍不是关联该副本".getBytes());
            channel.writeAndFlush(errotResponse);
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
        nettyResponse.setCmd(ConstantValue.ENTER_COPYSCENE_RESPONSE);
        nettyResponse.setStateCode(StateCode.SUCCESS);
        nettyResponse.setData(messageData.toByteArray());
        for (MmoSimpleRole m:copySceneBean.getMmoSimpleRoles()) {
            Channel c= ChannelMessageCache.getInstance().get(m.getId());
            if (c!=null) {
                c.writeAndFlush(nettyResponse);
            }
        }
    }

    @Override
    @HandlerCmdTag(cmd = ConstantValue.EXIT_COPYSCENE_REQUEST,module = ConstantValue.COPY_MODULE)
    public void exitCopySceneRequest(CopySceneModel.CopySceneModelMessage myMessage, Channel channel) {
        MmoSimpleRole mmoSimpleRole= CommonsUtil.checkLogin(channel);
        if (mmoSimpleRole==null) {
            return;
        }
        //判断玩家是否在副本中
        if (mmoSimpleRole.getCopySceneId()==null){
            NettyResponse errotResponse=new NettyResponse(StateCode.FAIL, ConstantValue.FAIL_RESPONSE,"当前玩家不在副本中".getBytes());
            channel.writeAndFlush(errotResponse);
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
        nettyResponse.setCmd(ConstantValue.EXIT_COPYSCENE_RESPONSE);
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
    @HandlerCmdTag(cmd = ConstantValue.CREATE_COPYSCENE_REQUEST,module = ConstantValue.COPY_MODULE)
    public void createCopySceneBeanRequest(CopySceneModel.CopySceneModelMessage myMessage, Channel channel) {
        Integer copySceneId=myMessage.getCreateCopySceneRequest().getCopySceneId();
        MmoSimpleRole mmoSimpleRole= CommonsUtil.checkLogin(channel);
        if (mmoSimpleRole==null) {
            return;
        }
        //判断是否在组队状态
        if (mmoSimpleRole.getTeamId()==null){
            NettyResponse errotResponse=new NettyResponse(StateCode.FAIL, ConstantValue.FAIL_RESPONSE,"请先组队".getBytes());
            channel.writeAndFlush(errotResponse);
            return;
        }
        TeamBean teamBean=TeamServiceProvider.getTeamBeanByTeamId(mmoSimpleRole.getTeamId());
        //判断是否是队长
        if (!teamBean.getLeaderId().equals(mmoSimpleRole.getId())){
            NettyResponse errotResponse=new NettyResponse(StateCode.FAIL, ConstantValue.FAIL_RESPONSE,"你不是队长没有该权利".getBytes());
            channel.writeAndFlush(errotResponse);
            return;
        }
        //判断队伍是否绑定了副本
        if (teamBean.getCopySceneBeanId()!=null){
            NettyResponse errotResponse=new NettyResponse(StateCode.FAIL, ConstantValue.FAIL_RESPONSE,"该队伍已经绑定副本，无法在创建".getBytes());
            channel.writeAndFlush(errotResponse);
            return;
        }
        CopySceneBean copySceneBean=CopySceneProvider.createNewCopyScene(copySceneId);

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
        for (MmoSimpleRole role:copySceneBean.getMmoSimpleRoles()){
            CopySceneModel.RoleDto roleDto=CommonsUtil.mmoSimpleRolesToCopyScneRoleDto(role);
            roleDtos.add(roleDto);
        }
        CopySceneModel.CopySceneModelMessage messageData=CopySceneModel.CopySceneModelMessage.newBuilder()
                .setDataType(CopySceneModel.CopySceneModelMessage.DateType.CreateCopySceneResponse)
                .setCreateCopySceneResponse(CopySceneModel.CreateCopySceneResponse.newBuilder()
                        .setCopySceneBeanDto(copySceneBeanDtoBuilder
                                .addAllRoleDto(roleDtos).addAllBossBeans(bossBeanDtos)
                                .setCopySceneId(copySceneBean.getId()).setCopySceneBeanId(copySceneBean.getCopySceneBeanId())
                                .setStatus(copySceneBean.getStatus())
                                .setCreateTime(copySceneBean.getCreateTime())
                                .setEndTime(copySceneBean.getEndTime())
                                .build()).build())
                .build();
        NettyResponse nettyResponse=new NettyResponse();
        nettyResponse.setCmd(ConstantValue.CREATE_COPYSCENE_RESPONSE);
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
        for (MmoSimpleRole role:copySceneBean.getMmoSimpleRoles()){
            CopySceneModel.RoleDto roleDto=CommonsUtil.mmoSimpleRolesToCopyScneRoleDto(role);
            roleDtos.add(roleDto);
        }
        CopySceneModel.CopySceneModelMessage messageData=CopySceneModel.CopySceneModelMessage.newBuilder()
                .setDataType(CopySceneModel.CopySceneModelMessage.DateType.CopySceneMessageResponse)
                .setCopySceneMessageResponse(CopySceneModel.CopySceneMessageResponse.newBuilder()
                        .setCopySceneBeanDto(copySceneBeanDtoBuilder
                                .addAllRoleDto(roleDtos).addAllBossBeans(bossBeanDtos)
                                .setCopySceneId(copySceneBean.getId()).setCopySceneBeanId(copySceneBean.getCopySceneBeanId())
                                .setStatus(copySceneBean.getStatus())
                                .setCreateTime(copySceneBean.getCreateTime())
                                .setEndTime(copySceneBean.getEndTime())
                                .build()).build())
                .build();
        NettyResponse nettyResponse=new NettyResponse();
        nettyResponse.setCmd(ConstantValue.COPYSCENE_MESSAGE_RESPONSE);
        nettyResponse.setStateCode(StateCode.SUCCESS);
        nettyResponse.setData(messageData.toByteArray());
        channel.writeAndFlush(nettyResponse);
    }
}
