package com.liqihao.pojo.bean;


import com.liqihao.Cache.*;
import com.liqihao.commons.ConstantValue;
import com.liqihao.commons.NettyResponse;
import com.liqihao.commons.StateCode;
import com.liqihao.commons.enums.*;
import com.liqihao.pojo.*;
import com.liqihao.pojo.baseMessage.BaseRoleMessage;
import com.liqihao.pojo.dto.EquipmentDto;
import com.liqihao.protobufObject.PlayModel;
import com.liqihao.protobufObject.SceneModel;
import com.liqihao.provider.TeamServiceProvider;
import com.liqihao.service.TeamService;
import com.liqihao.util.CommonsUtil;
import com.liqihao.util.LogicThreadPool;
import com.liqihao.util.ScheduledThreadPoolUtil;
import com.sun.scenario.effect.impl.prism.PrImage;
import io.netty.channel.Channel;
import org.apache.log4j.Logger;

import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;


/**
 * 缓存中存储的人物类
 *
 * @author lqhao
 */
public class MmoSimpleRole extends MmoRolePOJO {
    private Integer Blood;
    private volatile Integer nowBlood;
    private Integer mp;
    private volatile Integer nowMp;
    private volatile HashMap<Integer, Long> cdMap;
    private List<Integer> skillIdList;
    private List<SkillBean> skillBeans;
    private CopyOnWriteArrayList<BufferBean> bufferBeans;
    private Integer attack;
    private BackPackManager backpackManager;
    private List<Integer> needDeleteEquipmentIds = new ArrayList<>();
    private double damageAdd;
    private Integer teamId;
    private Integer lastSceneId;
    private Integer teamApplyOrInviteSize;
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
    private ConcurrentLinkedQueue<TeamApplyOrInviteBean> teamApplyOrInviteBeans=new ConcurrentLinkedQueue<>();

    public Integer getTeamApplyOrInviteSize() {
        return teamApplyOrInviteSize;
    }

    public void setTeamApplyOrInviteSize(Integer teamApplyOrInviteSize) {
        this.teamApplyOrInviteSize = teamApplyOrInviteSize;
    }



    public void addTeamApplyOrInviteBean(TeamApplyOrInviteBean teamApplyOrInviteBean) {
        checkOutTime();
        //邀请的大小，先进先出咯
        if (teamApplyOrInviteBeans.size()>=teamApplyOrInviteSize){
            teamApplyOrInviteBeans.poll();
        }
        teamApplyOrInviteBeans.add(teamApplyOrInviteBean);
    }


    public ConcurrentLinkedQueue<TeamApplyOrInviteBean> getTeamApplyOrInviteBeans() {
        return teamApplyOrInviteBeans;
    }

    public void setTeamApplyOrInviteBeans(ConcurrentLinkedQueue<TeamApplyOrInviteBean> teamApplyOrInviteBeans) {
        this.teamApplyOrInviteBeans = teamApplyOrInviteBeans;
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

    public Integer getTeamId() {
        return teamId;
    }

    public void setTeamId(Integer teamId) {
        this.teamId = teamId;
    }
    //    public final ReentrantReadWriteLock hpRwLock = new ReentrantReadWriteLock();
//    public final ReentrantReadWriteLock mpRwLock = new ReentrantReadWriteLock();

    public double getDamageAdd() {
        return damageAdd;
    }

    public void setDamageAdd(double damageAdd) {
        this.damageAdd = damageAdd;
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


    public Integer getAttack() {
        return attack;
    }

    public void setAttack(Integer attack) {
        this.attack = attack;
    }

    public CopyOnWriteArrayList<BufferBean> getBufferBeans() {
        return bufferBeans;
    }

    public void setBufferBeans(CopyOnWriteArrayList<BufferBean> bufferBeans) {
        this.bufferBeans = bufferBeans;
    }

    public Integer getNowMp() {
        return nowMp;
    }

    public void setNowMp(Integer nowMp) {
        this.nowMp = nowMp;
    }

    public List<Integer> getSkillIdList() {
        return skillIdList;
    }

    public void setSkillIdList(List<Integer> skillIdList) {
        this.skillIdList = skillIdList;
    }

    public Integer getMp() {
        return mp;
    }

    public void setMp(Integer mp) {
        this.mp = mp;
    }

    public Integer getBlood() {
        return Blood;
    }

    public void setBlood(Integer blood) {
        Blood = blood;

    }

    public Integer getNowBlood() {
        return nowBlood;
    }

    public void setNowBlood(Integer nowBlood) {
        this.nowBlood = nowBlood;
    }


    public HashMap<Integer, Long> getCdMap() {
        return cdMap;
    }

    public void setCdMap(HashMap<Integer, Long> cdMap) {
        this.cdMap = cdMap;
    }

    /**
     * 检测邀请过时
     */
    private void checkOutTime(){
        Iterator iterator=teamApplyOrInviteBeans.iterator();
        //每次插入都删除申请过时或者
        while (iterator.hasNext()){
            TeamApplyOrInviteBean bean= (TeamApplyOrInviteBean) iterator.next();
            if (bean.endTime<System.currentTimeMillis()){
                teamApplyOrInviteBeans.remove(bean);
            }
        }
    }
    /**
     * 初始化对象
     * @param role
     * @param baseRoleMessage
     */
    public void init(MmoRolePOJO role, BaseRoleMessage baseRoleMessage){
        setId(role.getId());
        setMmosceneid(role.getMmosceneid());
        setName(role.getName());
        setOnstatus(role.getOnstatus());
        setStatus(role.getStatus());
        setType(role.getType());
        List<SkillBean> skillBeans=CommonsUtil.skillIdsToSkillBeans(role.getSkillIds());
        setSkillBeans(skillBeans);
        setBlood(baseRoleMessage.getHp());
        setNowBlood(baseRoleMessage.getHp());
        setMp(baseRoleMessage.getMp());
        setDamageAdd(baseRoleMessage.getDamageAdd());
        setNowMp(baseRoleMessage.getMp());
        setAttack(baseRoleMessage.getAttack());
        List<Integer> skillIds=CommonsUtil.split(role.getSkillIds());
        setSkillIdList(skillIds);
        setCdMap(new HashMap<Integer, Long>());
        setBufferBeans(new CopyOnWriteArrayList<>());
        setEquipmentBeanHashMap(new HashMap<>());
    }
    //使用道具
    public Boolean useArticle(Integer articleId) {
        Article article = backpackManager.getArticleByArticleId(articleId);
        if (article != null && article.getArticleTypeCode().equals(ArticleTypeCode.MEDICINE.getCode())) {
            //药品
            MedicineBean medicineBean = (MedicineBean) article;
            //删减
            backpackManager.useOrAbandanArticle(articleId, 1);
            Boolean flag = medicineBean.useMedicene(getId());
            return flag;
        } else if (article != null && article.getArticleTypeCode().equals(ArticleTypeCode.EQUIPMENT.getCode())) {
            //装备
            EquipmentBean equipmentBean = (EquipmentBean) article;
            //删减
            backpackManager.useOrAbandanArticle(articleId, 1);
            //穿
            return useEquipment(equipmentBean);
        } else {
            return false;
        }

    }

    //穿装备 or替换装备
    private Boolean useEquipment(EquipmentBean equipmentBean) {
        //判断该位置是否有装备
        EquipmentBean oldBean = getEquipmentBeanHashMap().get(equipmentBean.getPosition());
        if (oldBean != null) {
            //放回背包内
            //背包新增数据
            //修改人物属性
            setAttack(getAttack() - oldBean.getAttackAdd());
            setDamageAdd(getDamageAdd() - oldBean.getDamageAdd());
            needDeleteEquipmentIds.add(oldBean.getEquipmentBagId());
            backpackManager.put(oldBean);
        }
        //背包减少装备
        backpackManager.useOrAbandanArticle(equipmentBean.getArticleId(), 1);
        //装备栏增加装备
        equipmentBeanHashMap.put(equipmentBean.getPosition(), equipmentBean);
        //人物属性
        setAttack(getAttack() + equipmentBean.getAttackAdd());
        setDamageAdd(getDamageAdd() + equipmentBean.getDamageAdd());
        return true;
    }

    //脱装备
    public Boolean unUseEquipment(Integer position) {
        //判断该位置是否有装备
        EquipmentBean equipmentBean = getEquipmentBeanHashMap().get(position);
        if (equipmentBean == null) {
            //无装备
            return false;
        } else {
            equipmentBeanHashMap.remove(position);
            //装备栏数据库减少该装备
            if (equipmentBean.getEquipmentBagId() != null) {
                needDeleteEquipmentIds.add(equipmentBean.getEquipmentBagId());
            }
            //装备栏id为null
            equipmentBean.setEquipmentBagId(null);
            //放入背包
            backpackManager.put(equipmentBean);
            setAttack(getAttack() - equipmentBean.getAttackAdd());
            setDamageAdd(getDamageAdd() - equipmentBean.getDamageAdd());
            return true;
        }
    }

    //获取装备栏所有信息
    public List<EquipmentDto> getEquipments() {
        List<EquipmentDto> equipmentDtos = new ArrayList<>();
        for (EquipmentBean bean : equipmentBeanHashMap.values()) {
            EquipmentDto equipmentDto = new EquipmentDto();
            equipmentDto.setId(bean.getId());
            equipmentDto.setNowdurability(bean.getNowDurability());
            equipmentDto.setPosition(bean.getPosition());
            equipmentDto.setEquipmentBagId(bean.getEquipmentBagId());
            equipmentDto.setEquipmentId(bean.getEquipmentId());
            equipmentDtos.add(equipmentDto);
        }
        return equipmentDtos;
    }

    //根据skillI获取技能
    public SkillBean getSkillBeanBySkillId(Integer skillId) {
        for (SkillBean b : getSkillBeans()) {
            if (b.getId().equals(skillId)) {
                return b;
            }
        }
        return null;
    }

    //使用技能
    public List<PlayModel.RoleIdDamage> useSkill(List<MmoSimpleNPC> target, Integer skillId) {
        SkillBean skillBean = getSkillBeanBySkillId(skillId);
        //武器耐久度-2
        EquipmentBean equipmentBean = this.getEquipmentBeanHashMap().get(PositionCode.ARMS.getCode());
        if (equipmentBean != null) {
            equipmentBean.setNowDurability(equipmentBean.getNowDurability() - 2);
            if (equipmentBean.getNowDurability() < 0) {
                equipmentBean.setNowDurability(0);
            }
        }
        if (skillBean.getConsumeType().equals(ConsuMeTypeCode.HP.getCode())) {
            //扣血
            setNowBlood(getNowBlood() - skillBean.getConsumeNum());

        } else {
            //扣篮
            setNowMp(getNowMp() - skillBean.getConsumeNum());
            //判断是否已经有自动回蓝任务
            ConcurrentHashMap<String, ScheduledFuture<?>> replyMpRoleMap = ScheduledThreadPoolUtil.getReplyMpRole();
            //自动回蓝任务的key
            String key = getId() + "AUTOMP";
            if (!replyMpRoleMap.containsKey(key)) {
                //number为空 代表着自动回蓝
                ScheduledThreadPoolUtil.ReplyMpTask replyMpTask = new ScheduledThreadPoolUtil.ReplyMpTask(getId(), null, DamageTypeCode.MP.getCode(), key);
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
        damageU.setAttackStyle(AttackStyleCode.USESKILL.getCode());
        damageU.setBufferId(-1);
        damageU.setDamage(skillBean.getConsumeNum());
        damageU.setDamageType(skillBean.getConsumeType());
        damageU.setMp(getNowMp());
        damageU.setNowblood(getNowBlood());
        damageU.setSkillId(skillBean.getId());
        damageU.setState(getStatus());
        list.add(damageU.build());
        //攻击怪物

        for (MmoSimpleNPC mmoSimpleNPC : target) {
            Integer hp = mmoSimpleNPC.getNowBlood();
            Integer reduce = 0;
            if (skillBean.getSkillType().equals(SkillTypeCode.FIED.getCode())) {
                //固伤 只有技能伤害
                reduce = (int) Math.ceil(skillBean.getBaseDamage() * (1 + this.getDamageAdd()));
                hp -= reduce;
            }
            if (skillBean.getSkillType().equals(SkillTypeCode.PERCENTAGE.getCode())) {
                //百分比 按照攻击力比例增加
                Integer damage = skillBean.getBaseDamage();
                damage = (int) Math.ceil(damage + mmoSimpleNPC.getAttack() * skillBean.getAddPercon());
                hp = hp - damage;
                reduce = damage;
            }
            if (hp <= 0) {
                reduce = reduce + hp;
                hp = 0;
                mmoSimpleNPC.setStatus(RoleStatusCode.DIE.getCode());
            }
            mmoSimpleNPC.setNowBlood(hp);
            // 扣血伤害
            PlayModel.RoleIdDamage.Builder damageR = PlayModel.RoleIdDamage.newBuilder();
            damageR.setFromRoleId(getId());
            damageR.setToRoleId(mmoSimpleNPC.getId());
            damageR.setAttackStyle(AttackStyleCode.ATTACK.getCode());
            damageR.setBufferId(-1);
            damageR.setDamage(reduce);
            damageR.setDamageType(DamageTypeCode.HP.getCode());
            damageR.setMp(mmoSimpleNPC.getNowMp());
            damageR.setNowblood(mmoSimpleNPC.getNowBlood());
            damageR.setSkillId(skillBean.getId());
            damageR.setState(mmoSimpleNPC.getStatus());
            list.add(damageR.build());
            //怪物攻击本人
            if (!mmoSimpleNPC.getStatus().equals(RoleStatusCode.DIE.getCode())) {
                mmoSimpleNPC.npcAttack(getId());
            }
        }
        //cd
        Map<Integer, Long> map = getCdMap();
        Long time = System.currentTimeMillis();
        int addTime = skillBean.getCd() * 1000;
        map.put(skillBean.getId(), time + addTime);
        //buffer
        skillBean.useBuffer(target, getId());
        return list;
    }

    //扣血
    public void changeNowBlood(int number, PlayModel.RoleIdDamage.Builder damageU, int type) {
        //获取对应线程的下标
        Channel channel = ChannelMessageCache.getInstance().get(getId());
        Integer index = CommonsUtil.getIndexByChannel(channel);
        if (type == AttackStyleCode.USESKILL.getCode()) {
            LogicThreadPool.getInstance().execute(new ChangeHpByAttackTask(number, this, damageU), index);
        } else {
            LogicThreadPool.getInstance().execute(new ChangeHpByMedicineTask(number, this, damageU), index);
        }
    }

    //扣蓝
    public void changeMp(int number, PlayModel.RoleIdDamage.Builder damageU) {
        Channel channel = ChannelMessageCache.getInstance().get(getId());
        Integer index = CommonsUtil.getIndexByChannel(channel);
        LogicThreadPool.getInstance().execute(new ChangeMpTask(number, this, damageU), index);
    }

    public List<MmoSimpleRole> wentScene(Integer nextSceneId) {
        //修改scene 如果为null 则是刚从副本中出来
        if (getMmosceneid()!=null) {
            SceneBeanMessageCache.getInstance().get(getMmosceneid()).getRoles().remove(getId());
        }
        SceneBeanMessageCache.getInstance().get(nextSceneId).getRoles().add(getId());
        setMmosceneid(nextSceneId);
        //查询出npc 和SimpleRole
        List<MmoSimpleRole> nextSceneRoles=new ArrayList<>();
        SceneBean nextScene=SceneBeanMessageCache.getInstance().get(nextSceneId);
        List<Integer> roles=nextScene.getRoles();
        List<Integer> npcs=nextScene.getNpcs();
        //NPC
        for (Integer npcId:npcs){
            MmoSimpleNPC temp= NpcMessageCache.getInstance().get(npcId);
            nextSceneRoles.add(CommonsUtil.NpcToMmoSimpleRole(temp));
        }
        //ROLES
        for (Integer rId:roles){
            MmoSimpleRole role=OnlineRoleMessageCache.getInstance().get(rId);
            nextSceneRoles.add(role);
        }
        return nextSceneRoles;
    }

    /**
     * 获取邀请信息
     * @return
     */
    public List<TeamApplyOrInviteBean> getInviteBeans() {
        checkOutTime();
       return teamApplyOrInviteBeans.stream().filter(e->e.getType().equals(TeamApplyInviteCode.INVITE.getCode())).collect(Collectors.toList());
    }
    /**
     * 拒绝邀请
     */
    public TeamApplyOrInviteBean refuseInvite(Integer teamId) {
        checkOutTime();
        Iterator iterator=teamApplyOrInviteBeans.iterator();
        TeamApplyOrInviteBean teamApplyOrInviteBean=null;
        while (iterator.hasNext()){
            teamApplyOrInviteBean= (TeamApplyOrInviteBean) iterator.next();
            if (teamApplyOrInviteBean.getTeamId().equals(teamId)&&
                    teamApplyOrInviteBean.getType().equals(TeamApplyInviteCode.INVITE.getCode())){
                teamApplyOrInviteBeans.remove(teamApplyOrInviteBean);
                getTeamApplyOrInviteBeans().remove(teamApplyOrInviteBean);
                return teamApplyOrInviteBean;
            }
        }
        return null;
    }

    public TeamApplyOrInviteBean constainsInvite(Integer teamId) {
        checkOutTime();
        Iterator iterator=teamApplyOrInviteBeans.iterator();
        TeamApplyOrInviteBean teamApplyOrInviteBean=null;
        while (iterator.hasNext()){
            teamApplyOrInviteBean= (TeamApplyOrInviteBean) iterator.next();
            if (teamApplyOrInviteBean.getTeamId().equals(teamId)&&
                    teamApplyOrInviteBean.getType().equals(TeamApplyInviteCode.INVITE.getCode())){
                return teamApplyOrInviteBean;
            }
        }
        return teamApplyOrInviteBean;
    }


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
            Integer oldHp = mmoSimpleRole.getNowBlood();
            Integer newNumber = oldHp + number;
            if (newNumber > getBlood()) {
                mmoSimpleRole.setNowBlood(getBlood());
                newNumber = getBlood() - oldHp;
            } else {
                mmoSimpleRole.setNowBlood(newNumber);
                newNumber=number;
            }
            if (mmoSimpleRole.getNowBlood() <= 0) {
                newNumber = getNowBlood() + Math.abs(number);
                mmoSimpleRole.setStatus(RoleStatusCode.DIE.getCode());
            }
            //生成数据包
            damageU.setDamage(newNumber);
            damageU.setMp(mmoSimpleRole.getNowMp());
            damageU.setNowblood(mmoSimpleRole.getNowBlood());
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
            Integer sceneId = mmoSimpleRole.getMmosceneid();
            List<Integer> players = SceneBeanMessageCache.getInstance().get(sceneId).getRoles();
            for (Integer playerId : players) {
                Channel cc = ChannelMessageCache.getInstance().get(playerId);
                if (cc != null) {
                    cc.writeAndFlush(nettyResponse);
                }
            }
        }
    }

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
            Integer oldHp = mmoSimpleRole.getNowBlood();
            Integer newNumber = oldHp + number;
            if (newNumber > getNowBlood()) {
                mmoSimpleRole.setNowBlood(getBlood());
                newNumber = getBlood() - oldHp;
            } else {
                mmoSimpleRole.setNowBlood(newNumber);
                newNumber = number;
            }
            if (mmoSimpleRole.getNowBlood() <= 0) {
                newNumber = getNowBlood() + Math.abs(number);
                mmoSimpleRole.setNowBlood(0);
                mmoSimpleRole.setStatus(RoleStatusCode.DIE.getCode());
            }
            //生成数据包
            List<PlayModel.RoleIdDamage> list = new ArrayList<>();
            damageU.setDamage(newNumber);
            damageU.setMp(mmoSimpleRole.getNowMp());
            damageU.setNowblood(mmoSimpleRole.getNowBlood());
            damageU.setState(mmoSimpleRole.getStatus());
            list.add(damageU.build());
            //封装成nettyResponse
            PlayModel.PlayModelMessage.Builder myMessageBuilder = PlayModel.PlayModelMessage.newBuilder();
            myMessageBuilder.setDataType(PlayModel.PlayModelMessage.DateType.UseSkillResponse);
            PlayModel.UseSkillResponse.Builder useSkillBuilder = PlayModel.UseSkillResponse.newBuilder();
            useSkillBuilder.addAllRoleIdDamages(list);
            myMessageBuilder.setUseSkillResponse(useSkillBuilder.build());
            NettyResponse nettyResponse = new NettyResponse();
            nettyResponse.setCmd(ConstantValue.USE_SKILL_RSPONSE);
            nettyResponse.setStateCode(StateCode.SUCCESS);
            nettyResponse.setData(myMessageBuilder.build().toByteArray());
            //广播
            Integer sceneId = mmoSimpleRole.getMmosceneid();
            List<Integer> players = SceneBeanMessageCache.getInstance().get(sceneId).getRoles();
            for (Integer playerId : players) {
                Channel c = ChannelMessageCache.getInstance().get(playerId);
                if (c != null) {
                    c.writeAndFlush(nettyResponse);
                }
            }

        }
    }

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
            damageU.setNowblood(mmoSimpleRole.getNowBlood());
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
            Integer sceneId = mmoSimpleRole.getMmosceneid();
            List<Integer> players = SceneBeanMessageCache.getInstance().get(sceneId).getRoles();
            for (Integer playerId : players) {
                Channel cc = ChannelMessageCache.getInstance().get(playerId);
                if (cc != null) {
                    cc.writeAndFlush(nettyResponse);
                }
            }
        }
    }
}
