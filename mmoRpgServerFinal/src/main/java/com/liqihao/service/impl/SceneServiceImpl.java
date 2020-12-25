package com.liqihao.service.impl;

import com.google.protobuf.InvalidProtocolBufferException;
import com.liqihao.Cache.ChannelMessageCache;
import com.liqihao.Cache.NpcMessageCache;
import com.liqihao.Cache.OnlineRoleMessageCache;
import com.liqihao.Cache.SceneBeanMessageCache;
import com.liqihao.annotation.HandlerCmdTag;
import com.liqihao.annotation.HandlerServiceTag;
import com.liqihao.commons.*;
import com.liqihao.commons.StateCode;
import com.liqihao.dao.MmoRolePOJOMapper;
import com.liqihao.pojo.MmoRolePOJO;
import com.liqihao.pojo.baseMessage.NPCMessage;
import com.liqihao.pojo.bean.MmoSimpleNPC;
import com.liqihao.pojo.bean.MmoSimpleRole;
import com.liqihao.pojo.bean.SceneBean;
import com.liqihao.protobufObject.SceneModel;
import com.liqihao.service.SceneService;
import com.liqihao.util.CommonsUtil;
import io.netty.channel.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;

/**
 * 场景模块
 * @author lqhao
 */
@Service
@HandlerServiceTag
public class SceneServiceImpl implements SceneService {
    private static final Logger log = LoggerFactory.getLogger(SceneServiceImpl.class);
    @Override
    @HandlerCmdTag(cmd = ConstantValue.ASK_CAN_REQUEST,module = ConstantValue.SCENE_MODULE)
    public void askCanRequest(NettyRequest request,Channel channel) throws InvalidProtocolBufferException {
        byte[] data=request.getData();
        SceneModel.SceneModelMessage myMessage;
        myMessage=SceneModel.SceneModelMessage.parseFrom(data);
        MmoSimpleRole mmoSimpleRole=CommonsUtil.checkLogin(channel);
        if (mmoSimpleRole==null){
            return;
        }
        Integer sceneId=mmoSimpleRole.getMmosceneid();
        log.info("SceneService accept sceneId: "+sceneId);
        //从缓存中读取
        List<Integer> sceneIds= SceneBeanMessageCache.getInstance().get(sceneId).getCanScenes();
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
        channel.writeAndFlush(response);
        return;
    }

    @Override
    @HandlerCmdTag(cmd = ConstantValue.WENT_REQUEST,module = ConstantValue.SCENE_MODULE)
    public void wentRequest(NettyRequest nettyRequest,Channel channel) throws InvalidProtocolBufferException {
        byte[] data=nettyRequest.getData();
        SceneModel.SceneModelMessage myMessage;
        myMessage=SceneModel.SceneModelMessage.parseFrom(data);
        Integer nextSceneId=myMessage.getWentRequest().getSceneId();
        MmoSimpleRole mmoSimpleRole=CommonsUtil.checkLogin(channel);
        if (mmoSimpleRole==null){
            return;
        }
        //先查询palyId所在场景
        //查询该场景可进入的场景与sceneId判断
        Integer nowSceneId=mmoSimpleRole.getMmosceneid();
        SceneBean sceneBean=SceneBeanMessageCache.getInstance().get(nowSceneId);
        List<Integer> canScene=sceneBean.getCanScenes();
        boolean canFlag=false;
        for (Integer id:canScene){
            if (id.equals(nextSceneId)){
                canFlag=true;
                break;
            }
        }
        if (!canFlag){
            //不包含 即不可进入
            NettyResponse errotResponse=new NettyResponse(StateCode.FAIL,ConstantValue.FAIL_RESPONSE,"无法前往该场景".getBytes());
            channel.writeAndFlush(errotResponse);
            return;
        }

        //进入场景，修改数据库 player 和scene
        List<MmoSimpleRole> nextSceneRoles=mmoSimpleRole.wentScene(nextSceneId);
        //ptotobuf生成wentResponse
        //生成response返回
        NettyResponse nettyResponse=new NettyResponse();
        nettyResponse.setCmd(ConstantValue.WENT_RESPONSE);
        nettyResponse.setStateCode(StateCode.SUCCESS);
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
            msr.setTeamId(mmoRole.getTeamId()==null?-1:mmoRole.getTeamId());
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
        channel.writeAndFlush(nettyResponse);
    }

    @Override
    @HandlerCmdTag(cmd = ConstantValue.FIND_ALL_ROLES_REQUEST,module = ConstantValue.SCENE_MODULE)
    public void findAllRolesRequest(NettyRequest nettyRequest,Channel channel) throws InvalidProtocolBufferException {
        byte[] data=nettyRequest.getData();
        SceneModel.SceneModelMessage myMessage;
        myMessage=SceneModel.SceneModelMessage.parseFrom(data);
        Integer sceneId=myMessage.getFindAllRolesRequest().getSceneId();
        MmoSimpleRole mmoSimpleRole=CommonsUtil.checkLogin(channel);
        if (mmoSimpleRole==null){
            return;
        }
        if (sceneId==null){
            channel.writeAndFlush(new NettyResponse(StateCode.FAIL,ConstantValue.FAIL_RESPONSE,"传入场景id为空".getBytes()));
            return;
        }
        List<MmoSimpleRole> sceneRoles=new ArrayList<>();
        SceneBean sceneBean=SceneBeanMessageCache.getInstance().get(sceneId);
        if (sceneBean==null){
            channel.writeAndFlush(new NettyResponse(StateCode.FAIL,ConstantValue.FAIL_RESPONSE,"传入场景id为无效id".getBytes()));
            return;
        }
        List<Integer> mmoSimpleRoles=sceneBean.getRoles();
        List<Integer> npcs=sceneBean.getNpcs();
        //NPC
        for (Integer id:npcs){
            MmoSimpleNPC temp=NpcMessageCache.getInstance().get(id);
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
            MmoSimpleRole temp=OnlineRoleMessageCache.getInstance().get(id);
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
                    .setTeamId(m.getTeamId()==null?-1:m.getTeamId())
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
        channel.writeAndFlush(nettyResponse);
        return;
    }

    @Override
    @HandlerCmdTag(cmd = ConstantValue.TALK_NPC_REQUEST,module = ConstantValue.SCENE_MODULE)
    public void talkNpcRequest(NettyRequest nettyRequest, Channel channel) throws InvalidProtocolBufferException {
        byte[] data=nettyRequest.getData();
        SceneModel.SceneModelMessage myMessage;
        myMessage=SceneModel.SceneModelMessage.parseFrom(data);

        Integer npcId=myMessage.getTalkNPCRequest().getRoleId();
        if (npcId==null){
            channel.writeAndFlush(new NettyResponse(StateCode.FAIL,ConstantValue.FAIL_RESPONSE,"传入的参数为空".getBytes()));
            return;
        }
        //判断是否与角色在同一场景
        MmoSimpleRole role=CommonsUtil.checkLogin(channel);
        if (role==null){
            return;
        }
        //缓存中获取NPC
        NPCMessage npc=NpcMessageCache.getInstance().get(npcId);
        if (!npc.getMmosceneid().equals(role.getMmosceneid())){
            channel.writeAndFlush(new NettyResponse(StateCode.FAIL,ConstantValue.FAIL_RESPONSE,"该NPC不在当前场景".getBytes()));
            return;
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
        channel.writeAndFlush(response);
        return;
    }


}
