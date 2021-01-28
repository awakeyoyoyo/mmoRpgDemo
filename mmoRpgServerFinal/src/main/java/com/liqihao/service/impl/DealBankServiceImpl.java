package com.liqihao.service.impl;

import com.googlecode.protobuf.format.JsonFormat;
import com.liqihao.annotation.HandlerCmdTag;
import com.liqihao.annotation.HandlerServiceTag;
import com.liqihao.commons.ConstantValue;
import com.liqihao.commons.NettyResponse;
import com.liqihao.commons.RpgServerException;
import com.liqihao.commons.StateCode;
import com.liqihao.pojo.bean.articleBean.Article;

import com.liqihao.pojo.bean.dealBankBean.DealBankArticleBean;
import com.liqihao.pojo.bean.roleBean.MmoSimpleRole;
import com.liqihao.protobufObject.DealBankModel;
import com.liqihao.protobufObject.DealModel;
import com.liqihao.provider.DealBankServiceProvider;
import com.liqihao.service.DealBankService;
import com.liqihao.util.CommonsUtil;
import com.liqihao.util.NotificationUtil;
import io.netty.channel.Channel;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 拍卖行模块
 * @author lqhao
 */@Service
@HandlerServiceTag(protobufModel = "DealBankModel$DealBankModelMessage")
public class DealBankServiceImpl implements DealBankService {
    @Override
    @HandlerCmdTag(cmd = ConstantValue.ADD_SELL_ARTICLE_REQUEST, module = ConstantValue.DEAL_BANK_MODULE)
    public void addSellArticleRequest(DealBankModel.DealBankModelMessage myMessage, MmoSimpleRole mmoSimpleRole) throws RpgServerException {
        int articleId = myMessage.getAddSellArticleRequest().getArticleId();
        int num = myMessage.getAddSellArticleRequest().getNum();
        int price = myMessage.getAddSellArticleRequest().getPrice();
        if (price<=0) {
            throw new RpgServerException(StateCode.FAIL,"错误范围金币");
        }
        int type=myMessage.getAddSellArticleRequest().getType();
        Channel channel=mmoSimpleRole.getChannel();
        Article article=mmoSimpleRole.getBackpackManager().useOrAbandonArticle(articleId,num,mmoSimpleRole.getId());
        if (article==null){
            throw new RpgServerException(StateCode.FAIL,"上架失败，无该物品或者数量不足");
        }
        DealBankServiceProvider.addSellArticleToDealBank(article,mmoSimpleRole,price,type,num);
        //protobuf
        NettyResponse nettyResponse = new NettyResponse();
        nettyResponse.setCmd(ConstantValue.ADD_SELL_ARTICLE_RESPONSE);
        nettyResponse.setStateCode(StateCode.SUCCESS);
        //protobuf 生成registerResponse
        DealBankModel.DealBankModelMessage.Builder messageData = DealBankModel.DealBankModelMessage.newBuilder();
        messageData.setDataType(DealBankModel.DealBankModelMessage.DateType.AddSellArticleResponse);
        DealBankModel.AddSellArticleResponse.Builder addSellArticleResponseBuilder = DealBankModel.AddSellArticleResponse.newBuilder();
        messageData.setAddSellArticleResponse(addSellArticleResponseBuilder.build());
        nettyResponse.setData(messageData.build().toByteArray());
        String json= JsonFormat.printToString(messageData.build());
        NotificationUtil.sendMessage(channel,nettyResponse,json);
    }


    @Override
    @HandlerCmdTag(cmd = ConstantValue.REDUCE_SELL_ARTICLE_REQUEST, module = ConstantValue.DEAL_BANK_MODULE)
    public void reduceSellArticleRequest(DealBankModel.DealBankModelMessage myMessage, MmoSimpleRole mmoSimpleRole) throws RpgServerException {
        int dealBankArticleId=myMessage.getReduceSellArticleRequest().getDealBankArticleId();
        DealBankServiceProvider.reduceSellArticleToDealBank(dealBankArticleId,mmoSimpleRole);
        Channel channel=mmoSimpleRole.getChannel();
        //protobuf
        NettyResponse nettyResponse = new NettyResponse();
        nettyResponse.setCmd(ConstantValue.REDUCE_SELL_ARTICLE_RESPONSE);
        nettyResponse.setStateCode(StateCode.SUCCESS);
        //protobuf 生成registerResponse
        DealBankModel.DealBankModelMessage.Builder messageData = DealBankModel.DealBankModelMessage.newBuilder();
        messageData.setDataType(DealBankModel.DealBankModelMessage.DateType.ReduceSellArticleResponse);
        DealBankModel.ReduceSellArticleResponse.Builder reduceSellArticleResponseBuilder = DealBankModel.ReduceSellArticleResponse
                .newBuilder();
        messageData.setReduceSellArticleResponse(reduceSellArticleResponseBuilder.build());
        nettyResponse.setData(messageData.build().toByteArray());
        String json= JsonFormat.printToString(messageData.build());
        NotificationUtil.sendMessage(channel,nettyResponse,json);
    }


    @Override
    @HandlerCmdTag(cmd = ConstantValue.BUY_ARTICLE_REQUEST, module = ConstantValue.DEAL_BANK_MODULE)
    public void buyArticleRequest(DealBankModel.DealBankModelMessage myMessage, MmoSimpleRole mmoSimpleRole) throws RpgServerException {
        int dealBankArticleId=myMessage.getBuyArticleRequest().getDealBankArticleId();
        DealBankServiceProvider.buySellArticleToDealBank(dealBankArticleId,mmoSimpleRole,0);
        Channel channel=mmoSimpleRole.getChannel();
        //protobuf
        NettyResponse nettyResponse = new NettyResponse();
        nettyResponse.setCmd(ConstantValue.BUY_ARTICLE_RESPONSE);
        nettyResponse.setStateCode(StateCode.SUCCESS);
        //protobuf 生成registerResponse
        DealBankModel.DealBankModelMessage.Builder messageData = DealBankModel.DealBankModelMessage.newBuilder();
        messageData.setDataType(DealBankModel.DealBankModelMessage.DateType.BuyArticleResponse);
        DealBankModel.BuyArticleResponse.Builder buyArticleResponseBuilder = DealBankModel.BuyArticleResponse
                .newBuilder();
        messageData.setBuyArticleResponse(buyArticleResponseBuilder.build());
        nettyResponse.setData(messageData.build().toByteArray());
        String json= JsonFormat.printToString(messageData.build());
        NotificationUtil.sendMessage(channel,nettyResponse,json);
    }

    @Override
    @HandlerCmdTag(cmd = ConstantValue.AUCTION_ARTICLE_REQUEST, module = ConstantValue.DEAL_BANK_MODULE)
    public void auctionArticleRequest(DealBankModel.DealBankModelMessage myMessage, MmoSimpleRole mmoSimpleRole) throws RpgServerException {
        int dealBankArticleId=myMessage.getAuctionArticleRequest().getDealBankArticleId();
        int money=myMessage.getAuctionArticleRequest().getMoney();
        if (money<0) {
            throw new RpgServerException(StateCode.FAIL,"错误范围金币");
        }
        DealBankServiceProvider.buySellArticleToDealBank(dealBankArticleId,mmoSimpleRole,money);
        Channel channel=mmoSimpleRole.getChannel();
        //protobuf
        NettyResponse nettyResponse = new NettyResponse();
        nettyResponse.setCmd(ConstantValue.AUCTION_ARTICLE_RESPONSE);
        nettyResponse.setStateCode(StateCode.SUCCESS);
        //protobuf 生成registerResponse
        DealBankModel.DealBankModelMessage.Builder messageData = DealBankModel.DealBankModelMessage.newBuilder();
        messageData.setDataType(DealBankModel.DealBankModelMessage.DateType.AuctionArticleResponse);
        DealBankModel.AuctionArticleResponse.Builder auctionArticleResponse = DealBankModel.AuctionArticleResponse
                .newBuilder();
        messageData.setAuctionArticleResponse(auctionArticleResponse.build());
        nettyResponse.setData(messageData.build().toByteArray());
        String json= JsonFormat.printToString(messageData.build());
        NotificationUtil.sendMessage(channel,nettyResponse,json);
    }

    @Override
    @HandlerCmdTag(cmd = ConstantValue.GET_ARTICLE_REQUEST, module = ConstantValue.DEAL_BANK_MODULE)
    public void getArticleRequest(DealBankModel.DealBankModelMessage myMessage, MmoSimpleRole mmoSimpleRole) throws RpgServerException {
        Channel channel=mmoSimpleRole.getChannel();
        List<DealBankArticleBean> dealBankArticleBeans=DealBankServiceProvider.getSellArticleToDealBank();
        List<DealBankModel.DealBankArticleDto> dealBankArticleDtos=new ArrayList<>();
        for (DealBankArticleBean dealBankArticleBean : dealBankArticleBeans) {
            DealBankModel.DealBankArticleDto dealBankArticleDto= CommonsUtil.dealBankArticleBeanToDealBankArticleDto(dealBankArticleBean);
            dealBankArticleDtos.add(dealBankArticleDto);
        }

        //protobuf
        NettyResponse nettyResponse = new NettyResponse();
        nettyResponse.setCmd(ConstantValue.GET_ARTICLE_RESPONSE);
        nettyResponse.setStateCode(StateCode.SUCCESS);
        //protobuf 生成registerResponse
        DealBankModel.DealBankModelMessage.Builder messageData = DealBankModel.DealBankModelMessage.newBuilder();
        messageData.setDataType(DealBankModel.DealBankModelMessage.DateType.GetArticleResponse);
        DealBankModel.GetArticleResponse.Builder getArticleResponse= DealBankModel.GetArticleResponse
                .newBuilder().addAllDealBankArticleDtos(dealBankArticleDtos);
        messageData.setGetArticleResponse(getArticleResponse.build());
        nettyResponse.setData(messageData.build().toByteArray());
        String json= JsonFormat.printToString(messageData.build());
        NotificationUtil.sendMessage(channel,nettyResponse,json);
    }
}
