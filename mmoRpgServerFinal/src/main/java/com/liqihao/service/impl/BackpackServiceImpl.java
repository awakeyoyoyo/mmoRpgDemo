package com.liqihao.service.impl;

import com.google.protobuf.InvalidProtocolBufferException;
import com.liqihao.Cache.ChannelMessageCache;
import com.liqihao.Cache.EquipmentMessageCache;
import com.liqihao.Cache.MediceneMessageCache;
import com.liqihao.annotation.HandlerCmdTag;
import com.liqihao.annotation.HandlerServiceTag;
import com.liqihao.commons.ConstantValue;
import com.liqihao.commons.NettyResponse;
import com.liqihao.commons.enums.ArticleTypeCode;
import com.liqihao.commons.StateCode;
import com.liqihao.pojo.baseMessage.EquipmentMessage;
import com.liqihao.pojo.baseMessage.MedicineMessage;
import com.liqihao.pojo.bean.*;
import com.liqihao.pojo.dto.ArticleDto;
import com.liqihao.protobufObject.BackPackModel;
import com.liqihao.provider.CopySceneProvider;
import com.liqihao.provider.GoodsServiceProvider;
import com.liqihao.service.BackpackService;
import com.liqihao.util.CommonsUtil;
import io.netty.channel.Channel;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 背包模块
 *
 * @author Administrator
 */
@Service
@HandlerServiceTag(protobufModel = "BackPackModel$BackPackModelMessage")
public class BackpackServiceImpl implements BackpackService {
    @Override
    @HandlerCmdTag(cmd = ConstantValue.ABANDON_REQUEST, module = ConstantValue.BAKCPACK_MODULE)
    public void abandonRequest(BackPackModel.BackPackModelMessage myMessage, MmoSimpleRole mmoSimpleRole) throws InvalidProtocolBufferException {
        Integer articleId = myMessage.getAbandonRequest().getArticleId();
        Integer number = myMessage.getAbandonRequest().getNumber();
        Channel channel = ChannelMessageCache.getInstance().get(mmoSimpleRole.getId());
        BackPackManager manager = mmoSimpleRole.getBackpackManager();
        Article article = manager.useOrAbandonArticle(articleId, number);
        if (article == null) {
            //使用失败，无该物品或者该数量超过
            //返回成功的数据包
            NettyResponse nettyResponse = new NettyResponse();
            nettyResponse.setCmd(ConstantValue.FAIL_RESPONSE);
            nettyResponse.setStateCode(StateCode.FAIL);
            //protobuf 生成registerResponse
            nettyResponse.setData("使用失败，无该物品或者数量不足".getBytes());
            channel.writeAndFlush(nettyResponse);
            return;
        } else {
            //返回成功的数据包
            NettyResponse nettyResponse = new NettyResponse();
            nettyResponse.setCmd(ConstantValue.ABANDON_RESPONSE);
            nettyResponse.setStateCode(StateCode.SUCCESS);
            //protobuf 生成registerResponse
            BackPackModel.BackPackModelMessage.Builder messageData = BackPackModel.BackPackModelMessage.newBuilder();
            messageData.setDataType(BackPackModel.BackPackModelMessage.DateType.AbandonResponse);
            BackPackModel.AbandonResponse.Builder abandonResponseBuilder = BackPackModel.AbandonResponse.newBuilder();
            messageData.setAbandonResponse(abandonResponseBuilder.build());
            nettyResponse.setData(messageData.build().toByteArray());
            channel.writeAndFlush(nettyResponse);
            return;
        }
    }

    @Override
    @HandlerCmdTag(cmd = ConstantValue.BACKPACK_MSG_REQUEST, module = ConstantValue.BAKCPACK_MODULE)
    public void backPackMsgRequest(BackPackModel.BackPackModelMessage myMessage, MmoSimpleRole mmoSimpleRole) throws InvalidProtocolBufferException {
        Channel channel = ChannelMessageCache.getInstance().get(mmoSimpleRole.getId());
        List<ArticleDto> articles = mmoSimpleRole.getBackpackManager().getBackpacks();
        List<BackPackModel.ArticleDto> articleDtoList = new ArrayList<>();
        for (ArticleDto a : articles) {
            BackPackModel.ArticleDto.Builder dtoBuilder = BackPackModel.ArticleDto.newBuilder();
            dtoBuilder.setArticleId(a.getArticleId())
                    .setId(a.getId())
                    .setArticleType(a.getArticleType())
                    .setQuantity(a.getQuantity());
            if (a.getArticleType().equals(ArticleTypeCode.EQUIPMENT.getCode())) {
                dtoBuilder.setNowDurability(a.getNowDurability());
                dtoBuilder.setEquipmentId(a.getEquipmentId());
            }
            articleDtoList.add(dtoBuilder.build());
        }
        NettyResponse nettyResponse = new NettyResponse();
        nettyResponse.setCmd(ConstantValue.BACKPACK_MSG_RESPONSE);
        nettyResponse.setStateCode(StateCode.SUCCESS);
        //protobuf 生成registerResponse
        BackPackModel.BackPackModelMessage.Builder messageData = BackPackModel.BackPackModelMessage.newBuilder();
        messageData.setDataType(BackPackModel.BackPackModelMessage.DateType.BackPackResponse);
        BackPackModel.BackPackResponse.Builder backPackResponse = BackPackModel.BackPackResponse.newBuilder();
        backPackResponse.addAllArticleDtos(articleDtoList);
        messageData.setBackPackResponse(backPackResponse.build());
        nettyResponse.setData(messageData.build().toByteArray());
        channel.writeAndFlush(nettyResponse);
        return;
    }

    @Override
    @HandlerCmdTag(cmd = ConstantValue.USE_REQUEST, module = ConstantValue.BAKCPACK_MODULE)
    public void useRequest(BackPackModel.BackPackModelMessage myMessage, MmoSimpleRole mmoSimpleRole) throws InvalidProtocolBufferException {
        Integer article = myMessage.getUseRequest().getArticleId();
        Channel channel = ChannelMessageCache.getInstance().get(mmoSimpleRole.getId());
        boolean flag = mmoSimpleRole.useArticle(article);
        NettyResponse nettyResponse = new NettyResponse();
        nettyResponse.setCmd(ConstantValue.USE_RESPONSE);
        if (flag) {
            nettyResponse.setStateCode(StateCode.SUCCESS);
            //protobuf 生成registerResponse
            BackPackModel.BackPackModelMessage.Builder messageData = BackPackModel.BackPackModelMessage.newBuilder();
            messageData.setDataType(BackPackModel.BackPackModelMessage.DateType.UseResponse);
            BackPackModel.UseResponse.Builder useResponse = BackPackModel.UseResponse.newBuilder();
            messageData.setUseResponse(useResponse.build());
            nettyResponse.setData(messageData.build().toByteArray());
        } else {
            nettyResponse.setStateCode(StateCode.FAIL);
            nettyResponse.setCmd(ConstantValue.FAIL_RESPONSE);
            //protobuf 生成registerResponse
            nettyResponse.setData("使用道具失败".getBytes());
        }
        channel.writeAndFlush(nettyResponse);
        return;
    }

    @Override
    @HandlerCmdTag(cmd = ConstantValue.ADD_ARTICLE_REQUEST, module = ConstantValue.BAKCPACK_MODULE)
    public void addArticleRequest(BackPackModel.BackPackModelMessage myMessage, MmoSimpleRole mmoSimpleRole) throws InvalidProtocolBufferException {
        Integer id = myMessage.getAddArticleRequest().getId();
        Integer articleType = myMessage.getAddArticleRequest().getArticleType();
        Integer number = myMessage.getAddArticleRequest().getNumber();
        Channel channel = ChannelMessageCache.getInstance().get(mmoSimpleRole.getId());
        //根据 articleType判断 然后生成物品对象存
        Article article;
        if (articleType.equals(ArticleTypeCode.MEDICINE.getCode())) {
            MedicineMessage medicineMessage = MediceneMessageCache.getInstance().get(id);
            if (medicineMessage == null) {
                NettyResponse nettyResponse = new NettyResponse();
                nettyResponse.setCmd(ConstantValue.FAIL_RESPONSE);
                nettyResponse.setStateCode(StateCode.FAIL);
                //protobuf 生成registerResponse
                nettyResponse.setData("存入错误物品id".getBytes());
                channel.writeAndFlush(nettyResponse);
                return;
            }
            MedicineBean medicineBean = CommonsUtil.medicineMessageToMedicineBean(medicineMessage);
            medicineBean.setQuantity(number);
            article = medicineBean;
        } else if (articleType.equals(ArticleTypeCode.EQUIPMENT.getCode())) {
            EquipmentMessage equipmentMessage = EquipmentMessageCache.getInstance().get(id);
            if (equipmentMessage == null) {
                NettyResponse nettyResponse = new NettyResponse();
                nettyResponse.setCmd(ConstantValue.FAIL_RESPONSE);
                nettyResponse.setStateCode(StateCode.FAIL);
                //protobuf 生成registerResponse
                nettyResponse.setData("存入错误物品id".getBytes());
                channel.writeAndFlush(nettyResponse);
                return;
            }
            EquipmentBean equipmentBean = CommonsUtil.equipmentMessageToEquipmentBean(equipmentMessage);
            equipmentBean.setQuantity(number);
            article = equipmentBean;
        } else {
            //未知物品
            NettyResponse nettyResponse = new NettyResponse();
            nettyResponse.setCmd(ConstantValue.FAIL_RESPONSE);
            nettyResponse.setStateCode(StateCode.FAIL);
            //protobuf 生成registerResponse
            nettyResponse.setData("未知物品不能存储".getBytes());
            channel.writeAndFlush(nettyResponse);
            return;
        }
        if (!mmoSimpleRole.getBackpackManager().put(article)) {
            NettyResponse nettyResponse = new NettyResponse();
            nettyResponse.setCmd(ConstantValue.FAIL_RESPONSE);
            nettyResponse.setStateCode(StateCode.FAIL);
            //protobuf 生成registerResponse
            nettyResponse.setData("背包已满".getBytes());
            channel.writeAndFlush(nettyResponse);
            return;
        }
        NettyResponse nettyResponse = new NettyResponse();
        nettyResponse.setCmd(ConstantValue.ADD_ARTICLE_RESPONSE);
        nettyResponse.setStateCode(StateCode.SUCCESS);
        BackPackModel.BackPackModelMessage.Builder messageData = BackPackModel.BackPackModelMessage.newBuilder();
        messageData.setDataType(BackPackModel.BackPackModelMessage.DateType.AddArticleResponse);
        BackPackModel.AddArticleResponse.Builder addArticleResponse = BackPackModel.AddArticleResponse.newBuilder();
        messageData.setAddArticleResponse(addArticleResponse.build());
        nettyResponse.setData(messageData.build().toByteArray());
        //protobuf 生成registerResponse
        channel.writeAndFlush(nettyResponse);
        return;
    }

    @Override
    @HandlerCmdTag(cmd = ConstantValue.FIND_ALL_CAN_REQUEST, module = ConstantValue.BAKCPACK_MODULE)
    public void findAllCanGetRequest(BackPackModel.BackPackModelMessage myMessage, MmoSimpleRole mmoSimpleRole) throws InvalidProtocolBufferException {
        Channel channel = ChannelMessageCache.getInstance().get(mmoSimpleRole.getId());
        //副本中可捡去的物品
        Integer copySceneBeanId = mmoSimpleRole.getCopySceneBeanId();
        if (copySceneBeanId == null) {
            NettyResponse errorResponse = new NettyResponse(StateCode.FAIL, ConstantValue.FAIL_RESPONSE, "当前角色还未进入副本".getBytes());
            channel.writeAndFlush(errorResponse);
            return;
        }
        CopySceneBean copySceneBean = CopySceneProvider.getCopySceneBeanById(copySceneBeanId);
        List<BackPackModel.ArticleFloorDto> resultList = new ArrayList<>();
        if (copySceneBean.getArticlesMap().size() > 0) {
            for (Article a : copySceneBean.getArticlesMap().values()) {
                BackPackModel.ArticleFloorDto.Builder dtoBuilder = BackPackModel.ArticleFloorDto.newBuilder();
                if (a.getArticleTypeCode().equals(ArticleTypeCode.EQUIPMENT.getCode())) {
                    //装备
                    EquipmentBean equipmentBean = (EquipmentBean) a;
                    dtoBuilder.setId(equipmentBean.getId())
                            .setArticleType(equipmentBean.getArticleType())
                            .setFloorIndex(equipmentBean.getFloorIndex())
                            .setNowDurability(equipmentBean.getNowDurability())
                            .setQuantity(1)
                            .setEquipmentId(equipmentBean.getEquipmentId());
                } else {
                    //道具
                    MedicineBean medicineBean = (MedicineBean) a;
                    dtoBuilder.setId(medicineBean.getId())
                            .setArticleType(medicineBean.getArticleType())
                            .setFloorIndex(medicineBean.getFloorIndex())
                            .setNowDurability(-1)
                            .setQuantity(1)
                            .setEquipmentId(-1);
                }
                resultList.add(dtoBuilder.build());
            }
        }
        NettyResponse nettyResponse = new NettyResponse();
        nettyResponse.setCmd(ConstantValue.FIND_ALL_CAN_RESPONSE);
        nettyResponse.setStateCode(StateCode.SUCCESS);
        //protobuf 生成registerResponse
        BackPackModel.BackPackModelMessage.Builder messageData = BackPackModel.BackPackModelMessage.newBuilder();
        messageData.setDataType(BackPackModel.BackPackModelMessage.DateType.FindAllCanGetResponse);
        BackPackModel.FindAllCanGetResponse.Builder findAllCanGetBuilder = BackPackModel.FindAllCanGetResponse.newBuilder();
        findAllCanGetBuilder.addAllArticleFloorDto(resultList);
        messageData.setFindAllCanGetResponse(findAllCanGetBuilder.build());
        nettyResponse.setData(messageData.build().toByteArray());
        channel.writeAndFlush(nettyResponse);
        return;
    }

    @Override
    @HandlerCmdTag(cmd = ConstantValue.GET_ARTICLE_FROM_FLOOR_REQUEST, module = ConstantValue.BAKCPACK_MODULE)
    public void getArticleFromFloorRequest(BackPackModel.BackPackModelMessage myMessage, MmoSimpleRole mmoSimpleRole) throws InvalidProtocolBufferException {
        Integer index = myMessage.getGetArticleFromFloorRequest().getIndex();
        Channel channel = ChannelMessageCache.getInstance().get(mmoSimpleRole.getId());
        Integer copySceneBeanId = mmoSimpleRole.getCopySceneBeanId();
        if (copySceneBeanId == null) {
            NettyResponse errorResponse = new NettyResponse(StateCode.FAIL, ConstantValue.FAIL_RESPONSE, "当前角色还未进入副本".getBytes());
            channel.writeAndFlush(errorResponse);
            return;
        }
        CopySceneBean copySceneBean = CopySceneProvider.getCopySceneBeanById(copySceneBeanId);
        Article article = copySceneBean.getArticleByIndex(index);
        if (article == null) {
            NettyResponse errorResponse = new NettyResponse(StateCode.FAIL, ConstantValue.FAIL_RESPONSE, "该物品已经被其他玩家获取了".getBytes());
            channel.writeAndFlush(errorResponse);
            return;
        }
        //放入背包
        mmoSimpleRole.getBackpackManager().put(article);
        //传输数据
        NettyResponse nettyResponse = new NettyResponse();
        nettyResponse.setCmd(ConstantValue.GET_ARTICLE_FROM_FLOOR_RESPONSE);
        nettyResponse.setStateCode(StateCode.SUCCESS);
        BackPackModel.BackPackModelMessage.Builder messageData = BackPackModel.BackPackModelMessage.newBuilder();
        messageData.setDataType(BackPackModel.BackPackModelMessage.DateType.GetArticleFromFloorResponse);
        BackPackModel.GetArticleFromFloorResponse.Builder getArticleFromFloorResponseBuilder = BackPackModel.GetArticleFromFloorResponse.newBuilder();
        messageData.setGetArticleFromFloorResponse(getArticleFromFloorResponseBuilder.build());
        nettyResponse.setData(messageData.build().toByteArray());
        channel.writeAndFlush(nettyResponse);
    }

    @Override
    @HandlerCmdTag(cmd = ConstantValue.CHECK_MONEY_NUMBER_REQUEST, module = ConstantValue.BAKCPACK_MODULE)
    public void checkMoneyNumber(BackPackModel.BackPackModelMessage myMessage, MmoSimpleRole mmoSimpleRole) throws InvalidProtocolBufferException {
        Channel channel = ChannelMessageCache.getInstance().get(mmoSimpleRole.getId());
        Integer money=mmoSimpleRole.getMoney();
        //传输数据
        NettyResponse nettyResponse = new NettyResponse();
        nettyResponse.setCmd(ConstantValue.CHECK_MONEY_NUMBER_RESPONSE);
        nettyResponse.setStateCode(StateCode.SUCCESS);
        BackPackModel.BackPackModelMessage.Builder messageData = BackPackModel.BackPackModelMessage.newBuilder();
        messageData.setDataType(BackPackModel.BackPackModelMessage.DateType.CheckMoneyNumberResponse);
        BackPackModel.CheckMoneyNumberResponse.Builder checkMoneyNumberResponseBuilder = BackPackModel.CheckMoneyNumberResponse.newBuilder().setMoney(money);
        messageData.setCheckMoneyNumberResponse(checkMoneyNumberResponseBuilder.build());
        nettyResponse.setData(messageData.build().toByteArray());
        channel.writeAndFlush(nettyResponse);
    }

    @Override
    @HandlerCmdTag(cmd = ConstantValue.BUY_GOODS_REQUEST, module = ConstantValue.BAKCPACK_MODULE)
    public void buyGoods(BackPackModel.BackPackModelMessage myMessage, MmoSimpleRole mmoSimpleRole) throws Exception {
        Channel channel = ChannelMessageCache.getInstance().get(mmoSimpleRole.getId());
        Integer num=myMessage.getBuyGoodsRequest().getNum();
        Integer goodsId=myMessage.getBuyGoodsRequest().getGoodsId();
        //买
        GoodsServiceProvider.sellArticle(goodsId,num,mmoSimpleRole);
        //传输数据
        NettyResponse nettyResponse = new NettyResponse();
        nettyResponse.setCmd(ConstantValue.BUY_GOODS_RESPONSE);
        nettyResponse.setStateCode(StateCode.SUCCESS);
        BackPackModel.BackPackModelMessage.Builder messageData = BackPackModel.BackPackModelMessage.newBuilder();
        messageData.setDataType(BackPackModel.BackPackModelMessage.DateType.BuyGoodsResponse);
        BackPackModel.BuyGoodsResponse.Builder buyGoodsResponseBuilder = BackPackModel.BuyGoodsResponse.newBuilder();
        messageData.setBuyGoodsResponse(buyGoodsResponseBuilder.build());
        nettyResponse.setData(messageData.build().toByteArray());
        channel.writeAndFlush(nettyResponse);

    }

    @Override
    @HandlerCmdTag(cmd = ConstantValue.FIND_ALL_GOODS_REQUEST, module = ConstantValue.BAKCPACK_MODULE)
    public void findAllGoods(BackPackModel.BackPackModelMessage myMessage, MmoSimpleRole mmoSimpleRole) throws Exception {
        Channel channel = ChannelMessageCache.getInstance().get(mmoSimpleRole.getId());
        List<GoodsBean> goodsBeans=GoodsServiceProvider.getAllArticles();
        //传输数据
        List<BackPackModel.GoodsDto> goodsDtos=new ArrayList<>();
        for (GoodsBean gg:goodsBeans) {
            BackPackModel.GoodsDto goodsDto= BackPackModel.GoodsDto.newBuilder()
                    .setId(gg.getId()).setNowNum(gg.getNowNum()).setNum(gg.getNum())
                    .setPrice(gg.getPrice()).setArticleTypeId(gg.getArticleTypeId()).setArticleMessageId(gg.getArticleMessageId()).build();
            goodsDtos.add(goodsDto);
        }

        NettyResponse nettyResponse = new NettyResponse();
        nettyResponse.setCmd(ConstantValue.FIND_ALL_GOODS_RESPONSE);
        nettyResponse.setStateCode(StateCode.SUCCESS);
        BackPackModel.BackPackModelMessage.Builder messageData = BackPackModel.BackPackModelMessage.newBuilder();
        messageData.setDataType(BackPackModel.BackPackModelMessage.DateType.FindAllGoodsResponse);
        BackPackModel.FindAllGoodsResponse.Builder findAllGoodsResponse = BackPackModel.FindAllGoodsResponse.newBuilder().addAllGoodsDtos(goodsDtos);
        messageData.setFindAllGoodsResponse(findAllGoodsResponse.build());
        nettyResponse.setData(messageData.build().toByteArray());
        channel.writeAndFlush(nettyResponse);
    }
}