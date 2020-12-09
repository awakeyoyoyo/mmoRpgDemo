package com.liqihao.service.impl;

import com.google.protobuf.InvalidProtocolBufferException;
import com.liqihao.commons.MmoCacheCilent;
import com.liqihao.commons.NettyResponse;
import com.liqihao.commons.StateCode;
import com.liqihao.pojo.MmoRole;
import com.liqihao.pojo.baseMessage.SceneMessage;
import com.liqihao.protobufObject.SceneModel;
import com.liqihao.service.SceneService;
import com.liqihao.utils.CommonsUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class SceneServiceImpl implements SceneService {
    private static final Logger log = LoggerFactory.getLogger(SceneServiceImpl.class);

    @Override
    public void askCanResponse(NettyResponse nettyResponse) throws InvalidProtocolBufferException {
        if (nettyResponse.getStateCode()== StateCode.FAIL){
            System.out.println(new String(nettyResponse.getData()));
            return;
        }
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
            System.out.println(new String(nettyResponse.getData()));
            return;
        }
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
        List<SceneModel.RoleDTO> roleDTOS=wentResponse.getRoleDTOList();
        HashMap<Integer,MmoRole> roles=new HashMap<>();
        for (SceneModel.RoleDTO roleDTO:roleDTOS) {
            MmoRole mmoRole=new MmoRole();
            mmoRole.setId(roleDTO.getId());
            mmoRole.setBlood(roleDTO.getBlood());
            mmoRole.setName(roleDTO.getName());
            mmoRole.setNowBlood(roleDTO.getNowBlood());
            mmoRole.setSkillIdList(roleDTO.getSkillIdListList());
            mmoRole.setMp(roleDTO.getMp());
            mmoRole.setOnstatus(roleDTO.getOnStatus());
            mmoRole.setStatus(roleDTO.getStatus());
            mmoRole.setType(roleDTO.getType());
            roles.put(mmoRole.getId(),mmoRole);
        }
        //本地缓存设置当前场景角色
        MmoCacheCilent.getInstance().setRoleHashMap(roles);
        //本地缓存设置当前的场景
        SceneMessage m=MmoCacheCilent.getInstance().getSceneMessageConcurrentHashMap().get(mmoScene);
        MmoCacheCilent.getInstance().setNowSceneId(mmoScene);
        log.info("已进入下一个场景");
        log.info("当前场景是: "+m.getPlaceName());
        log.info("当前场景角色数量是： "+(MmoCacheCilent.getInstance().getRoleHashMap().size()+1) );
        log.info("---------------------------------------------------");
    }


    @Override
    public void findAllRolesResponse(NettyResponse nettyResponse) throws InvalidProtocolBufferException {
        if (nettyResponse.getStateCode()== StateCode.FAIL){
            System.out.println(new String(nettyResponse.getData()));
            return;
        }
        byte[] data=nettyResponse.getData();
        SceneModel.SceneModelMessage myMessage;
        myMessage=SceneModel.SceneModelMessage.parseFrom(data);
        SceneModel.FindAllRolesResponse findAllRolesResponse=myMessage.getFindAllRolesResponse();
        List<SceneModel.RoleDTO> roleDTOS=findAllRolesResponse.getRoleDTOList();
        log.info("当前场景中的所有角色: ");
        HashMap<Integer,MmoRole> roles=new HashMap<>();
        for (SceneModel.RoleDTO roleDTO:roleDTOS) {
            MmoRole mmoRole=new MmoRole();
            mmoRole.setId(roleDTO.getId());
            mmoRole.setBlood(roleDTO.getBlood());
            mmoRole.setName(roleDTO.getName());
            mmoRole.setNowBlood(roleDTO.getNowBlood());
            mmoRole.setSkillIdList(roleDTO.getSkillIdListList());
            mmoRole.setMp(roleDTO.getMp());
            mmoRole.setOnstatus(roleDTO.getOnStatus());
            mmoRole.setStatus(roleDTO.getStatus());
            mmoRole.setType(roleDTO.getType());
            roles.put(mmoRole.getId(),mmoRole);
            log.info("角色id："+roleDTO.getId()+" 角色名: "+roleDTO.getName()+" 类型: "+roleDTO.getType()+" 状态: "+roleDTO.getStatus());
        }
        MmoCacheCilent.getInstance().setRoleHashMap(roles);
        log.info("---------------------------------------------------");
    }

    @Override
    public void talkNPCResponse(NettyResponse nettyResponse) throws InvalidProtocolBufferException {
        if (nettyResponse.getStateCode()== StateCode.FAIL){
            System.out.println(new String(nettyResponse.getData()));
            return;
        }
        byte[] data=nettyResponse.getData();
        SceneModel.SceneModelMessage myMessage;
        myMessage=SceneModel.SceneModelMessage.parseFrom(data);
        SceneModel.TalkNPCResponse talkNPCResponse=myMessage.getTalkNPCResponse();
        Integer npcId=talkNPCResponse.getNpcId();
        List<String> talks=CommonsUtil.splitToStringList(MmoCacheCilent.getInstance().getNpcMessageConcurrentHashMap().get(npcId).getTalk());
        String name=MmoCacheCilent.getInstance().getNpcMessageConcurrentHashMap().get(npcId).getName();
        for (String s:talks) {
            System.out.println(name+"： "+s);
        }
    }
}
