package com.liqihao.service.impl;

import com.liqihao.Cache.ChannelMessageCache;
import com.liqihao.Cache.MediceneMessageCache;
import com.liqihao.Cache.OnlineRoleMessageCache;
import com.liqihao.annotation.HandlerCmdTag;
import com.liqihao.annotation.HandlerServiceTag;
import com.liqihao.commons.ConstantValue;
import com.liqihao.commons.NettyResponse;
import com.liqihao.commons.RpgServerException;
import com.liqihao.commons.StateCode;
import com.liqihao.commons.enums.ArticleTypeCode;
import com.liqihao.pojo.baseMessage.MedicineMessage;
import com.liqihao.pojo.bean.*;
import com.liqihao.protobufObject.CopySceneModel;
import com.liqihao.protobufObject.EmailModel;
import com.liqihao.provider.EmailServiceProvider;
import com.liqihao.service.EmailService;
import com.liqihao.util.CommonsUtil;
import io.netty.channel.Channel;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 邮件模块
 * @author lqhao
 */
@Service
@HandlerServiceTag(protobufModel = "EmailModel$EmailModelMessage")
public class EmailServiceImpl implements EmailService {
    @Override
    @HandlerCmdTag(cmd = ConstantValue.GET_EMAIL_MESSAGE_REQUEST,module = ConstantValue.EMAIL_MODULE)
    public void getEmailMessageRequest(EmailModel.EmailModelMessage myMessage, MmoSimpleRole mmoSimpleRole) throws RpgServerException {
        Integer emailId=myMessage.getGetEmailMessageRequest().getEmailId();
        Channel channel = mmoSimpleRole.getChannel();
        MmoEmailBean mmoEmailBean=EmailServiceProvider.getEmailMessage(mmoSimpleRole,emailId);
        if (mmoEmailBean==null){
            throw new RpgServerException(StateCode.FAIL,"没有该id的邮件");
        }
        EmailModel.EmailDto emailDto= CommonsUtil.mmoEmailBeanToEmailDto(mmoEmailBean);
        EmailModel.EmailModelMessage messageData=EmailModel.EmailModelMessage.newBuilder()
                .setDataType(EmailModel.EmailModelMessage.DateType.GetEmailMessageResponse)
                .setGetEmailMessageResponse(EmailModel.GetEmailMessageResponse.newBuilder().setEmailDto(emailDto).build()).build();
        NettyResponse nettyResponse=new NettyResponse();
        nettyResponse.setCmd(ConstantValue.GET_EMAIL_MESSAGE_RESPONSE);
        nettyResponse.setStateCode(StateCode.SUCCESS);
        nettyResponse.setData(messageData.toByteArray());
        channel.writeAndFlush(nettyResponse);
    }

    @Override
    @HandlerCmdTag(cmd = ConstantValue.GET_EMAIL_ARTICLE_REQUEST,module = ConstantValue.EMAIL_MODULE)
    public void getEmailArticleRequest(EmailModel.EmailModelMessage myMessage, MmoSimpleRole mmoSimpleRole) throws Exception {
        Integer emailId=myMessage.getGetEmailArticleRequest().getEmailId();
        Channel channel = mmoSimpleRole.getChannel();
        //获取邮件详情
        MmoEmailBean mmoEmailBean=EmailServiceProvider.getEmailMessage(mmoSimpleRole,emailId);
        if (mmoEmailBean==null){
            throw new RpgServerException(StateCode.FAIL,"没有该id的邮件");
        }
        //判断邮件是否有物品
        if (!mmoEmailBean.getHasArticle()){
            throw new RpgServerException(StateCode.FAIL,"该邮件没有物品");
        }
        //根据邮件实体类信息初始化物品
        if (mmoEmailBean.getArticleType().equals(ArticleTypeCode.MEDICINE.getCode())){
            BackPackManager backPackManager=mmoSimpleRole.getBackpackManager();
            MedicineMessage medicineMessage= MediceneMessageCache.getInstance().get(mmoEmailBean.getArticleMessageId());
            MedicineBean medicineBean=CommonsUtil.medicineMessageToMedicineBean(medicineMessage);
            medicineBean.setQuantity(mmoEmailBean.getArticleNum());
            //上锁
            synchronized (mmoSimpleRole.getBackpackManager()) {
                if (!mmoSimpleRole.getBackpackManager().canPutArticle(medicineBean)) {
                    throw new RpgServerException(StateCode.FAIL,"背包已经满了");
                }
                mmoSimpleRole.getBackpackManager().put(medicineBean);
            }
            //邮件设置为没有物品
            mmoEmailBean.setArticleMessageId(-1);
            mmoEmailBean.setArticleType(-1);
            mmoEmailBean.setHasArticle(false);
            mmoEmailBean.setArticleNum(-1);
        }
        EmailModel.EmailModelMessage messageData=EmailModel.EmailModelMessage.newBuilder()
                .setDataType(EmailModel.EmailModelMessage.DateType.GetEmailArticleResponse)
                .setGetEmailArticleResponse(EmailModel.GetEmailArticleResponse.newBuilder().build()).build();
        NettyResponse nettyResponse=new NettyResponse();
        nettyResponse.setCmd(ConstantValue.GET_EMAIL_ARTICLE_RESPONSE);
        nettyResponse.setStateCode(StateCode.SUCCESS);
        nettyResponse.setData(messageData.toByteArray());
        channel.writeAndFlush(nettyResponse);
    }

    @Override
    @HandlerCmdTag(cmd = ConstantValue.ACCEPT_EMAIL_LIST_REQUEST,module = ConstantValue.EMAIL_MODULE)
    public void acceptEmailListRequest(EmailModel.EmailModelMessage myMessage, MmoSimpleRole mmoSimpleRole) {
        Channel channel = mmoSimpleRole.getChannel();
        List<MmoEmailBean> mmoEmailBeans=EmailServiceProvider.getToEmails(mmoSimpleRole);
        List<EmailModel.EmailSimpleDto> list=new ArrayList<>();
        if (mmoEmailBeans.size()>0){
            for (MmoEmailBean m:mmoEmailBeans) {
                EmailModel.EmailSimpleDto emailSimpleDto=CommonsUtil.mmoEmailBeanToEmailSimpleDto(m);
                list.add(emailSimpleDto);
            }
        }
        EmailModel.EmailModelMessage messageData=EmailModel.EmailModelMessage.newBuilder()
                .setDataType(EmailModel.EmailModelMessage.DateType.AcceptEmailListResponse)
                .setAcceptEmailListResponse(EmailModel.AcceptEmailListResponse.newBuilder().addAllEmailSimpleDtos(list).build()).build();
        NettyResponse nettyResponse=new NettyResponse();
        nettyResponse.setCmd(ConstantValue.ACCEPT_EMAIL_LIST_RESPONSE);
        nettyResponse.setStateCode(StateCode.SUCCESS);
        nettyResponse.setData(messageData.toByteArray());
        channel.writeAndFlush(nettyResponse);
    }

    @Override
    @HandlerCmdTag(cmd = ConstantValue.IS_SEND_EMAIL_LIST_REQUEST,module = ConstantValue.EMAIL_MODULE)
    public void isSendEmailListRequest(EmailModel.EmailModelMessage myMessage, MmoSimpleRole mmoSimpleRole) {
        Channel channel = mmoSimpleRole.getChannel();
        List<MmoEmailBean> mmoEmailBeans=EmailServiceProvider.getFromEmails(mmoSimpleRole);
        List<EmailModel.EmailSimpleDto> list=new ArrayList<>();
        if (mmoEmailBeans.size()>0){
            for (MmoEmailBean m:mmoEmailBeans) {
                EmailModel.EmailSimpleDto emailSimpleDto=CommonsUtil.mmoEmailBeanToEmailSimpleDto(m);
                list.add(emailSimpleDto);
            }
        }
        EmailModel.EmailModelMessage messageData=EmailModel.EmailModelMessage.newBuilder()
                .setDataType(EmailModel.EmailModelMessage.DateType.IsSendEmailListResponse)
                .setIsSendEmailListResponse(EmailModel.IsSendEmailListResponse.newBuilder().addAllEmailSimpleDtos(list).build()).build();
        NettyResponse nettyResponse=new NettyResponse();
        nettyResponse.setCmd(ConstantValue.IS_SEND_EMAIL_LIST_RESPONSE);
        nettyResponse.setStateCode(StateCode.SUCCESS);
        nettyResponse.setData(messageData.toByteArray());
        channel.writeAndFlush(nettyResponse);
    }

    @Override
    @HandlerCmdTag(cmd = ConstantValue.SEND_EMAIL_REQUEST,module = ConstantValue.EMAIL_MODULE)
    public void sendEmailRequest(EmailModel.EmailModelMessage myMessage, MmoSimpleRole mmoSimpleRole) throws RpgServerException {
        Integer articleId=myMessage.getSendEmailRequest().getArticleId();
        Channel channel = mmoSimpleRole.getChannel();
        String context=myMessage.getSendEmailRequest().getContext();
        String title=myMessage.getSendEmailRequest().getTitle();
        Integer articleNum=myMessage.getSendEmailRequest().getArticleNum();
        Integer toRoleId=myMessage.getSendEmailRequest().getToRoleId();
        MmoSimpleRole toRole= OnlineRoleMessageCache.getInstance().get(toRoleId);
        MmoEmailBean mmoEmailBean=new MmoEmailBean();
        mmoEmailBean.setContext(context);
        mmoEmailBean.setTitle(title);
        mmoEmailBean.setArticleNum(articleNum);
        mmoEmailBean.setArticleType(-1);
        mmoEmailBean.setArticleMessageId(-1);
        if (articleId!=-1) {
            //则需要扣除背包中的物品
           BackPackManager backPackManager=mmoSimpleRole.getBackpackManager();
           Article article=backPackManager.useOrAbandonArticle(articleId,articleNum);
           if (article==null){
               throw new RpgServerException(StateCode.FAIL,"背包中该物品数量不足");
           }
           mmoEmailBean.setArticleType(article.getArticleTypeCode());
           if (mmoEmailBean.getArticleType().equals(ArticleTypeCode.MEDICINE.getCode())) {
               MedicineBean medicineBean= (MedicineBean) article;
               mmoEmailBean.setArticleMessageId(medicineBean.getMedicineMessageId());
           }
        }
        mmoEmailBean.setToRoleId(toRoleId);
        mmoEmailBean.setFromRoleId(mmoSimpleRole.getId());
        EmailServiceProvider.sendArticleEmail(mmoSimpleRole,toRole,mmoEmailBean);
        EmailModel.EmailModelMessage messageData=EmailModel.EmailModelMessage.newBuilder()
                .setDataType(EmailModel.EmailModelMessage.DateType.SendEmailResponse)
                .setSendEmailResponse(EmailModel.SendEmailResponse.newBuilder().build()).build();
        NettyResponse nettyResponse=new NettyResponse();
        nettyResponse.setCmd(ConstantValue.SEND_EMAIL_RESPONSE);
        nettyResponse.setStateCode(StateCode.SUCCESS);
        nettyResponse.setData(messageData.toByteArray());
        channel.writeAndFlush(nettyResponse);
    }

    @Override
    @HandlerCmdTag(cmd = ConstantValue.DELETE_ACCEPT_EMAIL_REQUEST,module = ConstantValue.EMAIL_MODULE)
    public void deleteAcceptEmailRequest(EmailModel.EmailModelMessage myMessage, MmoSimpleRole mmoSimpleRole) {
        Integer emailId=myMessage.getDeleteAcceptEmailRequest().getEmailId();
        Channel channel = mmoSimpleRole.getChannel();
        EmailServiceProvider.deleteAcceptEmail(mmoSimpleRole,emailId);
        EmailModel.EmailModelMessage messageData=EmailModel.EmailModelMessage.newBuilder()
                .setDataType(EmailModel.EmailModelMessage.DateType.DeleteAcceptEmailResponse)
                .setDeleteAcceptEmailResponse(EmailModel.DeleteAcceptEmailResponse.newBuilder().build()).build();
        NettyResponse nettyResponse=new NettyResponse();
        nettyResponse.setCmd(ConstantValue.DELETE_ACCEPT_EMAIL_RESPONSE);
        nettyResponse.setStateCode(StateCode.SUCCESS);
        nettyResponse.setData(messageData.toByteArray());
        channel.writeAndFlush(nettyResponse);
    }

    @Override
    @HandlerCmdTag(cmd = ConstantValue.DELETE_SEND_EMAIL_REQUEST,module = ConstantValue.EMAIL_MODULE)
    public void deleteSendEmailRequest(EmailModel.EmailModelMessage myMessage, MmoSimpleRole mmoSimpleRole) {
        Integer emailId=myMessage.getDeleteSendEmailRequest().getEmailId();
        Channel channel = mmoSimpleRole.getChannel();
        EmailServiceProvider.deleteIsSendEmail(mmoSimpleRole,emailId);
        EmailModel.EmailModelMessage messageData=EmailModel.EmailModelMessage.newBuilder()
                .setDataType(EmailModel.EmailModelMessage.DateType.DeleteSendEmailResponse)
                .setDeleteSendEmailResponse(EmailModel.DeleteSendEmailResponse.newBuilder().build()).build();
        NettyResponse nettyResponse=new NettyResponse();
        nettyResponse.setCmd(ConstantValue.DELETE_SEND_EMAIL_RESPONSE);
        nettyResponse.setStateCode(StateCode.SUCCESS);
        nettyResponse.setData(messageData.toByteArray());
        channel.writeAndFlush(nettyResponse);
    }
}
