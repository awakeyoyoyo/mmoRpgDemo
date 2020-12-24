package com.liqihao.service.impl;

import com.google.protobuf.InvalidProtocolBufferException;
import com.liqihao.Cache.EquipmentMessageCache;
import com.liqihao.Cache.MediceneMessageCache;
import com.liqihao.annotation.HandlerCmdTag;
import com.liqihao.annotation.HandlerServiceTag;
import com.liqihao.commons.ConstantValue;
import com.liqihao.commons.NettyRequest;
import com.liqihao.commons.NettyResponse;
import com.liqihao.commons.enums.ArticleTypeCode;
import com.liqihao.commons.StateCode;
import com.liqihao.pojo.baseMessage.EquipmentMessage;
import com.liqihao.pojo.baseMessage.MedicineMessage;
import com.liqihao.pojo.bean.*;
import com.liqihao.pojo.dto.ArticleDto;
import com.liqihao.protobufObject.BackPackModel;
import com.liqihao.service.BackpackService;
import com.liqihao.util.CommonsUtil;
import io.netty.channel.Channel;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 背包模块
 * @author Administrator
 */
@Service
@HandlerServiceTag
public class BackpackServiceImpl implements BackpackService {
    @Override
    @HandlerCmdTag(cmd = ConstantValue.ABANDON_REQUEST,module = ConstantValue.BAKCPACK_MODULE)
    public void abandonRquest(NettyRequest nettyRequest, Channel channel) throws InvalidProtocolBufferException {
        byte[] data=nettyRequest.getData();
        BackPackModel.BackPackModelMessage myMessage;
        myMessage=BackPackModel.BackPackModelMessage.parseFrom(data);
        Integer articleId=myMessage.getAbandonRequest().getArticleId();
        Integer number=myMessage.getAbandonRequest().getNumber();
        MmoSimpleRole mmoSimpleRole=CommonsUtil.checkLogin(channel);
        if (mmoSimpleRole==null){
            return;
        }
        BackPackManager manager=mmoSimpleRole.getBackpackManager();
        Article article=manager.useOrAbandanArticle(articleId,number);
        if (article==null){
            //使用失败，无该物品或者该数量超过
            //返回成功的数据包
            NettyResponse nettyResponse=new NettyResponse();
            nettyResponse.setCmd(ConstantValue.FAIL_RESPONSE);
            nettyResponse.setStateCode(StateCode.FAIL);
            //protobuf 生成registerResponse
            nettyResponse.setData("使用失败，无该物品或者数量不足".getBytes());
            channel.writeAndFlush(nettyResponse);
            return;
        }else{
            //返回成功的数据包
            NettyResponse nettyResponse=new NettyResponse();
            nettyResponse.setCmd(ConstantValue.ABANDON_RESPONSE);
            nettyResponse.setStateCode(StateCode.SUCCESS);
            //protobuf 生成registerResponse
            BackPackModel.BackPackModelMessage.Builder messageData=BackPackModel.BackPackModelMessage.newBuilder();
            messageData.setDataType(BackPackModel.BackPackModelMessage.DateType.AbandonResponse);
            BackPackModel.AbandonResponse.Builder abandonResponseBuilder=BackPackModel.AbandonResponse.newBuilder();
            messageData.setAbandonResponse(abandonResponseBuilder.build());
            nettyResponse.setData(messageData.build().toByteArray());
            channel.writeAndFlush(nettyResponse);
            return;
        }
    }

    @Override
    @HandlerCmdTag(cmd = ConstantValue.BACKPACK_MSG_REQUEST,module = ConstantValue.BAKCPACK_MODULE)
    public void backPackMsgRequest(NettyRequest nettyRequest, Channel channel) throws InvalidProtocolBufferException {
        MmoSimpleRole mmoSimpleRole=CommonsUtil.checkLogin(channel);
        if (mmoSimpleRole==null){
            return;
        }
        List<ArticleDto> articles=mmoSimpleRole.getBackpackManager().getBackpacks();
        List<BackPackModel.ArticleDto> articleDtoList=new ArrayList<>();
        for (ArticleDto a:articles) {
            BackPackModel.ArticleDto.Builder dtoBuilder=BackPackModel.ArticleDto.newBuilder();
            dtoBuilder.setArticleId(a.getArticleId())
                    .setId(a.getId())
                    .setArticleType(a.getArticleType())
                    .setQuantity(a.getQuantity());
            if (a.getArticleType().equals(ArticleTypeCode.EQUIPMENT.getCode())){
                dtoBuilder.setNowDurability(a.getNowdurability());
            }
            articleDtoList.add(dtoBuilder.build());
        }
        NettyResponse nettyResponse=new NettyResponse();
        nettyResponse.setCmd(ConstantValue.BACKPACK_MSG_RESPONSE);
        nettyResponse.setStateCode(StateCode.SUCCESS);
        //protobuf 生成registerResponse
        BackPackModel.BackPackModelMessage.Builder messageData=BackPackModel.BackPackModelMessage.newBuilder();
        messageData.setDataType(BackPackModel.BackPackModelMessage.DateType.BackPackResponse);
        BackPackModel.BackPackResponse.Builder backPackResponse=BackPackModel.BackPackResponse.newBuilder();
        backPackResponse.addAllArticleDtos(articleDtoList);
        messageData.setBackPackResponse(backPackResponse.build());
        nettyResponse.setData(messageData.build().toByteArray());
        channel.writeAndFlush(nettyResponse);
        return;
    }

    @Override
    @HandlerCmdTag(cmd = ConstantValue.USE_REQUEST,module = ConstantValue.BAKCPACK_MODULE)
    public void useRequest(NettyRequest nettyRequest, Channel channel) throws InvalidProtocolBufferException {
        byte[] data=nettyRequest.getData();
        BackPackModel.BackPackModelMessage myMessage;
        myMessage=BackPackModel.BackPackModelMessage.parseFrom(data);
        Integer article=myMessage.getUseRequest().getArticleId();
        MmoSimpleRole mmoSimpleRole=CommonsUtil.checkLogin(channel);
        if (mmoSimpleRole==null){
            return;
        }
        boolean flag=mmoSimpleRole.useArticle(article);
        NettyResponse nettyResponse=new NettyResponse();
        nettyResponse.setCmd(ConstantValue.USE_RESPONSE);
        if (flag){
            nettyResponse.setStateCode(StateCode.SUCCESS);
            //protobuf 生成registerResponse
            BackPackModel.BackPackModelMessage.Builder messageData=BackPackModel.BackPackModelMessage.newBuilder();
            messageData.setDataType(BackPackModel.BackPackModelMessage.DateType.UseResponse);
            BackPackModel.UseResponse.Builder useResponse=BackPackModel.UseResponse.newBuilder();
            messageData.setUseResponse(useResponse.build());
            nettyResponse.setData(messageData.build().toByteArray());
        }else{
            nettyResponse.setStateCode(StateCode.FAIL);
            nettyResponse.setCmd(ConstantValue.FAIL_RESPONSE);
            //protobuf 生成registerResponse
            nettyResponse.setData("使用道具失败".getBytes());
        }
        channel.writeAndFlush(nettyResponse);
        return;
    }
    @Override
    @HandlerCmdTag(cmd = ConstantValue.ADD_ARTICLE_REQUEST,module = ConstantValue.BAKCPACK_MODULE)
    public void addArticleRequest(NettyRequest nettyRequest, Channel channel) throws InvalidProtocolBufferException {
        byte[] data=nettyRequest.getData();
        BackPackModel.BackPackModelMessage myMessage;
        myMessage=BackPackModel.BackPackModelMessage.parseFrom(data);
        Integer id=myMessage.getAddArticleRequest().getId();
        Integer articleType=myMessage.getAddArticleRequest().getArticleType();
        Integer number=myMessage.getAddArticleRequest().getNumber();
        MmoSimpleRole mmoSimpleRole=CommonsUtil.checkLogin(channel);
        if (mmoSimpleRole==null){
            return;
        }
        //根据 articleType判断 然后生成物品对象存
        Article article;
        if (articleType.equals(ArticleTypeCode.MEDICINE.getCode())){
            MedicineMessage medicineMessage= MediceneMessageCache.getInstance().get(id);
            if (medicineMessage==null) {
                NettyResponse nettyResponse=new NettyResponse();
                nettyResponse.setCmd(ConstantValue.FAIL_RESPONSE);
                nettyResponse.setStateCode(StateCode.FAIL);
                //protobuf 生成registerResponse
                nettyResponse.setData("存入错误物品id".getBytes());
                channel.writeAndFlush(nettyResponse);
                return;
            }
            MedicineBean medicineBean=CommonsUtil.medicineMessageToMedicineBean(medicineMessage);
            medicineBean.setQuantity(number);
            article=medicineBean;
        }else if (articleType.equals(ArticleTypeCode.EQUIPMENT.getCode())){
            EquipmentMessage equipmentMessage= EquipmentMessageCache.getInstance().get(id);
            if (equipmentMessage ==null) {
                NettyResponse nettyResponse=new NettyResponse();
                nettyResponse.setCmd(ConstantValue.FAIL_RESPONSE);
                nettyResponse.setStateCode(StateCode.FAIL);
                //protobuf 生成registerResponse
                nettyResponse.setData("存入错误物品id".getBytes());
                channel.writeAndFlush(nettyResponse);
                return;
            }
            EquipmentBean equipmentBean=CommonsUtil.equipmentMessageToEquipmentBean(equipmentMessage);
            equipmentBean.setQuantity(number);
            article=equipmentBean;
        }else{
            //未知物品
            NettyResponse nettyResponse=new NettyResponse();
            nettyResponse.setCmd(ConstantValue.FAIL_RESPONSE);
            nettyResponse.setStateCode(StateCode.FAIL);
            //protobuf 生成registerResponse
            nettyResponse.setData("未知物品不能存储".getBytes());
            channel.writeAndFlush(nettyResponse);
            return;
        }
        if (!mmoSimpleRole.getBackpackManager().put(article)){
            NettyResponse nettyResponse=new NettyResponse();
            nettyResponse.setCmd(ConstantValue.FAIL_RESPONSE);
            nettyResponse.setStateCode(StateCode.FAIL);
            //protobuf 生成registerResponse
            nettyResponse.setData("背包已满".getBytes());
            channel.writeAndFlush(nettyResponse);
            return;
        }
        NettyResponse nettyResponse=new NettyResponse();
        nettyResponse.setCmd(ConstantValue.ADD_ARTICLE_RESPONSE);
        nettyResponse.setStateCode(StateCode.SUCCESS);
        BackPackModel.BackPackModelMessage.Builder messageData=BackPackModel.BackPackModelMessage.newBuilder();
        messageData.setDataType(BackPackModel.BackPackModelMessage.DateType.AddArticleResponse);
        BackPackModel.AddArticleResponse.Builder addArticleResponse=BackPackModel.AddArticleResponse.newBuilder();
        messageData.setAddArticleResponse(addArticleResponse.build());
        nettyResponse.setData(messageData.build().toByteArray());
        //protobuf 生成registerResponse
        channel.writeAndFlush(nettyResponse);
        return;
    }
}