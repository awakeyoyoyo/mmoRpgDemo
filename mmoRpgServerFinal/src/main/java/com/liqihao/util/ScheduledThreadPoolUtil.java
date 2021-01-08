package com.liqihao.util;

import com.liqihao.Cache.*;
import com.liqihao.commons.enums.*;
import com.liqihao.pojo.baseMessage.BufferMessage;
import com.liqihao.pojo.baseMessage.SkillMessage;
import com.liqihao.pojo.bean.*;
import com.liqihao.protobufObject.PlayModel;
import com.liqihao.provider.CallerServiceProvider;
import com.liqihao.provider.CopySceneProvider;
import com.liqihao.provider.TeamServiceProvider;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.*;

/**
 * @author awakeyoyoyo
 * @className ScheduledThreadPoolUtil
 * @description
 * @date 2020-12-13 18:59
 */
public class ScheduledThreadPoolUtil {
    private static ScheduledThreadPoolExecutor scheduledExecutorService;
    /**
     * 存储了正在调度线程池中执行的回蓝的角色id
     */
    private static ConcurrentHashMap<String, ScheduledFuture<?>> replyMpRole = new ConcurrentHashMap<>();
    /**
     * 存储了正在调度线程池中执行的buffer的角色id
     */
    private static ConcurrentHashMap<String, ScheduledFuture<?>> bufferRole = new ConcurrentHashMap<>();
    /**
     * 存储了正在调度线程池中执行的buffer的npc id限定一个npc只能攻击一个人
     */
    private static ConcurrentHashMap<Integer, ScheduledFuture<?>> npcTaskMap = new ConcurrentHashMap<>();
    /**
     * 存储了正在延迟线程池中执行的副本id
     */
    private static ConcurrentHashMap<Integer, ScheduledFuture<?>> copySceneTaskMap = new ConcurrentHashMap<>();
    /**
     * 存储了正在调度线程池中执行的boss 攻击线程
     */
    private static ConcurrentHashMap<Integer, ScheduledFuture<?>> bossTaskMap = new ConcurrentHashMap<>();
    /**
     * 存储了正在调度线程池中执行的宠物 攻击线程
     */
    private static ConcurrentHashMap<Integer, ScheduledFuture<?>> helperTaskMap = new ConcurrentHashMap<>();
    public static void init() {
        replyMpRole = new ConcurrentHashMap<>();
        bufferRole = new ConcurrentHashMap<>();
        npcTaskMap = new ConcurrentHashMap<>();
        copySceneTaskMap = new ConcurrentHashMap<>();
        scheduledExecutorService = new ScheduledThreadPoolExecutor(4);
    }

    public static ConcurrentHashMap<Integer, ScheduledFuture<?>> getBossTaskMap() {
        return bossTaskMap;
    }

    public static void setBossTaskMap(ConcurrentHashMap<Integer, ScheduledFuture<?>> bossTaskMap) {
        ScheduledThreadPoolUtil.bossTaskMap = bossTaskMap;
    }

    public static ConcurrentHashMap<Integer, ScheduledFuture<?>> getHelperTaskMap() {
        return helperTaskMap;
    }

    public static void setHelperTaskMap(ConcurrentHashMap<Integer, ScheduledFuture<?>> helperTaskMap) {
        ScheduledThreadPoolUtil.helperTaskMap = helperTaskMap;
    }

    public static ConcurrentHashMap<Integer, ScheduledFuture<?>> getCopySceneTaskMap() {
        return copySceneTaskMap;
    }

    public static void setCopySceneTaskMap(ConcurrentHashMap<Integer, ScheduledFuture<?>> copySceneTaskMap) {
        ScheduledThreadPoolUtil.copySceneTaskMap = copySceneTaskMap;
    }

    public static ConcurrentHashMap<Integer, ScheduledFuture<?>> getNpcTaskMap() {
        return npcTaskMap;
    }

    public static void setNpcTaskMap(ConcurrentHashMap<Integer, ScheduledFuture<?>> npcTaskMap) {
        ScheduledThreadPoolUtil.npcTaskMap = npcTaskMap;
    }

    public static ConcurrentHashMap<String, ScheduledFuture<?>> getBufferRole() {
        return bufferRole;
    }

    public static void setBufferRole(ConcurrentHashMap<String, ScheduledFuture<?>> bufferRole) {
        ScheduledThreadPoolUtil.bufferRole = bufferRole;
    }

    public static ScheduledThreadPoolExecutor getScheduledExecutorService() {
        return scheduledExecutorService;
    }

    public static void setScheduledExecutorService(ScheduledThreadPoolExecutor scheduledExecutorService) {
        ScheduledThreadPoolUtil.scheduledExecutorService = scheduledExecutorService;
    }

    public static ConcurrentHashMap<String, ScheduledFuture<?>> getReplyMpRole() {
        return replyMpRole;
    }

    public static void setReplyMpRole(ConcurrentHashMap<String, ScheduledFuture<?>> replyMpRole) {
        ScheduledThreadPoolUtil.replyMpRole = replyMpRole;
    }

    public static class ReplyMpTask implements Runnable {
        private Logger logger = Logger.getLogger(ReplyMpTask.class);
        private Role role;
        private Integer number;
        private Integer damageTypeCode;
        private String key;
        private Integer times;

        public ReplyMpTask(Role role, Integer number, Integer damageTypeCode, String key) {
            this.role = role;
            this.number = number;
            this.damageTypeCode = damageTypeCode;
            this.key = key;
        }

        public Logger getLogger() {
            return logger;
        }

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public void setLogger(Logger logger) {
            this.logger = logger;
        }

        public Role getRole() {
            return role;
        }

        public void setRole(Role role) {
            this.role = role;
        }

        public Integer getNumber() {
            return number;
        }

        public void setNumber(Integer number) {
            this.number = number;
        }

        public ReplyMpTask(Role role, Integer number, Integer damageTypeCode, String key, Integer times) {
            this.role = role;
            this.number = number;
            this.damageTypeCode = damageTypeCode;
            this.key = key;
            this.times = times;
        }

        @Override
        public void run() {
            logger.info("回蓝/血线程-------------------" + Thread.currentThread().getName());
            Integer addNumber;
            Integer attackStyleCode;
            if (times != null && times <= 0 || role.getStatus().equals(RoleStatusCode.DIE.getCode())) {
                replyMpRole.get(key).cancel(false);
                replyMpRole.remove(key);
                return;
            }
            if (number == null) {
                //number没有传入 代表着这是自动回蓝
                addNumber = (int) Math.ceil(role.getMp() * 0.05);
                attackStyleCode = AttackStyleCode.AUTO_RE.getCode();
            } else {
                //传入则代表着是吃药
                addNumber = number;
                attackStyleCode = AttackStyleCode.MEDICINE.getCode();
            }
            PlayModel.RoleIdDamage.Builder damageU = PlayModel.RoleIdDamage.newBuilder();

            damageU.setFromRoleId(role.getId());
            damageU.setToRoleId(role.getId());
            damageU.setAttackStyle(attackStyleCode);
            damageU.setBufferId(-1);
            damageU.setDamageType(damageTypeCode);
            damageU.setSkillId(-1);
            //判断是玩家还是怪物执行不同的改变
            if (damageTypeCode.equals(DamageTypeCode.MP.getCode())) {
                damageU.setDamageType(damageTypeCode);
                role.changeMp(addNumber, damageU);
            } else {
                damageU.setDamageType(damageTypeCode);
                role.changeNowBlood(addNumber, damageU, AttackStyleCode.MEDICINE.getCode());
            }
            if (times != null) {
                times--;
            }
            //判断任务是否以及完成即 人物蓝是否满了
            if (number == null) {
                if (role.getNowMp().equals(role.getMp())) {
                    replyMpRole.get(key).cancel(false);
                    replyMpRole.remove(key);
                }
            }
        }
    }

    public static class BufferTask implements Runnable {
        private Logger logger = Logger.getLogger(BufferTask.class);
        private BufferBean bufferBean;
        private Integer count;
        private Role toRole;

        public BufferTask() {
        }

        public BufferTask(BufferBean bufferBean, Integer count, Role toRole) {
            this.bufferBean = bufferBean;
            this.count = count;
            this.toRole = toRole;
        }

        @Override
        public void run() {
            logger.info("buffer线程-------------------" + Thread.currentThread().getName());
            Integer toroleId = bufferBean.getToRoleId();
            if (toRole == null || toRole.getStatus().equals(RoleStatusCode.DIE.getCode()) || count <= 0) {
                //删除该buffer
                String taskId = toroleId.toString() + bufferBean.getId().toString();
                bufferRole.remove(taskId);
                toRole.getBufferBeans().remove(bufferBean);
                bufferRole.get(Integer.parseInt(taskId)).cancel(false);
                //人物身上删除buffer

            }
            toRole.effectByBuffer(bufferBean);
            count--;
        }
    }

    public static class NpcAttackTask implements Runnable {
        private Role target;
        private Integer npcId;
        private Logger logger = Logger.getLogger(NpcAttackTask.class);

        public NpcAttackTask() {
        }

        public NpcAttackTask(Role target, Integer npcId) {
            this.target = target;
            this.npcId = npcId;
        }

        @Override
        public void run() {
            logger.info("怪物攻击线程");
            MmoSimpleNPC npc = NpcMessageCache.getInstance().get(npcId);
            if (target == null ||
                    !npc.getMmoSceneId().equals(target.getMmoSceneId()) ||
                    target.getStatus().equals(RoleStatusCode.DIE.getCode()) ||
                    npc.getStatus().equals(RoleStatusCode.DIE.getCode())) {
                //中止任务  用户离线了 用户跑去别的场景了 死了
                npcTaskMap.get(npcId).cancel(false);
                npcTaskMap.remove(npcId);
                return;
            }
            //扣血咯
            if (target.getNowHp() <= 0) {
                npcTaskMap.get(npcId).cancel(false);
                npcTaskMap.remove(npcId);
                return;
            } else {
                //npc默认使用普通攻击
                //从缓存中找出技能
                SkillMessage skillMessage = SkillMessageCache.getInstance().get(3);
                SkillBean skillBean = new SkillBean();
                skillBean.setId(skillMessage.getId());
                skillBean.setConsumeType(skillMessage.getConsumeType());
                skillBean.setConsumeNum(skillMessage.getConsumeNum());
                skillBean.setCd(skillMessage.getCd());
                skillBean.setBufferIds(CommonsUtil.split(skillMessage.getBufferIds()));
                skillBean.setBaseDamage(skillMessage.getBaseDamage());
                skillBean.setSkillName(skillMessage.getSkillName());
                skillBean.setSkillAttackType(skillMessage.getSkillAttackType());
                skillBean.setSkillDamageType(skillMessage.getSkillDamageType());
                skillBean.setChantTime(skillMessage.getChantTime());
                skillBean.setAddPerson(skillMessage.getAddPerson());
                skillBean.setSkillType(skillMessage.getSkillType());
                target.beAttack(skillBean,npc);
            }
        }
    }

    public static class CopySceneOutTimeTask implements Runnable {
        private TeamBean teamBean;
        private CopySceneBean copySceneBean;
        private Logger logger = Logger.getLogger(CopySceneOutTimeTask.class);

        public CopySceneOutTimeTask() {

        }

        public CopySceneOutTimeTask(TeamBean teamBean, CopySceneBean copySceneBean) {
            this.teamBean = teamBean;
            this.copySceneBean = copySceneBean;
        }

        @Override
        public void run() {
            copySceneBean.changeFailTimeOut(teamBean);
        }
    }

    public static class BossAttackTask implements Runnable {
        private BossBean bossBean;
        private CopySceneBean copySceneBean;
        private List<SkillBean> skillBeans;
        private Integer attackCount;
        private Logger logger = Logger.getLogger(NpcAttackTask.class);

        public BossAttackTask() {
        }

        public BossAttackTask(BossBean bossBean, CopySceneBean copySceneBean, List<SkillBean> skillBeans) {
            this.bossBean = bossBean;
            attackCount = 1;
            this.skillBeans = skillBeans;
            this.copySceneBean = copySceneBean;
        }

        @Override
        public void run() {
            logger.info("boss攻击线程");
            //仇恨的第一人
            Role role = null;
            role = bossBean.getTarget();
            if (bossBean.getStatus().equals(RoleStatusCode.DIE.getCode())){
                bossTaskMap.remove(bossBean.getBossBeanId());
                bossTaskMap.get(bossBean.getBossBeanId()).cancel(false);
            }
            if (role == null) {
                // 挑战失败
                TeamBean teamBean = TeamServiceProvider.getTeamBeanByTeamId(copySceneBean.getTeamId());
                copySceneBean.changePeopleDie(teamBean);
                bossTaskMap.remove(bossBean.getBossBeanId());
                bossTaskMap.get(bossBean.getBossBeanId()).cancel(false);
            }
            SkillBean skillBean = null;
            //使用不同的技能
            if (attackCount%5==0){
                skillBean = skillBeans.get(1);
                attackCount=0;
            }else{
                skillBean = skillBeans.get(0);
            }
            attackCount++;
            List<Role> targetRoles = new ArrayList<>();
            if (skillBean.getSkillAttackType().equals(SkillAttackTypeCode.ALL_PEOPLE.getCode())) {
                for (Role r : copySceneBean.getRoles()) {
                    if (r.getStatus().equals(RoleStatusCode.ALIVE.getCode()) && r.getCopySceneBeanId() != null && r.getCopySceneBeanId().equals(bossBean.getCopySceneBeanId())) {
                        targetRoles.add(r);
                    }
                }
            } else {
                targetRoles.add(role);
            }
            //对role进行攻击
            bossBean.useSkill(targetRoles, skillBean.getId());
        }
    }

    public static class HelperAttackTask implements Runnable {
        private MmoHelperBean helperBean;
        private List<SkillBean> skillBeans;
        private Role target;
        private Integer attackCount;
        private Logger logger = Logger.getLogger(NpcAttackTask.class);

        public HelperAttackTask() {
        }

        public HelperAttackTask(MmoHelperBean helperBean, List<SkillBean> skillBeans, Role target) {
            this.helperBean = helperBean;
            attackCount = 1;
            this.skillBeans = skillBeans;
            this.target=target;
        }

        @Override
        public void run() {
            logger.info("helper攻击线程");
            //判断停止攻击
            if(target.getMmoSceneId()!=null){
                if(!target.getMmoSceneId().equals(helperBean.getMmoSceneId())){
                    helperTaskMap.remove(helperBean.getId());
                    helperTaskMap.get(helperBean.getId()).cancel(false);
                    return;
                }
            }else{
                if(!target.getCopySceneBeanId().equals(helperBean.getCopySceneBeanId())){
                    helperTaskMap.remove(helperBean.getId());
                    helperTaskMap.get(helperBean.getId()).cancel(false);
                    return;
                }
            }
            if (target.getStatus().equals(RoleStatusCode.DIE.getCode())||helperBean.getStatus().equals(RoleStatusCode.DIE.getCode())) {
                // 没目标攻击  停止攻击线程
                helperTaskMap.remove(helperBean.getId());
                helperTaskMap.get(helperBean.getId()).cancel(false);
                return;
            }
            SkillBean skillBean = null;
            //使用不同的技能
            if (attackCount%5==0){
                skillBean = skillBeans.get(1);
                attackCount=0;
            }else{
                skillBean = skillBeans.get(0);
            }
            attackCount++;
            List<Role> targetRoles = new ArrayList<>();
            //群攻技能
            if (skillBean.getSkillAttackType().equals(SkillAttackTypeCode.ALL_PEOPLE.getCode())) {
                if (helperBean.getMmoSceneId()!=null) {
                    //在场景中
                    targetRoles.addAll(findTargetInScene(helperBean));
                }else{
                    //副本中
                    targetRoles.addAll(findTargetCopyScene(helperBean));
                }
            } else {
                targetRoles.add(target);
            }
            //对role进行攻击
            helperBean.useSkill(targetRoles, skillBean.getId());
        }

        private List<Role> findTargetCopyScene(MmoHelperBean helperBean) {
            ArrayList<Role> target = new ArrayList<>();
            Integer copySceneId=helperBean.getCopySceneBeanId();
            CopySceneBean copySceneBean=CopySceneProvider.getCopySceneBeanById(copySceneId);
            target.add(copySceneBean.getNowBoss());
            return target;
        }

        private List<Role> findTargetInScene(MmoHelperBean helper){
            ArrayList<Role> target = new ArrayList<>();
            SceneBean sceneBean = SceneBeanMessageCache.getInstance().get(helper.getMmoSceneId());
            //npc
            for (Integer id : sceneBean.getNpcs()) {
                MmoSimpleNPC npc = NpcMessageCache.getInstance().get(id);
                if (npc.getType().equals(RoleTypeCode.ENEMY.getCode())) {
                    target.add(npc);
                }
            }
            //people
            for (Integer id : sceneBean.getRoles()) {
                if(id.equals(helperBean.getMasterId())){
                    continue;
                }
                MmoSimpleRole role = OnlineRoleMessageCache.getInstance().get(id);
                if (helper.getTeamId() == null) {
                    target.add(role);
                } else {
                    if (role.getTeamId() == null) {
                        target.add(role);
                    } else if (!helper.getTeamId().equals(role.getTeamId())) {
                        target.add(role);
                    }
                }
            }
            //hepler
            for (MmoHelperBean h:sceneBean.getHelperBeans()) {
                if (helper.getTeamId() == null) {
                    target.add(h);
                } else {
                    if (h.getTeamId() == null) {
                        target.add(h);
                    } else if (!helper.getTeamId().equals(h.getTeamId())) {
                        target.add(h);
                    }
                }
            }
            target.remove(helperBean);
            return target;
        }
    }

    public static class skillAttackTask implements Runnable {
        SkillBean skillBean;
        List<Role> target;
        MmoHelperBean mmoHelperBean;
        MmoSimpleRole user;
        private Logger logger = Logger.getLogger(NpcAttackTask.class);

        public skillAttackTask() {
        }

        public skillAttackTask(SkillBean skillBean, List<Role> target,MmoSimpleRole user,MmoHelperBean mmoHelperBean) {
            this.skillBean = skillBean;
            this.user = user;
            this.target=target;
            this.mmoHelperBean=mmoHelperBean;
        }

        @Override
        public void run() {
          skill(skillBean,target,user,mmoHelperBean);
        }
        /**
         * 技能释放
         */

        public void skill(SkillBean skillBean, List<Role> target,MmoSimpleRole user,MmoHelperBean mmoHelperBean){
            if (!skillBean.getSkillAttackType().equals(SkillAttackTypeCode.CALL.getCode())) {
                //  被攻击怪物or人物orBoss
                if (target.size() > 0) {
                    //触发宠物帮忙
                    Role t = target.get(0);
                    if (user.getMmoHelperBean() != null) {
                        user.getMmoHelperBean().npcAttack(t);
                    }
                }
                for (Role r : target) {
                    if (!skillBean.getBaseDamage().equals(0)) {
                        //伤害不为0才触发怪物的被攻击
                        r.beAttack(skillBean, user);
                    }
                    //伤害为0则是buffer技能 例如嘲讽
                    //buffer
                    for (Integer bufferId : skillBean.getBufferIds()) {
                        BufferMessage bufferMessage = BufferMessageCache.getInstance().get(bufferId);

                        skillBean.bufferToPeople(bufferMessage, user, r);
                    }
                }
            } else {
                //召唤的逻辑
                MmoHelperBean helperBean = CallerServiceProvider.callHelper(user);
                MmoHelperBean needDeleteBean = user.getMmoHelperBean();
                if (needDeleteBean != null) {
                    //已经有了则先消除
                    if (user.getMmoSceneId() != null) {
                        user.setMmoHelperBean(null);
                        if (needDeleteBean.getMmoSceneId() != null) {
                            SceneBean sceneBean = SceneBeanMessageCache.getInstance().get(needDeleteBean.getMmoSceneId());
                            sceneBean.getHelperBeans().remove(needDeleteBean);
                        } else if (needDeleteBean.getCopySceneBeanId() != null) {
                            CopySceneProvider.getCopySceneBeanById(needDeleteBean.getCopySceneBeanId()).getRoles().remove(needDeleteBean);
                        }
                    }
                }
                //新的召唤兽放到场景中

                user.setMmoHelperBean(helperBean);
                if (mmoHelperBean.getMmoSceneId() != null) {
                    SceneBean sceneBean = SceneBeanMessageCache.getInstance().get(mmoHelperBean.getMmoSceneId());
                    sceneBean.getHelperBeans().add(mmoHelperBean);
                } else if (mmoHelperBean.getCopySceneBeanId() != null) {
                    CopySceneProvider.getCopySceneBeanById(mmoHelperBean.getCopySceneBeanId()).getRoles().add(mmoHelperBean);
                }
            }
        }
    }

}
