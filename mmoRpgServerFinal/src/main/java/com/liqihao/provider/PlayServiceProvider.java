package com.liqihao.provider;

import com.liqihao.cache.*;
import com.liqihao.cache.base.MmoBaseMessageCache;
import com.liqihao.commons.enums.ArticleTypeCode;
import com.liqihao.commons.enums.RoleOnStatusCode;
import com.liqihao.commons.enums.RoleStatusCode;
import com.liqihao.dao.*;
import com.liqihao.pojo.*;
import com.liqihao.pojo.baseMessage.DetailBaseMessage;
import com.liqihao.pojo.baseMessage.EquipmentMessage;
import com.liqihao.pojo.baseMessage.MedicineMessage;
import com.liqihao.pojo.baseMessage.RoleBaseMessage;
import com.liqihao.pojo.bean.BackPackManager;
import com.liqihao.pojo.bean.EmailBean;
import com.liqihao.pojo.bean.articleBean.EquipmentBean;
import com.liqihao.pojo.bean.articleBean.MedicineBean;
import com.liqihao.pojo.bean.guildBean.GuildBean;
import com.liqihao.pojo.bean.roleBean.MmoSimpleRole;
import com.liqihao.pojo.bean.teamBean.TeamBean;
import com.liqihao.util.CommonsUtil;
import com.liqihao.util.ScheduledThreadPoolUtil;
import io.netty.channel.Channel;
import io.netty.util.AttributeKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;

/**
 * @Classname PlayServiceProvider
 * @Description 玩家服务提供者
 * @Author lqhao
 * @Date 2021/1/27 20:56
 * @Version 1.0
 */
@Component
public class PlayServiceProvider {
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

    public MmoSimpleRole initMmoPeople(MmoRolePOJO role){
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
            EquipmentMessage message = EquipmentMessageCache.getInstance().get(equipmentBean.getArticleMessageId());
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
        return simpleRole;
    }

    /**
     * description 注册用户
     * @param roleName
     * @param professionId
     * @param username
     * @param password
     * @return {@link null }
     * @author lqhao
     * @createTime 2021/2/1 11:57
     */
    public void registerRole(String roleName, Integer professionId, String username, String password) {
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
    }

    /**
     * description 退出登陆
     * @param role
     * @return {@link null }
     * @author lqhao
     * @createTime 2021/2/1 11:56
     */
    public void logout(MmoSimpleRole role) {
        ChannelMessageCache.getInstance().remove(role.getId());
        AttributeKey<MmoSimpleRole> key = AttributeKey.valueOf("role");
        Channel channel=role.getChannel();
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
    }
}
