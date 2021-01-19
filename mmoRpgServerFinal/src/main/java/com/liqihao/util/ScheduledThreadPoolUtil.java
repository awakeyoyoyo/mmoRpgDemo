package com.liqihao.util;

import com.liqihao.Cache.*;
import com.liqihao.commons.RpgServerException;
import com.liqihao.commons.enums.*;
import com.liqihao.pojo.baseMessage.BufferMessage;
import com.liqihao.pojo.baseMessage.SkillMessage;
import com.liqihao.pojo.bean.*;
import com.liqihao.pojo.bean.bufferBean.BaseBufferBean;
import com.liqihao.pojo.bean.dealBankBean.DealBankArticleBean;
import com.liqihao.pojo.bean.roleBean.*;
import com.liqihao.pojo.bean.teamBean.TeamBean;
import com.liqihao.protobufObject.PlayModel;
import com.liqihao.provider.CallerServiceProvider;
import com.liqihao.provider.CopySceneProvider;
import com.liqihao.provider.TeamServiceProvider;
import org.apache.log4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.*;

/**
 * @author awakeyoyoyo
 * @className ScheduledThreadPoolUtil
 * @description
 * @date 2020-12-13 18:59
 */
@Component
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
     * 存储了正在调度线程池中执行的boss 攻击任务
     */
    private static ConcurrentHashMap<Integer, ScheduledFuture<?>> bossTaskMap = new ConcurrentHashMap<>();
    /**
     * 存储了正在调度线程池中执行的宠物 攻击任务
     */
    private static ConcurrentHashMap<Integer, ScheduledFuture<?>> helperTaskMap = new ConcurrentHashMap<>();
    /**
     * 存储了正在调度线程池中拍卖物品任务
     */
    private static ConcurrentHashMap<Integer, ScheduledFuture<?>> dealBankTaskMap = new ConcurrentHashMap<>();
    /**
     * 工作者列表
     */
    private static DbTask dbTask;
    @PostConstruct
    public static void init() {
        replyMpRole = new ConcurrentHashMap<>();
        bufferRole = new ConcurrentHashMap<>();
        npcTaskMap = new ConcurrentHashMap<>();
        copySceneTaskMap = new ConcurrentHashMap<>();
        scheduledExecutorService = new ScheduledThreadPoolExecutor(10);
        dbTask = new DbTask();
        //每60s 修改过的数据落地
        scheduledExecutorService.scheduleAtFixedRate(dbTask, 0, 10, TimeUnit.SECONDS);
    }

    public static ConcurrentHashMap<Integer, ScheduledFuture<?>> getDealBankTaskMap() {
        return dealBankTaskMap;
    }

    public static void setDealBankTaskMap(ConcurrentHashMap<Integer, ScheduledFuture<?>> dealBankTaskMap) {
        ScheduledThreadPoolUtil.dealBankTaskMap = dealBankTaskMap;
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


    /**
     * 自动恢复蓝量线程任务
     */
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

    /**
     * buffer线程任务
     */
    public static class BufferTask implements Runnable {
        private Logger logger = Logger.getLogger(BufferTask.class);
        private BaseBufferBean bufferBean;
        private Integer count;
        private Role toRole;

        public BufferTask() {
        }

        public BufferTask(BaseBufferBean bufferBean, Integer count, Role toRole) {
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
                String taskId = toroleId.toString() + bufferBean.getBufferMessageId().toString();
                bufferRole.remove(taskId);
                toRole.getBufferBeans().remove(bufferBean);
                bufferRole.get(Integer.parseInt(taskId)).cancel(false);
                //人物身上删除buffer

            }
            toRole.effectByBuffer(bufferBean);
            count--;
        }
    }

    /**
     * npc攻击线程任务
     */
    public static class NpcAttackTask implements Runnable {
        private Integer npcId;
        private Logger logger = Logger.getLogger(NpcAttackTask.class);

        public NpcAttackTask() {
        }

        public NpcAttackTask(Integer npcId) {
            this.npcId = npcId;
        }

        @Override
        public void run() {
            logger.info("怪物攻击线程");
            MmoSimpleNPC npc = NpcMessageCache.getInstance().get(npcId);
            //判断是否有嘲讽buffer,则直接攻击嘲讽对象
            Role target = null;
            target = npc.getTarget();
            if (npc.getStatus().equals(RoleStatusCode.DIE.getCode())) {
                //死亡
                bossTaskMap.remove(npc.getId());
                bossTaskMap.get(npc.getId()).cancel(false);
            }
            if (target == null) {
                // 没目标
                bossTaskMap.remove(npc.getId());
                bossTaskMap.get(npc.getId()).cancel(false);
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
                target.beAttack(skillBean, npc);
            }
        }
    }

    /**
     * 副本超时任务
     */
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

    /**
     * boss攻击任务
     */
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

            if (bossBean.getStatus().equals(RoleStatusCode.DIE.getCode())) {
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
            if (attackCount % 5 == 0) {
                skillBean = skillBeans.get(1);
                attackCount = 0;
            } else {
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

    /**
     * 召唤物攻击任务
     */
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
            this.target = target;
        }

        @Override
        public void run() {
            logger.info("helper攻击线程");
            //判断停止攻击
            if (target.getMmoSceneId() != null) {
                if (!target.getMmoSceneId().equals(helperBean.getMmoSceneId())) {
                    helperTaskMap.remove(helperBean.getId());
                    helperTaskMap.get(helperBean.getId()).cancel(false);
                    return;
                }
            } else {
                if (!target.getCopySceneBeanId().equals(helperBean.getCopySceneBeanId())) {
                    helperTaskMap.remove(helperBean.getId());
                    helperTaskMap.get(helperBean.getId()).cancel(false);
                    return;
                }
            }
            if (target.getStatus().equals(RoleStatusCode.DIE.getCode()) || helperBean.getStatus().equals(RoleStatusCode.DIE.getCode())) {
                // 没目标攻击  停止攻击线程
                helperTaskMap.remove(helperBean.getId());
                helperTaskMap.get(helperBean.getId()).cancel(false);
                return;
            }
            SkillBean skillBean = null;
            //使用不同的技能
            if (attackCount % 5 == 0) {
                skillBean = skillBeans.get(1);
                attackCount = 0;
            } else {
                skillBean = skillBeans.get(0);
            }
            attackCount++;
            List<Role> targetRoles = new ArrayList<>();
            //群攻技能
            if (skillBean.getSkillAttackType().equals(SkillAttackTypeCode.ALL_PEOPLE.getCode())) {
                if (helperBean.getMmoSceneId() != null) {
                    //在场景中
                    targetRoles.addAll(findTargetInScene(helperBean));
                } else {
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
            Integer copySceneId = helperBean.getCopySceneBeanId();
            CopySceneBean copySceneBean = CopySceneProvider.getCopySceneBeanById(copySceneId);
            target.add(copySceneBean.getNowBoss());
            return target;
        }

        private List<Role> findTargetInScene(MmoHelperBean helper) {
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
                if (id.equals(helperBean.getMasterId())) {
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
            for (MmoHelperBean h : sceneBean.getHelperBeans()) {
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

    /**
     * 技能吟唱时间任务
     */
    public static class SkillAttackTask implements Runnable {
        SkillBean skillBean;
        List<Role> target;
        MmoHelperBean mmoHelperBean;
        MmoSimpleRole user;
        private Logger logger = Logger.getLogger(NpcAttackTask.class);

        public SkillAttackTask() {
        }

        public SkillAttackTask(SkillBean skillBean, List<Role> target, MmoSimpleRole user, MmoHelperBean mmoHelperBean) {
            this.skillBean = skillBean;
            this.user = user;
            this.target = target;
            this.mmoHelperBean = mmoHelperBean;
        }

        @Override
        public void run() {
            skill(skillBean, target, user, mmoHelperBean);
        }

        /**
         * 技能释放
         */

        public void skill(SkillBean skillBean, List<Role> target, MmoSimpleRole user, MmoHelperBean mmoHelperBean) {
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

    /**
     * 定时更新数据任务
     */
    public static class DbTask implements Runnable {

        private final org.slf4j.Logger log = LoggerFactory.getLogger(DbTask.class);
        /**
         * db任务队列
         */
        private LinkedList<Runnable> tasks = new LinkedList<>();

        @Override
        public void run() {
            log.info("定时数据落地任务：");
            Runnable job = null;
            //每隔时间清空任务队列里面得任务
            while(!tasks.isEmpty()) {
                synchronized (tasks) {
//                    if (tasks.isEmpty()) {
//                        //没任务 就直接溜溜球
//                        return;
//                    }
                    // 取出一个Job
                    job = tasks.removeFirst();
                }
                if (job != null) {
                    try {
                        job.run();
                    } catch (Exception ex) {
                        log.error("System.out...... -> " + ex);
                    }
                }
            }
        }
    }

    /**
     * addTask到队列中
     * @param job
     */
    public static void addTask(Runnable job) {
        if (job != null) {
            // 添加一个工作到任务队列即可
            synchronized (dbTask.tasks) {
                dbTask.tasks.addLast(job);
            }
        }
    }

    /**
     * 拍卖超时任务
     */
    public static class DealBankOutTimeTask implements Runnable {
        private DealBankArticleBean dealBankArticleBean;
        private Logger logger = Logger.getLogger(DealBankOutTimeTask.class);

        public DealBankOutTimeTask() {
        }

        public DealBankOutTimeTask(DealBankArticleBean dealBankArticleBean) {
            this.dealBankArticleBean=dealBankArticleBean;
        }

        @Override
        public void run() {
            try {
                dealBankArticleBean.dealBankTimeOut();
                //消除任务
                ScheduledFuture<?> t=ScheduledThreadPoolUtil.getCopySceneTaskMap().get(dealBankArticleBean.getDealBeanArticleBeanId());
                ScheduledThreadPoolUtil.getCopySceneTaskMap().remove(dealBankArticleBean.getDealBeanArticleBeanId());
                t.cancel(false);
            } catch (RpgServerException e) {
                e.printStackTrace();
            }
        }
    }

}
