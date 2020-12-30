package com.liqihao.service.impl;

import com.google.protobuf.InvalidProtocolBufferException;
import com.liqihao.commons.*;
import com.liqihao.commons.enums.*;
import com.liqihao.pojo.MmoRole;
import com.liqihao.pojo.baseMessage.BufferMessage;
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
        if (nettyResponse.getStateCode() == StateCode.FAIL) {
            System.out.println(new String(nettyResponse.getData()));
            return;
        }
        byte[] data = nettyResponse.getData();
        if (nettyResponse.getStateCode() == StateCode.FAIL) {
            String s = new String(nettyResponse.getData());
            log.info(s);
            return;
        }
        PlayModel.PlayModelMessage myMessage;
        myMessage = PlayModel.PlayModelMessage.parseFrom(data);
        PlayModel.LoginResponse loginResponse = myMessage.getLoginResponse();
        PlayModel.RoleDTO roleDTO = loginResponse.getRoleDto();
        Integer mmoScene = loginResponse.getSceneId();
        //将角色存储客户端缓存中
        MmoCacheCilent.getInstance().setNowSceneId(mmoScene);
        //将角色信息存储客户端缓存
        MmoRole mmoRole = new MmoRole();
        mmoRole.setId(roleDTO.getId());
        mmoRole.setBlood(roleDTO.getBlood());
        mmoRole.setName(roleDTO.getName());
        mmoRole.setNowBlood(roleDTO.getNowBlood());
        mmoRole.setNowMp(roleDTO.getNowMp());
        mmoRole.setSkillIdList(roleDTO.getSkillIdListList());
        mmoRole.setMp(roleDTO.getMp());
        mmoRole.setOnstatus(roleDTO.getOnStatus());
        mmoRole.setStatus(roleDTO.getStatus());
        mmoRole.setType(roleDTO.getType());
        MmoCacheCilent.getInstance().setNowRole(mmoRole);
        //将当前场景存入客户端缓存中
        //构建mmoscene对象
        SceneMessage s = MmoCacheCilent.getInstance().getSceneMessageConcurrentHashMap().get(mmoScene);
        //打印当前场景
        System.out.println("[-]--------------------------------------------------------");
        System.out.println("[-]当前场景: " + s.getPlaceName());
        System.out.println("[-]当前角色信息: ");
        System.out.println("[-]角色id：" + roleDTO.getId() + " 角色名: " + roleDTO.getName());
        System.out.println("[-]类型: " + RoleTypeCode.getValue(roleDTO.getType()) + " 状态: " + RoleStatusCode.getValue(roleDTO.getStatus()));
        System.out.println("[-]血量： " + roleDTO.getNowBlood() + "/" + roleDTO.getBlood());
        System.out.println("[-]蓝量： " + roleDTO.getNowMp() + "/" + roleDTO.getMp());
        System.out.println("[-]攻击力： "+roleDTO.getAttack()+" 技能伤害加成: "+roleDTO.getAttackAdd());
        System.out.println("[-]所在队伍id："+roleDTO.getTeamId());
        System.out.println("[-]--------------------------------------------------------");

    }

    @Override
    public void registerResponse(NettyResponse nettyResponse) throws InvalidProtocolBufferException {
        if (nettyResponse.getStateCode() == StateCode.FAIL) {
            System.out.println(new String(nettyResponse.getData()));
            return;
        }
        byte[] data = nettyResponse.getData();
        PlayModel.PlayModelMessage myMessage;
        myMessage = PlayModel.PlayModelMessage.parseFrom(data);
        PlayModel.RegisterResponse registerResponse = myMessage.getRegisterResponse();
        String msg = registerResponse.getMessage();
        int code = registerResponse.getStateCode();
        System.out.println("[-]--------------------------------------------------------");
        System.out.println("[-]code："+code);
        System.out.println("[-]响应："+msg);
        System.out.println("[-]--------------------------------------------------------");
    }

    @Override
    public void logoutResponse(NettyResponse nettyResponse) throws InvalidProtocolBufferException {
        if (nettyResponse.getStateCode() == StateCode.FAIL) {
            System.out.println(new String(nettyResponse.getData()));
            return;
        }
        byte[] data = nettyResponse.getData();
        PlayModel.PlayModelMessage myMessage;
        myMessage = PlayModel.PlayModelMessage.parseFrom(data);
        PlayModel.LogoutResponse logoutResponse = myMessage.getLogoutResponse();
        String msg = logoutResponse.getMxg();
        int code = logoutResponse.getCode();
        System.out.println("[-]--------------------------------------------------------");
        System.out.println("[-]code："+code);
        System.out.println("[-]响应："+msg);
        System.out.println("[-]--------------------------------------------------------");
    }

    @Override
    public void useSkillResponse(NettyResponse nettyResponse) throws InvalidProtocolBufferException {
        if (nettyResponse.getStateCode() == StateCode.FAIL) {
            System.out.println(new String(nettyResponse.getData()));
            return;
        }
        byte[] data = nettyResponse.getData();
        PlayModel.PlayModelMessage myMessage;
        myMessage = PlayModel.PlayModelMessage.parseFrom(data);
        PlayModel.UseSkillResponse useSkillResponse = myMessage.getUseSkillResponse();
        List<PlayModel.RoleIdDamage> roleIdDamages = useSkillResponse.getRoleIdDamagesList();
        System.out.println("[-]--------------------------------------------------------");
        for (PlayModel.RoleIdDamage roleIdDamage : roleIdDamages) {
            if (roleIdDamage.getFromRoleId() == roleIdDamage.getToRoleId()) {
                //施术者
                MmoRole mmoRole = MmoCacheCilent.getInstance().getNowRole();
                //判断减少的数据是啥
                mmoRole.setNowBlood(roleIdDamage.getNowblood());
                mmoRole.setNowMp(roleIdDamage.getMp());
                mmoRole.setStatus(roleIdDamage.getState());
                SkillMessage skillMessage = MmoCacheCilent.getInstance().getSkillMessageConcurrentHashMap().get(roleIdDamage.getSkillId());
                System.out.println("[-]角色id：" + mmoRole.getId() + " 角色名: " + mmoRole.getName());
                System.out.println("[-]类型: " + RoleTypeCode.getValue(mmoRole.getType()) + " 状态: " + RoleStatusCode.getValue(mmoRole.getStatus()));
                System.out.println("[-]血量： " + mmoRole.getNowBlood() + "/" + mmoRole.getBlood());
                System.out.println("[-]蓝量： " + mmoRole.getNowMp() + "/" + mmoRole.getMp());
                System.out.println("[-]");
                System.out.println("[-]角色:"+mmoRole.getName()+" 潇洒地使用了： "+skillMessage.getSkillName()+" 消耗了： "+roleIdDamage.getDamage()+"点"+DamageTypeCode.getValue(roleIdDamage.getDamageType()));

            } else {
                //受伤
                MmoRole mmoRole = MmoCacheCilent.getInstance().getRoleHashMap().get(roleIdDamage.getToRoleId());
                if (mmoRole == null) {
                    if (roleIdDamage.getToRoleId() == MmoCacheCilent.getInstance().getNowRole().getId()) {
                        mmoRole = MmoCacheCilent.getInstance().getNowRole();
                    }
                }
                //判断减少的数据是啥
                mmoRole.setNowBlood(roleIdDamage.getNowblood());
                mmoRole.setNowMp(roleIdDamage.getMp());
                mmoRole.setStatus(roleIdDamage.getState());
                SkillMessage skillMessage = MmoCacheCilent.getInstance().getSkillMessageConcurrentHashMap().get(roleIdDamage.getSkillId());
                System.out.println("[-]角色id：" + mmoRole.getId() + " 角色名: " + mmoRole.getName());
                System.out.println("[-]类型: " + RoleTypeCode.getValue(mmoRole.getType()) + " 状态: " + RoleStatusCode.getValue(mmoRole.getStatus()));
                System.out.println("[-]血量： " + mmoRole.getNowBlood() + "/" + mmoRole.getBlood());
                System.out.println("[-]蓝量： " + mmoRole.getNowMp() + "/" + mmoRole.getMp());
                System.out.println("[-]");
                System.out.println("[-]角色："+mmoRole.getName()+"被Id为:"+roleIdDamage.getFromRoleId()+"的"+RoleTypeCode.getValue(roleIdDamage.getFromRoleType())+"潇洒地使用了： "+skillMessage.getSkillName()+"给打了一顿");
                System.out.println("[-]减少了"+roleIdDamage.getDamage()+"点"+DamageTypeCode.getValue(roleIdDamage.getDamageType()));
            }
        }
        System.out.println("[-]--------------------------------------------------------");
    }

    @Override
    public void damagesNoticeResponse(NettyResponse nettyResponse) throws InvalidProtocolBufferException {
        if (nettyResponse.getStateCode() == StateCode.FAIL) {
            System.out.println(new String(nettyResponse.getData()));
            return;
        }
        byte[] data = nettyResponse.getData();
        PlayModel.PlayModelMessage myMessage;
        myMessage = PlayModel.PlayModelMessage.parseFrom(data);
        PlayModel.DamagesNoticeResponse damagesNoticeResponse = myMessage.getDamagesNoticeResponse();
        PlayModel.RoleIdDamage roleIdDamage = damagesNoticeResponse.getRoleIdDamage();
        //场景中的角色
        MmoRole mmoRole = MmoCacheCilent.getInstance().getNowRole();
        HashMap<Integer, MmoRole> roleHashMap = MmoCacheCilent.getInstance().getRoleHashMap();
        if (mmoRole.getId() != roleIdDamage.getToRoleId()) {
            mmoRole = roleHashMap.get(roleIdDamage.getToRoleId());
        }

        mmoRole.setNowBlood(roleIdDamage.getNowblood());
        mmoRole.setStatus(roleIdDamage.getState());
        mmoRole.setNowMp(roleIdDamage.getMp());
        System.out.println("[-]--------------------------------------------------------");
        if (roleIdDamage.getAttackStyle() == AttackStyleCode.AUTORE.getCode()) {
            System.out.println("[-]角色id：" + mmoRole.getId() + " 角色名: " + mmoRole.getName());
            System.out.println("[-]类型: " + RoleTypeCode.getValue(mmoRole.getType()) + " 状态: " + RoleStatusCode.getValue(mmoRole.getStatus()));
            System.out.println("[-]血量： " + mmoRole.getNowBlood() + "/" + mmoRole.getBlood());
            System.out.println("[-]蓝量： " + mmoRole.getNowMp() + "/" + mmoRole.getMp());
            System.out.println("[-]");
            System.out.println("[-]角色："+mmoRole.getName()+"靠着胡思乱想恢复了"+roleIdDamage.getDamage()+"点"+DamageTypeCode.getValue(roleIdDamage.getDamageType()));
        } else if (roleIdDamage.getAttackStyle() == AttackStyleCode.BUFFER.getCode()) {
            BufferMessage bufferMessage = MmoCacheCilent.getInstance().getBufferMessageConcurrentHashMap().get(roleIdDamage.getBufferId());
            System.out.println("[-]角色id：" + mmoRole.getId() + " 角色名: " + mmoRole.getName());
            System.out.println("[-]类型: " + RoleTypeCode.getValue(mmoRole.getType()) + " 状态: " + RoleStatusCode.getValue(mmoRole.getStatus()));
            System.out.println("[-]血量： " + mmoRole.getNowBlood() + "/" + mmoRole.getBlood());
            System.out.println("[-]蓝量： " + mmoRole.getNowMp() + "/" + mmoRole.getMp());
            System.out.println("[-]");
            System.out.println("[-]角色："+mmoRole.getName()+"收到了buffer名为："+bufferMessage.getName()+"的影响");
            System.out.println("[-]减少了"+roleIdDamage.getDamage()+"点"+DamageTypeCode.getValue(roleIdDamage.getDamageType()));
        } else if (roleIdDamage.getAttackStyle() == AttackStyleCode.MEDICENE.getCode()) {
            System.out.println("[-]角色id：" + mmoRole.getId() + " 角色名: " + mmoRole.getName());
            System.out.println("[-]类型: " + RoleTypeCode.getValue(mmoRole.getType()) + " 状态: " + RoleStatusCode.getValue(mmoRole.getStatus()));
            System.out.println("[-]血量： " + mmoRole.getNowBlood() + "/" + mmoRole.getBlood());
            System.out.println("[-]蓝量： " + mmoRole.getNowMp() + "/" + mmoRole.getMp());
            System.out.println("[-]");
            System.out.println("[-]角色："+mmoRole.getName()+" 反手恰了一口药恢复了"+roleIdDamage.getDamage()+"点"+DamageTypeCode.getValue(roleIdDamage.getDamageType()));
            System.out.println("[-]减少了"+roleIdDamage.getDamage()+"点"+DamageTypeCode.getValue(roleIdDamage.getDamageType()));
        }else if (roleIdDamage.getAttackStyle() == AttackStyleCode.ATTACK.getCode()){
            SkillMessage skillMessage = MmoCacheCilent.getInstance().getSkillMessageConcurrentHashMap().get(roleIdDamage.getSkillId());
            System.out.println("[-]角色id：" + mmoRole.getId() + " 角色名: " + mmoRole.getName());
            System.out.println("[-]类型: " + RoleTypeCode.getValue(mmoRole.getType()) + " 状态: " + RoleStatusCode.getValue(mmoRole.getStatus()));
            System.out.println("[-]血量： " + mmoRole.getNowBlood() + "/" + mmoRole.getBlood());
            System.out.println("[-]蓝量： " + mmoRole.getNowMp() + "/" + mmoRole.getMp());
            System.out.println("[-]");
            System.out.println("[-]角色："+mmoRole.getName()+"被Id为:"+roleIdDamage.getFromRoleId()+"的"+RoleTypeCode.getValue(roleIdDamage.getFromRoleType())+"潇洒地使用了： "+skillMessage.getSkillName()+"给打了一顿");
            System.out.println("[-]减少了"+roleIdDamage.getDamage()+"点"+DamageTypeCode.getValue(roleIdDamage.getDamageType()));
        }
        System.out.println("[-]--------------------------------------------------------");
    }

}
