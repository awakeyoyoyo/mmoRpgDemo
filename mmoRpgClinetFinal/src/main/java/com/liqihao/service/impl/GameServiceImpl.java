package com.liqihao.service.impl;

import com.google.protobuf.InvalidProtocolBufferException;
import com.liqihao.commons.NettyResponse;
import com.liqihao.protobufObject.GameSystemModel;
import com.liqihao.protobufObject.PlayModel;
import com.liqihao.service.GameService;
import org.springframework.stereotype.Service;

@Service
public class GameServiceImpl implements GameService {
    @Override
    public void outTimeResponse(NettyResponse nettyResponse) throws InvalidProtocolBufferException {
        byte[] data=nettyResponse.getData();
        GameSystemModel.GameSystemModelMessage myMessage;
        myMessage=GameSystemModel.GameSystemModelMessage.parseFrom(data);
        String msg=myMessage.getOutTimeResponse().getMessage();
        System.out.println("[-]--------------------------------------------------------");
        System.out.println("[-]"+msg);
        System.out.println("[-]--------------------------------------------------------");
    }
}
