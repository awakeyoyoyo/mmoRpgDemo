package com.liqihao.service;

import com.liqihao.commons.RpgServerException;
import com.liqihao.pojo.bean.MmoSimpleRole;
import com.liqihao.protobufObject.CopySceneModel;
import com.liqihao.protobufObject.EmailModel;

/**
 * 邮件模块
 * @author lqhao
 */
public interface EmailService {
    /**
     * 邮件的详细信息
     * @param myMessage
     * @param mmoSimpleRole
     */
    void getEmailMessageRequest(EmailModel.EmailModelMessage myMessage, MmoSimpleRole mmoSimpleRole) throws RpgServerException;
    /**
     * 收取邮件上的道具
     * @param myMessage
     * @param mmoSimpleRole
     */
    void getEmailArticleRequest(EmailModel.EmailModelMessage myMessage, MmoSimpleRole mmoSimpleRole) throws Exception;
    /**
     * 已接受邮件列表
     * @param myMessage
     * @param mmoSimpleRole
     */
    void acceptEmailListRequest(EmailModel.EmailModelMessage myMessage, MmoSimpleRole mmoSimpleRole);
    /**
     * 已发送邮件列表
     * @param myMessage
     * @param mmoSimpleRole
     */
    void isSendEmailListRequest(EmailModel.EmailModelMessage myMessage, MmoSimpleRole mmoSimpleRole);
    /**
     * 发送邮件
     * @param myMessage
     * @param mmoSimpleRole
     */
    void sendEmailRequest(EmailModel.EmailModelMessage myMessage, MmoSimpleRole mmoSimpleRole) throws RpgServerException;
    /**
     * 删除已接收邮件
     * @param myMessage
     * @param mmoSimpleRole
     */
    void deleteAcceptEmailRequest(EmailModel.EmailModelMessage myMessage, MmoSimpleRole mmoSimpleRole);
    /**
     * 删除已发送邮件
     * @param myMessage
     * @param mmoSimpleRole
     */
    void deleteSendEmailRequest(EmailModel.EmailModelMessage myMessage, MmoSimpleRole mmoSimpleRole);
}
