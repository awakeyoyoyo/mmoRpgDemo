package com.liqihao.service.impl;

import com.google.protobuf.InvalidProtocolBufferException;
import com.liqihao.commons.MmoCacheCilent;
import com.liqihao.commons.NettyResponse;
import com.liqihao.commons.StateCode;
import com.liqihao.pojo.MmoScene;
import com.liqihao.pojo.MmoSimpleRole;
import com.liqihao.pojo.MmoSimpleScene;
import com.liqihao.pojo.baseMessage.SceneMessage;
import com.liqihao.protobufObject.SceneModel;
import com.liqihao.service.SceneService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class SceneServiceImpl implements SceneService {
    private static final Logger log = LoggerFactory.getLogger(SceneServiceImpl.class);

    @Override
    public void askCanResponse(NettyResponse nettyResponse) throws InvalidProtocolBufferException {
        byte[] data=nettyResponse.getData();
        SceneModel.SceneModelMessage myMessage;
        myMessage=SceneModel.SceneModelMessage.parseFrom(data);
        List<Integer> Scenes=myMessage.getAskCanResponse().getScenesIdsList();
        ConcurrentHashMap<Integer, SceneMessage> sceneMap=MmoCacheCilent.getInstance().getSceneMessageConcurrentHashMap();
        log.info("当前可以进入的场景：");
        for (Integer id:Scenes){

            log.info("---------"+sceneMap.get(id).getPlaceName()+"---------");
        }
        log.info("---------------------------------------------------");
    }

    @Override
    public void wentResponse(NettyResponse nettyResponse) throws InvalidProtocolBufferException {
        if (nettyResponse.getStateCode()== StateCode.FAIL){
            byte[] data=nettyResponse.getData();
            String mxg=new String(data);
            log.info(mxg);
        }
        byte[] data=nettyResponse.getData();
        SceneModel.SceneModelMessage myMessage;
        myMessage=SceneModel.SceneModelMessage.parseFrom(data);
        SceneModel.WentResponse wentResponse=myMessage.getWentResponse();
        Integer mmoScene=wentResponse.getSceneId();
        //本地缓存设置当前的场景
        SceneMessage m=MmoCacheCilent.getInstance().getSceneMessageConcurrentHashMap().get(mmoScene);
        MmoCacheCilent.getInstance().setNowSceneId(mmoScene);
        log.info("已进入下一个场景");
        log.info("当前场景是: "+m.getPlaceName());
        log.info("---------------------------------------------------");
    }


    @Override
    public void findAllRolesResponse(NettyResponse nettyResponse) throws InvalidProtocolBufferException {
        byte[] data=nettyResponse.getData();
        SceneModel.SceneModelMessage myMessage;
        myMessage=SceneModel.SceneModelMessage.parseFrom(data);
        SceneModel.FindAllRolesResponse findAllRolesResponse=myMessage.getFindAllRolesResponse();
        List<SceneModel.MmoSimpleRole> mmoSimpleRoles=findAllRolesResponse.getMmoSimpleRolesList();
        log.info("当前场景中的所有角色: ");
        for (SceneModel.MmoSimpleRole role:mmoSimpleRoles) {
            log.info("角色名: "+role.getName()+" 类型: "+role.getType()+" 状态: "+role.getStatus());
        }
        log.info("---------------------------------------------------");
    }
}
