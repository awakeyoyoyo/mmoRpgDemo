package com.liqihao.service.impl;

import com.google.protobuf.InvalidProtocolBufferException;
import com.liqihao.Cache.MmoCache;
import com.liqihao.commons.*;
import com.liqihao.dao.MmoRolePOJOMapper;
import com.liqihao.dao.MmoScenePOJOMapper;
import com.liqihao.pojo.*;
import com.liqihao.protobufObject.SceneModel;
import com.liqihao.service.SceneService;
import com.liqihao.util.CommonsUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class SceneServiceImpl implements SceneService, ApplicationContextAware {
    private static final Logger log = LoggerFactory.getLogger(SceneServiceImpl.class);
    @Autowired
    private MmoScenePOJOMapper mmoScenePOJOMapper;
    @Autowired
    private MmoRolePOJOMapper mmoRolePOJOMapper;

    private ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public NettyResponse askCanRequest(NettyRequest request) throws InvalidProtocolBufferException {
        byte[] data=request.getData();
        SceneModel.SceneModelMessage myMessage;
        myMessage=SceneModel.SceneModelMessage.parseFrom(data);
        Integer sceneId=myMessage.getAskCanRequest().getSceneId();
        if (null==sceneId) {
            return new NettyResponse(StateCode.FAIL, ConstantValue.SCENE_MODULE, ConstantValue.ASK_CAN_RESPONSE, "无传入sceneId无法查询".getBytes());
        }
        log.info("SceneService accept sceneId: "+sceneId);


        //根据sceneId 从spirng容器中获取场景信息

//       MmoScene mmoScene=applicationContext.getBean("scene"+sceneId,MmoScene.class);
        //从缓存中读取
        ConcurrentHashMap<Integer,MmoScene> scenes= MmoCache.getInstance().getMmoSceneConcurrentHashMap();
        MmoScene nowScene=scenes.get(sceneId);
        List<SceneModel.MmoSimpleScene> mmoSimpleScenes=new ArrayList<>();
        //由MmoSimpleScene转化为SceneModel.MmoSimpleScene
        for (MmoSimpleScene m :nowScene.getCanScene()) {
            SceneModel.MmoSimpleScene mmoSimpleScene=SceneModel.MmoSimpleScene.newBuilder().setId(m.getId()).setPalceName(m.getPalceName()).build();
            mmoSimpleScenes.add(mmoSimpleScene);
        }
        //封装AskCanResponse data的数据
        SceneModel.SceneModelMessage Messagedata;
        Messagedata=SceneModel.SceneModelMessage.newBuilder()
                .setDataType(SceneModel.SceneModelMessage.DateType.AskCanResponse)
                .setAskCanResponse(SceneModel.AskCanResponse.newBuilder().addAllMmoSimpleScenes(mmoSimpleScenes)).build();
        //封装到NettyResponse中
        NettyResponse response=new NettyResponse();
        response.setCmd(ConstantValue.ASK_CAN_RESPONSE);
        response.setStateCode(StateCode.SUCCESS);
        response.setModule(ConstantValue.SCENE_MODULE);
        byte[] data2=Messagedata.toByteArray();
        response.setData(data2);
        return response;
    }

    @Override
    public NettyResponse wentRequest(NettyRequest nettyRequest) throws InvalidProtocolBufferException {
        byte[] data=nettyRequest.getData();
        SceneModel.SceneModelMessage myMessage;
        myMessage=SceneModel.SceneModelMessage.parseFrom(data);
        Integer sceneId=myMessage.getWentRequest().getSceneId();
        Integer playId=myMessage.getWentRequest().getPlayId();
        //先查询palyId所在场景
        MmoRolePOJO mmoRolePOJO= MmoCache.getInstance().getMmoSimpleRoleConcurrentHashMap().get(playId);
//        MmoRolePOJO mmoRolePOJO=mmoRolePOJOMapper.selectByPrimaryKey(playId);
        //查询该场景可进入的场景与sceneId判断
        MmoScene nowScene= MmoCache.getInstance().getMmoSceneConcurrentHashMap().get(mmoRolePOJO.getMmosceneid());
//        MmoScenePOJO nowScene=mmoScenePOJOMapper.selectByPrimaryKey(mmoRolePOJO.getMmosceneid());
        List<MmoSimpleScene> canScene=nowScene.getCanScene();
        boolean canFlag=false;
        for (MmoSimpleScene sc:canScene){
            if (sc.getId()==sceneId){
                canFlag=true;
                break;
            }
        }
        if (!canFlag){
            //不包含 即不可进入
            NettyResponse errotResponse=new NettyResponse(StateCode.FAIL,ConstantValue.SCENE_MODULE,ConstantValue.WENT_RESPONSE,"无法前往该场景".getBytes());
            return  errotResponse;
        }
        //进入场景，修改数据库 player 和scene
        MmoRolePOJO mmoRolePOJO1=new MmoRolePOJO();
        mmoRolePOJO1.setId(playId);
        mmoRolePOJO1.setMmosceneid(sceneId);
        mmoRolePOJOMapper.updateByPrimaryKeySelective(mmoRolePOJO1);
        //修改缓存中角色
        ConcurrentHashMap<Integer,MmoRolePOJO> cacheRoles= MmoCache.getInstance().getMmoSimpleRoleConcurrentHashMap();
        MmoRolePOJO player=cacheRoles.get(playId);
        player.setMmosceneid(sceneId);
        cacheRoles.put(player.getId(),player);
        //修改scene
        //新场景中增加该角色
        MmoScene nextScene= MmoCache.getInstance().getMmoSceneConcurrentHashMap().get(sceneId);
//        MmoScenePOJO nextScene=mmoScenePOJOMapper.selectByPrimaryKey(sceneId);
        List<MmoSimpleRole> nextRoles=nextScene.getRoles();
        MmoSimpleRole m=new MmoSimpleRole();
        m.setId(mmoRolePOJO.getId());
        m.setName(mmoRolePOJO.getName());
        m.setType(RoleTypeCode.getValue(mmoRolePOJO.getType()));
        m.setStatus(RoleStatusCode.getValue(mmoRolePOJO.getStatus()));
        m.setOnstatus(RoleOnStatusCode.getValue(mmoRolePOJO.getOnstatus()));
        nextRoles.add(m);
        List<Integer> rolesIds=new ArrayList<>();
        for (MmoSimpleRole r:nextRoles){
            rolesIds.add(r.getId());
        }
        MmoScenePOJO nextScenePOJO=new MmoScenePOJO();
        nextScenePOJO.setId(nextScene.getId());
        String newRoles=CommonsUtil.listToString(rolesIds);
        nextScenePOJO.setRoles(newRoles);
        mmoScenePOJOMapper.updateByPrimaryKeySelective(nextScenePOJO);
        //旧场景
        List<MmoSimpleRole> nowRoles=nowScene.getRoles();
        synchronized (nowScene) {
            Iterator<MmoSimpleRole> iterator = nowRoles.iterator();
            while (iterator.hasNext()) {
                MmoSimpleRole sbi = iterator.next();
                if (sbi.getId() == playId) {
                    iterator.remove();
                }
            }
        }
        List<Integer> nowRoleIds=new ArrayList<>();
        for (MmoSimpleRole r:nowRoles){
            nowRoleIds.add(r.getId());
        }
        MmoScenePOJO nowMmoScenePOJO=new MmoScenePOJO();
        nowMmoScenePOJO.setRoles(CommonsUtil.listToString(nowRoleIds));
        nowMmoScenePOJO.setId(nowScene.getId());
        mmoScenePOJOMapper.updateByPrimaryKeySelective(nowMmoScenePOJO);

        //查询出simpleScene 和SimpleRole
        List<MmoSimpleRole> nextSceneRoles=nextScene.getRoles();
        List<MmoSimpleScene> nextSceneCanScene=nextScene.getCanScene();
        //生成response返回
        NettyResponse nettyResponse=new NettyResponse();
        nettyResponse.setCmd(ConstantValue.WENT_RESPONSE);
        nettyResponse.setStateCode(StateCode.SUCCESS);
        nettyResponse.setModule(ConstantValue.SCENE_MODULE);
        //ptotobuf生成wentResponse
        SceneModel.SceneModelMessage.Builder builder=SceneModel.SceneModelMessage.newBuilder();
        builder.setDataType(SceneModel.SceneModelMessage.DateType.WentResponse);

        SceneModel.WentResponse.Builder wentResponsebuilder=SceneModel.WentResponse.newBuilder();

        SceneModel.MmoScene.Builder mmoSceneBuilder=SceneModel.MmoScene.newBuilder();
        mmoSceneBuilder.setId(nextScene.getId()).setPlaceName(nextScene.getPlaceName());
        //protobuf simpleCanScene
        List<SceneModel.MmoSimpleScene> simScenes=new ArrayList<>();
        for (MmoSimpleScene mmoSimpleScene:nextSceneCanScene){
            SceneModel.MmoSimpleScene.Builder mss=SceneModel.MmoSimpleScene.newBuilder();
            mss.setId(mmoSimpleScene.getId());
            mss.setPalceName(mmoSimpleScene.getPalceName());
            SceneModel.MmoSimpleScene mssobject=mss.build();
            simScenes.add(mssobject);
        }
        mmoSceneBuilder.addAllCanScene(simScenes);
       // simpleRole
        List<SceneModel.MmoSimpleRole> simRoles=new ArrayList<>();
        for (MmoSimpleRole mmoRole :nextSceneRoles){
            SceneModel.MmoSimpleRole.Builder msr=SceneModel.MmoSimpleRole.newBuilder();
            msr.setId(mmoRole.getId());
            msr.setName(mmoRole.getName());
            msr.setType(mmoRole.getType());
            msr.setStatus(mmoRole.getStatus());
            msr.setOnStatus(mmoRole.getOnstatus());
            SceneModel.MmoSimpleRole msrobject=msr.build();
            simRoles.add(msrobject);
        }
        mmoSceneBuilder.addAllCanScene(simScenes);
        mmoSceneBuilder.addAllRoles(simRoles);

        wentResponsebuilder.setMmoScene(mmoSceneBuilder.build());
        builder.setWentResponse(wentResponsebuilder.build());

        byte[] data2=builder.build().toByteArray();
        nettyResponse.setData(data2);
        log.info("wentRequest:"+data2.length);
        return nettyResponse;
    }

    @Override
    public NettyResponse findAllRolesRequest(NettyRequest nettyRequest) throws InvalidProtocolBufferException {
        byte[] data=nettyRequest.getData();
        SceneModel.SceneModelMessage myMessage;
        myMessage=SceneModel.SceneModelMessage.parseFrom(data);
        Integer sceneId=myMessage.getFindAllRolesRequest().getSceneId();
        MmoScene mmoScene= MmoCache.getInstance().getMmoSceneConcurrentHashMap().get(sceneId);
        List<MmoSimpleRole> mmoSimRoles=mmoScene.getRoles();
        //protobuf
        SceneModel.SceneModelMessage.Builder messagedataBuilder=SceneModel.SceneModelMessage.newBuilder();
        messagedataBuilder.setDataType(SceneModel.SceneModelMessage.DateType.FindAllRolesResponse);
        SceneModel.FindAllRolesResponse.Builder findAllRolesResponseBuilder=SceneModel.FindAllRolesResponse.newBuilder();
        List<SceneModel.MmoSimpleRole> mmoSimpleRoles=new ArrayList<>();
        for (MmoSimpleRole m :mmoSimRoles) {
            SceneModel.MmoSimpleRole msr=SceneModel.MmoSimpleRole.newBuilder().setId(
                    m.getId()).setName(m.getName()).setStatus(m.getStatus()).setType(m.getType()).setOnStatus(m.getOnstatus()).build();
            mmoSimpleRoles.add(msr);
        }
        findAllRolesResponseBuilder.addAllMmoSimpleRoles(mmoSimpleRoles);
        messagedataBuilder.setFindAllRolesResponse(findAllRolesResponseBuilder.build());
        byte[] data2=messagedataBuilder.build().toByteArray();
        NettyResponse nettyResponse=new NettyResponse();
        nettyResponse.setCmd(ConstantValue.FIND_ALL_ROLES_RESPONSE);
        nettyResponse.setStateCode(200);
        nettyResponse.setModule(ConstantValue.SCENE_MODULE);
        nettyResponse.setData(data2);
        return nettyResponse;
    }

}
