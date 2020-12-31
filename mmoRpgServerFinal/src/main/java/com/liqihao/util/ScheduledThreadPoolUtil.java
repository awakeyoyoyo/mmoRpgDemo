package com.liqihao.util;

import com.liqihao.Cache.*;
import com.liqihao.commons.ConstantValue;
import com.liqihao.commons.NettyResponse;
import com.liqihao.commons.StateCode;
import com.liqihao.commons.enums.*;
import com.liqihao.pojo.baseMessage.SkillMessage;
import com.liqihao.pojo.bean.*;
import com.liqihao.protobufObject.PlayModel;
import com.liqihao.provider.TeamServiceProvider;
import com.sun.org.apache.bcel.internal.generic.IF_ACMPEQ;
import io.netty.channel.Channel;
import org.apache.log4j.Logger;

import java.util.ArrayList;
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
    private static ConcurrentHashMap<Integer, ScheduledFuture<?>> bufferRole = new ConcurrentHashMap<>();
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

    public static void init() {
        replyMpRole = new ConcurrentHashMap<>();
        bufferRole = new ConcurrentHashMap<>();
        npcTaskMap = new ConcurrentHashMap<>();
        copySceneTaskMap = new ConcurrentHashMap<>();
        scheduledExecutorService = new ScheduledThreadPoolExecutor(4);
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

    public static ConcurrentHashMap<Integer, ScheduledFuture<?>> getBufferRole() {
        return bufferRole;
    }

    public static void setBufferRole(ConcurrentHashMap<Integer, ScheduledFuture<?>> bufferRole) {
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
                attackStyleCode = AttackStyleCode.AUTORE.getCode();
            } else {
                //传入则代表着是吃药
                addNumber = number;
                attackStyleCode = AttackStyleCode.MEDICENE.getCode();
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
                role.changeNowBlood(addNumber, damageU, AttackStyleCode.MEDICENE.getCode());
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

        public BufferTask(BufferBean bufferBean, Integer count,Role toRole) {
            this.bufferBean = bufferBean;
            this.count = count;
            this.toRole=toRole;
        }

        @Override
        public void run() {
            logger.info("buffer线程-------------------" + Thread.currentThread().getName());
            Integer bufferType = bufferBean.getBuffType();
            if (bufferType.equals(BufferTypeCode.ADDHP.getCode()) ||
                    bufferType.equals(BufferTypeCode.REDUCEHP.getCode()) ||
                    bufferType.equals(BufferTypeCode.ADDMP.getCode()) ||
                    bufferType.equals(BufferTypeCode.REDUCEMP.getCode())) {
                Integer toroleId = bufferBean.getToRoleId();
                if (toRole == null || toRole.getStatus().equals(RoleStatusCode.DIE.getCode()) || count <= 0) {
                        //删除该buffer
                        String taskId = toroleId.toString() + bufferBean.getId().toString();
                        bufferRole.get(Integer.parseInt(taskId)).cancel(false);
                        bufferRole.remove(taskId);
                }
                toRole.effectByBuffer(bufferBean);

                }
                count--;
            }
    }

    public static class NpcAttackTask implements Runnable {
        private Integer targetRoleId;
        private Integer npcId;
        private Logger logger = Logger.getLogger(NpcAttackTask.class);

        public NpcAttackTask() {
        }

        public NpcAttackTask(Integer targetRoleId, Integer npcId) {
            this.targetRoleId = targetRoleId;
            this.npcId = npcId;
        }

        @Override
        public void run() {
            logger.info("npc攻击线程");
            MmoSimpleRole mmoSimpleRole = OnlineRoleMessageCache.getInstance().get(targetRoleId);
            MmoSimpleNPC npc = NpcMessageCache.getInstance().get(npcId);
            if (mmoSimpleRole == null ||
                    !npc.getMmosceneid().equals(mmoSimpleRole.getMmosceneid()) ||
                    mmoSimpleRole.getStatus().equals(RoleStatusCode.DIE.getCode()) ||
                    npc.getStatus().equals(RoleStatusCode.DIE.getCode())) {
                //中止任务  用户离线了 用户跑去别的场景了 死了
                npcTaskMap.get(npcId).cancel(false);
                npcTaskMap.remove(npcId);
                return;
            }
            //扣血咯
            if (mmoSimpleRole.getNowHp() <= 0) {
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
                skillBean.setAddPercon(skillMessage.getAddPercon());
                skillBean.setSkillType(skillMessage.getSkillType());
                Integer number = 0;
                if (skillBean.getSkillType().equals(SkillTypeCode.FIED.getCode())) {
                    //固伤 只有技能伤害
                    number = skillBean.getBaseDamage();
                }
                if (skillBean.getSkillType().equals(SkillTypeCode.PERCENTAGE.getCode())) {
                    //百分比 增加攻击力的10%
                    Integer damage = skillBean.getBaseDamage();
                    number = (int) Math.ceil(damage + mmoSimpleRole.getAttack() * skillBean.getAddPercon());
                }

                //广播
                // 生成一个角色扣血或者扣篮
                PlayModel.RoleIdDamage.Builder damageU = PlayModel.RoleIdDamage.newBuilder();
                damageU.setFromRoleId(npcId);
                damageU.setToRoleId(targetRoleId);
                damageU.setAttackStyle(AttackStyleCode.ATTACK.getCode());
                damageU.setBufferId(-1);
                damageU.setDamageType(ConsuMeTypeCode.HP.getCode());
                damageU.setSkillId(skillBean.getId());
                mmoSimpleRole.changeNowBlood(-number, damageU, AttackStyleCode.USESKILL.getCode());

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
            this.copySceneBean=copySceneBean;
        }

        @Override
        public void run() {
            logger.info("boss攻击线程");
            //仇恨的第一人
            Role role = null;
            while (true) {
                role = bossBean.getTarget();
                if (role == null) {
                    //bossBean.getTarget(); 查找为null 则代表着无人可以攻击了
                    break;
                }
                if (role.getCopySceneBeanId() == null || !role.getCopySceneBeanId().equals(bossBean.getCopySceneBeanId()) ||
                        role.getStatus().equals(RoleStatusCode.DIE.getCode()) ||
                        bossBean.getStatus().equals(RoleStatusCode.DIE.getCode())) {
                    continue;
                } else {
                    break;
                }
            }
            if (role == null) {
                // 挑战失败
                TeamBean teamBean= TeamServiceProvider.getTeamBeanByTeamId(copySceneBean.getTeamId());
                copySceneBean.changePeopleDie(teamBean);
                bossTaskMap.get(bossBean.getBossBeanId()).cancel(false);
                bossTaskMap.remove(bossBean.getBossBeanId());
                return;
            }
            SkillBean skillBean = null;
            //使用不同的技能
            if (attackCount % 6 == 0) {
                skillBean = skillBeans.get(1);
            } else {
                skillBean = skillBeans.get(0);
            }
            attackCount++;
            List<Role> targetRoles = new ArrayList<>();
            if (skillBean.getSkillAttackType().equals(SkillAttackTypeCode.ALLPEOPLE.getCode())) {
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


}
