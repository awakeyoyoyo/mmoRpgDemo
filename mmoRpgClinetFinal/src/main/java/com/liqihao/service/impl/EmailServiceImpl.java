package com.liqihao.service.impl;

import com.google.protobuf.InvalidProtocolBufferException;
import com.liqihao.commons.NettyResponse;
import com.liqihao.commons.enums.ArticleTypeCode;
import com.liqihao.commons.enums.ChatTypeCode;
import com.liqihao.protobufObject.ChatModel;
import com.liqihao.protobufObject.EmailModel;
import com.liqihao.service.EmailService;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.List;

@Service
public class EmailServiceImpl implements EmailService {
    @Override
    public void getEmailMessageResponse(NettyResponse nettyResponse) throws InvalidProtocolBufferException {
        byte[] data=nettyResponse.getData();
        EmailModel.EmailModelMessage myMessage;
        myMessage=EmailModel.EmailModelMessage.parseFrom(data);
        EmailModel.EmailDto emailDto=myMessage.getGetEmailMessageResponse().getEmailDto();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss SSS");
        System.out.println("[-]--------------------------------------------------------");
        System.out.println("[-]邮件id："+ emailDto.getId()+" 邮件标题："+emailDto.getTitle());
        System.out.println("[-]邮件内容："+ emailDto.getContext());
        if (emailDto.getHasArticle()) {
            if (emailDto.getArticleType()==ArticleTypeCode.MEDICINE.getCode()) {
                System.out.println("[-]是否已经签收：" + emailDto.getIsGet());
                System.out.println("[-]附带的药品id：" + emailDto.getArticleMessageId());
                System.out.println("[-]附带的道具类型：" + ArticleTypeCode.getValue(emailDto.getArticleType()));
                System.out.println("[-]附带的道具数量：" + emailDto.getArticleNum());
            }else{
                System.out.println("[-]是否已经签收：" + emailDto.getIsGet());
                System.out.println("[-]附带的武器信息id：" + emailDto.getArticleMessageId());
                System.out.println("[-]附带的武器实例id：" + emailDto.getEquipmentId());
                System.out.println("[-]附带的道具类型：" + ArticleTypeCode.getValue(emailDto.getArticleType()));
                System.out.println("[-]附带的道具数量：" + emailDto.getArticleNum());
            }
        }
        System.out.println("[-]邮件携带金币："+ emailDto.getMoney() +"是否以签收金币："+emailDto.getIsGetMoney());
        System.out.println("[-]发送者id："+ emailDto.getFromRoleId());
        System.out.println("[-]发送时间："+ sdf.format(emailDto.getCreateTime()));
        System.out.println("[-]--------------------------------------------------------");
    }

    @Override
    public void getEmailArticleResponse(NettyResponse nettyResponse) throws InvalidProtocolBufferException {
        byte[] data=nettyResponse.getData();
        EmailModel.EmailModelMessage myMessage;
        myMessage=EmailModel.EmailModelMessage.parseFrom(data);
        System.out.println("[-]--------------------------------------------------------");
        System.out.println("[-]收取成功!");
        System.out.println("[-]--------------------------------------------------------");
    }

    @Override
    public void acceptEmailListResponse(NettyResponse nettyResponse) throws InvalidProtocolBufferException {
        byte[] data=nettyResponse.getData();
        EmailModel.EmailModelMessage myMessage;
        myMessage=EmailModel.EmailModelMessage.parseFrom(data);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss SSS");
        List<EmailModel.EmailSimpleDto> emailSimpleDtos=myMessage.getAcceptEmailListResponse().getEmailSimpleDtosList();
        System.out.println("[-]--------------------------------------------------------");
        System.out.println("[-]收件箱列表");
        for (EmailModel.EmailSimpleDto e:emailSimpleDtos)
        {
            System.out.println("[-]");
            System.out.println("[-][-]邮件id："+e.getId()+" 邮件标题："+e.getTitle()+" 发送日期："+sdf.format(e.getCreateTime())+" 是否已读："+e.getChecked());
            System.out.println("[-]");
        }
        System.out.println("[-]--------------------------------------------------------");
    }

    @Override
    public void isSendEmailListResponse(NettyResponse nettyResponse) throws InvalidProtocolBufferException {
        byte[] data=nettyResponse.getData();
        EmailModel.EmailModelMessage myMessage;
        myMessage=EmailModel.EmailModelMessage.parseFrom(data);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss SSS");
        List<EmailModel.EmailSimpleDto> emailSimpleDtos=myMessage.getIsSendEmailListResponse().getEmailSimpleDtosList();
        System.out.println("[-]--------------------------------------------------------");
        System.out.println("[-]已发送邮件列表");
        for (EmailModel.EmailSimpleDto e:emailSimpleDtos)
        {
            System.out.println("[-]");
            System.out.println("[-][-]邮件id："+e.getId()+" 邮件标题："+e.getTitle()+" 发送日期："+sdf.format(e.getCreateTime())+" 是否已读："+e.getChecked());
            System.out.println("[-]");
        }
        System.out.println("[-]--------------------------------------------------------");
    }

    @Override
    public void sendEmailResponse(NettyResponse nettyResponse) throws InvalidProtocolBufferException {
        byte[] data=nettyResponse.getData();
        EmailModel.EmailModelMessage myMessage;
        myMessage=EmailModel.EmailModelMessage.parseFrom(data);
        System.out.println("[-]--------------------------------------------------------");
        System.out.println("[-]发送成功！");
        System.out.println("[-]--------------------------------------------------------");
    }

    @Override
    public void deleteAcceptEmailResponse(NettyResponse nettyResponse) throws InvalidProtocolBufferException {
        byte[] data=nettyResponse.getData();
        EmailModel.EmailModelMessage myMessage;
        myMessage=EmailModel.EmailModelMessage.parseFrom(data);
        System.out.println("[-]--------------------------------------------------------");
        System.out.println("[-]删除成功！");
        System.out.println("[-]--------------------------------------------------------");
    }

    @Override
    public void deleteSendEmailResponse(NettyResponse nettyResponse) throws InvalidProtocolBufferException {
        byte[] data=nettyResponse.getData();
        EmailModel.EmailModelMessage myMessage;
        myMessage=EmailModel.EmailModelMessage.parseFrom(data);
        System.out.println("[-]--------------------------------------------------------");
        System.out.println("[-]删除成功！");
        System.out.println("[-]--------------------------------------------------------");
    }

    @Override
    public void getEmailMoneyResponse(NettyResponse nettyResponse) throws InvalidProtocolBufferException {
        byte[] data=nettyResponse.getData();
        EmailModel.EmailModelMessage myMessage;
        myMessage=EmailModel.EmailModelMessage.parseFrom(data);
        System.out.println("[-]--------------------------------------------------------");
        System.out.println("[-]获取成功！");
        System.out.println("[-]--------------------------------------------------------");
    }
}
