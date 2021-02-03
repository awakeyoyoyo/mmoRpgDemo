package com.liqihao.service.impl;

import com.googlecode.protobuf.format.JsonFormat;
import com.liqihao.cache.MedicineMessageCache;
import com.liqihao.cache.OnlineRoleMessageCache;
import com.liqihao.annotation.HandlerCmdTag;
import com.liqihao.annotation.HandlerServiceTag;
import com.liqihao.commons.ConstantValue;
import com.liqihao.commons.NettyResponse;
import com.liqihao.commons.RpgServerException;
import com.liqihao.commons.StateCode;
import com.liqihao.commons.enums.ArticleTypeCode;
import com.liqihao.pojo.baseMessage.MedicineMessage;
import com.liqihao.pojo.bean.*;
import com.liqihao.pojo.bean.articleBean.Article;
import com.liqihao.pojo.bean.articleBean.EquipmentBean;
import com.liqihao.pojo.bean.articleBean.MedicineBean;
import com.liqihao.pojo.bean.roleBean.MmoSimpleRole;
import com.liqihao.protobufObject.EmailModel;
import com.liqihao.provider.ArticleServiceProvider;
import com.liqihao.provider.EmailServiceProvider;
import com.liqihao.service.EmailService;
import com.liqihao.util.CommonsUtil;
import com.liqihao.util.DbUtil;
import com.liqihao.util.NotificationUtil;
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
        EmailBean mmoEmailBean=EmailServiceProvider.getEmailMessage(mmoSimpleRole,emailId);
        if (mmoEmailBean==null){
            throw new RpgServerException(StateCode.FAIL,"没有该id的邮件");
        }
        EmailModel.EmailDto emailDto= CommonsUtil.mmoEmailBeanToEmailDto(mmoEmailBean);
        //protobuf
        EmailModel.EmailModelMessage messageData=EmailModel.EmailModelMessage.newBuilder()
                .setDataType(EmailModel.EmailModelMessage.DateType.GetEmailMessageResponse)
                .setGetEmailMessageResponse(EmailModel.GetEmailMessageResponse.newBuilder().setEmailDto(emailDto).build()).build();
        NettyResponse nettyResponse=new NettyResponse();
        nettyResponse.setCmd(ConstantValue.GET_EMAIL_MESSAGE_RESPONSE);
        nettyResponse.setStateCode(StateCode.SUCCESS);
        nettyResponse.setData(messageData.toByteArray());
        //send
        String json= JsonFormat.printToString(messageData);
        NotificationUtil.sendMessage(channel,nettyResponse,json);
    }

    @Override
    @HandlerCmdTag(cmd = ConstantValue.GET_EMAIL_ARTICLE_REQUEST,module = ConstantValue.EMAIL_MODULE)
    public void getEmailArticleRequest(EmailModel.EmailModelMessage myMessage, MmoSimpleRole mmoSimpleRole) throws Exception {
        Integer emailId=myMessage.getGetEmailArticleRequest().getEmailId();
        Channel channel = mmoSimpleRole.getChannel();
        //获取邮件详情
        EmailBean mmoEmailBean=EmailServiceProvider.getEmailMessage(mmoSimpleRole,emailId);
        if (mmoEmailBean==null){
            throw new RpgServerException(StateCode.FAIL,"没有该id的邮件");
        }
        //判断邮件是否有物品
        if (!mmoEmailBean.getHasArticle()){
            throw new RpgServerException(StateCode.FAIL,"该邮件没有物品");
        }
        //判断邮件是否有物品
        if (mmoEmailBean.getGetFlag()){
            throw new RpgServerException(StateCode.FAIL,"已经获取过该物品");
        }
        if (!mmoEmailBean.getToRoleId().equals(mmoSimpleRole.getId())){
            throw new RpgServerException(StateCode.FAIL,"这道具不是给你的");
        }
        //根据邮件实体类信息初始化物品
        if (mmoEmailBean.getArticleType().equals(ArticleTypeCode.MEDICINE.getCode())){
            MedicineMessage medicineMessage= MedicineMessageCache.getInstance().get(mmoEmailBean.getArticleMessageId());
            MedicineBean medicineBean=CommonsUtil.medicineMessageToMedicineBean(medicineMessage);
            medicineBean.setQuantity(mmoEmailBean.getArticleNum());
            //上锁 获取物品
            getArticle(mmoSimpleRole,medicineBean,mmoEmailBean);
        }else{
            EquipmentBean equipmentBean= ArticleServiceProvider.getEquipmentBeanConcurrentHashMap().get(mmoEmailBean.getEquipmentId());
            //上锁 获取物品
            getArticle(mmoSimpleRole,equipmentBean,mmoEmailBean);
        }
        //protobuf
        EmailModel.EmailModelMessage messageData=EmailModel.EmailModelMessage.newBuilder()
                .setDataType(EmailModel.EmailModelMessage.DateType.GetEmailArticleResponse)
                .setGetEmailArticleResponse(EmailModel.GetEmailArticleResponse.newBuilder().build()).build();
        NettyResponse nettyResponse=new NettyResponse();
        nettyResponse.setCmd(ConstantValue.GET_EMAIL_ARTICLE_RESPONSE);
        nettyResponse.setStateCode(StateCode.SUCCESS);
        nettyResponse.setData(messageData.toByteArray());
        //send
        String json= JsonFormat.printToString(messageData);
        NotificationUtil.sendMessage(channel,nettyResponse,json);
    }

    @Override
    @HandlerCmdTag(cmd = ConstantValue.GET_EMAIL_MONEY_REQUEST,module = ConstantValue.EMAIL_MODULE)
    public void getEmailMoneyRequest(EmailModel.EmailModelMessage myMessage, MmoSimpleRole mmoSimpleRole) throws Exception {
        Integer emailId=myMessage.getGetEmailMoneyRequest().getEmailId();
        //获取邮件详情
        EmailBean mmoEmailBean=EmailServiceProvider.getEmailMessage(mmoSimpleRole,emailId);
        if (mmoEmailBean==null){
            throw new RpgServerException(StateCode.FAIL,"没有该id的邮件");
        }
        //判断邮件是否有物品
        if (mmoEmailBean.getMoney()<=0){
            throw new RpgServerException(StateCode.FAIL,"该邮件没有可获取金币");
        }
        //判断邮件是否有物品
        if (mmoEmailBean.getGetMoneyFlag()){
            throw new RpgServerException(StateCode.FAIL,"已经获取过该物品");
        }
        if (!mmoEmailBean.getToRoleId().equals(mmoSimpleRole.getId())){
            throw new RpgServerException(StateCode.FAIL,"这信不是给你的");
        }
        mmoEmailBean.setGetMoneyFlag(true);
        mmoSimpleRole.setMoney(mmoSimpleRole.getMoney()+mmoEmailBean.getMoney());

        DbUtil.updateEmailBeanDb(mmoEmailBean);
        DbUtil.updateRole(mmoSimpleRole);
    }

    @Override
    @HandlerCmdTag(cmd = ConstantValue.ACCEPT_EMAIL_LIST_REQUEST,module = ConstantValue.EMAIL_MODULE)
    public void acceptEmailListRequest(EmailModel.EmailModelMessage myMessage, MmoSimpleRole mmoSimpleRole) {
        Channel channel = mmoSimpleRole.getChannel();
        List<EmailBean> mmoEmailBeans=EmailServiceProvider.getToEmails(mmoSimpleRole);
        List<EmailModel.EmailSimpleDto> list=new ArrayList<>();
        if (mmoEmailBeans.size()>0){
            for (EmailBean m:mmoEmailBeans) {
                EmailModel.EmailSimpleDto emailSimpleDto=CommonsUtil.mmoEmailBeanToEmailSimpleDto(m);
                list.add(emailSimpleDto);
            }
        }
        //protobuf
        EmailModel.EmailModelMessage messageData=EmailModel.EmailModelMessage.newBuilder()
                .setDataType(EmailModel.EmailModelMessage.DateType.AcceptEmailListResponse)
                .setAcceptEmailListResponse(EmailModel.AcceptEmailListResponse.newBuilder().addAllEmailSimpleDtos(list).build()).build();
        NettyResponse nettyResponse=new NettyResponse();
        nettyResponse.setCmd(ConstantValue.ACCEPT_EMAIL_LIST_RESPONSE);
        nettyResponse.setStateCode(StateCode.SUCCESS);
        nettyResponse.setData(messageData.toByteArray());
        //send
        String json= JsonFormat.printToString(messageData);
        NotificationUtil.sendMessage(channel,nettyResponse,json);
    }




    @Override
    @HandlerCmdTag(cmd = ConstantValue.IS_SEND_EMAIL_LIST_REQUEST,module = ConstantValue.EMAIL_MODULE)
    public void isSendEmailListRequest(EmailModel.EmailModelMessage myMessage, MmoSimpleRole mmoSimpleRole) {
        Channel channel = mmoSimpleRole.getChannel();
        List<EmailBean> mmoEmailBeans=EmailServiceProvider.getFromEmails(mmoSimpleRole);
        List<EmailModel.EmailSimpleDto> list=new ArrayList<>();
        if (mmoEmailBeans.size()>0){
            for (EmailBean m:mmoEmailBeans) {
                EmailModel.EmailSimpleDto emailSimpleDto=CommonsUtil.mmoEmailBeanToEmailSimpleDto(m);
                list.add(emailSimpleDto);
            }
        }
        //protobuf
        EmailModel.EmailModelMessage messageData=EmailModel.EmailModelMessage.newBuilder()
                .setDataType(EmailModel.EmailModelMessage.DateType.IsSendEmailListResponse)
                .setIsSendEmailListResponse(EmailModel.IsSendEmailListResponse.newBuilder().addAllEmailSimpleDtos(list).build()).build();
        NettyResponse nettyResponse=new NettyResponse();
        nettyResponse.setCmd(ConstantValue.IS_SEND_EMAIL_LIST_RESPONSE);
        nettyResponse.setStateCode(StateCode.SUCCESS);
        nettyResponse.setData(messageData.toByteArray());
        //send
        String json= JsonFormat.printToString(messageData);
        NotificationUtil.sendMessage(channel,nettyResponse,json);
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
        //初始化信bean
        EmailBean mmoEmailBean=new EmailBean();
        mmoEmailBean.setContext(context);
        mmoEmailBean.setTitle(title);
        mmoEmailBean.setArticleNum(articleNum);
        mmoEmailBean.setArticleType(-1);
        mmoEmailBean.setArticleMessageId(-1);
        mmoEmailBean.setMoney(0);
        if (articleId!=-1) {
            //则需要扣除背包中的物品
           BackPackManager backPackManager=mmoSimpleRole.getBackpackManager();
           Article article=backPackManager.useOrAbandonArticle(articleId,articleNum,mmoSimpleRole.getId());
           if (article==null){
               throw new RpgServerException(StateCode.FAIL,"背包中该物品数量不足");
           }
           mmoEmailBean.setArticleType(article.getArticleTypeCode());
           if (mmoEmailBean.getArticleType().equals(ArticleTypeCode.MEDICINE.getCode())) {
               //药品
               MedicineBean medicineBean= (MedicineBean) article;
               mmoEmailBean.setArticleMessageId(medicineBean.getArticleMessageId());
           }else{
               //武器
               EquipmentBean equipmentBean= (EquipmentBean) article;
               mmoEmailBean.setArticleMessageId(equipmentBean.getArticleMessageId());
               mmoEmailBean.setEquipmentId(equipmentBean.getEquipmentId());
           }
        }
        mmoEmailBean.setToRoleId(toRoleId);
        mmoEmailBean.setFromRoleId(mmoSimpleRole.getId());
        //发邮件
        EmailServiceProvider.sendArticleEmail(mmoSimpleRole,toRole,mmoEmailBean);
        //protobuf
        EmailModel.EmailModelMessage messageData=EmailModel.EmailModelMessage.newBuilder()
                .setDataType(EmailModel.EmailModelMessage.DateType.SendEmailResponse)
                .setSendEmailResponse(EmailModel.SendEmailResponse.newBuilder().build()).build();
        NettyResponse nettyResponse=new NettyResponse();
        nettyResponse.setCmd(ConstantValue.SEND_EMAIL_RESPONSE);
        nettyResponse.setStateCode(StateCode.SUCCESS);
        nettyResponse.setData(messageData.toByteArray());
        //send
        String json= JsonFormat.printToString(messageData);
        NotificationUtil.sendMessage(channel,nettyResponse,json);
    }

    @Override
    @HandlerCmdTag(cmd = ConstantValue.DELETE_ACCEPT_EMAIL_REQUEST,module = ConstantValue.EMAIL_MODULE)
    public void deleteAcceptEmailRequest(EmailModel.EmailModelMessage myMessage, MmoSimpleRole mmoSimpleRole) {
        Integer emailId=myMessage.getDeleteAcceptEmailRequest().getEmailId();
        Channel channel = mmoSimpleRole.getChannel();
        //删除邮件
        EmailServiceProvider.deleteAcceptEmail(mmoSimpleRole,emailId);
        //protobuf
        EmailModel.EmailModelMessage messageData=EmailModel.EmailModelMessage.newBuilder()
                .setDataType(EmailModel.EmailModelMessage.DateType.DeleteAcceptEmailResponse)
                .setDeleteAcceptEmailResponse(EmailModel.DeleteAcceptEmailResponse.newBuilder().build()).build();
        NettyResponse nettyResponse=new NettyResponse();
        nettyResponse.setCmd(ConstantValue.DELETE_ACCEPT_EMAIL_RESPONSE);
        nettyResponse.setStateCode(StateCode.SUCCESS);
        nettyResponse.setData(messageData.toByteArray());
        //send
        String json= JsonFormat.printToString(messageData);
        NotificationUtil.sendMessage(channel,nettyResponse,json);
    }

    @Override
    @HandlerCmdTag(cmd = ConstantValue.DELETE_SEND_EMAIL_REQUEST,module = ConstantValue.EMAIL_MODULE)
    public void deleteSendEmailRequest(EmailModel.EmailModelMessage myMessage, MmoSimpleRole mmoSimpleRole) {
        Integer emailId=myMessage.getDeleteSendEmailRequest().getEmailId();
        Channel channel = mmoSimpleRole.getChannel();
        //删除邮件
        EmailServiceProvider.deleteIsSendEmail(mmoSimpleRole,emailId);
        //protobuf
        EmailModel.EmailModelMessage messageData=EmailModel.EmailModelMessage.newBuilder()
                .setDataType(EmailModel.EmailModelMessage.DateType.DeleteSendEmailResponse)
                .setDeleteSendEmailResponse(EmailModel.DeleteSendEmailResponse.newBuilder().build()).build();
        NettyResponse nettyResponse=new NettyResponse();
        nettyResponse.setCmd(ConstantValue.DELETE_SEND_EMAIL_RESPONSE);
        nettyResponse.setStateCode(StateCode.SUCCESS);
        nettyResponse.setData(messageData.toByteArray());
        //send
        String json= JsonFormat.printToString(messageData);
        NotificationUtil.sendMessage(channel,nettyResponse,json);
    }

    /**
     * description 获取邮件中的物品
     * @param mmoSimpleRole
     * @param article
     * @param mmoEmailBean
     * @return {@link null }
     * @author lqhao
     * @createTime 2021/1/28 15:12
     */
    public void getArticle(MmoSimpleRole mmoSimpleRole,Article article,EmailBean mmoEmailBean) throws RpgServerException {
        synchronized (mmoSimpleRole.getBackpackManager()) {
            if (!mmoSimpleRole.getBackpackManager().canPutArticle(article.getArticleMessageId(),article.getArticleTypeCode(),article.getQuantity())) {
                throw new RpgServerException(StateCode.FAIL,"背包已经满了");
            }
            mmoSimpleRole.getBackpackManager().put(article,mmoSimpleRole.getId());
        }
        //邮件设置为没有物品
        mmoEmailBean.setHasArticle(true);
        mmoEmailBean.setGetFlag(true);
        //数据库更新
        DbUtil.updateEmailBeanDb(mmoEmailBean);
    }
}
