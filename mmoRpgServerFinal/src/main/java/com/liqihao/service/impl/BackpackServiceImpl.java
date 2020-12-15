package com.liqihao.service.impl;

import com.google.protobuf.InvalidProtocolBufferException;
import com.liqihao.Cache.MmoCache;
import com.liqihao.annotation.HandlerCmdTag;
import com.liqihao.annotation.HandlerServiceTag;
import com.liqihao.commons.ConstantValue;
import com.liqihao.commons.NettyRequest;
import com.liqihao.commons.NettyResponse;
import com.liqihao.commons.enums.StateCode;
import com.liqihao.pojo.bean.Article;
import com.liqihao.pojo.bean.BackPackManager;
import com.liqihao.pojo.bean.MmoSimpleRole;
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
    public NettyResponse abandonRquest(NettyRequest nettyRequest, Channel channel) throws InvalidProtocolBufferException {
        byte[] data=nettyRequest.getData();
        BackPackModel.BackPackModelMessage myMessage;
        myMessage=BackPackModel.BackPackModelMessage.parseFrom(data);
        Integer articleId=myMessage.getAbandonRequest().getArticleId();
        Integer number=myMessage.getAbandonRequest().getNumber();
        Integer roleId= CommonsUtil.getRoleIdByChannel(channel);
        MmoSimpleRole mmoSimpleRole=MmoCache.getInstance().getMmoSimpleRoleConcurrentHashMap().get(roleId);
        BackPackManager manager=mmoSimpleRole.getBackpackManager();
        Article article=manager.useOrAbandanArticle(articleId,number);
        if (article==null){
            //使用失败，无该物品或者该数量超过
            //返回成功的数据包
            NettyResponse nettyResponse=new NettyResponse();
            nettyResponse.setCmd(ConstantValue.ABANDON_RESPONSE);
            nettyResponse.setStateCode(StateCode.FAIL);
            //protobuf 生成registerResponse
            nettyResponse.setData("使用失败，无该物品或者数量不足".getBytes());
            return nettyResponse;
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
            return nettyResponse;
        }
    }

    @Override
    @HandlerCmdTag(cmd = ConstantValue.BACKPACK_MSG_REQUEST,module = ConstantValue.BAKCPACK_MODULE)
    public NettyResponse backPackMsgRequest(NettyRequest nettyRequest, Channel channel) throws InvalidProtocolBufferException {
        Integer roleId= CommonsUtil.getRoleIdByChannel(channel);
        MmoSimpleRole mmoSimpleRole=MmoCache.getInstance().getMmoSimpleRoleConcurrentHashMap().get(roleId);
        List<ArticleDto> articles=mmoSimpleRole.getBackpackManager().getBackpacks();
        List<BackPackModel.ArticleDto> articleDtoList=new ArrayList<>();
        for (ArticleDto a:articles) {
            BackPackModel.ArticleDto.Builder dtoBuilder=BackPackModel.ArticleDto.newBuilder();
            dtoBuilder.setArticleId(a.getArticleId())
                    .setId(a.getId())
                    .setArticleType(a.getArticleType())
                    .setQuantity(a.getQuantity());
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
        return nettyResponse;
    }

    @Override
    @HandlerCmdTag(cmd = ConstantValue.USE_REQUEST,module = ConstantValue.BAKCPACK_MODULE)
    public NettyResponse useRequest(NettyRequest nettyRequest, Channel channel) throws InvalidProtocolBufferException {
        byte[] data=nettyRequest.getData();
        BackPackModel.BackPackModelMessage myMessage;
        myMessage=BackPackModel.BackPackModelMessage.parseFrom(data);
        Integer article=myMessage.getUseRequest().getArticleId();
        Integer roleId=CommonsUtil.getRoleIdByChannel(channel);
        MmoSimpleRole mmoSimpleRole=MmoCache.getInstance().getMmoSimpleRoleConcurrentHashMap().get(roleId);
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
            //protobuf 生成registerResponse
            nettyResponse.setData("使用道具失败".getBytes());
        }
        return nettyResponse;
    }
}
