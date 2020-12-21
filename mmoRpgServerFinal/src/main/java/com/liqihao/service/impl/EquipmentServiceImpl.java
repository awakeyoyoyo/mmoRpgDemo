package com.liqihao.service.impl;

import com.google.protobuf.InvalidProtocolBufferException;
import com.liqihao.Cache.OnlineRoleMessageCache;
import com.liqihao.annotation.HandlerCmdTag;
import com.liqihao.annotation.HandlerServiceTag;
import com.liqihao.commons.ConstantValue;
import com.liqihao.commons.NettyRequest;
import com.liqihao.commons.NettyResponse;
import com.liqihao.commons.enums.ArticleTypeCode;
import com.liqihao.commons.enums.StateCode;
import com.liqihao.pojo.bean.Article;
import com.liqihao.pojo.bean.EquipmentBean;
import com.liqihao.pojo.bean.MmoSimpleRole;
import com.liqihao.pojo.dto.EquipmentDto;
import com.liqihao.protobufObject.EquipmentModel;
import com.liqihao.service.EquipmentService;
import com.liqihao.util.CommonsUtil;
import io.netty.channel.Channel;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@HandlerServiceTag
public class EquipmentServiceImpl implements EquipmentService {
    @Override
    @HandlerCmdTag(cmd = ConstantValue.ADD_EQUIPMENT_REQUEST,module = ConstantValue.EQUIPMENT_MODULE)
    public void addEquipmentRequest(NettyRequest nettyRequest, Channel channel) throws InvalidProtocolBufferException {
        byte[] data=nettyRequest.getData();
        EquipmentModel.EquipmentModelMessage myMessage;
        myMessage=EquipmentModel.EquipmentModelMessage.parseFrom(data);
        Integer articleId=myMessage.getAddEquipmentRequest().getArticleId();
        Integer roleId= CommonsUtil.getRoleIdByChannel(channel);
        MmoSimpleRole mmoSimpleRole= OnlineRoleMessageCache.getInstance().get(roleId);
        Article article=mmoSimpleRole.getBackpackManager().getArticleByArticleId(articleId);
        if (article==null||!article.getArticleTypeCode().equals(ArticleTypeCode.EQUIPMENT.getCode())){
            //不是装备
            NettyResponse nettyResponse=new NettyResponse();
            nettyResponse.setCmd(ConstantValue.ADD_EQUIPMENT_RESPONSE);
            nettyResponse.setStateCode(StateCode.FAIL);
            //protobuf 生成registerResponse
            nettyResponse.setData("该物品不是装备or找不到该装备".getBytes());
            channel.writeAndFlush(nettyResponse);
            return;
        }
        mmoSimpleRole.useArticle(articleId);
        NettyResponse nettyResponse=new NettyResponse();
        nettyResponse.setCmd(ConstantValue.ADD_EQUIPMENT_RESPONSE);
        nettyResponse.setStateCode(StateCode.SUCCESS);
        //protobuf 生成registerResponse
        EquipmentModel.EquipmentModelMessage.Builder messageBuilder=EquipmentModel.EquipmentModelMessage.newBuilder();
        messageBuilder.setDataType(EquipmentModel.EquipmentModelMessage.DateType.AddEquipmentResponse);
        messageBuilder.setAddEquipmentResponse(EquipmentModel.AddEquipmentResponse.newBuilder().build());
        nettyResponse.setData(messageBuilder.build().toByteArray());
        channel.writeAndFlush(nettyResponse);
        return;
    }

    @Override
    @HandlerCmdTag(cmd = ConstantValue.EQUIPMENT_MSG_REQUEST,module = ConstantValue.EQUIPMENT_MODULE)
    public void equipmentMasRequest(NettyRequest nettyRequest, Channel channel) throws InvalidProtocolBufferException {
        Integer roleId= CommonsUtil.getRoleIdByChannel(channel);
        MmoSimpleRole mmoSimpleRole= OnlineRoleMessageCache.getInstance().get(roleId);
        List<EquipmentDto> equipmentDtos=mmoSimpleRole.getEquipments();
        //转化为protobuf
        List<EquipmentModel.EquipmentDto> equipmentDtoList=new ArrayList<>();
        for (EquipmentDto e:equipmentDtos) {
            EquipmentModel.EquipmentDto.Builder builder=EquipmentModel.EquipmentDto.newBuilder();
            builder.setId(e.getId()).setPosition(e.getPosition()).setNowDurability(e.getNowdurability());
            equipmentDtoList.add(builder.build());
        }
        NettyResponse nettyResponse=new NettyResponse();
        nettyResponse.setCmd(ConstantValue.EQUIPMENT_MSG_RESPONSE);
        nettyResponse.setStateCode(StateCode.SUCCESS);
        EquipmentModel.EquipmentModelMessage.Builder messageBuilder=EquipmentModel.EquipmentModelMessage.newBuilder();
        messageBuilder.setDataType(EquipmentModel.EquipmentModelMessage.DateType.EquipmentMsgResponse);
        messageBuilder.setEquipmentMsgResponse(EquipmentModel.EquipmentMsgResponse.newBuilder().addAllEquipments(equipmentDtoList).build());
        nettyResponse.setData(messageBuilder.build().toByteArray());
        channel.writeAndFlush(nettyResponse);
        return;
    }

    @Override
    @HandlerCmdTag(cmd = ConstantValue.REDUCE_EQUIPMENT_REQUEST,module = ConstantValue.EQUIPMENT_MODULE)
    public void reduceEquipmentRequest(NettyRequest nettyRequest, Channel channel) throws InvalidProtocolBufferException {
        byte[] data=nettyRequest.getData();
        EquipmentModel.EquipmentModelMessage myMessage;
        myMessage=EquipmentModel.EquipmentModelMessage.parseFrom(data);
        Integer position=myMessage.getReduceEquipmentRequest().getPosition();
        Integer roleId= CommonsUtil.getRoleIdByChannel(channel);
        MmoSimpleRole mmoSimpleRole= OnlineRoleMessageCache.getInstance().get(roleId);
        boolean flag=mmoSimpleRole.unUseEquipment(position);
        if (!flag){
            //不是装备
            NettyResponse nettyResponse=new NettyResponse();
            nettyResponse.setCmd(ConstantValue.REDUCE_EQUIPMENT_RESPONSE);
            nettyResponse.setStateCode(StateCode.FAIL);
            //protobuf 生成registerResponse
            nettyResponse.setData("该部位没有装备".getBytes());
            channel.writeAndFlush(nettyResponse);
            return;
        }
        NettyResponse nettyResponse=new NettyResponse();
        nettyResponse.setCmd(ConstantValue.REDUCE_EQUIPMENT_RESPONSE);
        nettyResponse.setStateCode(StateCode.SUCCESS);
        EquipmentModel.EquipmentModelMessage.Builder messageBuilder=EquipmentModel.EquipmentModelMessage.newBuilder();
        messageBuilder.setDataType(EquipmentModel.EquipmentModelMessage.DateType.ReduceEquipmentResponse);
        messageBuilder.setReduceEquipmentResponse(EquipmentModel.ReduceEquipmentResponse.newBuilder().build());
        nettyResponse.setData(messageBuilder.build().toByteArray());
        channel.writeAndFlush(nettyResponse);
        return;
    }

    @Override
    @HandlerCmdTag(cmd = ConstantValue.FIX_EQUIPMENT_REQUEST,module = ConstantValue.EQUIPMENT_MODULE)
    public void fixEquipmentRequest(NettyRequest nettyRequest, Channel channel) throws InvalidProtocolBufferException {
        byte[] data=nettyRequest.getData();
        EquipmentModel.EquipmentModelMessage myMessage;
        myMessage=EquipmentModel.EquipmentModelMessage.parseFrom(data);
        Integer articleId=myMessage.getFixEquipmentRequest().getArticleId();
        Integer roleId=CommonsUtil.getRoleIdByChannel(channel);
        MmoSimpleRole mmoSimpleRole= OnlineRoleMessageCache.getInstance().get(roleId);
        Article article=mmoSimpleRole.getBackpackManager().getArticleByArticleId(articleId);
        if (article.getArticleTypeCode()!=ArticleTypeCode.EQUIPMENT.getCode()){
            NettyResponse nettyResponse=new NettyResponse();
            nettyResponse.setCmd(ConstantValue.FIX_EQUIPMENT_RESPONSE);
            nettyResponse.setStateCode(StateCode.FAIL);
            //protobuf 生成registerResponse
            nettyResponse.setData("该物品不是装备".getBytes());
            channel.writeAndFlush(nettyResponse);
            return;
        }
        EquipmentBean equipmentBean= (EquipmentBean) article;
        //修复武器
        equipmentBean.fixDurability();
        NettyResponse nettyResponse=new NettyResponse();
        nettyResponse.setCmd(ConstantValue.FIX_EQUIPMENT_RESPONSE);
        nettyResponse.setStateCode(StateCode.SUCCESS);
        EquipmentModel.EquipmentModelMessage.Builder messageBuilder=EquipmentModel.EquipmentModelMessage.newBuilder();
        messageBuilder.setDataType(EquipmentModel.EquipmentModelMessage.DateType.FixEquipmentResponse);
        messageBuilder.setFixEquipmentResponse(EquipmentModel.FixEquipmentResponse.newBuilder().build());
        nettyResponse.setData(messageBuilder.build().toByteArray());
        channel.writeAndFlush(nettyResponse);
        return;
    }
}
