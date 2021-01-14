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
import com.liqihao.provider.CopySceneProvider;
import com.liqihao.provider.GuildServiceProvider;
import com.liqihao.provider.TeamServiceProvider;
import com.liqihao.service.PlayService;
import com.liqihao.util.CommonsUtil;
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
    @Autowired
    private MmoEmailPOJOMapper emailPOJOMapper;

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
        mmoRolePOJO.setMmoSceneId(1);
        mmoRolePOJO.setStatus(RoleStatusCode.ALIVE.getCode());
        mmoRolePOJO.setOnStatus(RoleOnStatusCode.EXIT.getCode());
        mmoRolePOJO.setType(RoleTypeCode.PLAYER.getCode());
        //职业
        mmoRolePOJO.setProfessionId(1);
        mmoRolePOJO.setGuildId(-1);
        mmoRolePOJOMapper.insert(mmoRolePOJO);
        //角色表也新增该用户
        RoleMessageCache.getInstance().put(mmoRolePOJO.getId(),mmoRolePOJO);
        MmoUserPOJO mmoUserPOJO = new MmoUserPOJO();
        mmoUserPOJO.setUserRoleId(mmoRolePOJO.getId().toString());
        mmoUserPOJO.setUserName(username);
        mmoUserPOJO.setUserPwd(password);
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
    public void loginRequest(PlayModel.PlayModelMessage myMessage, Channel channel) throws RpgServerException {
        String username = myMessage.getLoginRequest().getUsername();
        String password = myMessage.getLoginRequest().getPassword();
        Integer mmoUserId = mmoUserPOJOMapper.checkByUernameAndPassword(username, password);
        if (null == mmoUserId || mmoUserId < 0) {
            throw new RpgServerException(StateCode.FAIL,"密码错误or账号错误");
        }
        //将角色设置为在线模式
        MmoUserPOJO mmoUserPOJO = mmoUserPOJOMapper.selectByPrimaryKey(mmoUserId);
        //从数据库中读取角色,且修改其为在线模式，放入角色在线集合
        MmoRolePOJO role = mmoRolePOJOMapper.selectByPrimaryKey(Integer.parseInt(mmoUserPOJO.getUserRoleId()));
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
                MmoEquipmentPOJO mmoEquipmentPOJO = equipmentPOJOMapper.selectByPrimaryKey(mmoBagPOJO.getwId());
                EquipmentMessage message = EquipmentMessageCache.getInstance().get(mmoEquipmentPOJO.getMessageId());
                EquipmentBean equipmentBean = CommonsUtil.equipmentMessageToEquipmentBean(message);
                equipmentBean.setQuantity(mmoBagPOJO.getNumber());
                equipmentBean.setEquipmentId(mmoEquipmentPOJO.getId());
                equipmentBean.setBagId(mmoBagPOJO.getBagId());
                equipmentBean.setNowDurability(mmoEquipmentPOJO.getNowDurability());
                backPackManager.putOnDatabase(equipmentBean);
            } else if (mmoBagPOJO.getArticleType().equals(ArticleTypeCode.MEDICINE.getCode())) {
                MedicineMessage message = MediceneMessageCache.getInstance().get(mmoBagPOJO.getwId());
                MedicineBean medicineBean = CommonsUtil.medicineMessageToMedicineBean(message);
                medicineBean.setQuantity(mmoBagPOJO.getNumber());
                medicineBean.setBagId(mmoBagPOJO.getBagId());
                backPackManager.putOnDatabase(medicineBean);
            } else {
            }
        }
        simpleRole.setBackpackManager(backPackManager);
        //初始化收件邮箱信息
        List<MmoEmailPOJO> toEmailPOJOS = emailPOJOMapper.selectByToRoleId(role.getId());
        for (MmoEmailPOJO m : toEmailPOJOS) {
            MmoEmailBean emailBean = CommonsUtil.emailPOJOToMmoEmailBean(m);
            simpleRole.getToMmoEmailBeanConcurrentHashMap().put(emailBean.getId(), emailBean);
        }
        //初始化已发送邮箱信息
        List<MmoEmailPOJO> fromEmailPOJOS = emailPOJOMapper.selectByFromRoleId(role.getId());
        for (MmoEmailPOJO m : fromEmailPOJOS) {
            MmoEmailBean emailBean = CommonsUtil.emailPOJOToMmoEmailBean(m);
            simpleRole.getFromMmoEmailBeanConcurrentHashMap().put(emailBean.getId(), emailBean);
        }
        //初始化装备栏
        List<MmoEquipmentBagPOJO> equipmentBagPOJOS = equipmentBagPOJOMapper.selectByRoleId(role.getId());
        HashMap<Integer, EquipmentBean> equipmentBeanConcurrentHashMap = simpleRole.getEquipmentBeanHashMap();
        for (MmoEquipmentBagPOJO m : equipmentBagPOJOS) {
            MmoEquipmentPOJO mmoEquipmentPOJO = equipmentPOJOMapper.selectByPrimaryKey(m.getEquipmentId());
            EquipmentMessage message = EquipmentMessageCache.getInstance().get(mmoEquipmentPOJO.getMessageId());
            EquipmentBean equipmentBean = CommonsUtil.equipmentMessageToEquipmentBean(message);
            equipmentBean.setNowDurability(mmoEquipmentPOJO.getNowDurability());
            equipmentBean.setEquipmentId(m.getEquipmentId());
            equipmentBean.setEquipmentBagId(m.getEquipmentBagId());
            equipmentBeanConcurrentHashMap.put(message.getPosition(), equipmentBean);
            //修改人物属性
            simpleRole.setAttack(simpleRole.getAttack() + message.getAttackAdd());
            simpleRole.setDamageAdd(simpleRole.getDamageAdd() + message.getDamageAdd());
        }
        //初始化公会信息
        if (role.getGuildId()!=-1){
            GuildBean guildBean=GuildServiceProvider.getInstance().getGuildBeanById(role.getGuildId());
            simpleRole.setGuildBean(guildBean);
        }
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
                .setSceneId(simpleRole.getMmoSceneId())
                .setNowMp(simpleRole.getNowMp())
                .setTeamId(simpleRole.getTeamId() == null ? -1 : simpleRole.getTeamId())
                .setAttack(simpleRole.getAttack())
                .setAttackAdd(simpleRole.getDamageAdd())
                .setMoney(simpleRole.getMoney())
                .setProfessionId(simpleRole.getProfessionId())
                .setGuildName(simpleRole.getGuildBean()==null?"":simpleRole.getGuildBean().getName())
                .setGuildId(simpleRole.getGuildBean()==null?-1:simpleRole.getGuildBean().getId())
                .build();
        loginResponseBuilder.setRoleDto(roleDTO);
        //场景信息
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
        //获取信息
        List<Role> sceneRoles = new ArrayList<>();
        SceneBean sceneBean = SceneBeanMessageCache.getInstance().get(simpleRole.getMmoSceneId());
        //NPC
        for (Integer id : sceneBean.getNpcs()) {
            MmoSimpleNPC temp = NpcMessageCache.getInstance().get(id);
            MmoSimpleRole roleTemp = new MmoSimpleRole();
            roleTemp.setId(temp.getId());
            roleTemp.setName(temp.getName());
            roleTemp.setStatus(temp.getStatus());
            roleTemp.setType(temp.getType());
            roleTemp.setOnStatus(temp.getOnStatus());
            roleTemp.setHp(temp.getHp());
            roleTemp.setNowHp(temp.getNowHp());
            roleTemp.setMp(temp.getMp());
            roleTemp.setNowMp(temp.getNowMp());
            roleTemp.setMmoSceneId(temp.getMmoSceneId());
            roleTemp.setAttack(temp.getAttack());
            roleTemp.setDamageAdd(temp.getDamageAdd());
            sceneRoles.add(roleTemp);
        }
        //ROLES
        for (Integer id : sceneBean.getRoles()) {
            MmoSimpleRole temp = OnlineRoleMessageCache.getInstance().get(id);
            sceneRoles.add(temp);
        }
        //helper
        if (sceneBean.getHelperBeans().size() > 0) {
            for (MmoHelperBean helperBean : sceneBean.getHelperBeans()) {
                sceneRoles.add(helperBean);
            }
        }
        //发送给场景中其他角色 有角色登陆
        CommonsUtil.sendRoleResponse(sceneRoles, simpleRole.getMmoSceneId(), null);
        return;
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
        //保存背包信息入数据库
        CommonsUtil.bagIntoDataBase(role.getBackpackManager(), role.getId());
        CommonsUtil.equipmentIntoDataBase(role);
        CommonsUtil.roleInfoIntoDataBase(role);
        for (MmoEmailBean m : role.getFromMmoEmailBeanConcurrentHashMap().values()) {
            CommonsUtil.mmoEmailPOJOIntoDataBase(m);
        }
        for (MmoEmailBean m : role.getToMmoEmailBeanConcurrentHashMap().values()) {
            CommonsUtil.mmoEmailPOJOIntoDataBase(m);
        }
        //将数据库中设置为离线
        MmoRolePOJO mmoRolePOJO = mmoRolePOJOMapper.selectByPrimaryKey(role.getId());
        mmoRolePOJO.setOnStatus(RoleOnStatusCode.EXIT.getCode());
        mmoRolePOJOMapper.updateByPrimaryKeySelective(mmoRolePOJO);
        //缓存角色集合删除
        OnlineRoleMessageCache.getInstance().remove(role.getId());
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
        return;
    }

    @Override
    @HandlerCmdTag(cmd = ConstantValue.USE_SKILL_REQUEST, module = ConstantValue.PLAY_MODULE)
    public void useSkillRequest(PlayModel.PlayModelMessage myMessage, MmoSimpleRole mmoSimpleRole) throws Exception {
        Integer skillId = myMessage.getUseSkillRequest().getSkillId();
        Integer targetId = myMessage.getUseSkillRequest().getRoleId();
        Integer roleType = myMessage.getUseSkillRequest().getRoleType();
        Channel channel = ChannelMessageCache.getInstance().get(mmoSimpleRole.getId());
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
        //判断武器耐久是否足够
        if (mmoSimpleRole.getEquipmentBeanHashMap().get(PositionCode.ARMS.getCode()) != null && mmoSimpleRole.getEquipmentBeanHashMap().get(PositionCode.ARMS.getCode()).getNowDurability() <= 0) {
            throw new RpgServerException(StateCode.FAIL,"武器耐久度为0，请脱落武器再攻击");
        }
        //判断是单体技能 还是群体技能   可以攻击所有玩家 除了队友 npc
        //从缓存中查找出 怪物
        SkillBean skillBean = mmoSimpleRole.getSkillBeanBySkillId(skillId);
        if (skillBean.getSkillAttackType().equals(SkillAttackTypeCode.CALL.getCode())) {
            mmoSimpleRole.useSkill(null, skillId);
            return;
        }
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
     * 场景中的目标
     *
     * @param mmoSimpleRole
     * @param roleType
     * @param targetId
     * @return
     * @throws Exception
     */
    private List<Role> findTargetInScene(MmoSimpleRole mmoSimpleRole, Integer roleType, Integer targetId) throws Exception {
        ArrayList<Role> target = new ArrayList<>();
        //在场景中
        if (targetId == -1) {
            //群攻
            //可以攻击所有场景的人 除了队友 npc
            SceneBean sceneBean = SceneBeanMessageCache.getInstance().get(mmoSimpleRole.getMmoSceneId());
            //npc
            for (Integer id : sceneBean.getNpcs()) {
                MmoSimpleNPC npc = NpcMessageCache.getInstance().get(id);
                if (npc.getType().equals(RoleTypeCode.ENEMY.getCode())) {
                    target.add(npc);
                }
            }
            //people
            for (Integer id : sceneBean.getRoles()) {
                MmoSimpleRole role = OnlineRoleMessageCache.getInstance().get(id);
                if (role.getId().equals(mmoSimpleRole.getId())) {
                    continue;
                }
                if (mmoSimpleRole.getTeamId() == null) {
                    target.add(role);
                } else {
                    if (role.getTeamId() == null) {
                        target.add(role);
                    } else if (!mmoSimpleRole.getTeamId().equals(role.getTeamId())) {
                        target.add(role);
                    }
                }
            }
            //hepler
            for (MmoHelperBean h : sceneBean.getHelperBeans()) {
                if (mmoSimpleRole.getTeamId() == null) {
                    target.add(h);
                } else {
                    if (h.getTeamId() == null) {
                        target.add(h);
                    } else if (!mmoSimpleRole.getTeamId().equals(h.getTeamId())) {
                        target.add(h);
                    }
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
            target.add(role);
        }
        return target;
    }

    /**
     * 副本中目标
     *
     * @param mmoSimpleRole
     * @return
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
