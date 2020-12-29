package com.liqihao.service.impl;

import com.google.protobuf.InvalidProtocolBufferException;
import com.liqihao.commons.MmoCacheCilent;
import com.liqihao.commons.NettyResponse;
import com.liqihao.commons.enums.RoleStatusCode;
import com.liqihao.pojo.baseMessage.CopySceneMessage;
import com.liqihao.protobufObject.CopySceneModel;
import com.liqihao.service.CopySceneService;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class CopySceneServiceImpl implements CopySceneService {
    @Override
    public void askCanCopySceneResponse(NettyResponse nettyResponse) throws InvalidProtocolBufferException {
        byte[] data = nettyResponse.getData();
        CopySceneModel.CopySceneModelMessage myMessage;
        myMessage = CopySceneModel.CopySceneModelMessage.parseFrom(data);
        List<Integer> copySceneIds = myMessage.getAskCanCopySceneResponse().getCopySceneIdList();
        System.out.println("[-]--------------------------------------------------------");
        System.out.println("[-]所能进入得副本：");
        for (Integer id : copySceneIds) {
            CopySceneMessage copySceneMessage = MmoCacheCilent.getInstance().getCopySceneMessageConcurrentHashMap().get(id);
            System.out.println("[-]副本id：" + copySceneMessage.getId());
            System.out.println("[-]副本名称：" + copySceneMessage.getName());
            System.out.println("[-]副本怪物id：" + copySceneMessage.getBossIds());
            System.out.println("[-]副本攻略时间：" + copySceneMessage.getLastTime() + "秒");
        }
        System.out.println("[-]--------------------------------------------------------");
    }

    @Override
    public void copySceneMessageResponse(NettyResponse nettyResponse) throws InvalidProtocolBufferException {
        byte[] data = nettyResponse.getData();
        CopySceneModel.CopySceneModelMessage myMessage;
        myMessage = CopySceneModel.CopySceneModelMessage.parseFrom(data);
        CopySceneModel.CopySceneBeanDto copySceneBeanDto = myMessage.getCopySceneMessageResponse().getCopySceneBeanDto();
        CopySceneMessage copySceneMessage = MmoCacheCilent.getInstance().getCopySceneMessageConcurrentHashMap().get(copySceneBeanDto.getCopySceneId());
        System.out.println("[-]--------------------------------------------------------");
        System.out.println("[-]副本基本信息id：" + copySceneBeanDto.getCopySceneId());
        System.out.println("[-]副本实例id：" + copySceneBeanDto.getCopySceneBeanId());
        System.out.println("[-]副本名称：" + copySceneMessage.getName());
        System.out.println("[-]副本攻略时间：" + copySceneMessage.getLastTime() + "秒");
        System.out.println("[-]副本剩下时间：" + (copySceneBeanDto.getEndTime() - System.currentTimeMillis()) + "秒");
        System.out.println("[-]副本中的BOSS：");
        for (CopySceneModel.BossBeanDto bossBeanDto : copySceneBeanDto.getBossBeansList()) {
            System.out.println("[-][-]");
            System.out.println("[-][-]Boss基本信息id：" + bossBeanDto.getId());
            System.out.println("[-][-]Boss名称：" + bossBeanDto.getName());
            System.out.println("[-][-]Boss血量：" + bossBeanDto.getNowBlood() + "/" + bossBeanDto.getBlood());
            System.out.println("[-][-]Boss蓝量：" + bossBeanDto.getNowMp() + "/" + bossBeanDto.getMp());
            System.out.println("[-][-]Boss的攻击力：" + bossBeanDto.getAttack());
            System.out.println("[-][-]Boss的状态：" + RoleStatusCode.getValue(bossBeanDto.getRoleStatus()));
            System.out.println("[-][-][-]Boss的的buffer：" + bossBeanDto.getAttack());
            for (CopySceneModel.BufferDto bufferDto : bossBeanDto.getBufferDtosList()) {
                System.out.println("[-][-][-]buffer的Id：" + bufferDto.getId());
                System.out.println("[-][-][-]buffer名称：" + bufferDto.getName());
                long uTime = bufferDto.getCreateTime() + bufferDto.getLastTime() * 1000 - System.currentTimeMillis();
                System.out.println("[-][-][-]buffer剩余时间：" + uTime);
                System.out.println("[-][-][-]buffer来源于角色id：" + bufferDto.getFromRoleId());
            }
            System.out.println("[-][-]");
        }
        System.out.println("[-]副本中的角色：");
        for (CopySceneModel.RoleDto roleDto : copySceneBeanDto.getRoleDtoList()) {
            System.out.println("[-][-]");
            System.out.println("[-][-]角色id：" + roleDto.getId());
            System.out.println("[-][-]角色名称：" + roleDto.getName());
            System.out.println("[-][-]角色血量：" + roleDto.getNowBlood() + "/" + roleDto.getBlood());
            System.out.println("[-][-]角色蓝量：" + roleDto.getNowMp() + "/" + roleDto.getMp());
            System.out.println("[-][-]角色的状态：" + roleDto.getStatus());
            System.out.println("[-][-][-]角色的的buffer：");
            for (CopySceneModel.BufferDto bufferDto : roleDto.getBufferDtosList()) {
                System.out.println("[-][-][-]buffer的Id：" + bufferDto.getId());
                System.out.println("[-][-][-]buffer名称：" + bufferDto.getName());
                long uTime = bufferDto.getCreateTime() + bufferDto.getLastTime() * 1000 - System.currentTimeMillis();
                System.out.println("[-][-][-]buffer剩余时间：" + uTime);
                System.out.println("[-][-][-]buffer来源于角色id：" + bufferDto.getFromRoleId());
            }
            System.out.println("[-][-]");
        }
        System.out.println("[-]--------------------------------------------------------");
    }

    @Override
    public void enterCopySceneResponse(NettyResponse nettyResponse) throws InvalidProtocolBufferException {
        byte[] data = nettyResponse.getData();
        CopySceneModel.CopySceneModelMessage myMessage;
        myMessage = CopySceneModel.CopySceneModelMessage.parseFrom(data);
        CopySceneModel.RoleDto roleDto = myMessage.getEnterCopySceneResponse().getRoleDto();
        System.out.println("[-]--------------------------------------------------------");
        System.out.println("[-]有角色进入了副本：");
        System.out.println("[-]角色id：" + roleDto.getId());
        System.out.println("[-]角色名称：" + roleDto.getName());
        System.out.println("[-]角色血量：" + roleDto.getNowBlood() + "/" + roleDto.getBlood());
        System.out.println("[-]角色蓝量：" + roleDto.getNowMp() + "/" + roleDto.getMp());
        System.out.println("[-]角色的状态：" + roleDto.getStatus());
        System.out.println("[-][-]角色的的buffer：");
        for (CopySceneModel.BufferDto bufferDto : roleDto.getBufferDtosList()) {
            System.out.println("[-][-]buffer的Id：" + bufferDto.getId());
            System.out.println("[-][-]buffer名称：" + bufferDto.getName());
            long uTime = bufferDto.getCreateTime() + bufferDto.getLastTime() * 1000 - System.currentTimeMillis();
            System.out.println("[-][-]buffer剩余时间：" + uTime);
            System.out.println("[-][-]buffer来源于角色id：" + bufferDto.getFromRoleId());
        }
        System.out.println("[-][-]");
        System.out.println("[-]--------------------------------------------------------");
    }

    @Override
    public void exitCopySceneResponse(NettyResponse nettyResponse) throws InvalidProtocolBufferException {
        byte[] data = nettyResponse.getData();
        CopySceneModel.CopySceneModelMessage myMessage;
        myMessage = CopySceneModel.CopySceneModelMessage.parseFrom(data);
        CopySceneModel.RoleDto roleDto = myMessage.getExitCopySceneResponse().getRoleDto();
        System.out.println("[-]--------------------------------------------------------");
        System.out.println("[-]有角色离开了副本：");
        System.out.println("[-]角色id：" + roleDto.getId());
        System.out.println("[-]角色名称：" + roleDto.getName());
        System.out.println("[-]角色血量：" + roleDto.getNowBlood() + "/" + roleDto.getBlood());
        System.out.println("[-]角色蓝量：" + roleDto.getNowMp() + "/" + roleDto.getMp());
        System.out.println("[-]角色的状态：" + roleDto.getStatus());
        System.out.println("[-][-]角色的的buffer：");
        for (CopySceneModel.BufferDto bufferDto : roleDto.getBufferDtosList()) {
            System.out.println("[-][-]buffer的Id：" + bufferDto.getId());
            System.out.println("[-][-]buffer名称：" + bufferDto.getName());
            long uTime = bufferDto.getCreateTime() + bufferDto.getLastTime() * 1000 - System.currentTimeMillis();
            System.out.println("[-][-]buffer剩余时间：" + uTime);
            System.out.println("[-][-]buffer来源于角色id：" + bufferDto.getFromRoleId());
        }
        System.out.println("[-][-]");
        System.out.println("[-]--------------------------------------------------------");
    }

    @Override
    public void createCopySceneResponse(NettyResponse nettyResponse) throws InvalidProtocolBufferException {
        byte[] data = nettyResponse.getData();
        CopySceneModel.CopySceneModelMessage myMessage;
        myMessage = CopySceneModel.CopySceneModelMessage.parseFrom(data);
        CopySceneModel.CopySceneBeanDto copySceneBeanDto = myMessage.getCreateCopySceneResponse().getCopySceneBeanDto();
        CopySceneMessage copySceneMessage = MmoCacheCilent.getInstance().getCopySceneMessageConcurrentHashMap().get(copySceneBeanDto.getCopySceneId());
        System.out.println("[-]--------------------------------------------------------");
        System.out.println("[-]副本已经创建好，请尽快进入");
        System.out.println("[-]副本基本信息id：" + copySceneBeanDto.getCopySceneId());
        System.out.println("[-]副本实例id：" + copySceneBeanDto.getCopySceneBeanId());
        System.out.println("[-]副本名称：" + copySceneMessage.getName());
        System.out.println("[-]副本攻略时间：" + copySceneMessage.getLastTime() + "秒");
        System.out.println("[-]副本剩下时间：" + (copySceneBeanDto.getEndTime() - System.currentTimeMillis()) + "秒");
        System.out.println("[-]副本中的BOSS：");
        for (CopySceneModel.BossBeanDto bossBeanDto : copySceneBeanDto.getBossBeansList()) {
            System.out.println("[-][-]");
            System.out.println("[-][-]Boss基本信息id：" + bossBeanDto.getId());
            System.out.println("[-][-]Boss名称：" + bossBeanDto.getName());
            System.out.println("[-][-]Boss血量：" + bossBeanDto.getNowBlood() + "/" + bossBeanDto.getBlood());
            System.out.println("[-][-]Boss蓝量：" + bossBeanDto.getNowMp() + "/" + bossBeanDto.getMp());
            System.out.println("[-][-]Boss的攻击力：" + bossBeanDto.getAttack());
            System.out.println("[-][-]Boss的状态：" + RoleStatusCode.getValue(bossBeanDto.getRoleStatus()));
            System.out.println("[-][-][-]Boss的的buffer：" + bossBeanDto.getAttack());
            for (CopySceneModel.BufferDto bufferDto : bossBeanDto.getBufferDtosList()) {
                System.out.println("[-][-][-]buffer的Id：" + bufferDto.getId());
                System.out.println("[-][-][-]buffer名称：" + bufferDto.getName());
                long uTime = bufferDto.getCreateTime() + bufferDto.getLastTime() * 1000 - System.currentTimeMillis();
                System.out.println("[-][-][-]buffer剩余时间：" + uTime);
                System.out.println("[-][-][-]buffer来源于角色id：" + bufferDto.getFromRoleId());
            }
            System.out.println("[-][-]");
        }
    }
}
