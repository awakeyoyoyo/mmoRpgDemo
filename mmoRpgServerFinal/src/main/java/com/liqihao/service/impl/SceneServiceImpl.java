package com.liqihao.service.impl;

import com.google.protobuf.InvalidProtocolBufferException;
import com.liqihao.commons.*;
import com.liqihao.dao.MmoRolePOJOMapper;
import com.liqihao.dao.MmoScenePOJOMapper;
import com.liqihao.pojo.MmoRolePOJO;
import com.liqihao.pojo.MmoScene;
import com.liqihao.pojo.MmoScenePOJO;
import com.liqihao.pojo.MmoSimpleScene;
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

import javax.swing.plaf.nimbus.State;
import java.util.ArrayList;
import java.util.List;

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
        if (null==sceneId){
            return new NettyResponse(400,(short)444,(short)444,"无传入sceneId无法查询".getBytes());
        }
        log.info("SceneService accept sceneId: "+sceneId);


        //根据sceneId 从spirng容器中获取场景信息
        MmoScene mmoScene=applicationContext.getBean("scene"+sceneId,MmoScene.class);
        List<SceneModel.MmoSimpleScene> mmoSimpleScenes=new ArrayList<>();
        //由MmoSimpleScene转化为SceneModel.MmoSimpleScene
        for (MmoSimpleScene m :mmoScene.getCanScene()) {
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
        response.setCmd((short)1010);
        response.setStateCode(200);
        response.setModule((short)1111);
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
        MmoRolePOJO mmoRolePOJO=mmoRolePOJOMapper.selectByPrimaryKey(playId);
        //查询该场景可进入的场景与sceneId判断
        MmoScenePOJO nowScene=mmoScenePOJOMapper.selectByPrimaryKey(mmoRolePOJO.getMmosceneid());
        List<Integer> cans=CommonsUtil.split(nowScene.getCanscene());

        if (!cans.contains(sceneId)){
            //不包含 即不可进入
            NettyResponse errotResponse=new NettyResponse(400,(short)444,(short)444,"无法前往该场景".getBytes());
            return  errotResponse;
        }
        //进入场景，修改数据库 player 和scene
        MmoRolePOJO mmoRolePOJO1=new MmoRolePOJO();
        mmoRolePOJO1.setId(playId);
        mmoRolePOJO1.setMmosceneid(sceneId);
        mmoRolePOJOMapper.updateByPrimaryKeySelective(mmoRolePOJO1);
        //修改scene
        //新场景
        MmoScenePOJO nextScene=mmoScenePOJOMapper.selectByPrimaryKey(sceneId);
        List<Integer> oldRoles=CommonsUtil.split(nextScene.getRoles());
        if (!oldRoles.contains(playId)){
            oldRoles.add(playId);
        }
        String newRoles=CommonsUtil.listToString(oldRoles);
        nextScene.setRoles(newRoles);
        mmoScenePOJOMapper.updateByPrimaryKeySelective(nextScene);
        //旧场景
        List<Integer> oldRoles2=CommonsUtil.split(nowScene.getRoles());
        if (oldRoles2.contains(playId)){
            oldRoles2.remove(playId);
        }
        String newRoles2=CommonsUtil.listToString(oldRoles2);
        nowScene.setRoles(newRoles2);
        mmoScenePOJOMapper.updateByPrimaryKeySelective(nowScene);

        //查询出simpleScene 和SimpleRole
        List<MmoRolePOJO> mmoRolePOJOS=new ArrayList<>();
        List<MmoScenePOJO> mmoScenePOJOS=new ArrayList<>();
        for(Integer id:oldRoles){
            mmoRolePOJOS.add(mmoRolePOJOMapper.selectByPrimaryKeyAndOnStatus(id));
        }
        List<Integer> scenes=CommonsUtil.split(nextScene.getCanscene());
        if (scenes.size()>0) {
            for (Integer id : scenes) {
                mmoScenePOJOS.add(mmoScenePOJOMapper.selectByPrimaryKey(id));
            }
        }

        //生成response返回
        NettyResponse nettyResponse=new NettyResponse();
        nettyResponse.setCmd((short)1012);
        nettyResponse.setStateCode(200);
        nettyResponse.setModule((short)1111);
        //ptotobuf生成wentResponse
        SceneModel.SceneModelMessage.Builder builder=SceneModel.SceneModelMessage.newBuilder();
        builder.setDataType(SceneModel.SceneModelMessage.DateType.WentResponse);

        SceneModel.WentResponse.Builder wentResponsebuilder=SceneModel.SceneModelMessage.newBuilder().getWentResponseBuilder();

        SceneModel.MmoScene.Builder mmoSceneBuilder=SceneModel.MmoScene.newBuilder();
        mmoSceneBuilder.setId(nextScene.getId()).setPlaceName(nextScene.getPlacename());
        //protobuf simpleCanScene
        List<SceneModel.MmoSimpleScene> simScenes=new ArrayList<>();
        for (MmoScenePOJO mmoScenePOJO:mmoScenePOJOS){
            SceneModel.MmoSimpleScene.Builder mss=SceneModel.MmoSimpleScene.newBuilder();
            mss.setId(mmoScenePOJO.getId());
            mss.setPalceName(mmoScenePOJO.getPlacename());
            SceneModel.MmoSimpleScene mssobject=mss.build();
            simScenes.add(mssobject);
        }
        mmoSceneBuilder.addAllCanScene(simScenes);
       // simpleRole
        List<SceneModel.MmoSimpleRole> simRoles=new ArrayList<>();
        for (MmoRolePOJO mmoRole :mmoRolePOJOS){
            SceneModel.MmoSimpleRole.Builder msr=SceneModel.MmoSimpleRole.newBuilder();
            msr.setId(mmoRole.getId());
            msr.setName(mmoRole.getName());
            msr.setType(RoleTypeCode.getValue(mmoRole.getType()));
            msr.setStatus(RoleStatusCode.getValue(mmoRole.getStatus()));
            msr.setOnStatus(RoleStatusCode.getValue(mmoRole.getOnstatus()));
            SceneModel.MmoSimpleRole msrobject=msr.build();
            simRoles.add(msrobject);
        }
        mmoSceneBuilder.addAllCanScene(simScenes);
        mmoSceneBuilder.addAllRoles(simRoles);

        wentResponsebuilder.setMmoScene(mmoSceneBuilder.build());
        builder.setWentResponse(wentResponsebuilder.build());

        byte[] data2=builder.build().toByteArray();
        nettyRequest.setData(data2);
        return nettyResponse;
    }

    @Override
    public NettyResponse whereRequest(NettyRequest nettyRequest) throws InvalidProtocolBufferException {
        return new NettyResponse(StateCode.FAIL,(short)444,(short)444,"该请求未写".getBytes());
    }

    @Override
    public NettyResponse findAllRolesRequest(NettyRequest nettyRequest) throws InvalidProtocolBufferException {
        byte[] data=nettyRequest.getData();
        SceneModel.SceneModelMessage myMessage;
        myMessage=SceneModel.SceneModelMessage.parseFrom(data);
        Integer sceneId=myMessage.getFindAllRolesRequest().getSceneId();
        MmoScenePOJO mmoScene=mmoScenePOJOMapper.selectByPrimaryKey(sceneId);
        String roles=mmoScene.getRoles();
        List<Integer> roleIds=CommonsUtil.split(roles);
        List<MmoRolePOJO> mmoRolePOJOS=new ArrayList<>();
        for (Integer id:roleIds) {
            MmoRolePOJO mmoRolePOJO=mmoRolePOJOMapper.selectByPrimaryKeyAndOnStatus(id);
            mmoRolePOJOS.add(mmoRolePOJO);
        }
        //protobuf
        SceneModel.SceneModelMessage.Builder messagedataBuilder=SceneModel.SceneModelMessage.newBuilder();
        messagedataBuilder.setDataType(SceneModel.SceneModelMessage.DateType.FindAllRolesRequest);
        SceneModel.FindAllRolesResponse.Builder findAllRolesResponseBuilder=SceneModel.FindAllRolesResponse.newBuilder();
        List<SceneModel.MmoSimpleRole> mmoSimpleRoles=new ArrayList<>();
        for (MmoRolePOJO m :mmoRolePOJOS) {
            SceneModel.MmoSimpleRole msr=SceneModel.MmoSimpleRole.newBuilder().setId(
                    m.getId()).setName(m.getName()).setStatus(RoleStatusCode.getValue(m.getStatus())).setType(RoleTypeCode.getValue(m.getType())).setOnStatus(RoleOnStatusCode.getValue(m.getOnstatus())).build();
            mmoSimpleRoles.add(msr);
        }
        findAllRolesResponseBuilder.addAllMmoSimpleRoles(mmoSimpleRoles);
        byte[] data2=findAllRolesResponseBuilder.build().toByteArray();
        NettyResponse nettyResponse=new NettyResponse();
        nettyResponse.setCmd((short)1011);
        nettyResponse.setStateCode(200);
        nettyResponse.setModule((short)1111);
        nettyResponse.setData(data2);
        return nettyResponse;
    }

}