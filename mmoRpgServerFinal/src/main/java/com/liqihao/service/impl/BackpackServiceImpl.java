package com.liqihao.service.impl;

import com.google.protobuf.InvalidProtocolBufferException;
import com.liqihao.Cache.EquipmentMessageCache;
import com.liqihao.Cache.GoodsMessageCache;
import com.liqihao.Cache.MedicineMessageCache;
import com.liqihao.annotation.HandlerCmdTag;
import com.liqihao.annotation.HandlerServiceTag;
import com.liqihao.commons.ConstantValue;
import com.liqihao.commons.NettyResponse;
import com.liqihao.commons.RpgServerException;
import com.liqihao.commons.enums.ArticleTypeCode;
import com.liqihao.commons.StateCode;
import com.liqihao.pojo.baseMessage.EquipmentMessage;
import com.liqihao.pojo.baseMessage.GoodsMessage;
import com.liqihao.pojo.baseMessage.MedicineMessage;
import com.liqihao.pojo.bean.*;
import com.liqihao.pojo.bean.articleBean.Article;
import com.liqihao.pojo.bean.articleBean.EquipmentBean;
import com.liqihao.pojo.bean.articleBean.MedicineBean;
import com.liqihao.pojo.bean.roleBean.MmoSimpleRole;
import com.liqihao.pojo.dto.ArticleDto;
import com.liqihao.protobufObject.BackPackModel;
import com.liqihao.provider.ArticleServiceProvider;
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
    public void abandonRequest(BackPackModel.BackPackModelMessage myMessage, MmoSimpleRole mmoSimpleRole) throws InvalidProtocolBufferException, RpgServerException {
        Integer articleId = myMessage.getAbandonRequest().getArticleId();
        Integer number = myMessage.getAbandonRequest().getNumber();
        Channel channel = mmoSimpleRole.getChannel();
        BackPackManager manager = mmoSimpleRole.getBackpackManager();
        Article article = manager.useOrAbandonArticle(articleId, number,mmoSimpleRole.getId());
        if (article == null) {
            //使用失败，无该物品或者该数量超过
            throw new RpgServerException(StateCode.FAIL,"使用失败，无该物品或者数量不足");
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
        }
    }

    @Override
    @HandlerCmdTag(cmd = ConstantValue.BACKPACK_MSG_REQUEST, module = ConstantValue.BAKCPACK_MODULE)
    public void backPackMsgRequest(BackPackModel.BackPackModelMessage myMessage, MmoSimpleRole mmoSimpleRole) throws InvalidProtocolBufferException {
        Channel channel = mmoSimpleRole.getChannel();
        List<ArticleDto> articles = mmoSimpleRole.getBackpackManager().getBackpacksMessage();
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
    public void useRequest(BackPackModel.BackPackModelMessage myMessage, MmoSimpleRole mmoSimpleRole) throws InvalidProtocolBufferException, RpgServerException {
        Integer article = myMessage.getUseRequest().getArticleId();
        Channel channel = mmoSimpleRole.getChannel();
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
            channel.writeAndFlush(nettyResponse);
        } else {
            throw new RpgServerException(StateCode.FAIL,"使用道具失败");
        }

        return;
    }

    @Override
    @HandlerCmdTag(cmd = ConstantValue.ADD_ARTICLE_REQUEST, module = ConstantValue.BAKCPACK_MODULE)
    public void addArticleRequest(BackPackModel.BackPackModelMessage myMessage, MmoSimpleRole mmoSimpleRole) throws Exception {
        Integer id = myMessage.getAddArticleRequest().getId();
        Integer articleType = myMessage.getAddArticleRequest().getArticleType();
        Integer number = myMessage.getAddArticleRequest().getNumber();
        Channel channel = mmoSimpleRole.getChannel();
        //根据 articleType判断 然后生成物品对象存
        Article article;
        if (articleType.equals(ArticleTypeCode.MEDICINE.getCode())) {
            MedicineMessage medicineMessage = MedicineMessageCache.getInstance().get(id);
            if (medicineMessage == null) {
                throw new RpgServerException(StateCode.FAIL,"存入错误物品id");
            }
            MedicineBean medicineBean = CommonsUtil.medicineMessageToMedicineBean(medicineMessage);
            medicineBean.setQuantity(number);
            article = medicineBean;
        } else if (articleType.equals(ArticleTypeCode.EQUIPMENT.getCode())) {
            EquipmentMessage equipmentMessage = EquipmentMessageCache.getInstance().get(id);
            if (equipmentMessage == null) {
                throw new RpgServerException(StateCode.FAIL,"存入错误物品id");
            }
            article = ArticleServiceProvider.productEquipment(id);;
        } else {
            //未知物品
            throw new RpgServerException(StateCode.FAIL,"未知物品不能存储");
        }
        //上锁
        synchronized (mmoSimpleRole.getBackpackManager()) {
            if (!mmoSimpleRole.getBackpackManager().canPutArticle(article)) {
                throw new RpgServerException(StateCode.FAIL,"背包已经满了");
            }
            mmoSimpleRole.getBackpackManager().put(article,mmoSimpleRole.getId());
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
    public void findAllCanGetRequest(BackPackModel.BackPackModelMessage myMessage, MmoSimpleRole mmoSimpleRole) throws InvalidProtocolBufferException, RpgServerException {
        Channel channel = mmoSimpleRole.getChannel();
        //副本中可捡去的物品
        Integer copySceneBeanId = mmoSimpleRole.getCopySceneBeanId();
        if (copySceneBeanId == null) {
            throw new RpgServerException(StateCode.FAIL,"当前角色还未进入副本");
        }
        CopySceneBean copySceneBean = CopySceneProvider.getCopySceneBeanById(copySceneBeanId);
        List<BackPackModel.ArticleFloorDto> resultList = new ArrayList<>();
        if (copySceneBean.getArticlesMap().size() > 0) {
            for (Article a : copySceneBean.getArticlesMap().values()) {
                BackPackModel.ArticleFloorDto.Builder dtoBuilder = BackPackModel.ArticleFloorDto.newBuilder();
                if (a.getArticleTypeCode().equals(ArticleTypeCode.EQUIPMENT.getCode())) {
                    //装备
                    EquipmentBean equipmentBean = (EquipmentBean) a;
                    EquipmentMessage equipmentMessage=EquipmentMessageCache.getInstance().get(equipmentBean.getEquipmentMessageId());
                    dtoBuilder.setId(equipmentMessage.getId())
                            .setArticleType(equipmentMessage.getArticleType())
                            .setFloorIndex(equipmentBean.getFloorIndex())
                            .setNowDurability(equipmentBean.getNowDurability())
                            .setQuantity(1)
                            .setEquipmentId(equipmentBean.getEquipmentId());
                } else {
                    //道具
                    MedicineBean medicineBean = (MedicineBean) a;
                    MedicineMessage medicineMessage= MedicineMessageCache.getInstance().get(medicineBean.getMedicineMessageId());
                    dtoBuilder.setId(medicineBean.getMedicineMessageId())
                            .setArticleType(medicineMessage.getArticleType())
                            .setFloorIndex(medicineBean.getFloorIndex())
                            .setNowDurability(-1)
                            .setQuantity(medicineBean.getQuantity())
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
    public void getArticleFromFloorRequest(BackPackModel.BackPackModelMessage myMessage, MmoSimpleRole mmoSimpleRole) throws Exception {
        Integer index = myMessage.getGetArticleFromFloorRequest().getIndex();
        Channel channel = mmoSimpleRole.getChannel();
        Integer copySceneBeanId = mmoSimpleRole.getCopySceneBeanId();
        if (copySceneBeanId == null) {
            throw new RpgServerException(StateCode.FAIL,"当前角色还未进入副本");
        }
        CopySceneBean copySceneBean = CopySceneProvider.getCopySceneBeanById(copySceneBeanId);
        Article article = copySceneBean.getArticleByIndex(index);
        if (article == null) {
            throw new RpgServerException(StateCode.FAIL,"该物品已经被其他玩家获取了");
        }
        //放入背包
        synchronized (mmoSimpleRole.getBackpackManager()) {
            if (!mmoSimpleRole.getBackpackManager().canPutArticle(article)) {
                throw new RpgServerException(StateCode.FAIL,"背包已经满了");
            }
            mmoSimpleRole.getBackpackManager().put(article,mmoSimpleRole.getId());
        }
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
        Channel channel = mmoSimpleRole.getChannel();
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
        Channel channel = mmoSimpleRole.getChannel();
        Integer num=myMessage.getBuyGoodsRequest().getNum();
        Integer goodsId=myMessage.getBuyGoodsRequest().getGoodsId();
        if (num<=0){
            throw new RpgServerException(StateCode.FAIL,"请输入正确范围的数字");
        }
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
        Channel channel = mmoSimpleRole.getChannel();
        List<GoodsBean> goodsBeans=GoodsServiceProvider.getAllArticles();
        //传输数据
        List<BackPackModel.GoodsDto> goodsDtos=new ArrayList<>();
        for (GoodsBean gg:goodsBeans) {
            GoodsMessage goodsMessage= GoodsMessageCache.getInstance().get(gg.getId());
            BackPackModel.GoodsDto goodsDto= BackPackModel.GoodsDto.newBuilder()
                    .setId(gg.getId()).setNowNum(gg.getNowNum()).setNum(goodsMessage.getNum())
                    .setPrice(goodsMessage.getPrice()).setArticleTypeId(goodsMessage.getArticleTypeId()).setArticleMessageId(goodsMessage.getArticleMessageId()).build();
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

    @Override
    @HandlerCmdTag(cmd = ConstantValue.SORT_BACKPACK_REQUEST, module = ConstantValue.BAKCPACK_MODULE)
    public void sortBackPack(BackPackModel.BackPackModelMessage myMessage, MmoSimpleRole mmoSimpleRole) throws Exception {
        mmoSimpleRole.getBackpackManager().clearBackPack(mmoSimpleRole.getId());
    }
}