package com.liqihao.service.impl;

import com.google.protobuf.InvalidProtocolBufferException;
import com.liqihao.annotation.HandlerCmdTag;
import com.liqihao.annotation.HandlerServiceTag;
import com.liqihao.commons.ConstantValue;
import com.liqihao.commons.NettyResponse;
import com.liqihao.commons.RpgServerException;
import com.liqihao.commons.StateCode;
import com.liqihao.commons.enums.GuildAuthorityCode;
import com.liqihao.commons.enums.GuildRolePositionCode;
import com.liqihao.pojo.bean.guildBean.GuildApplyBean;
import com.liqihao.pojo.bean.guildBean.GuildBean;
import com.liqihao.pojo.bean.guildBean.GuildRoleBean;
import com.liqihao.pojo.bean.roleBean.MmoSimpleRole;
import com.liqihao.protobufObject.GuildModel;
import com.liqihao.protobufObject.PlayModel;
import com.liqihao.provider.GuildServiceProvider;
import com.liqihao.service.GuildService;
import com.liqihao.util.CommonsUtil;
import io.netty.channel.Channel;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 工会模块
 * @author lqhao
 */
@Service
@HandlerServiceTag(protobufModel = "GuildModel$GuildModelMessage")
public class GuildServiceImpl implements GuildService {
    @Override
    @HandlerCmdTag(cmd = ConstantValue.CREATE_GUILD_REQUEST, module = ConstantValue.GUILD_MODULE)
    public void createGuild(GuildModel.GuildModelMessage myMessage, MmoSimpleRole mmoSimpleRole) throws InvalidProtocolBufferException, RpgServerException {
        Channel channel = mmoSimpleRole.getChannel();
        String guildName=myMessage.getCreateGuildRequest().getName();
        if (mmoSimpleRole.getGuildBean()!=null){
            throw new RpgServerException(StateCode.FAIL,"已经身处在一个公会中");
        }
        GuildServiceProvider.getInstance().createGuildBean(mmoSimpleRole,guildName);
        //返回成功的数据包
        NettyResponse nettyResponse = new NettyResponse();
        nettyResponse.setCmd(ConstantValue.CREATE_GUILD_RESPONSE);
        nettyResponse.setStateCode(StateCode.SUCCESS);
        //protobuf 生成registerResponse
        GuildModel.GuildModelMessage.Builder messageData = GuildModel.GuildModelMessage.newBuilder();
        messageData.setDataType(GuildModel.GuildModelMessage.DateType.CreateGuildResponse);
        GuildModel.CreateGuildResponse.Builder createGuildResponseBuilder = GuildModel.CreateGuildResponse.newBuilder();
        messageData.setCreateGuildResponse(createGuildResponseBuilder.build());
        nettyResponse.setData(messageData.build().toByteArray());
        channel.writeAndFlush(nettyResponse);
        return;
    }

    @Override
    @HandlerCmdTag(cmd = ConstantValue.JOIN_GUILD_REQUEST, module = ConstantValue.GUILD_MODULE)
    public void joinGuild(GuildModel.GuildModelMessage myMessage, MmoSimpleRole mmoSimpleRole) throws InvalidProtocolBufferException, RpgServerException {
        Channel channel = mmoSimpleRole.getChannel();
        Integer guildId=myMessage.getJoinGuildRequest().getGuildId();
        if (mmoSimpleRole.getGuildBean()!=null){
            throw new RpgServerException(StateCode.FAIL,"已经身处在一个公会中");
        }
        GuildServiceProvider.getInstance().applyGuild(mmoSimpleRole,guildId);
        //返回成功的数据包
        NettyResponse nettyResponse = new NettyResponse();
        nettyResponse.setCmd(ConstantValue.JOIN_GUILD_RESPONSE);
        nettyResponse.setStateCode(StateCode.SUCCESS);
        //protobuf 生成registerResponse
        GuildModel.GuildModelMessage.Builder messageData = GuildModel.GuildModelMessage.newBuilder();
        messageData.setDataType(GuildModel.GuildModelMessage.DateType.JoinGuildResponse);
        GuildModel.JoinGuildResponse.Builder joinGuildResponseBuilder = GuildModel.JoinGuildResponse.newBuilder();
        messageData.setJoinGuildResponse(joinGuildResponseBuilder.build());
        nettyResponse.setData(messageData.build().toByteArray());
        channel.writeAndFlush(nettyResponse);
        return;
    }

    @Override
    @HandlerCmdTag(cmd = ConstantValue.SET_GUILD_POSITION_REQUEST, module = ConstantValue.GUILD_MODULE)
    public void setGuildPosition(GuildModel.GuildModelMessage myMessage, MmoSimpleRole mmoSimpleRole) throws InvalidProtocolBufferException, RpgServerException {
        Channel channel = mmoSimpleRole.getChannel();
        Integer roleId=myMessage.getSetGuildRequest().getRoleId();
        Integer position=myMessage.getSetGuildRequest().getGuildPosition();
        if (mmoSimpleRole.getGuildBean()==null){
            throw new RpgServerException(StateCode.FAIL,"当前没有加入任何公会");
        }
        if (GuildServiceProvider.getInstance().checkHasAuthority(mmoSimpleRole, GuildAuthorityCode.SET_POSITION.getCode())){
            throw new RpgServerException(StateCode.FAIL,"没有该权限");
        }
        if (GuildRolePositionCode.getValue(position)==null){
            throw new RpgServerException(StateCode.FAIL,"没有该职位");
        }
        //如果要将别人确立为会长，自己则退回普通成员
        if (position.equals(GuildRolePositionCode.HUI_ZHANG.getCode())){
            mmoSimpleRole.getGuildBean().setRolePosition(mmoSimpleRole.getId(),GuildRolePositionCode.COMMON_PEOPLE.getCode());
        }
        mmoSimpleRole.getGuildBean().setRolePosition(roleId,position);
        //返回成功的数据包
        NettyResponse nettyResponse = new NettyResponse();
        nettyResponse.setCmd(ConstantValue.SET_GUILD_POSITION_RESPONSE);
        nettyResponse.setStateCode(StateCode.SUCCESS);
        //protobuf 生成registerResponse
        GuildModel.GuildModelMessage.Builder messageData = GuildModel.GuildModelMessage.newBuilder();
        messageData.setDataType(GuildModel.GuildModelMessage.DateType.SetGuildResponse);
        GuildModel.SetGuildResponse.Builder setGuildResponseBuilder = GuildModel.SetGuildResponse.newBuilder();
        messageData.setSetGuildResponse(setGuildResponseBuilder.build());
        nettyResponse.setData(messageData.build().toByteArray());
        channel.writeAndFlush(nettyResponse);
        return;
    }

    @Override
    @HandlerCmdTag(cmd = ConstantValue.OUT_GUILD_REQUEST, module = ConstantValue.GUILD_MODULE)
    public void outGuild(GuildModel.GuildModelMessage myMessage, MmoSimpleRole mmoSimpleRole) throws InvalidProtocolBufferException, RpgServerException {
        Channel channel = mmoSimpleRole.getChannel();
        if (mmoSimpleRole.getGuildBean()==null){
            throw new RpgServerException(StateCode.FAIL,"当前没有加入任何公会");
        }
        if (mmoSimpleRole.getGuildBean().getChairmanId().equals(mmoSimpleRole.getId())){
            throw new RpgServerException(StateCode.FAIL,"你是会长不能退出公会，请先确立新的会长");
        }
        mmoSimpleRole.getGuildBean().leaveGuild(mmoSimpleRole.getId());
        //返回成功的数据包
        NettyResponse nettyResponse = new NettyResponse();
        nettyResponse.setCmd(ConstantValue.OUT_GUILD_RESPONSE);
        nettyResponse.setStateCode(StateCode.SUCCESS);
        //protobuf 生成registerResponse
        GuildModel.GuildModelMessage.Builder messageData = GuildModel.GuildModelMessage.newBuilder();
        messageData.setDataType(GuildModel.GuildModelMessage.DateType.OutGuildResponse);
        GuildModel.OutGuildResponse.Builder outGuildResponseBuilder = GuildModel.OutGuildResponse.newBuilder();
        messageData.setOutGuildResponse(outGuildResponseBuilder.build());
        nettyResponse.setData(messageData.build().toByteArray());
        channel.writeAndFlush(nettyResponse);
    }

    @Override
    public void contributeMoney(GuildModel.GuildModelMessage myMessage, MmoSimpleRole mmoSimpleRole) throws InvalidProtocolBufferException {

    }

    @Override
    public void contributeArticle(GuildModel.GuildModelMessage myMessage, MmoSimpleRole mmoSimpleRole) throws InvalidProtocolBufferException {

    }

    @Override
    public void getArticle(GuildModel.GuildModelMessage myMessage, MmoSimpleRole mmoSimpleRole) throws InvalidProtocolBufferException {

    }

    @Override
    public void getMoney(GuildModel.GuildModelMessage myMessage, MmoSimpleRole mmoSimpleRole) throws InvalidProtocolBufferException {

    }

    @Override
    @HandlerCmdTag(cmd = ConstantValue.GET_GUILD_APPLY_LIST_REQUEST, module = ConstantValue.GUILD_MODULE)
    public void getGuildApplyList(GuildModel.GuildModelMessage myMessage, MmoSimpleRole mmoSimpleRole) throws InvalidProtocolBufferException, RpgServerException {
        Channel channel = mmoSimpleRole.getChannel();
        if (mmoSimpleRole.getGuildBean()==null){
            throw new RpgServerException(StateCode.FAIL,"当前没有加入任何公会");
        }
        List<GuildApplyBean> guildApplyBeanList=mmoSimpleRole.getGuildBean().getGuildApplyBeans();
        List<GuildModel.GuildApplyDto> guildApplyDtos=new ArrayList<>();
        for (GuildApplyBean guildApplyBean:guildApplyBeanList) {
            GuildModel.GuildApplyDto guildApplyDto= CommonsUtil.guildApplyBeanToGuildApplyDto(guildApplyBean);
            guildApplyDtos.add(guildApplyDto);
        }
        //返回成功的数据包
        NettyResponse nettyResponse = new NettyResponse();
        nettyResponse.setCmd(ConstantValue.GET_GUILD_APPLY_LIST_RESPONSE);
        nettyResponse.setStateCode(StateCode.SUCCESS);
        //protobuf 生成registerResponse
        GuildModel.GuildModelMessage.Builder messageData = GuildModel.GuildModelMessage.newBuilder();
        messageData.setDataType(GuildModel.GuildModelMessage.DateType.GetGuildApplyListResponse);
        GuildModel.GetGuildApplyListResponse.Builder getGuildApplyListResponseBuilder = GuildModel.GetGuildApplyListResponse.newBuilder().addAllGuildApplyDtos(guildApplyDtos);
        messageData.setGetGuildApplyListResponse(getGuildApplyListResponseBuilder.build());
        nettyResponse.setData(messageData.build().toByteArray());
        channel.writeAndFlush(nettyResponse);
    }

    @Override
    @HandlerCmdTag(cmd = ConstantValue.GET_GUILD_MESSAGE_REQUEST, module = ConstantValue.GUILD_MODULE)
    public void getGuildBean(GuildModel.GuildModelMessage myMessage, MmoSimpleRole mmoSimpleRole) throws InvalidProtocolBufferException, RpgServerException {
        Channel channel = mmoSimpleRole.getChannel();
        Integer guildBeanId=myMessage.getGetGuildBeanRequest().getGuildBeanId();
        GuildBean guildBean=GuildServiceProvider.getInstance().getGuildBeanById(guildBeanId);
        if (guildBean==null){
            throw new RpgServerException(StateCode.FAIL,"不存在该公会");
        }
        //生成protobuf数据
        List<GuildModel.GuildPeopleDto> guildPeopleDtos=new ArrayList<>();
        for (GuildRoleBean guildRoleBean : guildBean.getGuildRoleBeans()) {
            GuildModel.GuildPeopleDto guildPeopleDto=CommonsUtil.guildRoleBeanToGuildPeopleDto(guildRoleBean);
            guildPeopleDtos.add(guildPeopleDto);
        }
        GuildModel.GuildDto guildDto=GuildModel.GuildDto.newBuilder()
                .setId(guildBean.getId()).setCreateTime(guildBean.getCreateTime()).setName(guildBean.getName())
                .setLevel(guildBean.getLevel()).setChairmanId(guildBean.getChairmanId())
                .addAllGuildPeopleDtos(guildPeopleDtos)
                .setPeopleNum(guildBean.getPeopleNum()).build();
        //返回成功的数据包
        NettyResponse nettyResponse = new NettyResponse();
        nettyResponse.setCmd(ConstantValue.GET_GUILD_MESSAGE_RESPONSE);
        nettyResponse.setStateCode(StateCode.SUCCESS);
        //protobuf 生成registerResponse
        GuildModel.GuildModelMessage.Builder messageData = GuildModel.GuildModelMessage.newBuilder();
        messageData.setDataType(GuildModel.GuildModelMessage.DateType.GetGuildBeanResponse);
        GuildModel.GetGuildBeanResponse.Builder getGuildBeanResponseBuilder = GuildModel.GetGuildBeanResponse.newBuilder().setGuildDto(guildDto);
        messageData.setGetGuildBeanResponse(getGuildBeanResponseBuilder.build());
        nettyResponse.setData(messageData.build().toByteArray());
        channel.writeAndFlush(nettyResponse);
    }

    @Override
    @HandlerCmdTag(cmd = ConstantValue.AGREE_GUILD_APPLY_REQUEST, module = ConstantValue.GUILD_MODULE)
    public void agreeGuildApply(GuildModel.GuildModelMessage myMessage, MmoSimpleRole mmoSimpleRole) throws InvalidProtocolBufferException, RpgServerException {
        Channel channel = mmoSimpleRole.getChannel();
        Integer guildApplyId=myMessage.getAgreeGuildApplyRequest().getGuildApplyId();
        mmoSimpleRole.getGuildBean().agreeApply(guildApplyId);
        //返回成功的数据包
        NettyResponse nettyResponse = new NettyResponse();
        nettyResponse.setCmd(ConstantValue.AGREE_GUILD_APPLY_RESPONSE);
        nettyResponse.setStateCode(StateCode.SUCCESS);
        //protobuf 生成registerResponse
        GuildModel.GuildModelMessage.Builder messageData = GuildModel.GuildModelMessage.newBuilder();
        messageData.setDataType(GuildModel.GuildModelMessage.DateType.AgreeGuildApplyResponse);
        GuildModel.AgreeGuildApplyResponse.Builder agreeGuildApplyResponseBuilder = GuildModel.AgreeGuildApplyResponse.newBuilder();
        messageData.setAgreeGuildApplyResponse(agreeGuildApplyResponseBuilder.build());
        nettyResponse.setData(messageData.build().toByteArray());
        channel.writeAndFlush(nettyResponse);
    }

    @Override
    @HandlerCmdTag(cmd = ConstantValue.REFUSE_GUILD_APPLY_REQUEST, module = ConstantValue.GUILD_MODULE)
    public void refuseGuildApply(GuildModel.GuildModelMessage myMessage, MmoSimpleRole mmoSimpleRole) throws InvalidProtocolBufferException, RpgServerException {
        Channel channel = mmoSimpleRole.getChannel();
        Integer guildApplyId=myMessage.getRefuseGuildApplyRequest().getGuildApplyId();
        mmoSimpleRole.getGuildBean().refuseApply(guildApplyId);
        //返回成功的数据包
        NettyResponse nettyResponse = new NettyResponse();
        nettyResponse.setCmd(ConstantValue.REFUSE_GUILD_APPLY_RESPONSE);
        nettyResponse.setStateCode(StateCode.SUCCESS);
        //protobuf 生成registerResponse
        GuildModel.GuildModelMessage.Builder messageData = GuildModel.GuildModelMessage.newBuilder();
        messageData.setDataType(GuildModel.GuildModelMessage.DateType.RefuseGuildApplyResponse);
        GuildModel.RefuseGuildApplyResponse.Builder refuseGuildApplyResponseBuilder = GuildModel.RefuseGuildApplyResponse.newBuilder();
        messageData.setRefuseGuildApplyResponse(refuseGuildApplyResponseBuilder.build());
        nettyResponse.setData(messageData.build().toByteArray());
        channel.writeAndFlush(nettyResponse);
    }
}
