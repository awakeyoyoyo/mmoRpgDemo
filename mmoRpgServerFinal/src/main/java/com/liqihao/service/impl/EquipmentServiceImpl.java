package com.liqihao.service.impl;

import com.google.protobuf.InvalidProtocolBufferException;
import com.liqihao.annotation.HandlerCmdTag;
import com.liqihao.annotation.HandlerServiceTag;
import com.liqihao.commons.ConstantValue;
import com.liqihao.commons.NettyResponse;
import com.liqihao.commons.RpgServerException;
import com.liqihao.commons.enums.ArticleTypeCode;
import com.liqihao.commons.StateCode;
import com.liqihao.pojo.bean.articleBean.Article;
import com.liqihao.pojo.bean.articleBean.EquipmentBean;
import com.liqihao.pojo.bean.roleBean.MmoSimpleRole;
import com.liqihao.pojo.dto.EquipmentDto;
import com.liqihao.protobufObject.EquipmentModel;
import com.liqihao.service.EquipmentService;
import com.liqihao.util.DbUtil;
import com.liqihao.util.ScheduledThreadPoolUtil;
import io.netty.channel.Channel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 装备模块
 *
 * @author lqhao
 */
@Service
@HandlerServiceTag(protobufModel = "EquipmentModel$EquipmentModelMessage")
public class EquipmentServiceImpl implements EquipmentService {
    @Override
    @HandlerCmdTag(cmd = ConstantValue.ADD_EQUIPMENT_REQUEST, module = ConstantValue.EQUIPMENT_MODULE)
    public void addEquipmentRequest(EquipmentModel.EquipmentModelMessage myMessage, MmoSimpleRole mmoSimpleRole) throws RpgServerException {
        Integer articleId = myMessage.getAddEquipmentRequest().getArticleId();
        Channel channel = mmoSimpleRole.getChannel();

        Article article = mmoSimpleRole.getBackpackManager().getArticleByArticleId(articleId);
        if (article == null || !article.getArticleTypeCode().equals(ArticleTypeCode.EQUIPMENT.getCode())) {
            //不是装备
            throw new RpgServerException(StateCode.FAIL,"该物品不是装备or找不到该装备");
        }
        mmoSimpleRole.useArticle(articleId);
        //修改
        NettyResponse nettyResponse = new NettyResponse();
        nettyResponse.setCmd(ConstantValue.ADD_EQUIPMENT_RESPONSE);
        nettyResponse.setStateCode(StateCode.SUCCESS);
        //protobuf 生成registerResponse
        EquipmentModel.EquipmentModelMessage.Builder messageBuilder = EquipmentModel.EquipmentModelMessage.newBuilder();
        messageBuilder.setDataType(EquipmentModel.EquipmentModelMessage.DateType.AddEquipmentResponse);
        messageBuilder.setAddEquipmentResponse(EquipmentModel.AddEquipmentResponse.newBuilder().build());
        nettyResponse.setData(messageBuilder.build().toByteArray());
        channel.writeAndFlush(nettyResponse);
    }

    @Override
    @HandlerCmdTag(cmd = ConstantValue.EQUIPMENT_MSG_REQUEST, module = ConstantValue.EQUIPMENT_MODULE)
    public void equipmentMasRequest(EquipmentModel.EquipmentModelMessage myMessage, MmoSimpleRole mmoSimpleRole) throws InvalidProtocolBufferException {
        Channel channel = mmoSimpleRole.getChannel();
        List<EquipmentDto> equipmentDtos = mmoSimpleRole.getEquipments();
        //转化为protobuf
        List<EquipmentModel.EquipmentDto> equipmentDtoList = new ArrayList<>();
        for (EquipmentDto e : equipmentDtos) {
            EquipmentModel.EquipmentDto.Builder builder = EquipmentModel.EquipmentDto.newBuilder();
            builder.setId(e.getId()).setPosition(e.getPosition()).setNowDurability(e.getNowDurability());
            equipmentDtoList.add(builder.build());
        }
        NettyResponse nettyResponse = new NettyResponse();
        nettyResponse.setCmd(ConstantValue.EQUIPMENT_MSG_RESPONSE);
        nettyResponse.setStateCode(StateCode.SUCCESS);
        EquipmentModel.EquipmentModelMessage.Builder messageBuilder = EquipmentModel.EquipmentModelMessage.newBuilder();
        messageBuilder.setDataType(EquipmentModel.EquipmentModelMessage.DateType.EquipmentMsgResponse);
        messageBuilder.setEquipmentMsgResponse(EquipmentModel.EquipmentMsgResponse.newBuilder().addAllEquipments(equipmentDtoList).build());
        nettyResponse.setData(messageBuilder.build().toByteArray());
        channel.writeAndFlush(nettyResponse);
        return;
    }

    @Override
    @HandlerCmdTag(cmd = ConstantValue.REDUCE_EQUIPMENT_REQUEST, module = ConstantValue.EQUIPMENT_MODULE)
    public void reduceEquipmentRequest(EquipmentModel.EquipmentModelMessage myMessage, MmoSimpleRole mmoSimpleRole) throws Exception {

        Integer position = myMessage.getReduceEquipmentRequest().getPosition();
        Channel channel = mmoSimpleRole.getChannel();
        if (position > 6 || position <= 0) {
            throw new RpgServerException(StateCode.FAIL,"传入无效的部位id");
        }
        boolean flag = mmoSimpleRole.unUseEquipment(position);
        if (!flag) {
            //不是装备
            throw new RpgServerException(StateCode.FAIL,"该部位没有装备");
        }
        NettyResponse nettyResponse = new NettyResponse();
        nettyResponse.setCmd(ConstantValue.REDUCE_EQUIPMENT_RESPONSE);
        nettyResponse.setStateCode(StateCode.SUCCESS);
        EquipmentModel.EquipmentModelMessage.Builder messageBuilder = EquipmentModel.EquipmentModelMessage.newBuilder();
        messageBuilder.setDataType(EquipmentModel.EquipmentModelMessage.DateType.ReduceEquipmentResponse);
        messageBuilder.setReduceEquipmentResponse(EquipmentModel.ReduceEquipmentResponse.newBuilder().build());
        nettyResponse.setData(messageBuilder.build().toByteArray());
        channel.writeAndFlush(nettyResponse);
        return;
    }

    @Override
    @HandlerCmdTag(cmd = ConstantValue.FIX_EQUIPMENT_REQUEST, module = ConstantValue.EQUIPMENT_MODULE)
    public void fixEquipmentRequest(EquipmentModel.EquipmentModelMessage myMessage, MmoSimpleRole mmoSimpleRole) throws InvalidProtocolBufferException, RpgServerException {
        Integer articleId = myMessage.getFixEquipmentRequest().getArticleId();
        Channel channel = mmoSimpleRole.getChannel();
        Article article = mmoSimpleRole.getBackpackManager().getArticleByArticleId(articleId);
        if (article == null) {
            throw new RpgServerException(StateCode.FAIL,"传入无效物品id");
        }
        if (article.getArticleTypeCode() != ArticleTypeCode.EQUIPMENT.getCode()) {
            throw new RpgServerException(StateCode.FAIL,"该物品不是装备");
        }
        EquipmentBean equipmentBean = (EquipmentBean) article;
        //修复武器
        equipmentBean.fixDurability();
        ScheduledThreadPoolUtil.addTask(() -> DbUtil.updateEquipment(equipmentBean));
        NettyResponse nettyResponse = new NettyResponse();
        nettyResponse.setCmd(ConstantValue.FIX_EQUIPMENT_RESPONSE);
        nettyResponse.setStateCode(StateCode.SUCCESS);
        EquipmentModel.EquipmentModelMessage.Builder messageBuilder = EquipmentModel.EquipmentModelMessage.newBuilder();
        messageBuilder.setDataType(EquipmentModel.EquipmentModelMessage.DateType.FixEquipmentResponse);
        messageBuilder.setFixEquipmentResponse(EquipmentModel.FixEquipmentResponse.newBuilder().build());
        nettyResponse.setData(messageBuilder.build().toByteArray());
        channel.writeAndFlush(nettyResponse);
        return;
    }
}
