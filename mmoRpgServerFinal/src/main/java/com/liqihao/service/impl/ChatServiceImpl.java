package com.liqihao.service.impl;

import com.liqihao.pojo.bean.MmoSimpleRole;
import com.liqihao.protobufObject.ChatModel;
import com.liqihao.protobufObject.CopySceneModel;
import com.liqihao.provider.ChatServiceProvider;
import com.liqihao.service.ChatService;
import com.liqihao.util.CommonsUtil;
import io.netty.channel.Channel;

/**
 * 聊天模块
 * @author lqhao
 */
public class ChatServiceImpl implements ChatService {
    @Override
    public void sendToAllRequest(ChatModel.ChatModelMessage myMessage, Channel channel) {
        MmoSimpleRole mmoSimpleRole= CommonsUtil.checkLogin(channel);
        if (mmoSimpleRole==null){
            return;
        }
        String str=myMessage.getSendToAllRequest().getStr();
        ChatServiceProvider.getInstance().notifyObserver(mmoSimpleRole,str);
    }

    @Override
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
