package com.liqihao.service.impl;

import com.liqihao.Cache.OnlineRoleMessageCache;
import com.liqihao.annotation.HandlerCmdTag;
import com.liqihao.annotation.HandlerServiceTag;
import com.liqihao.commons.ConstantValue;
import com.liqihao.commons.NettyResponse;
import com.liqihao.commons.RpgServerException;
import com.liqihao.commons.StateCode;
import com.liqihao.commons.enums.DealStatusCode;
import com.liqihao.pojo.bean.articleBean.Article;
import com.liqihao.pojo.bean.dealBean.DealBean;
import com.liqihao.pojo.bean.roleBean.MmoSimpleRole;
import com.liqihao.pojo.dto.ArticleDto;
import com.liqihao.protobufObject.DealModel;
import com.liqihao.provider.DealServiceProvider;
import com.liqihao.service.DealService;
import com.liqihao.util.CommonsUtil;
import io.netty.channel.Channel;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 交易模块
 * @author lqhao
 */
@Service
@HandlerServiceTag(protobufModel = "DealModel$DealModelMessage")
public class DealServiceImpl implements DealService {
    @Override
    @HandlerCmdTag(cmd = ConstantValue.ASK_DEAL_REQUEST,module = ConstantValue.DEAL_MODULE)
    public void askDealRequest(DealModel.DealModelMessage myMessage, MmoSimpleRole mmoSimpleRole) throws RpgServerException {
        Channel channel = mmoSimpleRole.getChannel();
        Integer roleId=myMessage.getAskDealRequest().getRoleId();
        MmoSimpleRole role2= OnlineRoleMessageCache.getInstance().get(roleId);
        if (role2==null){
            throw new RpgServerException(StateCode.FAIL,"该用户不在线或者不存在该用户");
        }
        DealServiceProvider.createDeal(mmoSimpleRole,role2);
        //返回成功的数据包
        NettyResponse nettyResponse = new NettyResponse();
        nettyResponse.setCmd(ConstantValue.ASK_DEAL_RESPONSE);
        nettyResponse.setStateCode(StateCode.SUCCESS);
        //protobuf 生成registerResponse
        DealModel.DealModelMessage.Builder messageData = DealModel.DealModelMessage.newBuilder();
        messageData.setDataType(DealModel.DealModelMessage.DateType.AskDealResponse);
        DealModel.AskDealResponse.Builder askDealResponseResponseBuilder = DealModel.AskDealResponse.newBuilder()
                .setRoleId(mmoSimpleRole.getId()).setRoleName(mmoSimpleRole.getName());
        messageData.setAskDealResponse(askDealResponseResponseBuilder.build());
        nettyResponse.setData(messageData.build().toByteArray());
        channel.writeAndFlush(nettyResponse);
        Channel channel02=role2.getChannel();
        channel02.writeAndFlush(nettyResponse);
    }

    @Override
    @HandlerCmdTag(cmd = ConstantValue.AGREE_DEAL_REQUEST,module = ConstantValue.DEAL_MODULE)
    public void agreeDealRequest(DealModel.DealModelMessage myMessage, MmoSimpleRole mmoSimpleRole) throws RpgServerException {
        Channel channel = mmoSimpleRole.getChannel();
        DealBean dealBean=DealServiceProvider.beginDeal(mmoSimpleRole);
        //返回成功的数据包
        NettyResponse nettyResponse = new NettyResponse();
        nettyResponse.setCmd(ConstantValue.AGREE_DEAL_RESPONSE);
        nettyResponse.setStateCode(StateCode.SUCCESS);
        //protobuf 生成registerResponse
        DealModel.DealModelMessage.Builder messageData = DealModel.DealModelMessage.newBuilder();
        messageData.setDataType(DealModel.DealModelMessage.DateType.AgreeDealResponse);
        DealModel.AgreeDealResponse.Builder agreeDealResponseBuilder = DealModel.AgreeDealResponse.newBuilder();
        messageData.setAgreeDealResponse(agreeDealResponseBuilder.build());
        nettyResponse.setData(messageData.build().toByteArray());
        channel.writeAndFlush(nettyResponse);
        Channel channel02=dealBean.getFirstRole().getChannel();
        if (channel02!=null) {
            channel02.writeAndFlush(nettyResponse);
        }
    }

    @Override
    @HandlerCmdTag(cmd = ConstantValue.REFUSE_DEAL_REQUEST,module = ConstantValue.DEAL_MODULE)
    public void refuseDealRequest(DealModel.DealModelMessage myMessage, MmoSimpleRole mmoSimpleRole) throws RpgServerException {
        Channel channel = mmoSimpleRole.getChannel();
        DealBean dealBean=DealServiceProvider.refuseDeal(mmoSimpleRole);
        //返回数据包
        NettyResponse nettyResponse = new NettyResponse();
        nettyResponse.setCmd(ConstantValue.REFUSE_DEAL_RESPONSE);
        nettyResponse.setStateCode(StateCode.SUCCESS);
        //protobuf 生成registerResponse
        DealModel.DealModelMessage.Builder messageData = DealModel.DealModelMessage.newBuilder();
        messageData.setDataType(DealModel.DealModelMessage.DateType.RefuseDealResponse);
        DealModel.RefuseDealResponse.Builder refuseDealResponseBuilder = DealModel.RefuseDealResponse.newBuilder();
        messageData.setRefuseDealResponse(refuseDealResponseBuilder.build());
        nettyResponse.setData(messageData.build().toByteArray());
        channel.writeAndFlush(nettyResponse);
        Channel channel02=dealBean.getFirstRole().getChannel();
        if (channel02!=null){
            channel02.writeAndFlush(nettyResponse);
        }
    }

    @Override
    @HandlerCmdTag(cmd = ConstantValue.CONFIRM_DEAL_REQUEST,module = ConstantValue.DEAL_MODULE)
    public void confirmDealRequest(DealModel.DealModelMessage myMessage, MmoSimpleRole mmoSimpleRole) throws RpgServerException {
        DealServiceProvider.confirmDeal(mmoSimpleRole);
    }

    @Override
    @HandlerCmdTag(cmd = ConstantValue.CANCEL_DEAL_REQUEST,module = ConstantValue.DEAL_MODULE)
    public void cancelDealRequest(DealModel.DealModelMessage myMessage, MmoSimpleRole mmoSimpleRole) throws RpgServerException {
        DealBean dealBean=DealServiceProvider.cancelDeal(mmoSimpleRole);
        //返回数据包
        NettyResponse nettyResponse = new NettyResponse();
        nettyResponse.setCmd(ConstantValue.CANCEL_DEAL_RESPONSE);
        nettyResponse.setStateCode(StateCode.SUCCESS);
        //protobuf 生成registerResponse
        DealModel.DealModelMessage.Builder messageData = DealModel.DealModelMessage.newBuilder();
        messageData.setDataType(DealModel.DealModelMessage.DateType.CancelDealResponse);
        DealModel.CancelDealResponse.Builder cancelDealResponseBuilder = DealModel.CancelDealResponse.newBuilder()
                .setRoleId(mmoSimpleRole.getId()).setRoleName(mmoSimpleRole.getName());
        messageData.setCancelDealResponse(cancelDealResponseBuilder.build());
        nettyResponse.setData(messageData.build().toByteArray());
        Channel channel=dealBean.getSecondRole().getChannel();
        if (channel!=null) {
            channel.writeAndFlush(nettyResponse);
        }
        Channel channel02=dealBean.getFirstRole().getChannel();
        if (channel02!=null) {
            channel02.writeAndFlush(nettyResponse);
        }
    }

    @Override
    @HandlerCmdTag(cmd = ConstantValue.GET_DEAL_MESSAGE_REQUEST,module = ConstantValue.DEAL_MODULE)
    public void getDealMessageRequest(DealModel.DealModelMessage myMessage, MmoSimpleRole mmoSimpleRole) throws RpgServerException {
        Channel channel = mmoSimpleRole.getChannel();
        Integer dealBeanId=mmoSimpleRole.getDealBeanId();
        if (dealBeanId==null){
            throw new RpgServerException(StateCode.FAIL,"不在交易状态中");
        }
        DealBean dealBean=DealServiceProvider.getDealBean(dealBeanId);
        if (dealBean.getStatus().equals(DealStatusCode.WAIT.getCode())){
            throw new RpgServerException(StateCode.FAIL,"交易还未开始，等待接收交易请求");
        }
        List<DealModel.ArticleDto> articleBuilders01=new ArrayList<>();
        List<DealModel.ArticleDto> articleBuilders02=new ArrayList<>();
        List<ArticleDto> firstArticles=dealBean.getFirstDealArticleBean().getArticleDto();
        List<ArticleDto> secondArticles=dealBean.getSecondDealArticleBean().getArticleDto();
        articleBuilders01=CommonsUtil.articlesToDealModelArticleDto(firstArticles);
        articleBuilders02=CommonsUtil.articlesToDealModelArticleDto(secondArticles);
        NettyResponse nettyResponse = new NettyResponse();
        nettyResponse.setCmd(ConstantValue.GET_DEAL_MESSAGE_RESPONSE);
        nettyResponse.setStateCode(StateCode.SUCCESS);
        //protobuf 生成registerResponse
        DealModel.DealModelMessage.Builder messageData = DealModel.DealModelMessage.newBuilder();
        messageData.setDataType(DealModel.DealModelMessage.DateType.GetDealMessageResponse);
        DealModel.GetDealMessageResponse.Builder getDealMessageResponseBuilder = DealModel.GetDealMessageResponse.newBuilder().setFirstMoney(dealBean.getFirstDealArticleBean().getMoney())
                .setFirstRoleId(dealBean.getFirstRole().getId()).setFirstRoleName(dealBean.getFirstRole().getName()).addAllFirstArticleDto(articleBuilders01)
                .setSecondRoleId(dealBean.getSecondRole().getId()).setSecondRoleName(dealBean.getSecondRole().getName()).addAllSecondArticleDto(articleBuilders02).setSecondMoney(dealBean.getSecondDealArticleBean().getMoney());
        messageData.setGetDealMessageResponse(getDealMessageResponseBuilder.build());
        nettyResponse.setData(messageData.build().toByteArray());
        channel.writeAndFlush(nettyResponse);
    }

    @Override
    @HandlerCmdTag(cmd = ConstantValue.SET_DEAL_MONEY_REQUEST,module = ConstantValue.DEAL_MODULE)
    public void setDealMoneyRequest(DealModel.DealModelMessage myMessage, MmoSimpleRole mmoSimpleRole) throws RpgServerException {
        Integer money=myMessage.getSetDealMoneyRequest().getMoney();
        DealBean dealBean=DealServiceProvider.setMoneyDeal(mmoSimpleRole,money);
        NettyResponse nettyResponse = new NettyResponse();
        nettyResponse.setCmd(ConstantValue.SET_DEAL_MONEY_RESPONSE);
        nettyResponse.setStateCode(StateCode.SUCCESS);
        //protobuf 生成registerResponse
        DealModel.DealModelMessage.Builder messageData = DealModel.DealModelMessage.newBuilder();
        messageData.setDataType(DealModel.DealModelMessage.DateType.SetDealMoneyResponse);
        DealModel.SetDealMoneyResponse.Builder setDealMoneyResponseBuilder = DealModel.SetDealMoneyResponse.newBuilder()
                .setMoney(money).setRoleId(mmoSimpleRole.getId()).setRoleName(mmoSimpleRole.getName());
        messageData.setSetDealMoneyResponse(setDealMoneyResponseBuilder.build());
        nettyResponse.setData(messageData.build().toByteArray());
        Channel channel=dealBean.getSecondRole().getChannel();
        if (channel!=null) {
            channel.writeAndFlush(nettyResponse);
        }
        Channel channel02=dealBean.getFirstRole().getChannel();
        if (channel02!=null) {
            channel02.writeAndFlush(nettyResponse);
        }
    }

    @Override
    @HandlerCmdTag(cmd = ConstantValue.ADD_DEAL_ARTICLE_REQUEST,module = ConstantValue.DEAL_MODULE)
    public void addDealArticleRequest(DealModel.DealModelMessage myMessage, MmoSimpleRole mmoSimpleRole) throws RpgServerException {
        Integer articleId=myMessage.getAddArticleRequest().getArticleId();
        Integer num=myMessage.getAddArticleRequest().getNum();
        Article article=DealServiceProvider.addArticleDeal(articleId,num,mmoSimpleRole);
        ArticleDto articleDto=article.getArticleMessage();
        DealModel.ArticleDto articleDto1= DealModel.ArticleDto.newBuilder().setArticleType(articleDto.getArticleType()).setArticleMessageId(articleDto.getId())
                .setEquipmentId(articleDto.getEquipmentId()==null?-1:articleDto.getEquipmentId()).setQuantity(articleDto.getQuantity()).setNowDurability(articleDto.getNowDurability()==null?-1:articleDto.getNowDurability()).build();
        //发送消息给双方 交易中增加了什么
        NettyResponse nettyResponse = new NettyResponse();
        nettyResponse.setCmd(ConstantValue.ADD_DEAL_ARTICLE_RESPONSE);
        nettyResponse.setStateCode(StateCode.SUCCESS);
        //protobuf 生成registerResponse
        DealModel.DealModelMessage.Builder messageData = DealModel.DealModelMessage.newBuilder();
        messageData.setDataType(DealModel.DealModelMessage.DateType.AddArticleResponse);
        DealModel.AddArticleResponse.Builder addArticleResponseBuilder = DealModel.AddArticleResponse.newBuilder()
                .setRoleId(mmoSimpleRole.getId()).setRoleName(mmoSimpleRole.getName()).setArticleDto(articleDto1);
        messageData.setAddArticleResponse(addArticleResponseBuilder.build());
        nettyResponse.setData(messageData.build().toByteArray());
        DealBean dealBean=DealServiceProvider.getDealBean(mmoSimpleRole.getDealBeanId());
        Channel channel=dealBean.getSecondRole().getChannel();
        if (channel!=null) {
            channel.writeAndFlush(nettyResponse);
        }
        Channel channel02=dealBean.getFirstRole().getChannel();
        if (channel02!=null) {
            channel02.writeAndFlush(nettyResponse);
        }
    }

    @Override
    @HandlerCmdTag(cmd = ConstantValue.ABANDON_DEAL_ARTICLE_REQUEST,module = ConstantValue.DEAL_MODULE)
    public void abandonDealArticleRequest(DealModel.DealModelMessage myMessage, MmoSimpleRole mmoSimpleRole) throws RpgServerException {
        Integer dealArticleId=myMessage.getAbandonArticleRequest().getDealArticleId();
        Integer num=myMessage.getAbandonArticleRequest().getNum();
        Article article=DealServiceProvider.reduceArticleDeal(dealArticleId,num,mmoSimpleRole);

        ArticleDto articleDto=article.getArticleMessage();
        DealModel.ArticleDto articleDto1= DealModel.ArticleDto.newBuilder().setArticleType(articleDto.getArticleType()).setArticleMessageId(articleDto.getId())
                .setEquipmentId(articleDto.getEquipmentId()).setQuantity(articleDto.getQuantity()).setNowDurability(articleDto.getNowDurability()).build();
        //发送消息给双方 交易中丢弃了什么
        NettyResponse nettyResponse = new NettyResponse();
        nettyResponse.setCmd(ConstantValue.ABANDON_DEAL_ARTICLE_RESPONSE);
        nettyResponse.setStateCode(StateCode.SUCCESS);
        //protobuf 生成registerResponse
        DealModel.DealModelMessage.Builder messageData = DealModel.DealModelMessage.newBuilder();
        messageData.setDataType(DealModel.DealModelMessage.DateType.AbandonArticleResponse);
        DealModel.AbandonArticleResponse.Builder abandonArticleResponseBuilder = DealModel.AbandonArticleResponse.newBuilder()
                .setRoleId(mmoSimpleRole.getId()).setRoleName(mmoSimpleRole.getName()).setArticleDto(articleDto1);
        messageData.setAbandonArticleResponse(abandonArticleResponseBuilder.build());
        nettyResponse.setData(messageData.build().toByteArray());
        DealBean dealBean=DealServiceProvider.getDealBean(mmoSimpleRole.getDealBeanId());
        Channel channel=dealBean.getSecondRole().getChannel();
        if (channel!=null) {
            channel.writeAndFlush(nettyResponse);
        }
        Channel channel02=dealBean.getFirstRole().getChannel();
        if (channel02!=null) {
            channel02.writeAndFlush(nettyResponse);
        }
    }
}
