package com.liqihao.service.impl;

import com.google.protobuf.InvalidProtocolBufferException;
import com.liqihao.Cache.*;
import com.liqihao.annotation.HandlerCmdTag;
import com.liqihao.annotation.HandlerServiceTag;
import com.liqihao.commons.*;
import com.liqihao.commons.enums.*;
import com.liqihao.dao.*;
import com.liqihao.pojo.*;
import com.liqihao.pojo.baseMessage.*;
import com.liqihao.pojo.bean.*;
import com.liqihao.protobufObject.PlayModel;
import com.liqihao.service.PlayService;
import com.liqihao.util.CommonsUtil;
import io.netty.channel.Channel;
import io.netty.util.AttributeKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.*;

@Service
@HandlerServiceTag
public class PlayServiceImpl implements PlayService{
    @Autowired
    private MmoRolePOJOMapper mmoRolePOJOMapper;
    @Autowired
    private MmoUserPOJOMapper mmoUserPOJOMapper;
    @Autowired
    private MmoScenePOJOMapper mmoScenePOJOMapper;
    @Autowired
    private MmoBagPOJOMapper mmoBagPOJOMapper;
    @Autowired
    private MmoEquipmentPOJOMapper equipmentPOJOMapper;
    @Autowired
    private MmoEquipmentBagPOJOMapper equipmentBagPOJOMapper;
    @Override
    @HandlerCmdTag(cmd = ConstantValue.REGISTER_REQUEST,module = ConstantValue.PLAY_MODULE)
    public NettyResponse registerRequest(NettyRequest nettyRequest,Channel channel) throws InvalidProtocolBufferException {
        byte[] data=nettyRequest.getData();
        PlayModel.PlayModelMessage myMessage;
        myMessage=PlayModel.PlayModelMessage.parseFrom(data);
        String username=myMessage.getRegisterRequest().getUsername();
        String password=myMessage.getRegisterRequest().getPassword();
        String roleName=myMessage.getRegisterRequest().getRolename();
        Integer count1=mmoUserPOJOMapper.selectByUsername(username);
        Integer count2=mmoRolePOJOMapper.selectByRoleName(roleName);
        if (count1>0||count2>0){
            //用户已存在
            NettyResponse nettyResponse=new NettyResponse();
            nettyResponse.setCmd(ConstantValue.REGISTER_RESPONSE);
            nettyResponse.setStateCode(StateCode.SUCCESS);
//            nettyResponse.setModule(ConstantValue.PLAY_MODULE);
            //protobuf 生成registerResponse
            PlayModel.PlayModelMessage.Builder messageData=PlayModel.PlayModelMessage.newBuilder();
            messageData.setDataType(PlayModel.PlayModelMessage.DateType.LoginResponse);
            PlayModel.RegisterResponse.Builder registerResponseBuilder=PlayModel.RegisterResponse.newBuilder();
            registerResponseBuilder.setMessage("用户已存在or角色名已经存在");
            registerResponseBuilder.setStateCode(StateCode.FAIL);
            messageData.setRegisterResponse(registerResponseBuilder.build());
            nettyResponse.setData(messageData.build().toByteArray());
            return nettyResponse;
        }
        //注册成功 数据库插入账号信息
        MmoRolePOJO mmoRolePOJO=new MmoRolePOJO();
        mmoRolePOJO.setName(roleName);
        mmoRolePOJO.setMmosceneid(1);
        mmoRolePOJO.setStatus(RoleStatusCode.ALIVE.getCode());
        mmoRolePOJO.setOnstatus(RoleOnStatusCode.EXIT.getCode());
        mmoRolePOJO.setType(RoleTypeCode.PLAYER.getCode());
        mmoRolePOJOMapper.insert(mmoRolePOJO);
        MmoUserPOJO mmoUserPOJO=new MmoUserPOJO();
        mmoUserPOJO.setUserroleid(mmoRolePOJO.getId().toString());
        mmoUserPOJO.setUsername(username);
        mmoUserPOJO.setUserpwd(password);
        mmoUserPOJOMapper.insert(mmoUserPOJO);

        //返回成功的数据包
        NettyResponse nettyResponse=new NettyResponse();
        nettyResponse.setCmd(ConstantValue.REGISTER_RESPONSE);
        nettyResponse.setStateCode(StateCode.SUCCESS);
        //protobuf 生成registerResponse
        PlayModel.PlayModelMessage.Builder messageData=PlayModel.PlayModelMessage.newBuilder();
        messageData.setDataType(PlayModel.PlayModelMessage.DateType.RegisterResponse);
        PlayModel.RegisterResponse.Builder registerResponseBuilder=PlayModel.RegisterResponse.newBuilder();
        registerResponseBuilder.setMessage("用户注册成功");
        registerResponseBuilder.setStateCode(200);
        messageData.setRegisterResponse(registerResponseBuilder.build());
        nettyResponse.setData(messageData.build().toByteArray());
        return nettyResponse;
    }

    @Override
    @HandlerCmdTag(cmd = ConstantValue.LOGIN_REQUEST,module = ConstantValue.PLAY_MODULE)
    public NettyResponse loginRequest(NettyRequest nettyRequest,Channel channel) throws InvalidProtocolBufferException {
        byte[] data=nettyRequest.getData();
        PlayModel.PlayModelMessage myMessage;
        myMessage=PlayModel.PlayModelMessage.parseFrom(data);
        String username=myMessage.getLoginRequest().getUsername();
        String password=myMessage.getLoginRequest().getPassword();
        Integer mmoUserId=mmoUserPOJOMapper.checkByUernameAndPassword(username,password);
        if (null==mmoUserId||mmoUserId<0){
            NettyResponse nettyResponse=new NettyResponse();
            nettyResponse.setCmd(ConstantValue.LOGIN_RESPONSE);
            nettyResponse.setStateCode(StateCode.FAIL);
            nettyResponse.setData("密码错误or账号错误".getBytes());
            return nettyResponse;
        }
        //将角色设置为在线模式
        MmoUserPOJO mmoUserPOJO=mmoUserPOJOMapper.selectByPrimaryKey(mmoUserId);
        //从数据库中读取角色,且修改其为在线模式，放入角色在线集合
        MmoRolePOJO role=mmoRolePOJOMapper.selectByPrimaryKey(Integer.parseInt(mmoUserPOJO.getUserroleid()));
        role.setOnstatus(RoleOnStatusCode.ONLINE.getCode());
        //缓存
        MmoSimpleRole simpleRole=new MmoSimpleRole();
        simpleRole.setId(role.getId());
        simpleRole.setMmosceneid(role.getMmosceneid());
        simpleRole.setName(role.getName());
        simpleRole.setOnstatus(role.getOnstatus());
        simpleRole.setStatus(role.getStatus());
        simpleRole.setType(role.getType());
        List<SkillBean> skillBeans=CommonsUtil.skillIdsToSkillBeans(role.getSkillIds());
        simpleRole.setSkillBeans(skillBeans);
        //从基础信息获取
        BaseRoleMessage baseRoleMessage= MmoBaseMessageCache.getInstance().getBaseRoleMessage();
        simpleRole.setBlood(baseRoleMessage.getHp());
        simpleRole.setNowBlood(baseRoleMessage.getHp());
        simpleRole.setMp(baseRoleMessage.getMp());
        simpleRole.setDamageAdd(baseRoleMessage.getDamageAdd());
        simpleRole.setNowMp(baseRoleMessage.getMp());
        simpleRole.setAttack(baseRoleMessage.getAttack());
        List<Integer> skillIds=CommonsUtil.split(role.getSkillIds());
        simpleRole.setSkillIdList(skillIds);
        simpleRole.setCdMap(new HashMap<Integer, Long>());
        simpleRole.setBufferBeans(new CopyOnWriteArrayList<>());
        simpleRole.setEquipmentBeanHashMap(new HashMap<>());
        BackPackManager backPackManager=new BackPackManager(MmoBaseMessageCache.getInstance().getBaseDetailMessage().getBagSize());
       //初始化背包
        List<MmoBagPOJO> mmoBagPOJOS=mmoBagPOJOMapper.selectByRoleId(role.getId());
        for (MmoBagPOJO mmoBagPOJO:mmoBagPOJOS){
            if (mmoBagPOJO.getArticletype().equals(ArticleTypeCode.EQUIPMENT.getCode())){
                MmoEquipmentPOJO mmoEquipmentPOJO=equipmentPOJOMapper.selectByPrimaryKey(mmoBagPOJO.getwId());
                EquipmentMessage message= EquipmentMessageCache.getInstance().get(mmoEquipmentPOJO.getMessageId());
                EquipmentBean equipmentBean= CommonsUtil.equipmentMessageToEquipmentBean(message);
                equipmentBean.setQuantity(mmoBagPOJO.getNumber());
                equipmentBean.setEquipmentId(mmoEquipmentPOJO.getMessageId());
                equipmentBean.setBagId(mmoBagPOJO.getBagId());
                equipmentBean.setNowDurability(mmoEquipmentPOJO.getNowdurability());
                backPackManager.put(equipmentBean);
            }else if (mmoBagPOJO.getArticletype().equals(ArticleTypeCode.MEDICINE.getCode())){
                MedicineMessage message=MediceneMessageCache.getInstance().get(mmoBagPOJO.getwId());
                MedicineBean medicineBean= CommonsUtil.medicineMessageToMedicineBean(message);
                medicineBean.setQuantity(mmoBagPOJO.getNumber());
                medicineBean.setBagId(mmoBagPOJO.getBagId());
                backPackManager.put(medicineBean);
            }else{ }
        }
        simpleRole.setBackpackManager(backPackManager);
        //初始化装备栏
        List<MmoEquipmentBagPOJO> equipmentBagPOJOS=equipmentBagPOJOMapper.selectByRoleId(role.getId());
        HashMap<Integer,EquipmentBean> equipmentBeanConcurrentHashMap=simpleRole.getEquipmentBeanHashMap();
        for (MmoEquipmentBagPOJO m:equipmentBagPOJOS) {
            MmoEquipmentPOJO mmoEquipmentPOJO=equipmentPOJOMapper.selectByPrimaryKey(m.getEquipmentId());
            EquipmentMessage message=EquipmentMessageCache.getInstance().get(mmoEquipmentPOJO.getMessageId());
            EquipmentBean equipmentBean= CommonsUtil.equipmentMessageToEquipmentBean(message);
            equipmentBean.setNowDurability(mmoEquipmentPOJO.getNowdurability());
            equipmentBean.setEquipmentId(m.getEquipmentId());
            equipmentBean.setEquipmentBagId(m.getEquipmentbagId());
            equipmentBeanConcurrentHashMap.put(equipmentBean.getPosition(),equipmentBean);
            //修改人物属性
            simpleRole.setAttack(simpleRole.getAttack()+equipmentBean.getAttackAdd());
            simpleRole.setDamageAdd(simpleRole.getDamageAdd()+equipmentBean.getDamageAdd());
        }
        OnlineRoleMessageCache.getInstance().put(role.getId(),simpleRole);
        //数据库中人物状态
        mmoRolePOJOMapper.updateByPrimaryKeySelective(role);
        //将channel绑定用户信息存储
        ChannelMessageCache.getInstance().put(role.getId(),channel);
        //channle绑定roleId
        AttributeKey<Integer> key = AttributeKey.valueOf("roleId");
        channel.attr(key).set(role.getId());
        //protobuf 生成loginResponse
        PlayModel.PlayModelMessage.Builder messageData=PlayModel.PlayModelMessage.newBuilder();
        messageData.setDataType(PlayModel.PlayModelMessage.DateType.LoginResponse);
        PlayModel.LoginResponse.Builder loginResponseBuilder=PlayModel.LoginResponse.newBuilder();
        PlayModel.RoleDTO.Builder mmoSimpleRoleBuilder=PlayModel.RoleDTO.newBuilder();
        //自身角色信息
        PlayModel.RoleDTO roleDTO=mmoSimpleRoleBuilder.setId(simpleRole.getId())
                .setName(simpleRole.getName())
                .setOnStatus(simpleRole.getOnstatus())
                .setStatus(simpleRole.getStatus())
                .setType(simpleRole.getType())
                .setBlood(simpleRole.getBlood())
                .setNowBlood(simpleRole.getBlood())
                .addAllSkillIdList(simpleRole.getSkillIdList())
                .setMp(simpleRole.getMp())
                .setNowMp(simpleRole.getNowMp())
                .setAttack(simpleRole.getAttack())
                .setAttackAdd(simpleRole.getDamageAdd())
                .build();
        loginResponseBuilder.setRoleDto(roleDTO);
        //场景信息
        loginResponseBuilder.setSceneId(role.getMmosceneid());
        SceneBeanMessageCache.getInstance().get(role.getMmosceneid()).getRoles().add(role.getId());
        //打包成messageData
        messageData.setLoginResponse(loginResponseBuilder.build());
        NettyResponse nettyResponse=new NettyResponse();
        nettyResponse.setData(messageData.build().toByteArray());
        nettyResponse.setCmd(ConstantValue.LOGIN_RESPONSE);
        nettyResponse.setStateCode(StateCode.SUCCESS);
        return nettyResponse;
    }

    @Override
    @HandlerCmdTag(cmd = ConstantValue.LOGOUT_REQUEST,module = ConstantValue.PLAY_MODULE)
    public NettyResponse logoutRequest(NettyRequest nettyRequest,Channel channel) throws InvalidProtocolBufferException {
        Integer roleId=CommonsUtil.getRoleIdByChannel(channel);
        if (roleId != null) {
            //删除缓存中 channel绑定的信息
            ChannelMessageCache.getInstance().remove(roleId);
        }
        if (roleId==null){
            NettyResponse errotResponse=new NettyResponse(StateCode.FAIL,ConstantValue.LOGOUT_RESPONSE,"请先登录".getBytes());
            return  errotResponse;
        }
        //保存背包信息入数据库
        MmoSimpleRole mmoSimpleRole= OnlineRoleMessageCache.getInstance().get(roleId);
        CommonsUtil.bagIntoDataBase(mmoSimpleRole.getBackpackManager(),roleId);
        CommonsUtil.equipmentIntoDataBase(mmoSimpleRole);
        //将数据库中设置为离线
        MmoRolePOJO mmoRolePOJO=mmoRolePOJOMapper.selectByPrimaryKey(roleId);
        mmoRolePOJO.setOnstatus(RoleOnStatusCode.EXIT.getCode());
        mmoRolePOJOMapper.updateByPrimaryKeySelective(mmoRolePOJO);
        MmoScenePOJO mmoScenePOJO=mmoScenePOJOMapper.selectByPrimaryKey(mmoRolePOJO.getMmosceneid());
        //缓存角色集合删除
        OnlineRoleMessageCache.getInstance().remove(roleId);
        SceneBeanMessageCache.getInstance().get(mmoSimpleRole.getMmosceneid()).getRoles().remove(mmoSimpleRole.getId());
        //protobuf生成消息
        PlayModel.PlayModelMessage.Builder myMessageBuilder=PlayModel.PlayModelMessage.newBuilder();
        myMessageBuilder.setDataType(PlayModel.PlayModelMessage.DateType.LogoutResponse);
        PlayModel.LogoutResponse.Builder logoutResponseBuilder=PlayModel.LogoutResponse.newBuilder();
        logoutResponseBuilder.setCode(StateCode.SUCCESS);
        logoutResponseBuilder.setMxg("退出登陆成功");
        myMessageBuilder.setLogoutResponse(logoutResponseBuilder.build());
        //封装成nettyResponse
        NettyResponse nettyResponse=new NettyResponse();
        nettyResponse.setCmd(ConstantValue.LOGOUT_RESPONSE);
        nettyResponse.setStateCode(StateCode.SUCCESS);
        nettyResponse.setData(myMessageBuilder.build().toByteArray());
        return  nettyResponse;
    }

    @Override
    @HandlerCmdTag(cmd = ConstantValue.USE_SKILL_REQUEST,module = ConstantValue.PLAY_MODULE)
    public NettyResponse useSkillRequest(NettyRequest nettyRequest, Channel channel) throws InvalidProtocolBufferException {
        byte[] data=nettyRequest.getData();
        PlayModel.PlayModelMessage myMessage;
        myMessage=PlayModel.PlayModelMessage.parseFrom(data);
        Integer skillId=myMessage.getUseSkillRequest().getSkillId();
        Integer roleId=CommonsUtil.getRoleIdByChannel(channel);
        MmoSimpleRole mmoSimpleRole=OnlineRoleMessageCache.getInstance().get(roleId);
        Integer sceneId=mmoSimpleRole.getMmosceneid();
        if (roleId==null){
            return new NettyResponse(StateCode.FAIL,ConstantValue.USE_SKILL_RSPONSE, "未登陆".getBytes());
        }
        //判断cd
        Long nextTime= mmoSimpleRole.getCdMap().get(skillId);
        if (nextTime!=null){
            if (System.currentTimeMillis()<nextTime){
                return new NettyResponse(StateCode.FAIL,ConstantValue.USE_SKILL_RSPONSE, "该技能cd中。。".getBytes());
            }
        }
        //判断蓝是否够
        SkillMessage skillMessage=SkillMessageCache.getInstance().get(skillId);
        if (skillMessage.getConsumeType().equals(ConsuMeTypeCode.HP.getCode())) {
            //扣血
            //判断血量是否足够
            if (mmoSimpleRole.getNowBlood() < skillMessage.getConsumeNum()) {
                //血量不够
                return new NettyResponse(StateCode.FAIL, ConstantValue.USE_SKILL_RSPONSE, "血量不够无法使用该技能".getBytes());
            }
        }else {
            //扣篮
            //判断蓝量是否足够
            if (mmoSimpleRole.getNowMp()<skillMessage.getConsumeNum()){
                //蓝量不够
                return new NettyResponse(StateCode.FAIL,ConstantValue.USE_SKILL_RSPONSE, "蓝量不够无法使用该技能".getBytes());
            }
        }
        //判断武器耐久是否足够
        if (mmoSimpleRole.getEquipmentBeanHashMap().get(PositionCode.ARMS.getCode())!=null&&mmoSimpleRole.getEquipmentBeanHashMap().get(PositionCode.ARMS.getCode()).getNowDurability()<=0){
            return new NettyResponse(StateCode.FAIL,ConstantValue.USE_SKILL_RSPONSE, "武器耐久度为0，请脱落武器再攻击".getBytes());
        }
        //从缓存中查找出 怪物
        List<Integer> npcs=SceneBeanMessageCache.getInstance().get(sceneId).getNpcs();
        ArrayList<MmoSimpleNPC> target=new ArrayList<>();
        for (Integer npcId:npcs) {
            MmoSimpleNPC npc=NpcMessageCache.getInstance().get(npcId);
            if (npc.getType().equals(RoleTypeCode.ENEMY.getCode())){
                if (npc.getStatus().equals(RoleStatusCode.ALIVE.getCode())){
                    target.add(npc);
                }
            }
        }

        //使用技能
        List<PlayModel.RoleIdDamage> list=mmoSimpleRole.useSkill(target,skillId);

        //封装成nettyResponse
        PlayModel.PlayModelMessage.Builder myMessageBuilder=PlayModel.PlayModelMessage.newBuilder();
        myMessageBuilder.setDataType(PlayModel.PlayModelMessage.DateType.UseSkillResponse);
        PlayModel.UseSkillResponse.Builder useSkillBuilder=PlayModel.UseSkillResponse.newBuilder();
        useSkillBuilder.addAllRoleIdDamages(list);
        myMessageBuilder.setUseSkillResponse(useSkillBuilder.build());
        NettyResponse nettyResponse=new NettyResponse();
        nettyResponse.setCmd(ConstantValue.USE_SKILL_RSPONSE);
        nettyResponse.setStateCode(StateCode.SUCCESS);
        nettyResponse.setData(myMessageBuilder.build().toByteArray());
        //广播
        List<Integer> players=SceneBeanMessageCache.getInstance().get(sceneId).getRoles();
        for (Integer playerId:players){
            if (playerId.equals(roleId)){
                continue;
            }
            Channel c= ChannelMessageCache.getInstance().get(playerId);
            if (c!=null){
                c.writeAndFlush(nettyResponse);
            }
        }
        return  nettyResponse;
    }


}
