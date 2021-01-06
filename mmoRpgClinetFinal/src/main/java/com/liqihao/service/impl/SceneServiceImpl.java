package com.liqihao.service.impl;

import com.google.protobuf.InvalidProtocolBufferException;
import com.liqihao.commons.*;
import com.liqihao.commons.enums.RoleStatusCode;
import com.liqihao.commons.enums.RoleTypeCode;
import com.liqihao.commons.enums.StateCode;
import com.liqihao.pojo.MmoRole;
import com.liqihao.pojo.baseMessage.ProfessionMessage;
import com.liqihao.pojo.baseMessage.SceneMessage;
import com.liqihao.protobufObject.SceneModel;
import com.liqihao.service.SceneService;
import com.liqihao.utils.CommonsUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class SceneServiceImpl implements SceneService {
    private static final Logger log = LoggerFactory.getLogger(SceneServiceImpl.class);


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
            mmoRole.setNowMp(roleDTO.getNowMp());
            mmoRole.setOnstatus(roleDTO.getOnStatus());
            mmoRole.setStatus(roleDTO.getStatus());
            mmoRole.setType(roleDTO.getType());
            roles.put(mmoRole.getId(),mmoRole);
        }
        //本地缓存设置当前场景角色
        MmoCacheCilent.getInstance().setRoleHashMap(roles);
        //本地缓存设置当前的场景
        SceneMessage m=MmoCacheCilent.getInstance().getSceneMessageConcurrentHashMap().get(mmoScene);
        ConcurrentHashMap<Integer, ProfessionMessage> p=MmoCacheCilent.getInstance().getProfessionMessageConcurrentHashMap();
        MmoCacheCilent.getInstance().setNowSceneId(mmoScene);
        System.out.println("[-]--------------------------------------------------------");
        System.out.println("[-]当前场景是: "+m.getPlaceName());
        System.out.println("[-]当前场景角色数量是: "+roleDTOS.size());
        System.out.println("[-][-]当前场景角色有: ");
        for (SceneModel.RoleDTO roleDTO:roleDTOS) {
            System.out.println("[-]");
            System.out.println("[-][-]角色id：" + roleDTO.getId() + " 角色名: " + roleDTO.getName());
            System.out.println("[-][-]类型: " + RoleTypeCode.getValue(roleDTO.getType()) + " 状态: " + RoleStatusCode.getValue(roleDTO.getStatus()));
            if (roleDTO.getType()==RoleTypeCode.PLAYER.getCode()){
                System.out.println("[-][-]职业："+p.get(roleDTO.getProfessionId()).getName());

            }
            System.out.println("[-][-]血量： " + roleDTO.getNowBlood() + "/" + roleDTO.getBlood());
            System.out.println("[-][-]蓝量： " + roleDTO.getNowMp() + "/" + roleDTO.getMp());
            System.out.println("[-][-]攻击力： "+roleDTO.getAttack()+" 技能伤害加成: "+roleDTO.getAttackAdd());
            System.out.println("[-][-]所在队伍id："+roleDTO.getTeamId());
            System.out.println("[-]");
        }
        System.out.println("[-]--------------------------------------------------------");
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
        ConcurrentHashMap<Integer, ProfessionMessage> p=MmoCacheCilent.getInstance().getProfessionMessageConcurrentHashMap();
        System.out.println("[-]--------------------------------------------------------");
        System.out.println("[-][-]当前场景角色有: ");
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
            mmoRole.setNowMp(roleDTO.getNowMp());
            mmoRole.setType(roleDTO.getType());
            roles.put(mmoRole.getId(),mmoRole);
            System.out.println("[-]");
            System.out.println("[-][-]角色id：" + roleDTO.getId() + " 角色名: " + roleDTO.getName());
            if (roleDTO.getType()==RoleTypeCode.PLAYER.getCode()){
                System.out.println("[-][-]职业："+p.get(roleDTO.getProfessionId()).getName());

            }
            System.out.println("[-][-]类型: " + RoleTypeCode.getValue(roleDTO.getType()) + " 状态: " + RoleStatusCode.getValue(roleDTO.getStatus()));
            System.out.println("[-][-]血量： " + roleDTO.getNowBlood() + "/" + roleDTO.getBlood());
            System.out.println("[-][-]蓝量： " + roleDTO.getNowMp() + "/" + roleDTO.getMp());
            System.out.println("[-][-]攻击力： "+roleDTO.getAttack()+" 技能伤害加成: "+roleDTO.getAttackAdd());
            System.out.println("[-][-]所在队伍id："+roleDTO.getTeamId());
            System.out.println("[-]");
        }
        MmoCacheCilent.getInstance().setRoleHashMap(roles);
        System.out.println("[-]--------------------------------------------------------");
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
        System.out.println("[-]--------------------------------------------------------");
        for (String s:talks) {
            System.out.println("[-]"+name+"不急不慢的说："+s);
        }
        System.out.println("[-]--------------------------------------------------------");
    }
}
