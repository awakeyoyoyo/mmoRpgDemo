package com.liqihao.service.impl;

import com.google.protobuf.InvalidProtocolBufferException;
import com.liqihao.commons.NettyResponse;
import com.liqihao.commons.enums.ChatTypeCode;
import com.liqihao.protobufObject.BackPackModel;
import com.liqihao.protobufObject.ChatModel;
import com.liqihao.service.ChatService;
import org.springframework.stereotype.Service;

/**
 * @author awakeyoyoyo
 * @className ChatServiceImpl
 * @date 2021-01-03 19:30
 */
@Service
public class ChatServiceImpl implements ChatService {
    @Override
    public void acceptMessageResopnse(NettyResponse nettyResponse) throws InvalidProtocolBufferException {
        byte[] data=nettyResponse.getData();
        ChatModel.ChatModelMessage myMessage;
        myMessage=ChatModel.ChatModelMessage.parseFrom(data);
        ChatModel.RoleDto fromRoleDto=myMessage.getAcceptMessageResponse().getFromRole();
        Integer chatType=myMessage.getAcceptMessageResponse().getChatType();
        String chatStr=myMessage.getAcceptMessageResponse().getStr();
        System.out.println("[-]--------------------------------------------------------");
        System.out.println("[-]当前是："+ ChatTypeCode.getValue(chatType));
        System.out.println("[-]玩家id："+ fromRoleDto.getId());
        System.out.println("[-]玩家名："+ fromRoleDto.getName());
        System.out.println("[-]大喊："+ chatStr);
        System.out.println("[-]--------------------------------------------------------");
    }
}
