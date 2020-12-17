package com.liqihao.service.impl;

import com.google.protobuf.InvalidProtocolBufferException;
import com.liqihao.Cache.MmoCache;
import com.liqihao.annotation.HandlerCmdTag;
import com.liqihao.annotation.HandlerServiceTag;
import com.liqihao.commons.*;
import com.liqihao.commons.enums.StateCode;
import com.liqihao.dao.MmoRolePOJOMapper;
import com.liqihao.dao.MmoScenePOJOMapper;
import com.liqihao.pojo.*;
import com.liqihao.pojo.baseMessage.NPCMessage;
import com.liqihao.pojo.baseMessage.SceneMessage;
import com.liqihao.pojo.bean.MmoSimpleNPC;
import com.liqihao.pojo.bean.MmoSimpleRole;
import com.liqihao.pojo.bean.SceneBean;
import com.liqihao.protobufObject.SceneModel;
import com.liqihao.service.SceneService;
import com.liqihao.util.CommonsUtil;
import io.netty.channel.Channel;
import io.netty.util.AttributeKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Service
@HandlerServiceTag
public class SceneServiceImpl implements SceneService {
    private static final Logger log = LoggerFactory.getLogger(SceneServiceImpl.class);
    @Autowired
    private MmoScenePOJOMapper mmoScenePOJOMapper;
    @Autowired
    private MmoRolePOJOMapper mmoRolePOJOMapper;


    @Override
    @HandlerCmdTag(cmd = ConstantValue.ASK_CAN_REQUEST,module = ConstantValue.SCENE_MODULE)
    public NettyResponse askCanRequest(NettyRequest request,Channel channel) throws InvalidProtocolBufferException {
        byte[] data=request.getData();
        SceneModel.SceneModelMessage myMessage;
        myMessage=SceneModel.SceneModelMessage.parseFrom(data);
        Integer roleId=CommonsUtil.getRoleIdByChannel(channel);
        MmoSimpleRole mmoSimpleRole=MmoCache.getInstance().getMmoSimpleRoleConcurrentHashMap().get(roleId);
        Integer sceneId=mmoSimpleRole.getMmosceneid();
        log.info("SceneService accept sceneId: "+sceneId);
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
        byte[] data2=Messagedata.toByteArray();
        response.setData(data2);
        return response;
    }

    @Override
    @HandlerCmdTag(cmd = ConstantValue.WENT_REQUEST,module = ConstantValue.SCENE_MODULE)
    public NettyResponse wentRequest(NettyRequest nettyRequest,Channel channel) throws InvalidProtocolBufferException {
        byte[] data=nettyRequest.getData();
        SceneModel.SceneModelMessage myMessage;
        myMessage=SceneModel.SceneModelMessage.parseFrom(data);
        Integer nextSceneId=myMessage.getWentRequest().getSceneId();
        ConcurrentHashMap<Integer, Channel> channelConcurrentHashMap= MmoCache.getInstance().getChannelConcurrentHashMap();
        Integer roleId=CommonsUtil.getRoleIdByChannel(channel);
        if (roleId==null){
            NettyResponse errotResponse=new NettyResponse(StateCode.FAIL,ConstantValue.WENT_RESPONSE,"请先登录".getBytes());
            return  errotResponse;
        }
        //先查询palyId所在场景
        MmoSimpleRole mmoSimpleRole= MmoCache.getInstance().getMmoSimpleRoleConcurrentHashMap().get(roleId);
        //查询该场景可进入的场景与sceneId判断
        Integer nowSceneId=mmoSimpleRole.getMmosceneid();
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
            NettyResponse errotResponse=new NettyResponse(StateCode.FAIL,ConstantValue.WENT_RESPONSE,"无法前往该场景".getBytes());
            return  errotResponse;
        }
        //进入场景，修改数据库 player 和scene
        MmoRolePOJO mmoRolePOJO1=new MmoRolePOJO();
        mmoRolePOJO1.setId(roleId);
        mmoRolePOJO1.setMmosceneid(nextSceneId);
        mmoRolePOJOMapper.updateByPrimaryKeySelective(mmoRolePOJO1);
        //修改缓存中角色
        ConcurrentHashMap<Integer,MmoSimpleRole> cacheRoles= MmoCache.getInstance().getMmoSimpleRoleConcurrentHashMap();
        MmoSimpleRole player=cacheRoles.get(roleId);
        player.setMmosceneid(nextSceneId);
        cacheRoles.put(player.getId(),player);
        //修改scene
        MmoCache.getInstance().getSceneBeanConcurrentHashMap().get(nowSceneId).getRoles().remove(roleId);
        MmoCache.getInstance().getSceneBeanConcurrentHashMap().get(nextSceneId).getRoles().add(roleId);
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
        ConcurrentHashMap<Integer, MmoSimpleNPC> npcMsgMap=MmoCache.getInstance().getNpcMessageConcurrentHashMap();
        ConcurrentHashMap<Integer, MmoSimpleRole> roleMsgMap=MmoCache.getInstance().getMmoSimpleRoleConcurrentHashMap();
        Iterator<MmoSimpleNPC> npcItor=npcMsgMap.values().iterator();
        //NPC
        while(npcItor.hasNext()){
            MmoSimpleNPC temp=npcItor.next();
            if (temp.getMmosceneid().equals(nextSceneId)){
                nextSceneRoles.add(CommonsUtil.NpcToMmoSimpleRole(temp));
            }
        }
        //ROLES
        Iterator<MmoSimpleRole> roleItor=roleMsgMap.values().iterator();
        while(roleItor.hasNext()){
            MmoSimpleRole temp=roleItor.next();
            //排除自己咯
            if (temp.getMmosceneid().equals(nextSceneId)&&!temp.getId().equals(roleId)){
                nextSceneRoles.add(temp);
            }
        }
        //生成response返回
        NettyResponse nettyResponse=new NettyResponse();
        nettyResponse.setCmd(ConstantValue.WENT_RESPONSE);
        nettyResponse.setStateCode(StateCode.SUCCESS);
        //ptotobuf生成wentResponse
        SceneModel.SceneModelMessage.Builder builder=SceneModel.SceneModelMessage.newBuilder();
        builder.setDataType(SceneModel.SceneModelMessage.DateType.WentResponse);

        SceneModel.WentResponse.Builder wentResponsebuilder=SceneModel.WentResponse.newBuilder();
        //simpleRole
        List<SceneModel.RoleDTO> roleDTOS=new ArrayList<>();
        for (MmoSimpleRole mmoRole :nextSceneRoles){
            SceneModel.RoleDTO.Builder msr=SceneModel.RoleDTO.newBuilder();
            msr.setId(mmoRole.getId());
            msr.setName(mmoRole.getName());
            msr.setType(mmoRole.getType());
            msr.setStatus(mmoRole.getStatus());
            msr.setOnStatus(mmoRole.getOnstatus());
            msr.setBlood(mmoRole.getBlood());
            msr.setNowBlood(mmoRole.getNowBlood());
            msr.setMp(mmoRole.getMp());
            msr.setNowMp(mmoRole.getNowMp());
            msr.setAttack(mmoRole.getAttack());
            msr.setAttackAdd(mmoRole.getDamageAdd());
            SceneModel.RoleDTO msrobject=msr.build();
            roleDTOS.add(msrobject);
        }
        wentResponsebuilder.setSceneId(nextSceneId);
        wentResponsebuilder.addAllRoleDTO(roleDTOS);
        builder.setWentResponse(wentResponsebuilder.build());
        byte[] data2=builder.build().toByteArray();
        nettyResponse.setData(data2);
        log.info("wentRequest:"+data2.length);
        return nettyResponse;
    }

    @Override
    @HandlerCmdTag(cmd = ConstantValue.FIND_ALL_ROLES_REQUEST,module = ConstantValue.SCENE_MODULE)
    public NettyResponse findAllRolesRequest(NettyRequest nettyRequest,Channel channel) throws InvalidProtocolBufferException {
        byte[] data=nettyRequest.getData();
        SceneModel.SceneModelMessage myMessage;
        myMessage=SceneModel.SceneModelMessage.parseFrom(data);
        Integer sceneId=myMessage.getFindAllRolesRequest().getSceneId();
        List<MmoSimpleRole> sceneRoles=new ArrayList<>();
        SceneBean sceneBean=MmoCache.getInstance().getSceneBeanConcurrentHashMap().get(sceneId);
        List<Integer> mmoSimpleRoles=sceneBean.getRoles();
        List<Integer> npcs=sceneBean.getNpcs();
        //NPC
        for (Integer id:npcs){
            MmoSimpleNPC temp=MmoCache.getInstance().getNpcMessageConcurrentHashMap().get(id);
            MmoSimpleRole roleTemp=new MmoSimpleRole();
            roleTemp.setId(temp.getId());
            roleTemp.setName(temp.getName());
            roleTemp.setStatus(temp.getStatus());
            roleTemp.setType(temp.getType());
            roleTemp.setOnstatus(temp.getOnstatus());
            roleTemp.setBlood(temp.getBlood());
            roleTemp.setNowBlood(temp.getNowBlood());
            roleTemp.setMp(temp.getMp());
            roleTemp.setNowMp(temp.getNowMp());
            roleTemp.setMmosceneid(temp.getMmosceneid());
            roleTemp.setAttack(temp.getAttack());
            roleTemp.setDamageAdd(temp.getDamageAdd());
            sceneRoles.add(roleTemp);
        }
        //ROLES
        for (Integer id:mmoSimpleRoles){
            MmoSimpleRole temp=MmoCache.getInstance().getMmoSimpleRoleConcurrentHashMap().get(id);
            sceneRoles.add(temp);
        }
        //protobuf
        SceneModel.SceneModelMessage.Builder messagedataBuilder=SceneModel.SceneModelMessage.newBuilder();
        messagedataBuilder.setDataType(SceneModel.SceneModelMessage.DateType.FindAllRolesResponse);
        SceneModel.FindAllRolesResponse.Builder findAllRolesResponseBuilder=SceneModel.FindAllRolesResponse.newBuilder();
        List<SceneModel.RoleDTO> roleDTOS=new ArrayList<>();
        for (MmoSimpleRole m :sceneRoles) {
            SceneModel.RoleDTO msr=SceneModel.RoleDTO.newBuilder().setId(m.getId())
                    .setName(m.getName())
                    .setOnStatus(m.getOnstatus())
                    .setStatus(m.getStatus())
                    .setType(m.getType())
                    .setBlood(m.getBlood())
                    .setNowBlood(m.getNowBlood())
                    .setMp(m.getMp())
                    .setNowMp(m.getNowMp())
                    .setAttack(m.getAttack())
                    .setAttackAdd(m.getDamageAdd())
                    .build();
            roleDTOS.add(msr);
        }
        findAllRolesResponseBuilder.addAllRoleDTO(roleDTOS);
        messagedataBuilder.setFindAllRolesResponse(findAllRolesResponseBuilder.build());
        byte[] data2=messagedataBuilder.build().toByteArray();
        NettyResponse nettyResponse=new NettyResponse();
        nettyResponse.setCmd(ConstantValue.FIND_ALL_ROLES_RESPONSE);
        nettyResponse.setStateCode(200);
        nettyResponse.setData(data2);
        return nettyResponse;
    }

    @Override
    @HandlerCmdTag(cmd = ConstantValue.TALK_NPC_REQUEST,module = ConstantValue.SCENE_MODULE)
    public NettyResponse talkNpcRequest(NettyRequest nettyRequest, Channel channel) throws InvalidProtocolBufferException {
        byte[] data=nettyRequest.getData();
        SceneModel.SceneModelMessage myMessage;
        myMessage=SceneModel.SceneModelMessage.parseFrom(data);
        Integer npcId=myMessage.getTalkNPCRequest().getRoleId();
        //判断是否与角色在同一场景
        ConcurrentHashMap<Integer,Channel> channelConcurrentHashMap=MmoCache.getInstance().getChannelConcurrentHashMap();
        Integer roleId=null;
        for (Integer id :channelConcurrentHashMap.keySet()) {
            if (channelConcurrentHashMap.get(id).equals(channel)){
                roleId=id;
                break;
            }
        }
        if (roleId==null){
            return new NettyResponse(StateCode.FAIL,ConstantValue.TALK_NPC_RESPONSE,"未登录".getBytes());
        }
        //缓存中获取NPC
        NPCMessage npc=MmoCache.getInstance().getNpcMessageConcurrentHashMap().get(npcId);
        MmoRolePOJO role=MmoCache.getInstance().getMmoSimpleRoleConcurrentHashMap().get(roleId);
        if (!npc.getMmosceneid().equals(role.getMmosceneid())){
            return new NettyResponse(StateCode.FAIL,ConstantValue.TALK_NPC_RESPONSE,"该NPC不在当前场景".getBytes());
        }
        //无问题 返回npcId
        SceneModel.SceneModelMessage Messagedata;
        Messagedata=SceneModel.SceneModelMessage.newBuilder()
                .setDataType(SceneModel.SceneModelMessage.DateType.TalkNPCResponse)
                .setTalkNPCResponse(SceneModel.TalkNPCResponse.newBuilder().setNpcId(npcId).build()).build();
        //封装到NettyResponse中
        NettyResponse response=new NettyResponse();
        response.setCmd(ConstantValue.TALK_NPC_RESPONSE);
        response.setStateCode(StateCode.SUCCESS);
        byte[] data2=Messagedata.toByteArray();
        response.setData(data2);
        return response;
    }

}
