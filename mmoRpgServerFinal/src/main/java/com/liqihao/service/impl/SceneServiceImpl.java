package com.liqihao.service.impl;

import com.google.protobuf.InvalidProtocolBufferException;
import com.liqihao.cache.NpcMessageCache;
import com.liqihao.cache.OnlineRoleMessageCache;
import com.liqihao.cache.SceneBeanMessageCache;
import com.liqihao.annotation.HandlerCmdTag;
import com.liqihao.annotation.HandlerServiceTag;
import com.liqihao.commons.*;
import com.liqihao.commons.StateCode;
import com.liqihao.commons.enums.TaskTargetTypeCode;
import com.liqihao.pojo.bean.*;
import com.liqihao.pojo.bean.roleBean.BossBean;
import com.liqihao.pojo.bean.roleBean.MmoSimpleNPC;
import com.liqihao.pojo.bean.roleBean.MmoSimpleRole;
import com.liqihao.pojo.bean.roleBean.Role;
import com.liqihao.pojo.bean.taskBean.talkTask.TalkTaskAction;
import com.liqihao.protobufObject.SceneModel;
import com.liqihao.provider.CopySceneProvider;
import com.liqihao.service.SceneService;
import com.liqihao.util.CommonsUtil;
import com.liqihao.util.NotificationUtil;
import io.netty.channel.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 场景模块
 *
 * @author lqhao
 */
@Service
@HandlerServiceTag(protobufModel = "SceneModel$SceneModelMessage")
public class SceneServiceImpl implements SceneService {
    private static final Logger log = LoggerFactory.getLogger(SceneServiceImpl.class);


    @Override
    @HandlerCmdTag(cmd = ConstantValue.WENT_REQUEST, module = ConstantValue.SCENE_MODULE)
    public void wentRequest(SceneModel.SceneModelMessage myMessage, MmoSimpleRole mmoSimpleRole) throws RpgServerException {
        Integer nextSceneId = myMessage.getWentRequest().getSceneId();
        Channel channel = mmoSimpleRole.getChannel();
        //先查询playId所在场景
        //查询该场景可进入的场景与sceneId判断
        Integer nowSceneId = mmoSimpleRole.getMmoSceneId();
        SceneBean sceneBean = SceneBeanMessageCache.getInstance().get(nowSceneId);
        List<Integer> canScene = sceneBean.getCanScenes();
        boolean canFlag = false;
        for (Integer id : canScene) {
            if (id.equals(nextSceneId)) {
                canFlag = true;
                break;
            }
        }
        if (!canFlag) {
            //不包含 即不可进入
            throw new RpgServerException(StateCode.FAIL,"无法前往该场景");
        }

        //进入场景，修改数据库 player 和scene
        List<Role> nextSceneRoles = mmoSimpleRole.wentScene(nextSceneId);
        //ptotobuf生成wentResponse
        //生成response返回
        NettyResponse nettyResponse = new NettyResponse();
        nettyResponse.setCmd(ConstantValue.WENT_RESPONSE);
        nettyResponse.setStateCode(StateCode.SUCCESS);
        SceneModel.SceneModelMessage.Builder builder = SceneModel.SceneModelMessage.newBuilder();
        builder.setDataType(SceneModel.SceneModelMessage.DateType.WentResponse);
        SceneModel.WentResponse.Builder wentResponseBuilder = SceneModel.WentResponse.newBuilder();
        //simpleRole
        List<SceneModel.RoleDTO> roleDTOS = new ArrayList<>();
        for (Role mmoRole : nextSceneRoles) {
            SceneModel.RoleDTO.Builder msr = CommonsUtil.roleToSceneModelRoleDto(mmoRole);
            roleDTOS.add(msr.build());
        }
        wentResponseBuilder.setSceneId(nextSceneId);
        wentResponseBuilder.addAllRoleDTO(roleDTOS);
        builder.setWentResponse(wentResponseBuilder.build());
        byte[] data2 = builder.build().toByteArray();
        nettyResponse.setData(data2);
        //send
        NotificationUtil.sendMessage(channel,nettyResponse,wentResponseBuilder);
    }

    @Override
    @HandlerCmdTag(cmd = ConstantValue.FIND_ALL_ROLES_REQUEST, module = ConstantValue.SCENE_MODULE)
    public void findAllRolesRequest(SceneModel.SceneModelMessage myMessage, MmoSimpleRole mmoSimpleRole) throws InvalidProtocolBufferException {
        Channel channel = mmoSimpleRole.getChannel();
        Integer sceneId = mmoSimpleRole.getMmoSceneId();

        List<Role> sceneRoles = new ArrayList<>();
        if (sceneId != null) {
            SceneBean sceneBean = SceneBeanMessageCache.getInstance().get(sceneId);
            //NPC
            for (Integer id : sceneBean.getNpcs()) {
                MmoSimpleNPC temp = NpcMessageCache.getInstance().get(id);
                MmoSimpleRole roleTemp = mmoSimpleNpcToMmoSimpleRole(temp);
                sceneRoles.add(roleTemp);
            }
            //ROLES
            for (Integer id : sceneBean.getRoles()) {
                MmoSimpleRole temp = OnlineRoleMessageCache.getInstance().get(id);
                sceneRoles.add(temp);
            }
            //helper
            if (sceneBean.getHelperBeans().size() > 0) {
                sceneRoles.addAll(sceneBean.getHelperBeans());
            }
        } else {
            //在副本中
            Integer copySceneBeanId = mmoSimpleRole.getCopySceneBeanId();
            CopySceneBean copySceneBean = CopySceneProvider.getCopySceneBeanById(copySceneBeanId);
            sceneRoles.addAll(copySceneBean.getRoles());
            BossBean bossBean = copySceneBean.getNowBoss();
            sceneRoles.add(bossBean);
        }
        //protobuf
        SceneModel.SceneModelMessage.Builder messageDataBuilder = SceneModel.SceneModelMessage.newBuilder();
        messageDataBuilder.setDataType(SceneModel.SceneModelMessage.DateType.FindAllRolesResponse);
        SceneModel.FindAllRolesResponse.Builder findAllRolesResponseBuilder = SceneModel.FindAllRolesResponse.newBuilder();
        List<SceneModel.RoleDTO> roleDTOS = new ArrayList<>();
        for (Role m : sceneRoles) {
            SceneModel.RoleDTO.Builder msr = CommonsUtil.roleToSceneModelRoleDto(m);
            roleDTOS.add(msr.build());
        }
        findAllRolesResponseBuilder.addAllRoleDTO(roleDTOS);
        messageDataBuilder.setFindAllRolesResponse(findAllRolesResponseBuilder.build());
        byte[] data2 = messageDataBuilder.build().toByteArray();
        NettyResponse nettyResponse = new NettyResponse();
        nettyResponse.setCmd(ConstantValue.FIND_ALL_ROLES_RESPONSE);
        nettyResponse.setStateCode(200);
        nettyResponse.setData(data2);
        //send
        NotificationUtil.sendMessage(channel,nettyResponse,messageDataBuilder);
    }



    @Override
    @HandlerCmdTag(cmd = ConstantValue.TALK_NPC_REQUEST, module = ConstantValue.SCENE_MODULE)
    public void talkNpcRequest(SceneModel.SceneModelMessage myMessage, MmoSimpleRole mmoSimpleRole) throws RpgServerException {
        Channel channel = mmoSimpleRole.getChannel();
        Integer npcId = myMessage.getTalkNPCRequest().getRoleId();
        //缓存中获取NPC
        MmoSimpleNPC npc = NpcMessageCache.getInstance().get(npcId);
        if (!npc.getMmoSceneId().equals(mmoSimpleRole.getMmoSceneId())) {
            throw new RpgServerException(StateCode.FAIL,"该NPC不在当前场景");
        }
        //任务条件触发
        TalkTaskAction taskAction=new TalkTaskAction();
        taskAction.setRoleId(npcId);
        taskAction.setTaskTargetType(TaskTargetTypeCode.TALK.getCode());
        mmoSimpleRole.getTaskManager().handler(taskAction,mmoSimpleRole);
        //protobuf
        SceneModel.SceneModelMessage messageData;
        messageData = SceneModel.SceneModelMessage.newBuilder()
                .setDataType(SceneModel.SceneModelMessage.DateType.TalkNPCResponse)
                .setTalkNPCResponse(SceneModel.TalkNPCResponse.newBuilder().setNpcId(npcId).build()).build();
        NettyResponse response = new NettyResponse();
        response.setCmd(ConstantValue.TALK_NPC_RESPONSE);
        response.setStateCode(StateCode.SUCCESS);
        byte[] data2 = messageData.toByteArray();
        response.setData(data2);
        //send
        NotificationUtil.sendMessage(channel,response,messageData.toBuilder());
    }



    private MmoSimpleRole mmoSimpleNpcToMmoSimpleRole(MmoSimpleNPC temp) {
        MmoSimpleRole roleTemp = new MmoSimpleRole();
        roleTemp.setId(temp.getId());
        roleTemp.setName(temp.getName());
        roleTemp.setStatus(temp.getStatus());
        roleTemp.setType(temp.getType());
        roleTemp.setOnStatus(temp.getOnStatus());
        roleTemp.setHp(temp.getHp());
        roleTemp.setNowHp(temp.getNowHp());
        roleTemp.setMp(temp.getMp());
        roleTemp.setLevel(temp.getLevel());
        roleTemp.setEquipmentLevel(temp.getEquipmentLevel());
        roleTemp.setNowMp(temp.getNowMp());
        roleTemp.setMmoSceneId(temp.getMmoSceneId());
        roleTemp.setAttack(temp.getAttack());
        roleTemp.setDamageAdd(temp.getDamageAdd());
        return roleTemp;
    }
}
