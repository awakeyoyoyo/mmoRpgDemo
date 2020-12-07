package com.liqihao.service.impl;

import com.google.protobuf.InvalidProtocolBufferException;
import com.liqihao.Cache.MmoCache;
import com.liqihao.commons.*;
import com.liqihao.dao.MmoRolePOJOMapper;
import com.liqihao.dao.MmoScenePOJOMapper;
import com.liqihao.pojo.*;
import com.liqihao.pojo.baseMessage.NPCMessage;
import com.liqihao.pojo.baseMessage.SceneMessage;
import com.liqihao.protobufObject.SceneModel;
import com.liqihao.service.SceneService;
import com.liqihao.util.CommonsUtil;
import io.netty.channel.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.management.relation.Role;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class SceneServiceImpl implements SceneService {
    private static final Logger log = LoggerFactory.getLogger(SceneServiceImpl.class);
    @Autowired
    private MmoScenePOJOMapper mmoScenePOJOMapper;
    @Autowired
    private MmoRolePOJOMapper mmoRolePOJOMapper;


    @Override
    public NettyResponse askCanRequest(NettyRequest request,Channel channel) throws InvalidProtocolBufferException {
        byte[] data=request.getData();
        SceneModel.SceneModelMessage myMessage;
        myMessage=SceneModel.SceneModelMessage.parseFrom(data);
        Integer sceneId=myMessage.getAskCanRequest().getSceneId();
        if (null==sceneId) {
            return new NettyResponse(StateCode.FAIL, ConstantValue.SCENE_MODULE, ConstantValue.ASK_CAN_RESPONSE, "无传入sceneId无法查询".getBytes());
        }
        log.info("SceneService accept sceneId: "+sceneId);
//       MmoScene mmoScene=applicationContext.getBean("scene"+sceneId,MmoScene.class);
        //从缓存中读取
        ConcurrentHashMap<Integer, SceneMessage> scenes= MmoCache.getInstance().getSceneMessageConcurrentHashMap();
        String canScene=scenes.get(sceneId).getCanScene();
        List<Integer> sceneIds=CommonsUtil.split(canScene);
        //封装AskCanResponse data的数据
        SceneModel.SceneModelMessage Messagedata;
        Messagedata=SceneModel.SceneModelMessage.newBuilder()
                .setDataType(SceneModel.SceneModelMessage.DateType.AskCanResponse)
                .setAskCanResponse(SceneModel.AskCanResponse.newBuilder().addAllScenesIds(sceneIds)).build();
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
    public NettyResponse wentRequest(NettyRequest nettyRequest,Channel channel) throws InvalidProtocolBufferException {
        byte[] data=nettyRequest.getData();
        SceneModel.SceneModelMessage myMessage;
        myMessage=SceneModel.SceneModelMessage.parseFrom(data);
        Integer nextSceneId=myMessage.getWentRequest().getSceneId();
        ConcurrentHashMap<Integer, Channel> channelConcurrentHashMap= MmoCache.getInstance().getChannelConcurrentHashMap();
        Integer roleId=null;
        synchronized (channelConcurrentHashMap) {
            for (Integer key : channelConcurrentHashMap.keySet()) {
                if (channelConcurrentHashMap.get(key).equals(channel)) {
                    roleId = key;
                }
            }
        }
        if (roleId==null){
            NettyResponse errotResponse=new NettyResponse(StateCode.FAIL,ConstantValue.SCENE_MODULE,ConstantValue.WENT_RESPONSE,"请先登录".getBytes());
            return  errotResponse;
        }
        //先查询palyId所在场景
        MmoRolePOJO mmoRolePOJO= MmoCache.getInstance().getMmoSimpleRoleConcurrentHashMap().get(roleId);
        //查询该场景可进入的场景与sceneId判断
        Integer nowSceneId=mmoRolePOJO.getMmosceneid();
        SceneMessage sceneMessage=MmoCache.getInstance().getSceneMessageConcurrentHashMap().get(nowSceneId);
        List<Integer> canScene=CommonsUtil.split(sceneMessage.getCanScene());
        boolean canFlag=false;
        for (Integer id:canScene){
            if (id.equals(nextSceneId)){
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
        mmoRolePOJO1.setId(roleId);
        mmoRolePOJO1.setMmosceneid(nextSceneId);
        mmoRolePOJOMapper.updateByPrimaryKeySelective(mmoRolePOJO1);
        //修改缓存中角色
        ConcurrentHashMap<Integer,MmoRolePOJO> cacheRoles= MmoCache.getInstance().getMmoSimpleRoleConcurrentHashMap();
        MmoRolePOJO player=cacheRoles.get(roleId);
        player.setMmosceneid(nextSceneId);
        cacheRoles.put(player.getId(),player);
        //修改scene
        //数据库中新场景中增加该角色
        MmoScenePOJO nextScenePOJO=mmoScenePOJOMapper.selectByPrimaryKey(nextSceneId);
        List<Integer> rolesIds=CommonsUtil.split(nextScenePOJO.getRoles());
        rolesIds.add(roleId);
        String newRoles=CommonsUtil.listToString(rolesIds);
        nextScenePOJO.setRoles(newRoles);
        mmoScenePOJOMapper.updateByPrimaryKeySelective(nextScenePOJO);
        //旧场景
        MmoScenePOJO nowScenePOJO=mmoScenePOJOMapper.selectByPrimaryKey(nowSceneId);
        String  nowRoles=nowScenePOJO.getRoles();
        List<Integer> nowrolesIds=CommonsUtil.split(nowRoles);
        nowrolesIds.remove(roleId);
        nowScenePOJO.setRoles(CommonsUtil.listToString(nowrolesIds));
        mmoScenePOJOMapper.updateByPrimaryKeySelective(nowScenePOJO);

        //查询出npc 和SimpleRole
        List<MmoSimpleRole> nextSceneRoles=new ArrayList<>();
        ConcurrentHashMap<Integer, NPCMessage> npcMsgMap=MmoCache.getInstance().getNpcMessageConcurrentHashMap();
        ConcurrentHashMap<Integer, MmoRolePOJO> roleMsgMap=MmoCache.getInstance().getMmoSimpleRoleConcurrentHashMap();
        Iterator<NPCMessage> npcItor=npcMsgMap.values().iterator();
        //NPC
        while(npcItor.hasNext()){
            NPCMessage temp=npcItor.next();
            if (temp.getMmosceneid().equals(nextSceneId)){
                MmoSimpleRole roleTemp=new MmoSimpleRole();
                roleTemp.setId(temp.getId());
                roleTemp.setName(temp.getName());
                roleTemp.setStatus(RoleStatusCode.getValue(temp.getStatus()));
                roleTemp.setType(RoleTypeCode.getValue(temp.getType()));
                roleTemp.setOnstatus(RoleOnStatusCode.getValue(temp.getOnstatus()));
                nextSceneRoles.add(roleTemp);
            }
        }
        //ROLES
        Iterator<MmoRolePOJO> roleItor=roleMsgMap.values().iterator();
        while(roleItor.hasNext()){
            MmoRolePOJO temp=roleItor.next();
            if (temp.getMmosceneid().equals(nextSceneId)){
                MmoSimpleRole roleTemp=new MmoSimpleRole();
                roleTemp.setId(temp.getId());
                roleTemp.setName(temp.getName());
                roleTemp.setStatus(RoleStatusCode.getValue(temp.getStatus()));
                roleTemp.setType(RoleTypeCode.getValue(temp.getType()));
                roleTemp.setOnstatus(RoleOnStatusCode.getValue(temp.getOnstatus()));
                nextSceneRoles.add(roleTemp);
            }
        }
        //生成response返回
        NettyResponse nettyResponse=new NettyResponse();
        nettyResponse.setCmd(ConstantValue.WENT_RESPONSE);
        nettyResponse.setStateCode(StateCode.SUCCESS);
        nettyResponse.setModule(ConstantValue.SCENE_MODULE);
        //ptotobuf生成wentResponse
        SceneModel.SceneModelMessage.Builder builder=SceneModel.SceneModelMessage.newBuilder();
        builder.setDataType(SceneModel.SceneModelMessage.DateType.WentResponse);

        SceneModel.WentResponse.Builder wentResponsebuilder=SceneModel.WentResponse.newBuilder();
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
        wentResponsebuilder.setSceneId(nextSceneId);
        wentResponsebuilder.addAllMmoSimpleRoles(simRoles);
        builder.setWentResponse(wentResponsebuilder.build());
        byte[] data2=builder.build().toByteArray();
        nettyResponse.setData(data2);
        log.info("wentRequest:"+data2.length);
        return nettyResponse;
    }

    @Override
    public NettyResponse findAllRolesRequest(NettyRequest nettyRequest,Channel channel) throws InvalidProtocolBufferException {
        byte[] data=nettyRequest.getData();
        SceneModel.SceneModelMessage myMessage;
        myMessage=SceneModel.SceneModelMessage.parseFrom(data);
        Integer sceneId=myMessage.getFindAllRolesRequest().getSceneId();
        List<MmoSimpleRole> nextSceneRoles=new ArrayList<>();
        ConcurrentHashMap<Integer, NPCMessage> npcMsgMap=MmoCache.getInstance().getNpcMessageConcurrentHashMap();
        ConcurrentHashMap<Integer, MmoRolePOJO> roleMsgMap=MmoCache.getInstance().getMmoSimpleRoleConcurrentHashMap();
        Iterator<NPCMessage> npcItor=npcMsgMap.values().iterator();
        //NPC
        while(npcItor.hasNext()){
            NPCMessage temp=npcItor.next();
            if (temp.getMmosceneid().equals(sceneId)){
                MmoSimpleRole roleTemp=new MmoSimpleRole();
                roleTemp.setId(temp.getId());
                roleTemp.setName(temp.getName());
                roleTemp.setStatus(RoleStatusCode.getValue(temp.getStatus()));
                roleTemp.setType(RoleTypeCode.getValue(temp.getType()));
                roleTemp.setOnstatus(RoleOnStatusCode.getValue(temp.getOnstatus()));
                nextSceneRoles.add(roleTemp);
            }
        }
        //ROLES
        Iterator<MmoRolePOJO> roleItor=roleMsgMap.values().iterator();
        while(roleItor.hasNext()){
            MmoRolePOJO temp=roleItor.next();
            if (temp.getMmosceneid().equals(sceneId)){
                MmoSimpleRole roleTemp=new MmoSimpleRole();
                roleTemp.setId(temp.getId());
                roleTemp.setName(temp.getName());
                roleTemp.setStatus(RoleStatusCode.getValue(temp.getStatus()));
                roleTemp.setType(RoleTypeCode.getValue(temp.getType()));
                roleTemp.setOnstatus(RoleOnStatusCode.getValue(temp.getOnstatus()));
                nextSceneRoles.add(roleTemp);
            }
        }
        //protobuf
        SceneModel.SceneModelMessage.Builder messagedataBuilder=SceneModel.SceneModelMessage.newBuilder();
        messagedataBuilder.setDataType(SceneModel.SceneModelMessage.DateType.FindAllRolesResponse);
        SceneModel.FindAllRolesResponse.Builder findAllRolesResponseBuilder=SceneModel.FindAllRolesResponse.newBuilder();
        List<SceneModel.MmoSimpleRole> mmoSimpleRoles=new ArrayList<>();
        for (MmoSimpleRole m :nextSceneRoles) {
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
