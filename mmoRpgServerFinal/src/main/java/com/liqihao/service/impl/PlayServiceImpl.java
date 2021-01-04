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
import com.liqihao.provider.CopySceneProvider;
import com.liqihao.provider.TeamServiceProvider;
import com.liqihao.service.PlayService;
import com.liqihao.util.CommonsUtil;
import com.sun.org.apache.bcel.internal.generic.IF_ACMPEQ;
import io.netty.channel.Channel;
import io.netty.util.AttributeKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 用户模块
 *
 * @author lqhao
 */
@Service
@HandlerServiceTag(protobufModel = "PlayModel$PlayModelMessage")
public class PlayServiceImpl implements PlayService {
    @Autowired
    private MmoRolePOJOMapper mmoRolePOJOMapper;
    @Autowired
    private MmoUserPOJOMapper mmoUserPOJOMapper;
    @Autowired
    private MmoBagPOJOMapper mmoBagPOJOMapper;
    @Autowired
    private MmoEquipmentPOJOMapper equipmentPOJOMapper;
    @Autowired
    private MmoEquipmentBagPOJOMapper equipmentBagPOJOMapper;

    @Override
    @HandlerCmdTag(cmd = ConstantValue.REGISTER_REQUEST, module = ConstantValue.PLAY_MODULE)
    public void registerRequest(PlayModel.PlayModelMessage myMessage, Channel channel) {
        String username = myMessage.getRegisterRequest().getUsername();
        String password = myMessage.getRegisterRequest().getPassword();
        String roleName = myMessage.getRegisterRequest().getRolename();
        Integer count1 = mmoUserPOJOMapper.selectByUsername(username);
        Integer count2 = mmoRolePOJOMapper.selectByRoleName(roleName);
        if (count1 > 0 || count2 > 0) {
            //用户已存在
            NettyResponse nettyResponse = new NettyResponse();
            nettyResponse.setCmd(ConstantValue.REGISTER_RESPONSE);
            nettyResponse.setStateCode(StateCode.SUCCESS);
            //protobuf 生成registerResponse
            PlayModel.PlayModelMessage.Builder messageData = PlayModel.PlayModelMessage.newBuilder();
            messageData.setDataType(PlayModel.PlayModelMessage.DateType.LoginResponse);
            PlayModel.RegisterResponse.Builder registerResponseBuilder = PlayModel.RegisterResponse.newBuilder();
            registerResponseBuilder.setMessage("用户已存在or角色名已经存在");
            registerResponseBuilder.setStateCode(StateCode.FAIL);
            messageData.setRegisterResponse(registerResponseBuilder.build());
            nettyResponse.setData(messageData.build().toByteArray());
            channel.writeAndFlush(nettyResponse);
            return;
        }
        //注册成功 数据库插入账号信息
        MmoRolePOJO mmoRolePOJO = new MmoRolePOJO();
        mmoRolePOJO.setName(roleName);
        mmoRolePOJO.setMmosceneid(1);
        mmoRolePOJO.setStatus(RoleStatusCode.ALIVE.getCode());
        mmoRolePOJO.setOnstatus(RoleOnStatusCode.EXIT.getCode());
        mmoRolePOJO.setType(RoleTypeCode.PLAYER.getCode());
        mmoRolePOJOMapper.insert(mmoRolePOJO);
        MmoUserPOJO mmoUserPOJO = new MmoUserPOJO();
        mmoUserPOJO.setUserroleid(mmoRolePOJO.getId().toString());
        mmoUserPOJO.setUsername(username);
        mmoUserPOJO.setUserpwd(password);
        mmoUserPOJOMapper.insert(mmoUserPOJO);

        //返回成功的数据包
        NettyResponse nettyResponse = new NettyResponse();
        nettyResponse.setCmd(ConstantValue.REGISTER_RESPONSE);
        nettyResponse.setStateCode(StateCode.SUCCESS);
        //protobuf 生成registerResponse
        PlayModel.PlayModelMessage.Builder messageData = PlayModel.PlayModelMessage.newBuilder();
        messageData.setDataType(PlayModel.PlayModelMessage.DateType.RegisterResponse);
        PlayModel.RegisterResponse.Builder registerResponseBuilder = PlayModel.RegisterResponse.newBuilder();
        registerResponseBuilder.setMessage("用户注册成功");
        registerResponseBuilder.setStateCode(200);
        messageData.setRegisterResponse(registerResponseBuilder.build());
        nettyResponse.setData(messageData.build().toByteArray());
        channel.writeAndFlush(nettyResponse);
        return;
    }

    @Override
    @HandlerCmdTag(cmd = ConstantValue.LOGIN_REQUEST, module = ConstantValue.PLAY_MODULE)
    public void loginRequest(PlayModel.PlayModelMessage myMessage, Channel channel) throws InvalidProtocolBufferException {

        String username = myMessage.getLoginRequest().getUsername();
        String password = myMessage.getLoginRequest().getPassword();
        Integer mmoUserId = mmoUserPOJOMapper.checkByUernameAndPassword(username, password);
        if (null == mmoUserId || mmoUserId < 0) {
            NettyResponse nettyResponse = new NettyResponse();
            nettyResponse.setCmd(ConstantValue.FAIL_RESPONSE);
            nettyResponse.setStateCode(StateCode.FAIL);
            nettyResponse.setData("密码错误or账号错误".getBytes());
            channel.writeAndFlush(nettyResponse);
            return;
        }
        //将角色设置为在线模式
        MmoUserPOJO mmoUserPOJO = mmoUserPOJOMapper.selectByPrimaryKey(mmoUserId);
        //从数据库中读取角色,且修改其为在线模式，放入角色在线集合
        MmoRolePOJO role = mmoRolePOJOMapper.selectByPrimaryKey(Integer.parseInt(mmoUserPOJO.getUserroleid()));
        role.setOnstatus(RoleOnStatusCode.ONLINE.getCode());
        //初始化基础信息获取
        MmoSimpleRole simpleRole = new MmoSimpleRole();
        role.setStatus(RoleStatusCode.ALIVE.getCode());
        BaseDetailMessage baseDetailMessage = MmoBaseMessageCache.getInstance().getBaseDetailMessage();
        simpleRole.setTeamApplyOrInviteSize(baseDetailMessage.getTeamApplyOrInviteSize());
        BaseRoleMessage baseRoleMessage = MmoBaseMessageCache.getInstance().getBaseRoleMessage();
        simpleRole.init(role, baseRoleMessage);
        BackPackManager backPackManager = new BackPackManager(MmoBaseMessageCache.getInstance().getBaseDetailMessage().getBagSize());
        //初始化背包
        List<MmoBagPOJO> mmoBagPOJOS = mmoBagPOJOMapper.selectByRoleId(role.getId());
        for (MmoBagPOJO mmoBagPOJO : mmoBagPOJOS) {
            if (mmoBagPOJO.getArticletype().equals(ArticleTypeCode.EQUIPMENT.getCode())) {
                MmoEquipmentPOJO mmoEquipmentPOJO = equipmentPOJOMapper.selectByPrimaryKey(mmoBagPOJO.getwId());
                EquipmentMessage message = EquipmentMessageCache.getInstance().get(mmoEquipmentPOJO.getMessageId());
                EquipmentBean equipmentBean = CommonsUtil.equipmentMessageToEquipmentBean(message);
                equipmentBean.setQuantity(mmoBagPOJO.getNumber());
                equipmentBean.setEquipmentId(mmoEquipmentPOJO.getMessageId());
                equipmentBean.setBagId(mmoBagPOJO.getBagId());
                equipmentBean.setNowDurability(mmoEquipmentPOJO.getNowdurability());
                backPackManager.put(equipmentBean);
            } else if (mmoBagPOJO.getArticletype().equals(ArticleTypeCode.MEDICINE.getCode())) {
                MedicineMessage message = MediceneMessageCache.getInstance().get(mmoBagPOJO.getwId());
                MedicineBean medicineBean = CommonsUtil.medicineMessageToMedicineBean(message);
                medicineBean.setQuantity(mmoBagPOJO.getNumber());
                medicineBean.setBagId(mmoBagPOJO.getBagId());
                backPackManager.put(medicineBean);
            } else {
            }
        }
        simpleRole.setBackpackManager(backPackManager);
        //初始化装备栏
        List<MmoEquipmentBagPOJO> equipmentBagPOJOS = equipmentBagPOJOMapper.selectByRoleId(role.getId());
        HashMap<Integer, EquipmentBean> equipmentBeanConcurrentHashMap = simpleRole.getEquipmentBeanHashMap();
        for (MmoEquipmentBagPOJO m : equipmentBagPOJOS) {
            MmoEquipmentPOJO mmoEquipmentPOJO = equipmentPOJOMapper.selectByPrimaryKey(m.getEquipmentId());
            EquipmentMessage message = EquipmentMessageCache.getInstance().get(mmoEquipmentPOJO.getMessageId());
            EquipmentBean equipmentBean = CommonsUtil.equipmentMessageToEquipmentBean(message);
            equipmentBean.setNowDurability(mmoEquipmentPOJO.getNowdurability());
            equipmentBean.setEquipmentId(m.getEquipmentId());
            equipmentBean.setEquipmentBagId(m.getEquipmentbagId());
            equipmentBeanConcurrentHashMap.put(equipmentBean.getPosition(), equipmentBean);
            //修改人物属性
            simpleRole.setAttack(simpleRole.getAttack() + equipmentBean.getAttackAdd());
            simpleRole.setDamageAdd(simpleRole.getDamageAdd() + equipmentBean.getDamageAdd());
        }
        OnlineRoleMessageCache.getInstance().put(role.getId(), simpleRole);
        //数据库中人物状态
        mmoRolePOJOMapper.updateByPrimaryKeySelective(role);
        //将channel绑定用户信息存储
        ChannelMessageCache.getInstance().put(role.getId(), channel);
        //channle绑定roleId
        AttributeKey<MmoSimpleRole> key = AttributeKey.valueOf("role");
        channel.attr(key).set(simpleRole);
        //protobuf 生成loginResponse
        PlayModel.PlayModelMessage.Builder messageData = PlayModel.PlayModelMessage.newBuilder();
        messageData.setDataType(PlayModel.PlayModelMessage.DateType.LoginResponse);
        PlayModel.LoginResponse.Builder loginResponseBuilder = PlayModel.LoginResponse.newBuilder();
        PlayModel.RoleDTO.Builder mmoSimpleRoleBuilder = PlayModel.RoleDTO.newBuilder();
        //自身角色信息
        PlayModel.RoleDTO roleDTO = mmoSimpleRoleBuilder.setId(simpleRole.getId())
                .setName(simpleRole.getName())
                .setOnStatus(simpleRole.getOnStatus())
                .setStatus(simpleRole.getStatus())
                .setType(simpleRole.getType())
                .setBlood(simpleRole.getHp())
                .setNowBlood(simpleRole.getHp())
                .addAllSkillIdList(simpleRole.getSkillIdList())
                .setMp(simpleRole.getMp())
                .setNowMp(simpleRole.getNowMp())
                .setTeamId(simpleRole.getTeamId() == null ? -1 : simpleRole.getTeamId())
                .setAttack(simpleRole.getAttack())
                .setAttackAdd(simpleRole.getDamageAdd())
                .build();
        loginResponseBuilder.setRoleDto(roleDTO);
        //场景信息
        loginResponseBuilder.setSceneId(role.getMmosceneid());
        SceneBeanMessageCache.getInstance().get(role.getMmosceneid()).getRoles().add(role.getId());
        //打包成messageData
        messageData.setLoginResponse(loginResponseBuilder.build());
        NettyResponse nettyResponse = new NettyResponse();
        nettyResponse.setData(messageData.build().toByteArray());
        nettyResponse.setCmd(ConstantValue.LOGIN_RESPONSE);
        nettyResponse.setStateCode(StateCode.SUCCESS);
        channel.writeAndFlush(nettyResponse);
        return;
    }

    @Override
    @HandlerCmdTag(cmd = ConstantValue.LOGOUT_REQUEST, module = ConstantValue.PLAY_MODULE)
    public void logoutRequest(PlayModel.PlayModelMessage myMessage, Channel channel) throws InvalidProtocolBufferException {
        MmoSimpleRole role = CommonsUtil.checkLogin(channel);
        if (role == null) {
            return;
        } else {
            ChannelMessageCache.getInstance().remove(role.getId());
        }
        //保存背包信息入数据库
        CommonsUtil.bagIntoDataBase(role.getBackpackManager(), role.getId());
        CommonsUtil.equipmentIntoDataBase(role);
        CommonsUtil.RoleInfoIntoDataBase(role);
        //将数据库中设置为离线
        MmoRolePOJO mmoRolePOJO = mmoRolePOJOMapper.selectByPrimaryKey(role.getId());
        mmoRolePOJO.setOnstatus(RoleOnStatusCode.EXIT.getCode());
        mmoRolePOJOMapper.updateByPrimaryKeySelective(mmoRolePOJO);
        //缓存角色集合删除
        OnlineRoleMessageCache.getInstance().remove(role.getId());
        SceneBeanMessageCache.getInstance().get(role.getMmoSceneId()).getRoles().remove(role.getId());
        //protobuf生成消息
        PlayModel.PlayModelMessage.Builder myMessageBuilder = PlayModel.PlayModelMessage.newBuilder();
        myMessageBuilder.setDataType(PlayModel.PlayModelMessage.DateType.LogoutResponse);
        PlayModel.LogoutResponse.Builder logoutResponseBuilder = PlayModel.LogoutResponse.newBuilder();
        logoutResponseBuilder.setCode(StateCode.SUCCESS);
        logoutResponseBuilder.setMxg("退出登陆成功");
        myMessageBuilder.setLogoutResponse(logoutResponseBuilder.build());
        //封装成nettyResponse
        NettyResponse nettyResponse = new NettyResponse();
        nettyResponse.setCmd(ConstantValue.LOGOUT_RESPONSE);
        nettyResponse.setStateCode(StateCode.SUCCESS);
        nettyResponse.setData(myMessageBuilder.build().toByteArray());
        channel.writeAndFlush(nettyResponse);
        return;
    }

    @Override
    @HandlerCmdTag(cmd = ConstantValue.USE_SKILL_REQUEST, module = ConstantValue.PLAY_MODULE)
    public void useSkillRequest(PlayModel.PlayModelMessage myMessage, Channel channel) throws InvalidProtocolBufferException {

        Integer skillId = myMessage.getUseSkillRequest().getSkillId();
        Integer targetId = myMessage.getUseSkillRequest().getRoleId();
        Integer roleType=myMessage.getUseSkillRequest().getRoleType();
        MmoSimpleRole mmoSimpleRole = CommonsUtil.checkLogin(channel);
        if (mmoSimpleRole == null) {
            return;
        }
        //判断cd
        Long nextTime = mmoSimpleRole.getCdMap().get(skillId);
        if (nextTime != null) {
            if (System.currentTimeMillis() < nextTime) {
                channel.writeAndFlush(new NettyResponse(StateCode.FAIL, ConstantValue.FAIL_RESPONSE, "该技能cd中。。".getBytes()));
                return;
            }
        }
        //判断蓝是否够
        SkillMessage skillMessage = SkillMessageCache.getInstance().get(skillId);
        if (skillMessage == null) {
            channel.writeAndFlush(new NettyResponse(StateCode.FAIL, ConstantValue.FAIL_RESPONSE, "没有该技能。。".getBytes()));
            return;
        }
        if (skillMessage.getConsumeType().equals(ConsuMeTypeCode.HP.getCode())) {
            //扣血
            //判断血量是否足够
            if (mmoSimpleRole.getNowHp() < skillMessage.getConsumeNum()) {
                //血量不够
                channel.writeAndFlush(new NettyResponse(StateCode.FAIL, ConstantValue.FAIL_RESPONSE, "血量不够无法使用该技能".getBytes()));
                return;
            }
        } else {
            //扣篮
            //判断蓝量是否足够
            if (mmoSimpleRole.getNowMp() < skillMessage.getConsumeNum()) {
                //蓝量不够
                channel.writeAndFlush(new NettyResponse(StateCode.FAIL, ConstantValue.FAIL_RESPONSE, "蓝量不够无法使用该技能".getBytes()));
                return;
            }
        }
        //判断武器耐久是否足够
        if (mmoSimpleRole.getEquipmentBeanHashMap().get(PositionCode.ARMS.getCode()) != null && mmoSimpleRole.getEquipmentBeanHashMap().get(PositionCode.ARMS.getCode()).getNowDurability() <= 0) {
            channel.writeAndFlush(new NettyResponse(StateCode.FAIL, ConstantValue.FAIL_RESPONSE, "武器耐久度为0，请脱落武器再攻击".getBytes()));
            return;
        }
        //判断是单体技能 还是群体技能  可以攻击所有玩家 除了队友 npc
        //从缓存中查找出 怪物
        ArrayList<Role> target = new ArrayList<>();
        if (mmoSimpleRole.getMmoSceneId() != null) {
            //在场景中
            if (targetId == -1) {
                //群攻
                //可以攻击所有场景的人 除了队友 npc
                SceneBean sceneBean = SceneBeanMessageCache.getInstance().get(mmoSimpleRole.getMmoSceneId());
                List<Integer> npcs = sceneBean.getNpcs();
                List<Integer> roles = sceneBean.getRoles();
                for (Integer id : npcs) {
                    MmoSimpleNPC npc = NpcMessageCache.getInstance().get(id);
                    if (npc.getType().equals(RoleTypeCode.ENEMY.getCode())) {
                        target.add(npc);
                    }
                }
                for (Integer id : roles) {
                    MmoSimpleRole role = OnlineRoleMessageCache.getInstance().get(id);
                    if (role.getId().equals(mmoSimpleRole.getId())){
                        continue;
                    }
                    if (mmoSimpleRole.getTeamId() == null ) {
                        target.add(role);
                    }else{
                        if(role.getTeamId() == null){
                            target.add(role);
                        } else if (!mmoSimpleRole.getTeamId().equals(role.getTeamId())){
                            target.add(role);
                        }
                    }
                }
            } else {
                //在场景中 只能打npcOF
                Role role;
                if (roleType.equals(RoleTypeCode.ENEMY.getCode())) {
                    role = NpcMessageCache.getInstance().get(targetId);
                }else if(roleType.equals(RoleTypeCode.PLAYER.getCode())){
                    role=OnlineRoleMessageCache.getInstance().get(targetId);
                }else{
                    role=null;
                }
                if (role == null) {
                    channel.writeAndFlush(new NettyResponse(StateCode.FAIL, ConstantValue.FAIL_RESPONSE, "当前场景没有该id的角色或者选择了攻击npc".getBytes()));
                    return;
                }
                if (!role.getMmoSceneId().equals(mmoSimpleRole.getMmoSceneId())) {
                    channel.writeAndFlush(new NettyResponse(StateCode.FAIL, ConstantValue.FAIL_RESPONSE, "当前场景没有该id的角色".getBytes()));
                    return;
                }
                if (mmoSimpleRole.getTeamId() != null) {
                    TeamBean teamBean = TeamServiceProvider.getTeamBeanByTeamId(mmoSimpleRole.getTeamId());
                    if (teamBean.getMmoSimpleRoles().contains(role)) {
                        channel.writeAndFlush(new NettyResponse(StateCode.FAIL, ConstantValue.FAIL_RESPONSE, "该角色是队友啊，兄弟".getBytes()));
                        return;
                    }
                }
                target.add(role);
            }
        } else {
            //在副本中
            Integer copySceneBeanId = mmoSimpleRole.getCopySceneBeanId();
            CopySceneBean copySceneBean = CopySceneProvider.getCopySceneBeanById(copySceneBeanId);
            if (copySceneBean.getNowBoss() != null) {
                target.add(copySceneBean.getNowBoss());
            }
        }
        //使用技能
        if (target.size() > 0) {
            mmoSimpleRole.useSkill(target, skillId);
        }
        return;
    }
}
