package com.liqihao.service.impl;

import com.liqihao.annotation.HandlerCmdTag;
import com.liqihao.annotation.HandlerServiceTag;
import com.liqihao.commons.ConstantValue;
import com.liqihao.commons.NettyResponse;
import com.liqihao.commons.StateCode;
import com.liqihao.pojo.bean.MmoSimpleRole;
import com.liqihao.pojo.bean.TeamBean;
import com.liqihao.protobufObject.ChatModel;
import com.liqihao.protobufObject.CopySceneModel;
import com.liqihao.provider.ChatServiceProvider;
import com.liqihao.provider.TeamServiceProvider;
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

    @Override
    @HandlerCmdTag(cmd = ConstantValue.SEND_TO_TEAM_REQUEST,module = ConstantValue.CHAT_MODULE)
    public void sendToTeamRequest(ChatModel.ChatModelMessage myMessage, Channel channel) throws Exception {
        MmoSimpleRole mmoSimpleRole= CommonsUtil.checkLogin(channel);
        if (mmoSimpleRole==null){
            return;
        }
        String str=myMessage.getSendToTeamRequest().getStr();
        Integer teamId=mmoSimpleRole.getTeamId();
        if (teamId==null){
            NettyResponse errotResponse=new NettyResponse(StateCode.FAIL, ConstantValue.FAIL_RESPONSE,"当前角色不在组队状态".getBytes());
            channel.writeAndFlush(errotResponse);
            return;
        }
        ChatServiceProvider.getInstance().notifyTeam(teamId,mmoSimpleRole,str);
    }

    @Override
    @HandlerCmdTag(cmd = ConstantValue.SEND_TO_SCENE_REQUEST,module = ConstantValue.CHAT_MODULE)
    public void sendToSceneRequest(ChatModel.ChatModelMessage myMessage, Channel channel) throws Exception {
        MmoSimpleRole mmoSimpleRole= CommonsUtil.checkLogin(channel);
        if (mmoSimpleRole==null){
            return;
        }
        String str=myMessage.getSendToSceneRequest().getStr();
        ChatServiceProvider.getInstance().notifyScene(mmoSimpleRole,str);
    }
}