package com.liqihao.service.impl;

import com.liqihao.Cache.*;
import com.liqihao.annotation.HandlerCmdTag;
import com.liqihao.annotation.HandlerServiceTag;
import com.liqihao.commons.*;
import com.liqihao.commons.enums.*;
import com.liqihao.dao.*;
import com.liqihao.pojo.*;
import com.liqihao.pojo.baseMessage.*;
import com.liqihao.pojo.bean.*;
import com.liqihao.pojo.bean.articleBean.EquipmentBean;
import com.liqihao.pojo.bean.articleBean.MedicineBean;
import com.liqihao.pojo.bean.guildBean.GuildBean;
import com.liqihao.pojo.bean.roleBean.MmoHelperBean;
import com.liqihao.pojo.bean.roleBean.MmoSimpleNPC;
import com.liqihao.pojo.bean.roleBean.MmoSimpleRole;
import com.liqihao.pojo.bean.roleBean.Role;
import com.liqihao.pojo.bean.teamBean.TeamBean;
import com.liqihao.protobufObject.PlayModel;
import com.liqihao.provider.*;
import com.liqihao.service.PlayService;
import com.liqihao.util.CommonsUtil;
import com.liqihao.util.ScheduledThreadPoolUtil;
import io.netty.channel.Channel;
import io.netty.util.AttributeKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 用户模块
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
    private MmoEquipmentBagPOJOMapper equipmentBagPOJOMapper;
    @Autowired
    private MmoEmailPOJOMapper emailPOJOMapper;

    @Override
    @HandlerCmdTag(cmd = ConstantValue.REGISTER_REQUEST, module = ConstantValue.PLAY_MODULE)
    public void registerRequest(PlayModel.PlayModelMessage myMessage, Channel channel) throws RpgServerException {
        String username = myMessage.getRegisterRequest().getUsername();
        String password = myMessage.getRegisterRequest().getPassword();
        String roleName = myMessage.getRegisterRequest().getRolename();
        Integer professionId=myMessage.getRegisterRequest().getProfessionId();
        if (professionId<1||professionId>4){
            throw new RpgServerException(StateCode.FAIL,"错误职业id");
        }
        //查库
        Integer count1 = mmoUserPOJOMapper.selectByUsername(username);
        Integer count2 = mmoRolePOJOMapper.selectByRoleName(roleName);
        if (count1 > 0 || count2 > 0) {
            //用户已存在
           throw new RpgServerException(StateCode.FAIL,"用户名已存在or角色名已经存在");
        }
        //注册成功 数据库插入账号信息
        MmoRolePOJO mmoRolePOJO = new MmoRolePOJO();
        mmoRolePOJO.init(roleName);
        mmoRolePOJO.setProfessionId(professionId);
        mmoRolePOJOMapper.insert(mmoRolePOJO);
        //角色表新增该用户
        RoleMessageCache.getInstance().put(mmoRolePOJO.getId(),mmoRolePOJO);
        MmoUserPOJO mmoUserPOJO = new MmoUserPOJO();
        mmoUserPOJO.setUserRoleId(mmoRolePOJO.getId().toString());
        mmoUserPOJO.setUserName(username);
        mmoUserPOJO.setUserPwd(password);
        mmoUserPOJOMapper.insert(mmoUserPOJO);
        //角色新增所有的成就任务
        TaskServiceProvider.insertAllAchievements(mmoRolePOJO.getId());
        RoleMessageCache.getInstance().put(mmoRolePOJO.getId(),mmoRolePOJO);
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
    }

    @Override
    @HandlerCmdTag(cmd = ConstantValue.LOGIN_REQUEST, module = ConstantValue.PLAY_MODULE)
    public void loginRequest(PlayModel.PlayModelMessage myMessage, Channel channel) throws RpgServerException {
        String username = myMessage.getLoginRequest().getUsername();
        String password = myMessage.getLoginRequest().getPassword();
        MmoUserPOJO mmoUserPOJO  = mmoUserPOJOMapper.checkByUserNameAndPassword(username, password);
        if (null == mmoUserPOJO) {
            throw new RpgServerException(StateCode.FAIL,"密码错误or账号错误");
        }
        //从数据库中读取角色,且修改其为在线模式，放入角色在线集合
        MmoRolePOJO role = mmoRolePOJOMapper.selectByPrimaryKey(Integer.parseInt(mmoUserPOJO.getUserRoleId()));
        //检测是否在线
        String nodeIndex=channel.remoteAddress().toString();
        if (NodeCheckMessageCache.getInstance().contains(nodeIndex)){
            throw new RpgServerException(StateCode.FAIL,"当前已经登陆，请勿重复登陆");
        }
        MmoSimpleRole lastRole=OnlineRoleMessageCache.getInstance().get(role.getId());
        if (lastRole!=null){
            //另一个客户端在线
            lastRole.logout();
        }
        NodeCheckMessageCache.getInstance().put(nodeIndex,true);
        role.setOnStatus(RoleOnStatusCode.ONLINE.getCode());
        //初始化基础信息获取
        MmoSimpleRole simpleRole = new MmoSimpleRole();
        role.setStatus(RoleStatusCode.ALIVE.getCode());
        DetailBaseMessage baseDetailMessage = MmoBaseMessageCache.getInstance().getBaseDetailMessage();
        simpleRole.setTeamApplyOrInviteSize(baseDetailMessage.getTeamApplyOrInviteSize());
        RoleBaseMessage baseRoleMessage = MmoBaseMessageCache.getInstance().getBaseRoleMessage();
        simpleRole.init(role, baseRoleMessage);
        BackPackManager backPackManager = new BackPackManager(MmoBaseMessageCache.getInstance().getBaseDetailMessage().getBagSize());
        //初始化背包
        List<MmoBagPOJO> mmoBagPOJOS = mmoBagPOJOMapper.selectByRoleId(role.getId());
        for (MmoBagPOJO mmoBagPOJO : mmoBagPOJOS) {
            if (mmoBagPOJO.getArticleType().equals(ArticleTypeCode.EQUIPMENT.getCode())) {
                EquipmentBean equipmentBean =ArticleServiceProvider.getEquipmentBeanConcurrentHashMap().get(mmoBagPOJO.getwId());
                equipmentBean.setBagId(mmoBagPOJO.getBagId());
                backPackManager.putOnDatabase(equipmentBean);
            } else if (mmoBagPOJO.getArticleType().equals(ArticleTypeCode.MEDICINE.getCode())) {
                MedicineMessage message = MedicineMessageCache.getInstance().get(mmoBagPOJO.getwId());
                MedicineBean medicineBean = CommonsUtil.medicineMessageToMedicineBean(message);
                medicineBean.setQuantity(mmoBagPOJO.getNumber());
                medicineBean.setBagId(mmoBagPOJO.getBagId());
                backPackManager.putOnDatabase(medicineBean);
            }
        }
        simpleRole.setBackpackManager(backPackManager);
        //初始化收件邮箱信息
        List<MmoEmailPOJO> toEmailPOJOS = emailPOJOMapper.selectByToRoleId(role.getId());
        for (MmoEmailPOJO m : toEmailPOJOS) {
            EmailBean emailBean = CommonsUtil.emailPOJOToMmoEmailBean(m);
            simpleRole.getToMmoEmailBeanConcurrentHashMap().put(emailBean.getId(), emailBean);
        }
        //初始化已发送邮箱信息
        List<MmoEmailPOJO> fromEmailPOJOS = emailPOJOMapper.selectByFromRoleId(role.getId());
        for (MmoEmailPOJO m : fromEmailPOJOS) {
            EmailBean emailBean = CommonsUtil.emailPOJOToMmoEmailBean(m);
            simpleRole.getFromMmoEmailBeanConcurrentHashMap().put(emailBean.getId(), emailBean);
        }
        //初始化装备栏
        List<MmoEquipmentBagPOJO> equipmentBagPOJOS = equipmentBagPOJOMapper.selectByRoleId(role.getId());
        HashMap<Integer, EquipmentBean> equipmentBeanConcurrentHashMap = simpleRole.getEquipmentBeanHashMap();
        for (MmoEquipmentBagPOJO m : equipmentBagPOJOS) {
            //从内存中取
            EquipmentBean equipmentBean =ArticleServiceProvider.getEquipmentBeanConcurrentHashMap().get(m.getEquipmentId());
            equipmentBean.setEquipmentBagId(m.getEquipmentBagId());
            EquipmentMessage message = EquipmentMessageCache.getInstance().get(equipmentBean.getEquipmentMessageId());
            equipmentBeanConcurrentHashMap.put(message.getPosition(), equipmentBean);
            //修改人物属性
            simpleRole.setAttack(simpleRole.getAttack() + message.getAttackAdd());
            simpleRole.setDamageAdd(simpleRole.getDamageAdd() + message.getDamageAdd());
            //改变装备星级
            Integer olderEquipmentLevel=simpleRole.getEquipmentLevel();
            simpleRole.setEquipmentLevel(olderEquipmentLevel+message.getEquipmentLevel());
        }
        //初始化任务信息
        TaskServiceProvider.initTask(simpleRole);
        //初始化公会信息
        if (role.getGuildId()!=-1){
            GuildBean guildBean=GuildServiceProvider.getInstance().getGuildBeanById(role.getGuildId());
            simpleRole.setGuildBean(guildBean);
        }
        //初始化好友
        simpleRole.setFriends(CommonsUtil.split(role.getFriendIds()));
        OnlineRoleMessageCache.getInstance().put(role.getId(), simpleRole);
        //数据库中人物状态
        mmoRolePOJOMapper.updateByPrimaryKeySelective(role);
        //将channel绑定用户信息存储
        simpleRole.setChannel(channel);
        ChannelMessageCache.getInstance().put(role.getId(), channel);
        //channle绑定roleId
        AttributeKey<MmoSimpleRole> key = AttributeKey.valueOf("role");
        channel.attr(key).set(simpleRole);
        //protobuf 生成loginResponse
        PlayModel.PlayModelMessage.Builder messageData = PlayModel.PlayModelMessage.newBuilder();
        messageData.setDataType(PlayModel.PlayModelMessage.DateType.LoginResponse);
        PlayModel.LoginResponse.Builder loginResponseBuilder = PlayModel.LoginResponse.newBuilder();
        //自身角色信息
        PlayModel.RoleDTO roleDTO = CommonsUtil.mmoRoleToPlayModelRoleDto(simpleRole);
        loginResponseBuilder.setRoleDto(roleDTO);
        loginResponseBuilder.setSceneId(role.getMmoSceneId());
        SceneBeanMessageCache.getInstance().get(role.getMmoSceneId()).getRoles().add(role.getId());
        List<Role> newRoles = new ArrayList<>();
        newRoles.add(simpleRole);
        //打包成messageData
        messageData.setLoginResponse(loginResponseBuilder.build());
        NettyResponse nettyResponse = new NettyResponse();
        nettyResponse.setData(messageData.build().toByteArray());
        nettyResponse.setCmd(ConstantValue.LOGIN_RESPONSE);
        nettyResponse.setStateCode(StateCode.SUCCESS);
        channel.writeAndFlush(nettyResponse);
        //获取场景所有角色信息
        List<Role> sceneRoles=CommonsUtil.getAllRolesFromScene(simpleRole);
        //发送给场景中其他角色 有角色登陆
        CommonsUtil.sendRoleResponse(sceneRoles, simpleRole.getMmoSceneId(), null);
    }

    @Override
    @HandlerCmdTag(cmd = ConstantValue.LOGOUT_REQUEST, module = ConstantValue.PLAY_MODULE)
    public void logoutRequest(PlayModel.PlayModelMessage myMessage, Channel channel) throws Exception {
        MmoSimpleRole role = CommonsUtil.checkLogin(channel);
        ChannelMessageCache.getInstance().remove(role.getId());
        AttributeKey<MmoSimpleRole> key = AttributeKey.valueOf("role");
        channel.attr(key).set(null);
        //退出副本
        if (role.getCopySceneBeanId()!=null){
            CopySceneProvider.getCopySceneBeanById(role.getCopySceneBeanId()).peopleExit(role.getId());
        }
        //退出队伍
        if(role.getTeamId()!=null){
            TeamServiceProvider.getTeamBeanByTeamId(role.getTeamId()).exitPeople(role.getId());
        }
        //退出场景
        if(role.getMmoSceneId()!=null){
            SceneBeanMessageCache.getInstance().get(role.getMmoSceneId()).getRoles().remove(role.getId());
        }
        //将数据库中设置为离线
        MmoRolePOJO mmoRolePOJO = mmoRolePOJOMapper.selectByPrimaryKey(role.getId());
        mmoRolePOJO.setOnStatus(RoleOnStatusCode.EXIT.getCode());
        ScheduledThreadPoolUtil.addTask(() -> mmoRolePOJOMapper.updateByPrimaryKeySelective(mmoRolePOJO));
        //缓存角色集合删除
        OnlineRoleMessageCache.getInstance().remove(role.getId());
        NodeCheckMessageCache.getInstance().remove(role.getChannel().remoteAddress().toString());
        if (role.getMmoSceneId() != null) {
            SceneBeanMessageCache.getInstance().get(role.getMmoSceneId()).getRoles().remove(role.getId());
        } else {
            Integer teamId = role.getTeamId();
            TeamBean teamBean = TeamServiceProvider.getTeamBeanByTeamId(teamId);
            teamBean.exitPeople(role.getId());
        }
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
    }

    @Override
    @HandlerCmdTag(cmd = ConstantValue.USE_SKILL_REQUEST, module = ConstantValue.PLAY_MODULE)
    public void useSkillRequest(PlayModel.PlayModelMessage myMessage, MmoSimpleRole mmoSimpleRole) throws Exception {
        Integer skillId = myMessage.getUseSkillRequest().getSkillId();
        Integer targetId = myMessage.getUseSkillRequest().getRoleId();
        Integer roleType = myMessage.getUseSkillRequest().getRoleType();
        //判断cd
        Long nextTime = mmoSimpleRole.getCdMap().get(skillId);
        if (nextTime != null) {
            if (System.currentTimeMillis() < nextTime) {
                throw new RpgServerException(StateCode.FAIL,"该技能cd中。。");
            }
        }
        //判断蓝是否够
        SkillMessage skillMessage = SkillMessageCache.getInstance().get(skillId);
        if (skillMessage == null) {
            throw new RpgServerException(StateCode.FAIL,"没有该技能。。");
        }
        if (skillMessage.getConsumeType().equals(ConsumeTypeCode.HP.getCode())) {
            //扣血
            //判断血量是否足够
            if (mmoSimpleRole.getNowHp() < skillMessage.getConsumeNum()) {
                //血量不够
                throw new RpgServerException(StateCode.FAIL,"血量不够无法使用该技能");
            }
        } else {
            //扣篮
            //判断蓝量是否足够
            if (mmoSimpleRole.getNowMp() < skillMessage.getConsumeNum()) {
                //蓝量不够
                throw new RpgServerException(StateCode.FAIL,"蓝量不够无法使用该技能");
            }
        }
        if(targetId.equals(mmoSimpleRole.getId())){
            if (!skillMessage.getSkillDamageType().equals(SkillDamageTypeCode.ADD.getCode())){
                throw new RpgServerException(StateCode.FAIL,"该技能不能对自身使用");
            }
        }
        //判断武器耐久是否足够
        if (mmoSimpleRole.getEquipmentBeanHashMap().get(PositionCode.ARMS.getCode()) != null && mmoSimpleRole.getEquipmentBeanHashMap().get(PositionCode.ARMS.getCode()).getNowDurability() <= 0) {
            throw new RpgServerException(StateCode.FAIL,"武器耐久度为0，请脱落武器再攻击");
        }
        //判断是单体技能 还是群体技能   可以攻击所有玩家 除了队友 npc
        //从缓存中查找出 怪物
        SkillBean skillBean = mmoSimpleRole.getSkillBeanBySkillId(skillId);
        if (skillBean==null){
            throw new RpgServerException(StateCode.FAIL,"人物没有该技能。。");
        }
        //召唤技能
        if (skillBean.getSkillAttackType().equals(SkillAttackTypeCode.CALL.getCode())) {
            mmoSimpleRole.useSkill(null, skillId);
            return;
        }
        //寻找目标
        ArrayList<Role> target = new ArrayList<>();
        if (mmoSimpleRole.getMmoSceneId() != null) {
            target.addAll(findTargetInScene(mmoSimpleRole, roleType, targetId));
        } else {
            target.addAll(findTargetInCopyScene(mmoSimpleRole));
        }
        //使用技能
        if (target.size() > 0) {
            mmoSimpleRole.useSkill(target, skillId);
        }
    }
 
    /**
     * description 在场景中找目标
     * @param mmoSimpleRole  
     * @param roleType  
     * @param targetId  
     * @return {@link List< Role> }
     * @author lqhao
     * @createTime 2021/1/21 12:18
     */
    private List<Role> findTargetInScene(MmoSimpleRole mmoSimpleRole, Integer roleType, Integer targetId) throws Exception {
        ArrayList<Role> targets = new ArrayList<>();
        //在场景中
        if (targetId == -1) {
            //群攻
            //可以攻击所有场景的人 除了队友 npc
            SceneBean sceneBean = SceneBeanMessageCache.getInstance().get(mmoSimpleRole.getMmoSceneId());
            //npc
            for (Integer id : sceneBean.getNpcs()) {
                MmoSimpleNPC npc = NpcMessageCache.getInstance().get(id);
                if (npc.getType().equals(RoleTypeCode.ENEMY.getCode())) {
                    targets.add(npc);
                }
            }
            //people
            for (Integer id : sceneBean.getRoles()) {
                MmoSimpleRole role = OnlineRoleMessageCache.getInstance().get(id);
                if (role.getId().equals(mmoSimpleRole.getId())) {
                    continue;
                }
                if (mmoSimpleRole.getTeamId() == null||role.getTeamId() == null||!mmoSimpleRole.getTeamId().equals(role.getTeamId())){
                    targets.add(role);
                }
            }
            //hepler
            for (MmoHelperBean h : sceneBean.getHelperBeans()) {
                if (mmoSimpleRole.getTeamId() == null||h.getTeamId() == null||!mmoSimpleRole.getTeamId().equals(h.getTeamId())) {
                    targets.add(h);
                }
            }
        } else {
            //单体攻击 在场景中 只能打怪物和玩家
            Role role;
            if (roleType.equals(RoleTypeCode.ENEMY.getCode())) {
                role = NpcMessageCache.getInstance().get(targetId);
            } else if (roleType.equals(RoleTypeCode.PLAYER.getCode())) {
                role = OnlineRoleMessageCache.getInstance().get(targetId);
            } else {
                role = null;
            }

            if (role == null) {
                throw new RpgServerException(StateCode.FAIL,"当前场景没有该id的角色或者选择了攻击npc");
            }
            if (!role.getMmoSceneId().equals(mmoSimpleRole.getMmoSceneId())) {
                throw new RpgServerException(StateCode.FAIL,"当前场景没有该id的角色");
            }
            if (mmoSimpleRole.getTeamId() != null) {
                TeamBean teamBean = TeamServiceProvider.getTeamBeanByTeamId(mmoSimpleRole.getTeamId());
                if (teamBean.getMmoSimpleRoles().contains(role)) {
                    throw new RpgServerException(StateCode.FAIL,"该角色是队友啊，兄弟");
                }
            }
            targets.add(role);
        }
        return targets;
    }

    /**
     * description 副本中目标
     * @param mmoSimpleRole
     * @return {@link List< Role> }
     * @author lqhao
     * @createTime 2021/1/21 12:18
     */
    private List<Role> findTargetInCopyScene(MmoSimpleRole mmoSimpleRole) {
        ArrayList<Role> target = new ArrayList<>();
        //在副本中
        Integer copySceneBeanId = mmoSimpleRole.getCopySceneBeanId();
        CopySceneBean copySceneBean = CopySceneProvider.getCopySceneBeanById(copySceneBeanId);
        if (copySceneBean.getNowBoss() != null) {
            target.add(copySceneBean.getNowBoss());
        }
        return target;
    }

}
