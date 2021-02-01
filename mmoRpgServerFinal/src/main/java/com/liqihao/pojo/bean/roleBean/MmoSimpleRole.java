package com.liqihao.pojo.bean.roleBean;


import com.googlecode.protobuf.format.JsonFormat;
import com.liqihao.Cache.*;
import com.liqihao.commons.ConstantValue;
import com.liqihao.commons.NettyResponse;
import com.liqihao.commons.RpgServerException;
import com.liqihao.commons.StateCode;
import com.liqihao.commons.enums.*;
import com.liqihao.pojo.*;
import com.liqihao.pojo.baseMessage.*;
import com.liqihao.pojo.bean.*;
import com.liqihao.pojo.bean.friendBean.FriendApplyBean;
import com.liqihao.pojo.bean.taskBean.TaskManager;
import com.liqihao.pojo.bean.articleBean.Article;
import com.liqihao.pojo.bean.articleBean.EquipmentBean;
import com.liqihao.pojo.bean.buffBean.BaseBuffBean;
import com.liqihao.pojo.bean.guildBean.GuildBean;
import com.liqihao.pojo.bean.taskBean.guildFirstTask.GuildFirstAction;
import com.liqihao.pojo.bean.taskBean.moneyNumTask.MoneyTaskAction;
import com.liqihao.pojo.bean.taskBean.pkFirstTask.PkFirstTaskAction;
import com.liqihao.pojo.bean.taskBean.sceneFirstTask.SceneTaskAction;
import com.liqihao.pojo.bean.taskBean.skillTask.SkillTaskAction;
import com.liqihao.pojo.bean.teamBean.TeamApplyOrInviteBean;
import com.liqihao.pojo.dto.EquipmentDto;
import com.liqihao.protobufObject.ChatModel;
import com.liqihao.protobufObject.PlayModel;
import com.liqihao.provider.*;
import com.liqihao.util.*;
import io.netty.channel.Channel;
import org.apache.log4j.Logger;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;


/**
 * 缓存中存储的人物类
 *
 * @author lqhao
 */
public class MmoSimpleRole extends Role implements MyObserver {
    /**
     * 召唤兽
     */
    private MmoHelperBean mmoHelperBean;
    /**
     * 技能id
     */
    private List<Integer> skillIdList;
    /**
     * 技能实体bean
     */
    private List<SkillBean> skillBeans;

    /**
     * CD Map
     */
    private volatile HashMap<Integer, Long> cdMap;
    /**
     * 背包管理器
     */
    private BackPackManager backpackManager;
    /**
     * 需要删除的装备栏 装备id
     */
    private List<Integer> needDeleteEquipmentIds = new ArrayList<>();
    /**
     * 上一个场景id
     */
    private Integer lastSceneId;
    /**
     * 队伍邀请数量
     */
    private Integer teamApplyOrInviteSize;
    /**
     * 金币
     */
    private Integer money;
    /**
     * 职业id
     */
    private Integer professionId;
    /**
     * 装备栏
     */
    private HashMap<Integer, EquipmentBean> equipmentBeanHashMap;
    /**
     * 非副本beanId 而是副本基本信息id
     */
    private Integer copySceneId;
    /**
     * 队伍邀请函
     */
    private ConcurrentLinkedQueue<TeamApplyOrInviteBean> teamApplyOrInviteBeans = new ConcurrentLinkedQueue<>();
    /**
     * 已发送邮件
     */
    private ConcurrentHashMap<Integer, EmailBean> fromMmoEmailBeanConcurrentHashMap=new ConcurrentHashMap<>();
    /**
     * 已接受邮件
     */
    private ConcurrentHashMap<Integer, EmailBean> toMmoEmailBeanConcurrentHashMap=new ConcurrentHashMap<>();
    /**
     * 角色channel
     */
    private Channel channel;
    /**
     * 公会
     */
    private GuildBean guildBean;
    /**
     *  是否在交易中
     */
    private boolean onDeal;
    /**
     * 交易beanId
     */
    private Integer dealBeanId;
    /**
     * 任务管理
     */
    private TaskManager taskManager;

    /**
     * 好友
     */
    private List<Integer> friends;

    /**
     * 好友申请
     */
    private HashMap<Integer,FriendApplyBean> friendApplyBeanHashMap=new HashMap<>();

    /**
     * 好友申请自增id
     */
    private AtomicInteger friendApplyIdAuto=new AtomicInteger(0);

    public AtomicInteger getFriendApplyIdAuto() {
        return friendApplyIdAuto;
    }

    public void setFriendApplyIdAuto(AtomicInteger friendApplyIdAuto) {
        this.friendApplyIdAuto = friendApplyIdAuto;
    }

    public void setFriendApplyBeanHashMap(HashMap<Integer, FriendApplyBean> friendApplyBeanHashMap) {
        this.friendApplyBeanHashMap = friendApplyBeanHashMap;
    }

    public HashMap<Integer, FriendApplyBean> getFriendApplyBeanHashMap() {
        return friendApplyBeanHashMap;
    }


    public boolean isOnDeal() {
        return onDeal;
    }

    public List<Integer> getFriends() {
        return friends;
    }

    public void setFriends(List<Integer> friends) {
        this.friends = friends;
    }

    public TaskManager getTaskManager() {
        return taskManager;
    }

    public void setTaskManager(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    public Integer getDealBeanId() {
        return dealBeanId;
    }

    public void setDealBeanId(Integer dealBeanId) {
        this.dealBeanId = dealBeanId;
    }

    public boolean getOnDeal() {
        return onDeal;
    }

    public void setOnDeal(boolean onDeal) {
        this.onDeal = onDeal;
    }

    public GuildBean getGuildBean() {
        return guildBean;
    }

    public void setGuildBean(GuildBean guildBean) {
        this.guildBean = guildBean;
        GuildFirstAction guildFirstAction=new GuildFirstAction();
        guildFirstAction.setTaskTargetType(TaskTargetTypeCode.FIRST_TIME_GUILD.getCode());
        getTaskManager().handler(guildFirstAction,this);
    }

    public Channel getChannel() {
        return channel;
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
    }

    public MmoHelperBean getMmoHelperBean() {
        return mmoHelperBean;
    }

    public void setMmoHelperBean(MmoHelperBean mmoHelperBean) {
        this.mmoHelperBean = mmoHelperBean;
    }

    public Integer getMoney() {
        return money;
    }

    public void setMoney(Integer money) {
        //任务条件触发
        if (getMoney()!=null) {
            Integer addMoney = money - getMoney();
            if (addMoney > 0) {
                MoneyTaskAction moneyTaskAction = new MoneyTaskAction();
                moneyTaskAction.setMoneyAddNum(addMoney);
                moneyTaskAction.setTaskTargetType(TaskTargetTypeCode.Money.getCode());
                getTaskManager().handler(moneyTaskAction, this);
            }
        }
        this.money = money;
    }

    public ConcurrentHashMap<Integer, EmailBean> getFromMmoEmailBeanConcurrentHashMap() {
        return fromMmoEmailBeanConcurrentHashMap;
    }

    public void setFromMmoEmailBeanConcurrentHashMap(ConcurrentHashMap<Integer, EmailBean> fromMmoEmailBeanConcurrentHashMap) {
        this.fromMmoEmailBeanConcurrentHashMap = fromMmoEmailBeanConcurrentHashMap;
    }

    public ConcurrentHashMap<Integer, EmailBean> getToMmoEmailBeanConcurrentHashMap() {
        return toMmoEmailBeanConcurrentHashMap;
    }

    public void setToMmoEmailBeanConcurrentHashMap(ConcurrentHashMap<Integer, EmailBean> toMmoEmailBeanConcurrentHashMap) {
        this.toMmoEmailBeanConcurrentHashMap = toMmoEmailBeanConcurrentHashMap;
    }

    public ConcurrentLinkedQueue<TeamApplyOrInviteBean> getTeamApplyOrInviteBeans() {
        return teamApplyOrInviteBeans;
    }

    public void setTeamApplyOrInviteBeans(ConcurrentLinkedQueue<TeamApplyOrInviteBean> teamApplyOrInviteBeans) {
        this.teamApplyOrInviteBeans = teamApplyOrInviteBeans;
    }

    public Integer getTeamApplyOrInviteSize() {
        return teamApplyOrInviteSize;
    }

    public void setTeamApplyOrInviteSize(Integer teamApplyOrInviteSize) {
        this.teamApplyOrInviteSize = teamApplyOrInviteSize;
    }
    public Integer getLastSceneId() {
        return lastSceneId;
    }

    public void setLastSceneId(Integer lastSceneId) {
        this.lastSceneId = lastSceneId;
    }

    public Integer getCopySceneId() {
        return copySceneId;
    }

    public void setCopySceneId(Integer copySceneId) {
        this.copySceneId = copySceneId;
    }

    public List<Integer> getNeedDeleteEquipmentIds() {
        return needDeleteEquipmentIds;
    }

    public void setNeedDeleteEquipmentIds(List<Integer> needDeleteEquipmentIds) {
        this.needDeleteEquipmentIds = needDeleteEquipmentIds;
    }

    public HashMap<Integer, EquipmentBean> getEquipmentBeanHashMap() {
        return equipmentBeanHashMap;
    }

    public void setEquipmentBeanHashMap(HashMap<Integer, EquipmentBean> equipmentBeanHashMap) {
        this.equipmentBeanHashMap = equipmentBeanHashMap;
    }

    public BackPackManager getBackpackManager() {
        return backpackManager;
    }

    public void setBackpackManager(BackPackManager backpackManager) {
        this.backpackManager = backpackManager;
    }

    public List<SkillBean> getSkillBeans() {
        return skillBeans;
    }

    public void setSkillBeans(List<SkillBean> skillBeans) {
        this.skillBeans = skillBeans;
    }


    public List<Integer> getSkillIdList() {
        return skillIdList;
    }

    public void setSkillIdList(List<Integer> skillIdList) {
        this.skillIdList = skillIdList;
    }

    public HashMap<Integer, Long> getCdMap() {
        return cdMap;
    }

    public void setCdMap(HashMap<Integer, Long> cdMap) {
        this.cdMap = cdMap;
    }

    public Integer getProfessionId() {
        return professionId;
    }

    public void setProfessionId(Integer professionId) {
        this.professionId = professionId;
    }

    /**
     * 增加邀请
     * @param teamApplyOrInviteBean
     */
    public void addTeamApplyOrInviteBean(TeamApplyOrInviteBean teamApplyOrInviteBean) {
        checkOutTime();
        //邀请的大小，先进先出咯
        if (teamApplyOrInviteBeans.size() >= teamApplyOrInviteSize) {
            teamApplyOrInviteBeans.poll();
        }
        teamApplyOrInviteBeans.add(teamApplyOrInviteBean);
    }
    /**
     * 检测邀请过时
     */
    private void checkOutTime() {
        Iterator iterator = teamApplyOrInviteBeans.iterator();
        //每次插入都删除申请过时或者
        while (iterator.hasNext()) {
            TeamApplyOrInviteBean bean = (TeamApplyOrInviteBean) iterator.next();
            if (bean.getEndTime() < System.currentTimeMillis()) {
                teamApplyOrInviteBeans.remove(bean);
            }
        }
    }

    /**
     * 初始化对象
     *
     * @param role
     * @param baseRoleMessage
     */
    public void init(MmoRolePOJO role, RoleBaseMessage baseRoleMessage) {
        setId(role.getId());
        setMmoSceneId(role.getMmoSceneId());
        setName(role.getName());
        setOnStatus(role.getOnStatus());
        setStatus(role.getStatus());
        setType(role.getType());
        setExp(role.getExp());
        setLevel(getExp()/10);
        setHp(baseRoleMessage.getHp());
        setNowHp(baseRoleMessage.getHp());
        setMp(baseRoleMessage.getMp());
        setMoney(role.getMoney());
        setProfessionId(role.getProfessionId());
        setDamageAdd(baseRoleMessage.getDamageAdd());
        setNowMp(baseRoleMessage.getMp());
        setAttack(baseRoleMessage.getAttack());
        //根据职业获取技能
        List<Integer> skillIds = new ArrayList();
        if (role.getSkillIds()!=null){
            skillIds.addAll(CommonsUtil.split(role.getSkillIds()));
        }
        //根据职业id获取技能
        ProfessionMessage professionMessage=ProfessionMessageCache.getInstance().get(professionId);
        skillIds.addAll(CommonsUtil.split(professionMessage.getSkillIds()));
        setSkillIdList(skillIds);
        List<SkillBean> skillBeans = CommonsUtil.skillIdsToSkillBeans(skillIds);
        setSkillBeans(skillBeans);
        setCdMap(new HashMap<Integer, Long>());
        setBufferBeans(new CopyOnWriteArrayList<>());
        setEquipmentBeanHashMap(new HashMap<>());
    }

    /**
     * 使用道具
     */
    public Boolean useArticle(Integer articleId) {
        Article article;
        synchronized (backpackManager) {
            article = backpackManager.getArticleByArticleId(articleId);
            if (article == null) {
                return false;
            }
            backpackManager.useOrAbandonArticle(articleId, 1, getId());
        }
        return article.use(getBackpackManager(), this);
    }


    /**
     * 脱装备
     */
    public Boolean unUseEquipment(Integer position) throws Exception {
        //判断该位置是否有装备
        EquipmentBean equipmentBean = getEquipmentBeanHashMap().get(position);
        //锁住背包
        synchronized (backpackManager) {
            if (equipmentBean == null) {
                //无装备
                return false;
            } else {
                EquipmentMessage equipmentMessage=EquipmentMessageCache.getInstance().get(equipmentBean.getArticleMessageId());
                equipmentBeanHashMap.remove(position);
                //装备栏数据库减少该装备
                if (equipmentBean.getEquipmentBagId() != null) {
                    Integer bagId=equipmentBean.getEquipmentBagId();
                     DbUtil.deleteEquipmentBagById(bagId);
                }
                //装备栏id为null
                equipmentBean.setEquipmentBagId(null);
                //放入背包
                if (!backpackManager.canPutArticle(equipmentBean.getArticleMessageId(),equipmentBean.getArticleTypeCode(),equipmentBean.getQuantity())){
                    throw new RpgServerException(StateCode.FAIL,"背包已经满了");
                }
                backpackManager.put(equipmentBean,getId());
                setAttack(getAttack() - equipmentMessage.getAttackAdd());
                setDamageAdd(getDamageAdd() - equipmentMessage.getDamageAdd());
                //设置装备星级
                Integer olderEquipmentLevel=getEquipmentLevel();
                olderEquipmentLevel=olderEquipmentLevel-equipmentMessage.getEquipmentLevel();
                changeEquipmentLevel(olderEquipmentLevel);
                //数据库
                return true;
            }
        }
    }

    /**
     * 获取装备栏所有信息
     */
    public List<EquipmentDto> getEquipments() {
        List<EquipmentDto> equipmentDtos = new ArrayList<>();
        for (EquipmentBean bean : equipmentBeanHashMap.values()) {
            EquipmentMessage equipmentMessage= EquipmentMessageCache.getInstance().get(bean.getArticleMessageId());
            EquipmentDto equipmentDto = new EquipmentDto();
            equipmentDto.setId(bean.getArticleMessageId());
            equipmentDto.setNowDurability(bean.getNowDurability());
            equipmentDto.setPosition(equipmentMessage.getPosition());
            equipmentDto.setEquipmentBagId(bean.getEquipmentBagId());
            equipmentDto.setEquipmentId(bean.getEquipmentId());
            equipmentDto.setEquipmentLevel(equipmentMessage.getEquipmentLevel());
            equipmentDtos.add(equipmentDto);
        }
        return equipmentDtos;
    }

    /**
     * 根据skillI获取技能
     */
    public SkillBean getSkillBeanBySkillId(Integer skillId) {
        for (SkillBean b : getSkillBeans()) {
            if (b.getId().equals(skillId)) {
                return b;
            }
        }
        return null;
    }

    /**
     * 使用技能
     */
    public  void useSkill(List<Role> target, Integer skillId) {
        //任务条件触发
        SkillTaskAction skillTaskAction=new SkillTaskAction();
        skillTaskAction.setSkillId(skillId);
        skillTaskAction.setTaskTargetType(TaskTargetTypeCode.SKILL.getCode());
        this.getTaskManager().handler(skillTaskAction,this);
        //技能逻辑
        SkillBean skillBean = getSkillBeanBySkillId(skillId);
        //武器耐久度-2
        EquipmentBean equipmentBean = this.getEquipmentBeanHashMap().get(PositionCode.ARMS.getCode());
        if (equipmentBean != null) {
            equipmentBean.setNowDurability(equipmentBean.getNowDurability() - 2);
            if (equipmentBean.getNowDurability() < 0) {
                equipmentBean.setNowDurability(0);
            }
        }
        if (skillBean.getConsumeType().equals(ConsumeTypeCode.HP.getCode())) {
            //扣血
            setNowHp(getNowHp() - skillBean.getConsumeNum());

        } else {
            //扣篮
            setNowMp(getNowMp() - skillBean.getConsumeNum());
            //判断是否已经有自动回蓝任务
            ConcurrentHashMap<String, ScheduledFuture<?>> replyMpRoleMap = ScheduledThreadPoolUtil.getReplyMpRole();
            //自动回蓝任务的key
            String key = getId() + "AUTO_MP";
            if (!replyMpRoleMap.containsKey(key)) {
                //number为空 代表着自动回蓝
                ScheduledThreadPoolUtil.ReplyMpTask replyMpTask = new ScheduledThreadPoolUtil.ReplyMpTask(this, null, DamageTypeCode.MP.getCode(), key);
                // 周期性执行，每5秒执行一次
                ScheduledFuture<?> t = ScheduledThreadPoolUtil.getScheduledExecutorService().scheduleAtFixedRate(replyMpTask, 0, 5, TimeUnit.SECONDS);
                replyMpRoleMap.put(key, t);
            }
        }
        List<PlayModel.RoleIdDamage> list = new ArrayList<>();
        // 生成一个角色扣血或者扣篮
        PlayModel.RoleIdDamage.Builder damageU = PlayModel.RoleIdDamage.newBuilder();
        damageU.setFromRoleId(getId());
        damageU.setToRoleId(getId());
        damageU.setToRoleType(RoleTypeCode.PLAYER.getCode());
        damageU.setFromRoleType(RoleTypeCode.PLAYER.getCode());
        damageU.setArticleId(-1);
        damageU.setArticleType(-1);
        damageU.setAttackStyle(AttackStyleCode.USE_SKILL.getCode());
        damageU.setBufferId(-1);
        damageU.setDamage(skillBean.getConsumeNum());
        damageU.setDamageType(skillBean.getConsumeType());
        damageU.setMp(getNowMp());
        damageU.setNowblood(getNowHp());
        damageU.setSkillId(skillBean.getId());
        damageU.setState(getStatus());
        list.add(damageU.build());
        PlayModel.PlayModelMessage.Builder myMessageBuilder=PlayModel.PlayModelMessage.newBuilder();
        myMessageBuilder.setDataType(PlayModel.PlayModelMessage.DateType.UseSkillResponse);
        PlayModel.UseSkillResponse.Builder useSkillBuilder=PlayModel.UseSkillResponse.newBuilder();
        useSkillBuilder.addAllRoleIdDamages(list);
        myMessageBuilder.setUseSkillResponse(useSkillBuilder.build());
        NettyResponse nettyResponse=new NettyResponse();
        nettyResponse.setCmd(ConstantValue.USE_SKILL_RESPONSE);
        nettyResponse.setStateCode(StateCode.SUCCESS);
        nettyResponse.setData(myMessageBuilder.build().toByteArray());
        //广播
        String json= JsonFormat.printToString(myMessageBuilder.build());
        NotificationUtil.notificationSceneRole(nettyResponse,this,json);
        //cd
        Map<Integer, Long> map = getCdMap();
        Long time = System.currentTimeMillis();
        int addTime = skillBean.getCd() * 1000;
        map.put(skillBean.getId(), time + addTime);
        //吟唱时间
        if (skillBean.getChantTime()>0) {
            ScheduledThreadPoolUtil.SkillAttackTask skillAttackTask=new ScheduledThreadPoolUtil.SkillAttackTask(skillBean,target,this,mmoHelperBean);
            ScheduledThreadPoolUtil.getScheduledExecutorService().schedule(skillAttackTask,skillBean.getChantTime(),TimeUnit.SECONDS);
        }else{
            skill(skillBean,target);
        }

        //buffer
    }
    /**
     * 技能释放
     */
    public void skill(SkillBean skillBean, List<Role> target){
        if (!skillBean.getSkillAttackType().equals(SkillAttackTypeCode.CALL.getCode())) {
            for (Role r : target) {
                if (!skillBean.getBaseDamage().equals(0)) {
                    //伤害不为0才触发怪物的被攻击
                    r.beAttack(skillBean, this);
                }
                //伤害为0则是buffer技能 例如嘲讽
                //buffer
                for (Integer bufferId : skillBean.getBufferIds()) {
                    BufferMessage bufferMessage = BufferMessageCache.getInstance().get(bufferId);

                    skillBean.bufferToPeople(bufferMessage, this, r);
                }
            }
            if (target.size() > 0) {
                //触发宠物帮忙
                Role t = target.get(0);
                if (getMmoHelperBean() != null) {
                    getMmoHelperBean().npcAttack(t);
                }
            }
        } else {
            //召唤的逻辑
            MmoHelperBean helperBean = CallerServiceProvider.callHelper(this);
            MmoHelperBean needDeleteBean = this.getMmoHelperBean();
            if (needDeleteBean != null) {
                //已经有了则先消除
                if (this.getMmoSceneId() != null) {
                    this.setMmoHelperBean(null);
                    if (needDeleteBean.getMmoSceneId() != null) {
                        SceneBean sceneBean = SceneBeanMessageCache.getInstance().get(needDeleteBean.getMmoSceneId());
                        sceneBean.getHelperBeans().remove(needDeleteBean);
                    } else if (needDeleteBean.getCopySceneBeanId() != null) {
                        CopySceneProvider.getCopySceneBeanById(needDeleteBean.getCopySceneBeanId()).getRoles().remove(needDeleteBean);
                    }
                }
            }
            //新的召唤兽放到场景中

            this.setMmoHelperBean(helperBean);
            if (getMmoHelperBean().getMmoSceneId() != null) {
                SceneBean sceneBean = SceneBeanMessageCache.getInstance().get(getMmoHelperBean().getMmoSceneId());
                sceneBean.getHelperBeans().add(getMmoHelperBean());
                List<Role> newRoles=new ArrayList<>();
                newRoles.add(getMmoHelperBean());
                CommonsUtil.sendRoleResponse(newRoles,getMmoHelperBean().getMmoSceneId(),null);
            } else if (getMmoHelperBean().getCopySceneBeanId() != null) {
                CopySceneProvider.getCopySceneBeanById(getMmoHelperBean().getCopySceneBeanId()).getRoles().add(getMmoHelperBean());
                List<Role> newRoles=new ArrayList<>();
                newRoles.add(getMmoHelperBean());
                CommonsUtil.sendRoleResponse(newRoles,null,getMmoHelperBean().getCopySceneBeanId());
            }
        }
    }
    /**
     * 被攻击
     */
    @Override
    public void beAttack(SkillBean skillBean,Role fromRole) {
        Role role=this;
        Integer reduce = 0;
        if (skillBean.getSkillType().equals(SkillTypeCode.FIX.getCode())) {
            //固伤 只有技能伤害
            reduce = (int) Math.ceil(skillBean.getBaseDamage() * (1 + fromRole.getDamageAdd()));
            PlayModel.RoleIdDamage.Builder damageU = PlayModel.RoleIdDamage.newBuilder();
            damageU.setFromRoleId(fromRole.getId());
            damageU.setFromRoleType(fromRole.getType());
            damageU.setToRoleId(getId());
            damageU.setToRoleType(getType());
            damageU.setBufferId(-1);
            damageU.setDamageType(ConsumeTypeCode.HP.getCode());
            damageU.setSkillId(skillBean.getId());
            if(skillBean.getSkillDamageType().equals(SkillDamageTypeCode.ADD.getCode())) {
                damageU.setAttackStyle(AttackStyleCode.SKILL_ADD.getCode());
                changeNowBlood(reduce,damageU,AttackStyleCode.SKILL_ADD.getCode());
            }else{
                damageU.setAttackStyle(AttackStyleCode.ATTACK.getCode());
                changeNowBlood(-reduce,damageU,AttackStyleCode.USE_SKILL.getCode());
            }
        }else if (skillBean.getSkillType().equals(SkillTypeCode.PERCENTAGE.getCode())) {
            //百分比 按照攻击力比例增加
            Integer damage = skillBean.getBaseDamage();
            reduce = (int) Math.ceil(damage + fromRole.getAttack() * skillBean.getAddPerson());
            PlayModel.RoleIdDamage.Builder damageU = PlayModel.RoleIdDamage.newBuilder();
            damageU.setFromRoleId(fromRole.getId());
            damageU.setFromRoleType(fromRole.getType());
            damageU.setToRoleId(getId());
            damageU.setToRoleType(getType());
            damageU.setBufferId(-1);
            damageU.setDamageType(ConsumeTypeCode.HP.getCode());
            damageU.setSkillId(skillBean.getId());
            if(skillBean.getSkillDamageType().equals(SkillDamageTypeCode.ADD.getCode())) {
                damageU.setAttackStyle(AttackStyleCode.SKILL_ADD.getCode());
                changeNowBlood(reduce,damageU,AttackStyleCode.SKILL_ADD.getCode());
            }else{
                damageU.setAttackStyle(AttackStyleCode.ATTACK.getCode());
                changeNowBlood(-reduce,damageU,AttackStyleCode.USE_SKILL.getCode());
            }
        }
        //召唤兽攻击
        if (getMmoHelperBean() != null) {
            getMmoHelperBean().npcAttack(fromRole);
        }
    }

    @Override
    public void die(Role fromRole) {
        if(fromRole!=null&&fromRole.getType()==RoleTypeCode.PLAYER.getCode()) {
            PkFirstTaskAction pkFirstTaskAction=new PkFirstTaskAction();
            pkFirstTaskAction.setTaskTargetType(TaskTargetTypeCode.FIRST_TIME_PK.getCode());
            MmoSimpleRole role=OnlineRoleMessageCache.getInstance().get(fromRole.getId());
            if (role!=null) {
                role.getTaskManager().handler(pkFirstTaskAction, role);
            }
        }
        //重生到启始之地xinx 延时5s后复活  不在副本中得时候
        if (getMmoSceneId()!=null) {
            ScheduledThreadPoolUtil.PeopleRestartTask restartTask = new ScheduledThreadPoolUtil.PeopleRestartTask(this);
            ScheduledThreadPoolUtil.getScheduledExecutorService().schedule(restartTask, 5, TimeUnit.SECONDS);
        }
    }

    /**
     * 扣血
     */
    @Override
    public void changeNowBlood(int number, PlayModel.RoleIdDamage.Builder damageU, int type) {
        //获取对应线程的下标
        Channel channel = ChannelMessageCache.getInstance().get(getId());
        Integer index = CommonsUtil.getIndexByChannel(channel);
        if (type == AttackStyleCode.USE_SKILL.getCode()) {
            LogicThreadPool.getInstance().execute(new ChangeHpByAttackTask(number, this, damageU), index);
        } else if (type == AttackStyleCode.MEDICINE.getCode()){
            LogicThreadPool.getInstance().execute(new ChangeHpByMedicineTask(number, this, damageU), index);
        }else{
            LogicThreadPool.getInstance().execute(new ChangeHpByBufferTask(number,this,damageU),index);
        }
    }

    /**
     * 扣蓝
     */
    @Override
    public void changeMp(int number, PlayModel.RoleIdDamage.Builder damageU) {
        Channel channel = ChannelMessageCache.getInstance().get(getId());
        Integer index = CommonsUtil.getIndexByChannel(channel);
        LogicThreadPool.getInstance().execute(new ChangeMpTask(number, this, damageU), index);
    }

    /**
     * buffer影响
     * @param bufferBean
     */
    @Override
    public void effectByBuffer(BaseBuffBean bufferBean,Role fromRole) {
        //根据buffer类型扣血扣蓝
        bufferBean.effectToPeople(this);
    }

    /**
     * 前往场景
     * @param nextSceneId
     * @return
     */
    public List<Role> wentScene(Integer nextSceneId) {
        //修改scene 如果为null 则是刚从副本中出来
        if (getMmoSceneId() != null) {
            SceneBeanMessageCache.getInstance().get(getMmoSceneId()).getRoles().remove(getId());
            //召唤兽
            if (getMmoHelperBean()!=null){
                SceneBeanMessageCache.getInstance().get(getMmoSceneId()).getHelperBeans().remove(getMmoHelperBean());
            }
        }
        SceneBeanMessageCache.getInstance().get(nextSceneId).getRoles().add(getId());
        setMmoSceneId(nextSceneId);
        if (getMmoHelperBean()!=null){
            SceneBeanMessageCache.getInstance().get(nextSceneId).getHelperBeans().add(getMmoHelperBean());
            getMmoHelperBean().setMmoSceneId(nextSceneId);
        }
        //查询出npc 和SimpleRole
        List<Role> nextSceneRoles = new ArrayList<>();
        SceneBean nextScene = SceneBeanMessageCache.getInstance().get(nextSceneId);
        List<Integer> roles = nextScene.getRoles();
        List<Integer> npcs = nextScene.getNpcs();
        List<MmoHelperBean> helpers=nextScene.getHelperBeans();
        //NPC
        for (Integer npcId : npcs) {
            MmoSimpleNPC temp = NpcMessageCache.getInstance().get(npcId);
            nextSceneRoles.add(CommonsUtil.NpcToMmoSimpleRole(temp));
        }
        //ROLES
        for (Integer rId : roles) {
            MmoSimpleRole role = OnlineRoleMessageCache.getInstance().get(rId);
            if (role!=null) {
                nextSceneRoles.add(role);
            }
        }
        //Helper
        nextSceneRoles.addAll(helpers);
        List<Role> newRoles=new ArrayList<>();
        newRoles.add(this);
        //同步给场景中的角色 有用户来了
        CommonsUtil.sendRoleResponse(newRoles,nextSceneId,null);
        //任务条件触发
        SceneTaskAction sceneTaskAction=new SceneTaskAction();
        sceneTaskAction.setSceneId(nextSceneId);
        sceneTaskAction.setTaskTargetType(TaskTargetTypeCode.FIRST_TIME_SCENE.getCode());
        getTaskManager().handler(sceneTaskAction,this);
        //同步角色信息
        MmoSimpleRole role=this;
        DbUtil.updateRole(role);
        return nextSceneRoles;
    }

    /**
     * 获取邀请信息
     */
    public List<TeamApplyOrInviteBean> getInviteBeans() {
        checkOutTime();
        return teamApplyOrInviteBeans.stream().filter(e -> e.getType().equals(TeamApplyInviteCode.INVITE.getCode())).collect(Collectors.toList());
    }

    /**
     * 拒绝邀请
     */
    public TeamApplyOrInviteBean refuseInvite(Integer teamId) {
        checkOutTime();
        Iterator iterator = teamApplyOrInviteBeans.iterator();
        TeamApplyOrInviteBean teamApplyOrInviteBean = null;
        while (iterator.hasNext()) {
            teamApplyOrInviteBean = (TeamApplyOrInviteBean) iterator.next();
            if (teamApplyOrInviteBean.getTeamId().equals(teamId) &&
                    teamApplyOrInviteBean.getType().equals(TeamApplyInviteCode.INVITE.getCode())) {
                teamApplyOrInviteBeans.remove(teamApplyOrInviteBean);
                getTeamApplyOrInviteBeans().remove(teamApplyOrInviteBean);
                return teamApplyOrInviteBean;
            }
        }
        return null;
    }

    /**
     * 是否包含该邀请
     * @param teamId
     * @return
     */
    public TeamApplyOrInviteBean containInvite(Integer teamId) {
        checkOutTime();
        Iterator iterator = teamApplyOrInviteBeans.iterator();
        TeamApplyOrInviteBean teamApplyOrInviteBean = null;
        while (iterator.hasNext()) {
            teamApplyOrInviteBean = (TeamApplyOrInviteBean) iterator.next();
            if (teamApplyOrInviteBean.getTeamId().equals(teamId) &&
                    teamApplyOrInviteBean.getType().equals(TeamApplyInviteCode.INVITE.getCode())) {
                return teamApplyOrInviteBean;
            }
        }
        return teamApplyOrInviteBean;
    }

    /**
     * 前往副本
     * @param copySceneBean
     * @return
     */
    public Boolean wentCopyScene(CopySceneBean copySceneBean)  {
        synchronized (copySceneBean.getRoles()) {
            //从当前场景消失
            Integer sceneId = getMmoSceneId();
            SceneBean sceneBean = SceneBeanMessageCache.getInstance().get(sceneId);
            sceneBean.getRoles().remove(getId());
            setMmoSceneId(null);
            //召唤兽
            if (getMmoHelperBean() != null) {
                SceneBeanMessageCache.getInstance().get(sceneId).getHelperBeans().remove(getMmoHelperBean());
                getMmoHelperBean().setMmoSceneId(null);
                getMmoHelperBean().setCopySceneBeanId(copySceneBean.getCopySceneBeanId());
                copySceneBean.getRoles().add(getMmoHelperBean());
            }
            //人物设置副本
            this.setCopySceneId(copySceneBean.getCopySceneMessageId());
            this.setLastSceneId(sceneId);
            this.setCopySceneBeanId(copySceneBean.getCopySceneBeanId());
            //副本操作
            copySceneBean.getRoles().add(this);
            if (copySceneBean.getNowBoss() == null) {
                copySceneBean.bossComeOrFinish();
            }
            List<Role> newRoles = new ArrayList<>();
            newRoles.add(this);
            //同步给场景中的角色 有角色进入
            CommonsUtil.sendRoleResponse(newRoles, null, copySceneBean.getCopySceneBeanId());
            return true;
        }
    }

    @Override
    /**
     * 聊天
     */
    public void update(Role fromRole, String str,Integer chatType) {
        Channel c=ChannelMessageCache.getInstance().get(getId());
        if (c!=null){
            ChatModel.RoleDto roleDto=ChatModel.RoleDto.newBuilder()
                    .setId(fromRole.getId()).setName(fromRole.getName()).setOnStatus(fromRole.getOnStatus())
                    .setStatus(fromRole.getStatus()).setTeamId(fromRole.getTeamId()==null?-1:fromRole.getTeamId()).setType(fromRole.getType()).build();
            ChatModel.ChatModelMessage myMessage=ChatModel.ChatModelMessage.newBuilder()
                    .setDataType(ChatModel.ChatModelMessage.DateType.AcceptMessageResponse)
                    .setAcceptMessageResponse(ChatModel.AcceptMessageResponse.newBuilder().setFromRole(roleDto).setChatType(chatType).setStr(str)).build();
            NettyResponse nettyResponse=new NettyResponse();
            nettyResponse.setCmd(ConstantValue.ACCEPT_MESSAGE_RESPONSE);
            nettyResponse.setStateCode(StateCode.SUCCESS);
            nettyResponse.setData(myMessage.toByteArray());
            String json= JsonFormat.printToString(myMessage);
            NotificationUtil.sendMessage(channel,nettyResponse,json);
        }
    }

    @Override
    public Integer returnRoleId() {
        return getId();
    }

    /**
     * 改变血量buffer的任务
     */
    private class ChangeHpByBufferTask implements Runnable{
        Logger logger = Logger.getLogger(ChangeHpByBufferTask.class);
        private int number;
        private MmoSimpleRole mmoSimpleRole;
        private PlayModel.RoleIdDamage.Builder damageU;
        public ChangeHpByBufferTask() {
        }

        public ChangeHpByBufferTask(int number, MmoSimpleRole mmoSimpleRole, PlayModel.RoleIdDamage.Builder damageU) {
            this.number = number;
            this.mmoSimpleRole = mmoSimpleRole;
            this.damageU = damageU;
        }
        @Override
        public void run() {
            logger.info("当前changeHpByBuffer线程是：" + Thread.currentThread().getName() + " 操作的角色是： " + mmoSimpleRole.getName());
            Integer oldHp = mmoSimpleRole.getNowHp();
            Integer newNumber = oldHp + number;
            if (newNumber > getHp()) {
                mmoSimpleRole.setNowHp(getHp());
                newNumber = getHp() - oldHp;
            } else {
                mmoSimpleRole.setNowHp(newNumber);
                newNumber = number;
            }
            if (mmoSimpleRole.getNowHp() <= 0) {
                newNumber = getNowHp() + Math.abs(number);
                mmoSimpleRole.setNowHp(0);
                mmoSimpleRole.setStatus(RoleStatusCode.DIE.getCode());
                //抛出被打败事件
                if(damageU.getFromRoleType()==RoleTypeCode.PLAYER.getCode()) {
                    MmoSimpleRole role=OnlineRoleMessageCache.getInstance().get(damageU.getFromRoleId());
                    if (role!=null) {
                        mmoSimpleRole.die(role);
                    }
                }else {
                    mmoSimpleRole.die(null);
                }
            }
            //生成数据包
            damageU.setDamage(newNumber);
            damageU.setMp(mmoSimpleRole.getNowMp());
            damageU.setNowblood(mmoSimpleRole.getNowHp());
            damageU.setState(mmoSimpleRole.getStatus());

            //封装成nettyResponse
            PlayModel.PlayModelMessage.Builder myMessageBuilder = PlayModel.PlayModelMessage.newBuilder();
            myMessageBuilder.setDataType(PlayModel.PlayModelMessage.DateType.DamagesNoticeResponse);
            PlayModel.DamagesNoticeResponse.Builder damageRpsponse = PlayModel.DamagesNoticeResponse.newBuilder();
            damageRpsponse.setRoleIdDamage(damageU);
            myMessageBuilder.setDamagesNoticeResponse(damageRpsponse.build());
            NettyResponse nettyResponse = new NettyResponse();
            nettyResponse.setCmd(ConstantValue.DAMAGES_NOTICE_RESPONSE);
            nettyResponse.setStateCode(StateCode.SUCCESS);
            nettyResponse.setData(myMessageBuilder.build().toByteArray());
            //广播
            List<Integer> players;
            if (getMmoSceneId()!=null) {
                players = SceneBeanMessageCache.getInstance().get(mmoSimpleRole.getMmoSceneId()).getRoles();
                for (Integer playerId:players){
                    Channel c= ChannelMessageCache.getInstance().get(playerId);
                    if (c!=null){
                        c.writeAndFlush(nettyResponse);
                    }
                }

            }else{
                List<Role> roles = CopySceneProvider.getCopySceneBeanById(getCopySceneBeanId()).getRoles();
                for (Role role:roles) {
                    if (role.getType().equals(RoleTypeCode.PLAYER.getCode())){
                        Channel c= ChannelMessageCache.getInstance().get(role.getId());
                        if (c!=null){
                            c.writeAndFlush(nettyResponse);
                        }
                    }
                }
            }
        }
    }
    /**
     * 改变蓝量buffer的任务
     */
    private class ChangeHpByMedicineTask implements Runnable {
        Logger logger = Logger.getLogger(ChangeMpTask.class);
        private int number;
        private MmoSimpleRole mmoSimpleRole;
        private PlayModel.RoleIdDamage.Builder damageU;

        public ChangeHpByMedicineTask() {
        }

        public ChangeHpByMedicineTask(int number, MmoSimpleRole mmoSimpleRole, PlayModel.RoleIdDamage.Builder damageU) {
            this.number = number;
            this.mmoSimpleRole = mmoSimpleRole;
            this.damageU = damageU;
        }

        @Override
        public void run() {
            logger.info("当前changeHp线程是：" + Thread.currentThread().getName() + " 操作的角色是： " + mmoSimpleRole.getName());
            Integer oldHp = mmoSimpleRole.getNowHp();
            Integer newNumber = oldHp + number;
            if (newNumber > getHp()) {
                mmoSimpleRole.setNowHp(getHp());
                newNumber = getHp() - oldHp;
            } else {
                mmoSimpleRole.setNowHp(newNumber);
                newNumber = number;
            }
            if (mmoSimpleRole.getNowHp() <= 0) {
                newNumber = getNowHp() + Math.abs(number);
                mmoSimpleRole.setStatus(RoleStatusCode.DIE.getCode());
            }
            //生成数据包
            damageU.setDamage(newNumber);
            damageU.setMp(mmoSimpleRole.getNowMp());
            damageU.setNowblood(mmoSimpleRole.getNowHp());
            damageU.setState(mmoSimpleRole.getStatus());
            PlayModel.PlayModelMessage.Builder myMessageBuilder = PlayModel.PlayModelMessage.newBuilder();
            myMessageBuilder.setDataType(PlayModel.PlayModelMessage.DateType.DamagesNoticeResponse);
            PlayModel.DamagesNoticeResponse.Builder damagesNoticeBuilder = PlayModel.DamagesNoticeResponse.newBuilder();
            damagesNoticeBuilder.setRoleIdDamage(damageU);
            myMessageBuilder.setDamagesNoticeResponse(damagesNoticeBuilder.build());
            NettyResponse nettyResponse = new NettyResponse();
            nettyResponse.setCmd(ConstantValue.DAMAGES_NOTICE_RESPONSE);
            nettyResponse.setStateCode(StateCode.SUCCESS);
            nettyResponse.setData(myMessageBuilder.build().toByteArray());
            Integer sceneId = mmoSimpleRole.getMmoSceneId();
            List<Integer> players;
            if (getMmoSceneId()!=null) {
                players = SceneBeanMessageCache.getInstance().get(mmoSimpleRole.getMmoSceneId()).getRoles();
                for (Integer playerId:players){
                    Channel c= ChannelMessageCache.getInstance().get(playerId);
                    if (c!=null){
                        c.writeAndFlush(nettyResponse);
                    }
                }

            }else{
                List<Role> roles = CopySceneProvider.getCopySceneBeanById(getCopySceneBeanId()).getRoles();
                for (Role role:roles) {
                    if (role.getType().equals(RoleTypeCode.PLAYER.getCode())){
                        Channel c= ChannelMessageCache.getInstance().get(role.getId());
                        if (c!=null){
                            c.writeAndFlush(nettyResponse);
                        }
                    }
                }
            }
        }
    }
    /**
     * 被攻击改变血量的任务
     */
    private class ChangeHpByAttackTask implements Runnable {
        Logger logger = Logger.getLogger(ChangeHpByMedicineTask.class);
        private int number;
        private MmoSimpleRole mmoSimpleRole;
        private PlayModel.RoleIdDamage.Builder damageU;

        public ChangeHpByAttackTask() {
        }

        public ChangeHpByAttackTask(int number, MmoSimpleRole mmoSimpleRole, PlayModel.RoleIdDamage.Builder damageU) {
            this.number = number;
            this.mmoSimpleRole = mmoSimpleRole;
            this.damageU = damageU;
        }

        @Override
        public void run() {
            logger.info("当前changeHpByAttack线程是：" + Thread.currentThread().getName() + " 操作的角色是： " + mmoSimpleRole.getName());
            Integer oldHp = mmoSimpleRole.getNowHp();
            Integer newNumber = oldHp + number;
            if (newNumber > getHp()) {
                mmoSimpleRole.setNowHp(getHp());
                newNumber = getHp() - oldHp;
            } else {
                mmoSimpleRole.setNowHp(newNumber);
                newNumber = number;
            }
            if (mmoSimpleRole.getNowHp() <= 0) {
                newNumber = getNowHp() + Math.abs(number);
                mmoSimpleRole.setNowHp(0);
                mmoSimpleRole.setStatus(RoleStatusCode.DIE.getCode());
                //抛出被打败事件
                if(damageU.getFromRoleType()==RoleTypeCode.PLAYER.getCode()) {
                    MmoSimpleRole role=OnlineRoleMessageCache.getInstance().get(damageU.getFromRoleId());
                    if (role!=null) {
                        mmoSimpleRole.die(role);
                    }
                }else {
                    mmoSimpleRole.die(null);
                }
            }
            //生成数据包
            List<PlayModel.RoleIdDamage> list = new ArrayList<>();
            damageU.setDamage(newNumber);
            damageU.setMp(mmoSimpleRole.getNowMp());
            damageU.setNowblood(mmoSimpleRole.getNowHp());
            damageU.setState(mmoSimpleRole.getStatus());
            list.add(damageU.build());
            //封装成nettyResponse
            PlayModel.PlayModelMessage.Builder myMessageBuilder = PlayModel.PlayModelMessage.newBuilder();
            myMessageBuilder.setDataType(PlayModel.PlayModelMessage.DateType.UseSkillResponse);
            PlayModel.UseSkillResponse.Builder useSkillBuilder = PlayModel.UseSkillResponse.newBuilder();
            useSkillBuilder.addAllRoleIdDamages(list);
            myMessageBuilder.setUseSkillResponse(useSkillBuilder.build());
            NettyResponse nettyResponse = new NettyResponse();
            nettyResponse.setCmd(ConstantValue.USE_SKILL_RESPONSE);
            nettyResponse.setStateCode(StateCode.SUCCESS);
            nettyResponse.setData(myMessageBuilder.build().toByteArray());
            //广播
            List<Integer> players;
            if (getMmoSceneId()!=null) {
                players = SceneBeanMessageCache.getInstance().get(mmoSimpleRole.getMmoSceneId()).getRoles();
                for (Integer playerId:players){
                    Channel c= ChannelMessageCache.getInstance().get(playerId);
                    if (c!=null){
                        c.writeAndFlush(nettyResponse);
                    }
                }

            }else{
                List<Role> roles = CopySceneProvider.getCopySceneBeanById(getCopySceneBeanId()).getRoles();
                for (Role role:roles) {
                    if (role.getType().equals(RoleTypeCode.PLAYER.getCode())){
                        Channel c= ChannelMessageCache.getInstance().get(role.getId());
                        if (c!=null){
                            c.writeAndFlush(nettyResponse);
                        }
                    }
                }
            }

        }
    }
    /**
     * 改变蓝量的任务
     */
    private class ChangeMpTask implements Runnable {
        Logger logger = Logger.getLogger(ChangeMpTask.class);
        private int number;
        private MmoSimpleRole mmoSimpleRole;
        private PlayModel.RoleIdDamage.Builder damageU;

        public ChangeMpTask() {
        }

        public ChangeMpTask(int number, MmoSimpleRole mmoSimpleRole, PlayModel.RoleIdDamage.Builder damageU) {
            this.number = number;
            this.mmoSimpleRole = mmoSimpleRole;
            this.damageU = damageU;
        }

        @Override
        public void run() {
            logger.info("当前changeMp线程是：" + Thread.currentThread().getName() + " 操作的角色是： " + mmoSimpleRole.getName());
            Integer oldMp = mmoSimpleRole.getNowMp();
            Integer newNumber = oldMp + number;
            if (newNumber > getMp()) {
                mmoSimpleRole.setNowMp(getMp());
                number = getMp() - oldMp;
            } else {
                mmoSimpleRole.setNowMp(newNumber);
            }
            damageU.setDamage(number);
            damageU.setMp(mmoSimpleRole.getNowMp());
            damageU.setNowblood(mmoSimpleRole.getNowHp());
            damageU.setState(mmoSimpleRole.getStatus());
            PlayModel.PlayModelMessage.Builder myMessageBuilder = PlayModel.PlayModelMessage.newBuilder();
            myMessageBuilder.setDataType(PlayModel.PlayModelMessage.DateType.DamagesNoticeResponse);
            PlayModel.DamagesNoticeResponse.Builder damagesNoticeBuilder = PlayModel.DamagesNoticeResponse.newBuilder();
            damagesNoticeBuilder.setRoleIdDamage(damageU);
            myMessageBuilder.setDamagesNoticeResponse(damagesNoticeBuilder.build());
            NettyResponse nettyResponse = new NettyResponse();
            nettyResponse.setCmd(ConstantValue.DAMAGES_NOTICE_RESPONSE);
            nettyResponse.setStateCode(StateCode.SUCCESS);
            nettyResponse.setData(myMessageBuilder.build().toByteArray());
            List<Integer> players;
            if (getMmoSceneId()!=null) {
                players = SceneBeanMessageCache.getInstance().get(mmoSimpleRole.getMmoSceneId()).getRoles();
                for (Integer playerId:players){
                    Channel c= ChannelMessageCache.getInstance().get(playerId);
                    if (c!=null){
                        c.writeAndFlush(nettyResponse);
                    }
                }

            }else{
                List<Role> roles = CopySceneProvider.getCopySceneBeanById(getCopySceneBeanId()).getRoles();
                for (Role role:roles) {
                    if (role.getType().equals(RoleTypeCode.PLAYER.getCode())){
                        Channel c= ChannelMessageCache.getInstance().get(role.getId());
                        if (c!=null){
                            c.writeAndFlush(nettyResponse);
                        }
                    }
                }
            }
        }
    }

    /**
     * description 退出
     * @return
     * @author lqhao
     * @createTime 2021/1/21 15:25
     */
    public void logout(){
        //退出副本
        if (getCopySceneBeanId()!=null){
            CopySceneProvider.getCopySceneBeanById(getCopySceneBeanId()).peopleExit(getId());
        }
        //退出队伍
        if(getTeamId()!=null){
            TeamServiceProvider.getTeamBeanByTeamId(getTeamId()).exitPeople(getId());
        }
        //退出场景
        if(getMmoSceneId()!=null){
            SceneBeanMessageCache.getInstance().get(getMmoSceneId()).getRoles().remove(getId());
        }
        //断线
        Channel c=ChannelMessageCache.getInstance().get(getId());
        NettyResponse nettyResponse=new NettyResponse();
        nettyResponse.setCmd(ConstantValue.FAIL_RESPONSE);
        String message="服务器：角色已退出登录";
        nettyResponse.setData(message.getBytes(StandardCharsets.UTF_8));
        nettyResponse.setStateCode(StateCode.FAIL);
        c.writeAndFlush(nettyResponse);
        c.close();
        //移出缓存
        OnlineRoleMessageCache.getInstance().remove(getId());
        NodeCheckMessageCache.getInstance().remove(getChannel().remoteAddress().toString());
    }
}
