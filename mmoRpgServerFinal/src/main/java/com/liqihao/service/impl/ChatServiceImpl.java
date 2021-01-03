package com.liqihao.service.impl;

import com.liqihao.annotation.HandlerCmdTag;
import com.liqihao.annotation.HandlerServiceTag;
import com.liqihao.commons.ConstantValue;
import com.liqihao.pojo.bean.MmoSimpleRole;
import com.liqihao.protobufObject.ChatModel;
import com.liqihao.protobufObject.CopySceneModel;
import com.liqihao.provider.ChatServiceProvider;
import com.liqihao.service.ChatService;
import com.liqihao.util.CommonsUtil;
import io.netty.channel.Channel;
import org.springframework.stereotype.Service;

/**
 * 聊天模块
 * @author lqhao
 */
@Service
@HandlerServiceTag(protobufModel ="ChatModel$ChatModelMessage")
public class ChatServiceImpl implements ChatService {
    @Override
    @HandlerCmdTag(cmd = ConstantValue.SEND_TO_ALL_REQUEST,module = ConstantValue.CHAT_MODULE)
    public void sendToAllRequest(ChatModel.ChatModelMessage myMessage, Channel channel) {
        MmoSimpleRole mmoSimpleRole= CommonsUtil.checkLogin(channel);
        if (mmoSimpleRole==null){
            return;
        }
        String str=myMessage.getSendToAllRequest().getStr();
        ChatServiceProvider.getInstance().notifyObserver(mmoSimpleRole,str);
    }

    @Override
    @HandlerCmdTag(cmd = ConstantValue.SEND_TO_ONE_REQUEST,module = ConstantValue.CHAT_MODULE)
    public void sendToOneRequest(ChatModel.ChatModelMessage myMessage, Channel channel) {
        MmoSimpleRole mmoSimpleRole= CommonsUtil.checkLogin(channel);
        if (mmoSimpleRole==null){
            return;
        }
        String str=myMessage.getSendToOneRequest().getStr();
        Integer roleId=myMessage.getSendToOneRequest().getRoleId();
        ChatServiceProvider.getInstance().notifyOne(roleId,mmoSimpleRole,str);
    }
}
