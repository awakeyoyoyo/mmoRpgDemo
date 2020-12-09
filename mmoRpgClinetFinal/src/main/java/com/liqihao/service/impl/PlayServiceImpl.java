package com.liqihao.service.impl;

import com.google.protobuf.InvalidProtocolBufferException;
import com.liqihao.commons.MmoCacheCilent;
import com.liqihao.commons.NettyResponse;
import com.liqihao.commons.StateCode;
import com.liqihao.pojo.MmoScene;
import com.liqihao.pojo.MmoSimpleRole;
import com.liqihao.pojo.MmoSimpleScene;
import com.liqihao.pojo.baseMessage.SceneMessage;
import com.liqihao.protobufObject.PlayModel;
import com.liqihao.service.PlayService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
@Service
public class PlayServiceImpl implements PlayService {
    private static final Logger log = LoggerFactory.getLogger(PlayServiceImpl.class);

    @Override
    public void loginResponse(NettyResponse nettyResponse) throws InvalidProtocolBufferException {
        if (nettyResponse.getStateCode()== StateCode.FAIL){
            System.out.println(new String(nettyResponse.getData()));
            return;
        }
        byte[] data=nettyResponse.getData();
        if (nettyResponse.getStateCode()== StateCode.FAIL){
            String s = new String(nettyResponse.getData());
            log.info(s);
            return;
        }
        PlayModel.PlayModelMessage myMessage;
        myMessage=PlayModel.PlayModelMessage.parseFrom(data);
        PlayModel.LoginResponse loginResponse=myMessage.getLoginResponse();
        PlayModel.MmoSimpleRole mmoSimpleRole=loginResponse.getMmoSimpleRole();
        Integer mmoScene=loginResponse.getSceneId();
        //将角色存储客户端缓存中
        MmoCacheCilent.getInstance().setNowSceneId(mmoScene);
        //将当前场景存入客户端缓存中
        //构建mmoscene对象
        SceneMessage s=MmoCacheCilent.getInstance().getSceneMessageConcurrentHashMap().get(mmoScene);
        //打印当前场景
        log.info("当前角色: "+mmoSimpleRole.getName());
        log.info("当前场景: "+s.getPlaceName());
        log.info("---------------------------------------------------");
    }

    @Override
    public void registerResponse(NettyResponse nettyResponse) throws InvalidProtocolBufferException {
        if (nettyResponse.getStateCode()== StateCode.FAIL){
            System.out.println(new String(nettyResponse.getData()));
            return;
        }
        byte[] data=nettyResponse.getData();
        PlayModel.PlayModelMessage myMessage;
        myMessage=PlayModel.PlayModelMessage.parseFrom(data);
        PlayModel.RegisterResponse registerResponse=myMessage.getRegisterResponse();
        String msg=registerResponse.getMessage();
        int code =registerResponse.getStateCode();
        log.info("code: "+code+" message: "+msg);
        log.info("---------------------------------------------------");
    }

    @Override
    public void logoutResponse(NettyResponse nettyResponse) throws InvalidProtocolBufferException {
        if (nettyResponse.getStateCode()== StateCode.FAIL){
            System.out.println(new String(nettyResponse.getData()));
            return;
        }
        byte[] data=nettyResponse.getData();
        PlayModel.PlayModelMessage myMessage;
        myMessage=PlayModel.PlayModelMessage.parseFrom(data);
        PlayModel.LogoutResponse logoutResponse=myMessage.getLogoutResponse();
        String msg=logoutResponse.getMxg();
        int code =logoutResponse.getCode();
        log.info("code: "+code+" message: "+msg);
        log.info("---------------------------------------------------");
    }
}
