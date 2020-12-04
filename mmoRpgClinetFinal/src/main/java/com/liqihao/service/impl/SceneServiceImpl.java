package com.liqihao.service.impl;

import com.google.protobuf.InvalidProtocolBufferException;
import com.liqihao.commons.CacheUtil;
import com.liqihao.commons.NettyResponse;
import com.liqihao.pojo.MmoScene;
import com.liqihao.pojo.MmoSimpleRole;
import com.liqihao.pojo.MmoSimpleScene;
import com.liqihao.protobufObject.PlayModel;
import com.liqihao.protobufObject.SceneModel;
import com.liqihao.service.SceneService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
@Service
public class SceneServiceImpl implements SceneService {
    private static final Logger log = LoggerFactory.getLogger(SceneServiceImpl.class);

    @Override
    public void askCanResponse(NettyResponse nettyResponse) throws InvalidProtocolBufferException {
        byte[] data=nettyResponse.getData();
        SceneModel.SceneModelMessage myMessage;
        myMessage=SceneModel.SceneModelMessage.parseFrom(data);
        List<SceneModel.MmoSimpleScene> mmoSimpleScenes=myMessage.getAskCanResponse().getMmoSimpleScenesList();

        log.info("当前可以进入的场景：");
        for (SceneModel.MmoSimpleScene mmoSimpleScene:mmoSimpleScenes){
            log.info("---------"+mmoSimpleScene.getPalceName()+"---------");
        }
        log.info("---------------------------------------------------");
    }

    @Override
    public void wentResponse(NettyResponse nettyResponse) throws InvalidProtocolBufferException {
        byte[] data=nettyResponse.getData();
        SceneModel.SceneModelMessage myMessage;
        myMessage=SceneModel.SceneModelMessage.parseFrom(data);
        SceneModel.WentResponse wentResponse=myMessage.getWentResponse();
        SceneModel.MmoScene mmoScene=wentResponse.getMmoScene();
        //本地缓存设置当前的场景
        MmoScene scene=new MmoScene();
        List<MmoSimpleRole> simpleRoles=new ArrayList<>();
        for (SceneModel.MmoSimpleRole m:mmoScene.getRolesList()) {
            MmoSimpleRole role=new MmoSimpleRole();
            role.setName(m.getName());
            role.setStatus(m.getStatus());
            role.setType(m.getType());
            role.setOnstatus(m.getOnStatus());
            role.setId(m.getId());
            simpleRoles.add(role);
        }
        scene.setRoles(simpleRoles);
        List<MmoSimpleScene> simpleScenes=new ArrayList<>();
        for (SceneModel.MmoSimpleScene m:mmoScene.getCanSceneList()) {
            MmoSimpleScene sscene=new MmoSimpleScene();
            sscene.setPalceName(m.getPalceName());
            sscene.setId(m.getId());
            simpleScenes.add(sscene);
        }
        scene.setCanScene(simpleScenes);
        scene.setId(mmoScene.getId());
        scene.setPlaceName(mmoScene.getPlaceName());
        CacheUtil.setNowScene(scene);
        log.info("已进入下一个场景");
        log.info("当前场景是: "+mmoScene.getPlaceName());
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
