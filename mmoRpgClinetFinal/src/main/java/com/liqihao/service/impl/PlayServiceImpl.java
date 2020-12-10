package com.liqihao.service.impl;

import com.google.protobuf.InvalidProtocolBufferException;
import com.liqihao.commons.*;
import com.liqihao.commons.enums.*;
import com.liqihao.pojo.MmoRole;
import com.liqihao.pojo.baseMessage.SceneMessage;
import com.liqihao.pojo.baseMessage.SkillMessage;
import com.liqihao.protobufObject.PlayModel;
import com.liqihao.service.PlayService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class PlayServiceImpl implements PlayService {
    private static final Logger log = LoggerFactory.getLogger(PlayServiceImpl.class);

    @Override
    public void loginResponse(NettyResponse nettyResponse) throws InvalidProtocolBufferException {
        if (nettyResponse.getStateCode()== StateCode.FAIL){
            System.out.println(new String(nettyResponse.getData()));
            return;
        }
        byte[] data=nettyResponse.getData();
        if (nettyResponse.getStateCode()== StateCode.FAIL){
            String s = new String(nettyResponse.getData());
            log.info(s);
            return;
        }
        PlayModel.PlayModelMessage myMessage;
        myMessage=PlayModel.PlayModelMessage.parseFrom(data);
        PlayModel.LoginResponse loginResponse=myMessage.getLoginResponse();
        PlayModel.RoleDTO roleDTO=loginResponse.getRoleDto();
        Integer mmoScene=loginResponse.getSceneId();
        //将角色存储客户端缓存中
        MmoCacheCilent.getInstance().setNowSceneId(mmoScene);
        //将角色信息存储客户端缓存
        MmoRole mmoRole=new MmoRole();
        mmoRole.setId(roleDTO.getId());
        mmoRole.setBlood(roleDTO.getBlood());
        mmoRole.setName(roleDTO.getName());
        mmoRole.setNowBlood(roleDTO.getNowBlood());
        mmoRole.setNowMp(roleDTO.getNowMp());
//        mmoRole.setCdMap(roleDTO.get);
//        mmoRole.setMmosceneid(roleDTO.g);
        mmoRole.setSkillIdList(roleDTO.getSkillIdListList());
        mmoRole.setMp(roleDTO.getMp());
        mmoRole.setOnstatus(roleDTO.getOnStatus());
        mmoRole.setStatus(roleDTO.getStatus());
        mmoRole.setType(roleDTO.getType());
        MmoCacheCilent.getInstance().setNowRole(mmoRole);
        //将当前场景存入客户端缓存中
        //构建mmoscene对象
        SceneMessage s=MmoCacheCilent.getInstance().getSceneMessageConcurrentHashMap().get(mmoScene);
        //打印当前场景
        log.info("角色id："+roleDTO.getId()+" 角色名: "+roleDTO.getName()
                +" 类型: "+ RoleTypeCode.getValue(roleDTO.getType())+" 状态: "+ RoleStatusCode.getValue(roleDTO.getStatus())
                +" 血量： "+roleDTO.getNowBlood()+"/"+roleDTO.getBlood()+" 蓝量： "+roleDTO.getNowMp()+"/"+roleDTO.getMp());
        log.info("当前场景: "+s.getPlaceName());
        log.info("---------------------------------------------------");
    }

    @Override
    public void registerResponse(NettyResponse nettyResponse) throws InvalidProtocolBufferException {
        if (nettyResponse.getStateCode()== StateCode.FAIL){
            System.out.println(new String(nettyResponse.getData()));
            return;
        }
        byte[] data=nettyResponse.getData();
        PlayModel.PlayModelMessage myMessage;
        myMessage=PlayModel.PlayModelMessage.parseFrom(data);
        PlayModel.RegisterResponse registerResponse=myMessage.getRegisterResponse();
        String msg=registerResponse.getMessage();
        int code =registerResponse.getStateCode();
        log.info("code: "+code+" message: "+msg);
        log.info("---------------------------------------------------");
    }

    @Override
    public void logoutResponse(NettyResponse nettyResponse) throws InvalidProtocolBufferException {
        if (nettyResponse.getStateCode()== StateCode.FAIL){
            System.out.println(new String(nettyResponse.getData()));
            return;
        }
        byte[] data=nettyResponse.getData();
        PlayModel.PlayModelMessage myMessage;
        myMessage=PlayModel.PlayModelMessage.parseFrom(data);
        PlayModel.LogoutResponse logoutResponse=myMessage.getLogoutResponse();
        String msg=logoutResponse.getMxg();
        int code =logoutResponse.getCode();
        log.info("code: "+code+" message: "+msg);
        log.info("---------------------------------------------------");
    }

    @Override
    public void useSkillResponse(NettyResponse nettyResponse) throws InvalidProtocolBufferException {
        if (nettyResponse.getStateCode()== StateCode.FAIL){
            System.out.println(new String(nettyResponse.getData()));
            return;
        }
        byte[] data=nettyResponse.getData();
        PlayModel.PlayModelMessage myMessage;
        myMessage=PlayModel.PlayModelMessage.parseFrom(data);
        PlayModel.UseSkillResponse useSkillResponse=myMessage.getUseSkillResponse();
        List<PlayModel.RoleIdDamage> roleIdDamages=useSkillResponse.getRoleIdDamagesList();
        for (PlayModel.RoleIdDamage roleIdDamage: roleIdDamages) {
            if (roleIdDamage.getFromRoleId()==roleIdDamage.getToRoleId()){
                //施术者
                MmoRole mmoRole=MmoCacheCilent.getInstance().getNowRole();
                //判断减少的数据是啥
                if(roleIdDamage.getDamageType()==DamageTypeCode.HP.getCode()){
                    mmoRole.setNowBlood(roleIdDamage.getNowblood());
                }else if (roleIdDamage.getDamageType()==DamageTypeCode.MP.getCode()){
                    mmoRole.setNowMp(roleIdDamage.getMp());
                }
                SkillMessage skillMessage=MmoCacheCilent.getInstance().getSkillMessageConcurrentHashMap().get(roleIdDamage.getSkillId());
                log.info("角色id："+mmoRole.getId()+" 角色名: "+mmoRole.getName()
                        +" 类型: "+ RoleTypeCode.getValue(mmoRole.getType())+" 状态: "+ RoleStatusCode.getValue(mmoRole.getStatus())
                        +" 血量： "+mmoRole.getNowBlood()+"/"+mmoRole.getBlood()+" 蓝量： "+mmoRole.getNowMp()+"/"+mmoRole.getMp()
                        +" 使用了技能："+skillMessage.getSkillName()+" 减少了： "+DamageTypeCode.getValue(roleIdDamage.getDamageType())+"-"+
                        roleIdDamage.getDamage());
            }else{
                //受伤
                MmoRole mmoRole=MmoCacheCilent.getInstance().getRoleHashMap().get(roleIdDamage.getToRoleId());
                //判断减少的数据是啥
                if(roleIdDamage.getDamageType()==DamageTypeCode.HP.getCode()){
                    mmoRole.setNowBlood(roleIdDamage.getNowblood());
                }else if (roleIdDamage.getDamageType()==DamageTypeCode.MP.getCode()){
                    mmoRole.setNowMp(roleIdDamage.getMp());
                }
                SkillMessage skillMessage=MmoCacheCilent.getInstance().getSkillMessageConcurrentHashMap().get(roleIdDamage.getSkillId());
                log.info("角色id："+mmoRole.getId()+" 角色名: "+mmoRole.getName()
                        +" 类型: "+ RoleTypeCode.getValue(mmoRole.getType())+" 状态: "+ RoleStatusCode.getValue(mmoRole.getStatus())
                        +" 血量： "+mmoRole.getNowBlood()+"/"+mmoRole.getBlood()+" 蓝量： "+mmoRole.getNowMp()+"/"+mmoRole.getMp()
                        +" 被：角色Id:"+roleIdDamage.getFromRoleId()+"的"+skillMessage.getSkillName()+"攻击 减少了： "+DamageTypeCode.getValue(roleIdDamage.getDamageType())+"-"+
                        roleIdDamage.getDamage());
            }
        }
        log.info("---------------------------------------------------");
    }

    @Override
    public void damagesNoticeResponse(NettyResponse nettyResponse) throws InvalidProtocolBufferException {
        if (nettyResponse.getStateCode()== StateCode.FAIL){
            System.out.println(new String(nettyResponse.getData()));
            return;
        }
        byte[] data=nettyResponse.getData();
        PlayModel.PlayModelMessage myMessage;
        myMessage=PlayModel.PlayModelMessage.parseFrom(data);
        PlayModel.DamagesNoticeResponse damagesNoticeResponse=myMessage.getDamagesNoticeResponse();
        PlayModel.RoleIdDamage roleIdDamage=damagesNoticeResponse.getRoleIdDamage();
        MmoRole mmoRole=MmoCacheCilent.getInstance().getNowRole();
        HashMap<Integer, MmoRole> roleHashMap=MmoCacheCilent.getInstance().getRoleHashMap();
        if (mmoRole.getId()==roleIdDamage.getToRoleId()){
            if(roleIdDamage.getDamageType()==DamageTypeCode.HP.getCode()){
                mmoRole.setNowBlood(roleIdDamage.getNowblood());
            }else if (roleIdDamage.getDamageType()==DamageTypeCode.MP.getCode()){
                mmoRole.setNowMp(roleIdDamage.getMp());
            }
            if (roleIdDamage.getAttackStyle()== AttackStyleCode.AUTORE.getCode()) {
                log.info("角色id：" + mmoRole.getId() + " 角色名: " + mmoRole.getName()
                        + " 类型: " + RoleTypeCode.getValue(mmoRole.getType()) + " 状态: " + RoleStatusCode.getValue(mmoRole.getStatus())
                        + " 血量： " + mmoRole.getNowBlood() + "/" + mmoRole.getBlood() + " 蓝量： " + mmoRole.getNowMp() + "/" + mmoRole.getMp()
                        + " 恢复：" + DamageTypeCode.getValue(roleIdDamage.getDamageType()) +" 恢复了"+
                        roleIdDamage.getDamage());
            }else if (roleIdDamage.getAttackStyle()== AttackStyleCode.BUFFER.getCode()){
                //todo
            }
        }
    }

}
