package com.liqihao.util;

import com.liqihao.Cache.MmoCache;
import com.liqihao.commons.ConstantValue;
import com.liqihao.commons.NettyResponse;
import com.liqihao.commons.enums.*;
import com.liqihao.pojo.baseMessage.SkillMessage;
import com.liqihao.pojo.bean.BufferBean;
import com.liqihao.pojo.bean.MmoSimpleNPC;
import com.liqihao.pojo.bean.MmoSimpleRole;
import com.liqihao.pojo.bean.SkillBean;
import com.liqihao.protobufObject.PlayModel;
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
    //存储了正在调度线程池中执行的回蓝的角色id
    private static ConcurrentHashMap<String, ScheduledFuture<?>> replyMpRole = new ConcurrentHashMap<>();
    //存储了正在调度线程池中执行的buffer的角色id
    private static ConcurrentHashMap<Integer, ScheduledFuture<?>> bufferRole = new ConcurrentHashMap<>();
    //存储了正在调度线程池中执行的buffer的npc id限定一个npc只能攻击一个人
    private static ConcurrentHashMap<Integer, ScheduledFuture<?>> npcTaskMap = new ConcurrentHashMap<>();

    public static void init() {
        replyMpRole = new ConcurrentHashMap<>();
        bufferRole = new ConcurrentHashMap<>();
        npcTaskMap = new ConcurrentHashMap<>();
        scheduledExecutorService = new ScheduledThreadPoolExecutor(6);
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
        private Integer roleId;
        private Integer number;
        private Integer damageTypeCode;
        private String key;
        private Integer times;

        public ReplyMpTask(Integer roleId, Integer number, Integer damageTypeCode, String key) {
            this.roleId = roleId;
            this.number = number;
            this.damageTypeCode=damageTypeCode;
            this.key=key;
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

        public Integer getRoleId() {
            return roleId;
        }

        public void setRoleId(Integer roleId) {
            this.roleId = roleId;
        }

        public Integer getNumber() {
            return number;
        }

        public void setNumber(Integer number) {
            this.number = number;
        }

        public ReplyMpTask(Integer roleId, Integer number,Integer damageTypeCode,String key,Integer times) {
            this.roleId = roleId;
            this.number = number;
            this.damageTypeCode=damageTypeCode;
            this.key=key;
            this.times=times;
        }

        @Override
        public void run() {
            logger.info("回蓝/血线程-------------------"+Thread.currentThread().getName());
            MmoSimpleRole mmoSimpleRole = MmoCache.getInstance().getMmoSimpleRoleConcurrentHashMap().get(roleId);
            Integer addNumber;
            Integer attackStyleCode;
            if (times!=null&&times<=0||mmoSimpleRole.getStatus().equals(RoleStatusCode.DIE.getCode())){
                replyMpRole.get(key).cancel(false);
                replyMpRole.remove(key);
                return;
            }
            if (number == null) {
                //number没有传入 代表着这是自动回蓝
                addNumber = (int) Math.ceil(mmoSimpleRole.getMp() * 0.05);
                attackStyleCode=AttackStyleCode.AUTORE.getCode();
            } else {
                //传入则代表着是吃药
                addNumber = number;
                attackStyleCode=AttackStyleCode.MEDICENE.getCode();

            }
            PlayModel.RoleIdDamage.Builder damageU = PlayModel.RoleIdDamage.newBuilder();
            if (damageTypeCode.equals(DamageTypeCode.MP.getCode())) {
                Integer oldMp = mmoSimpleRole.getNowMp();
                Integer newNumber = oldMp + addNumber;
                if (newNumber > mmoSimpleRole.getMp()) {
                    mmoSimpleRole.setNowMp(mmoSimpleRole.getMp());
                    addNumber = mmoSimpleRole.getMp() - oldMp;
                    //发送数据包
                } else {
                    mmoSimpleRole.setNowMp(newNumber);
                }
                if (times!=null) {
                    times--;
                }
            }else{
                Integer oldHP = mmoSimpleRole.getNowBlood();
                Integer newNumber = oldHP + addNumber;
                if (newNumber > mmoSimpleRole.getBlood()) {
                    mmoSimpleRole.setNowBlood(mmoSimpleRole.getBlood());
                    addNumber = mmoSimpleRole.getBlood() - oldHP;
                    //发送数据包
                } else {
                    mmoSimpleRole.setNowBlood(newNumber);
                }
                if (times!=null) {
                    times--;
                }
            }
            damageU.setFromRoleId(mmoSimpleRole.getId());
            damageU.setToRoleId(mmoSimpleRole.getId());
            damageU.setAttackStyle(attackStyleCode);
            damageU.setBufferId(-1);
            damageU.setDamage(addNumber);
            damageU.setDamageType(damageTypeCode);
            damageU.setMp(mmoSimpleRole.getNowMp());
            damageU.setNowblood(mmoSimpleRole.getNowBlood());
            damageU.setSkillId(-1);
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
            ConcurrentHashMap<Integer, MmoSimpleRole> roleMap = MmoCache.getInstance().getMmoSimpleRoleConcurrentHashMap();
            Integer sceneId = mmoSimpleRole.getMmosceneid();
            ArrayList<Integer> players = new ArrayList<>();
            for (Integer npcId : roleMap.keySet()) {
                MmoSimpleRole role = roleMap.get(npcId);
                if (role.getMmosceneid().equals(sceneId) && role.getType().equals(RoleTypeCode.PLAYER.getCode())) {
                    players.add(role.getId());
                }
            }
            for (Integer playerId : players) {
                ConcurrentHashMap<Integer, Channel> cMap = MmoCache.getInstance().getChannelConcurrentHashMap();
                Channel cc = cMap.get(playerId);
                if (cc != null) {
                    cc.writeAndFlush(nettyResponse);
                }
            }
            //判断任务是否以及完成即 人物蓝是否满了
            if (number==null) {
                if (mmoSimpleRole.getNowMp().equals(mmoSimpleRole.getMp())) {
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

        public BufferTask() {
        }

        public BufferTask(BufferBean bufferBean, Integer count) {
            this.bufferBean = bufferBean;
            this.count = count;
        }

        @Override
        public void run() {
            logger.info("buffer线程-------------------"+Thread.currentThread().getName());
            Integer bufferType = bufferBean.getBuffType();
            if (bufferType.equals(BufferTypeCode.ADDHP.getCode()) ||
                    bufferType.equals(BufferTypeCode.REDUCEHP.getCode()) ||
                    bufferType.equals(BufferTypeCode.ADDMP.getCode()) ||
                    bufferType.equals(BufferTypeCode.REDUCEMP.getCode())) {
                Integer ToroleId = bufferBean.getToRoleId();
                MmoSimpleNPC npc = MmoCache.getInstance().getNpcMessageConcurrentHashMap().get(ToroleId);
                if (npc == null || npc.getStatus().equals(RoleStatusCode.DIE.getCode()) || count <= 0) {
                    //删除该buffer
                    String taskId=ToroleId.toString()+bufferBean.getId().toString();
                    replyMpRole.get(Integer.parseInt(taskId)).cancel(false);
                    replyMpRole.remove(taskId);
                } else {
                    //根据buffer类型扣血扣蓝
                    if (bufferBean.getBuffType().equals(BufferTypeCode.REDUCEHP.getCode())) {
                        Integer hp = npc.getNowBlood() - bufferBean.getBuffNum();
                        if (hp <= 0) {
                            hp = 0;
                        }
                        npc.setNowBlood(hp);
                    } else if (bufferBean.getBuffType().equals(BufferTypeCode.REDUCEMP.getCode())) {
                        Integer mp = npc.getNowMp() - bufferBean.getBuffNum();
                        if (mp <= 0) {
                            mp = 0;
                        }
                        npc.setNowMp(mp);
                    }
                    //广播信息
                    ConcurrentHashMap<Integer, MmoSimpleRole> roleMap = MmoCache.getInstance().getMmoSimpleRoleConcurrentHashMap();
                    Integer sceneId = roleMap.get(bufferBean.getFromRoleId()).getMmosceneid();
                    ArrayList<Integer> players = new ArrayList<>();
                    for (Integer npcId : roleMap.keySet()) {
                        MmoSimpleRole role = roleMap.get(npcId);
                        if (role.getMmosceneid().equals(sceneId) && role.getType().equals(RoleTypeCode.PLAYER.getCode())) {
                            players.add(role.getId());
                        }
                    }
                    //生成数据包
                    PlayModel.PlayModelMessage.Builder myMessageBuilder = PlayModel.PlayModelMessage.newBuilder();
                    myMessageBuilder.setDataType(PlayModel.PlayModelMessage.DateType.DamagesNoticeResponse);
                    PlayModel.DamagesNoticeResponse.Builder damagesNoticeBuilder = PlayModel.DamagesNoticeResponse.newBuilder();
                    PlayModel.RoleIdDamage.Builder damageU = PlayModel.RoleIdDamage.newBuilder();
                    damageU.setDamageType(DamageTypeCode.HP.getCode()).setAttackStyle(AttackStyleCode.BUFFER.getCode())
                            .setDamage(bufferBean.getBuffNum()).setFromRoleId(bufferBean.getFromRoleId()).setToRoleId(bufferBean.getToRoleId())
                            .setState(npc.getStatus()).setMp(npc.getNowMp()).setBufferId(bufferBean.getId()).setNowblood(npc.getNowBlood());
                    damagesNoticeBuilder.setRoleIdDamage(damageU);
                    myMessageBuilder.setDamagesNoticeResponse(damagesNoticeBuilder.build());
                    NettyResponse nettyResponse = new NettyResponse();
                    nettyResponse.setCmd(ConstantValue.DAMAGES_NOTICE_RESPONSE);
                    nettyResponse.setStateCode(StateCode.SUCCESS);
                    nettyResponse.setData(myMessageBuilder.build().toByteArray());
                    for (Integer playerId : players) {
                        ConcurrentHashMap<Integer, Channel> cMap = MmoCache.getInstance().getChannelConcurrentHashMap();
                        Channel c = cMap.get(playerId);
                        if (c != null) {
                            c.writeAndFlush(nettyResponse);
                        }

                    }
                    count--;
                }

            }

        }
    }

    public static class NpcAttackTask implements Runnable{
        private Integer targetRoleId;
        private Integer npcId;

        public NpcAttackTask() {
        }

        public NpcAttackTask(Integer targetRoleId, Integer npcId) {
            this.targetRoleId = targetRoleId;
            this.npcId = npcId;
        }

        @Override
        public void run() {

            MmoSimpleRole mmoSimpleRole=MmoCache.getInstance().getMmoSimpleRoleConcurrentHashMap().get(targetRoleId);
            MmoSimpleNPC npc=MmoCache.getInstance().getNpcMessageConcurrentHashMap().get(npcId);
            if (mmoSimpleRole==null||
                    !npc.getMmosceneid().equals(mmoSimpleRole.getMmosceneid())||
                    mmoSimpleRole.getStatus().equals(RoleStatusCode.DIE.getCode())||
                    npc.getStatus().equals(RoleStatusCode.DIE.getCode())){
                //中止任务  用户离线了 用户跑去别的场景了 死了
                npcTaskMap.get(npcId).cancel(false);
                npcTaskMap.remove(npcId);
                return;
            }
            //扣血咯
            if (mmoSimpleRole.getNowBlood()<=0){
                npcTaskMap.get(npcId).cancel(false);
                npcTaskMap.remove(npcId);
                return;
            }else {
                //npc默认使用普通攻击
                //从缓存中找出技能
                SkillMessage skillMessage=MmoCache.getInstance().getSkillMessageConcurrentHashMap().get(3);
                SkillBean skillBean=new SkillBean();
                skillBean.setId(skillMessage.getId());
                skillBean.setConsumeType(skillMessage.getConsumeType());
                skillBean.setConsumeNum(skillMessage.getConsumeNum());
                skillBean.setCd(skillMessage.getCd());
                skillBean.setBufferIds(CommonsUtil.split(skillMessage.getBufferIds()));
                skillBean.setBaseDamage(skillMessage.getBaseDamage());
                skillBean.setSkillName(skillMessage.getSkillName());
                skillBean.setAddPercon(skillMessage.getAddPercon());
                skillBean.setSkillType(skillMessage.getSkillType());
                Integer hp=mmoSimpleRole.getNowBlood();
                Integer reduce=0;
                if (skillBean.getSkillType().equals(SkillTypeCode.FIED.getCode())){
                    //固伤 只有技能伤害
                    hp-=skillBean.getBaseDamage();
                    reduce=skillBean.getBaseDamage();
                }
                if(skillBean.getSkillType().equals(SkillTypeCode.PERCENTAGE.getCode())){
                    //百分比 增加攻击力的10%
                    Integer damage=skillBean.getBaseDamage();
                    damage=(int)Math.ceil(damage+mmoSimpleRole.getAttack()*skillBean.getAddPercon());
                    hp=hp-damage;
                    reduce=damage;
                }
                if (hp<=0){
                    reduce=reduce+hp;
                    hp=0;
                    mmoSimpleRole.setStatus(RoleStatusCode.DIE.getCode());
                }
                mmoSimpleRole.setNowBlood(hp);
                //广播
                // 生成一个角色扣血或者扣篮
                List<PlayModel.RoleIdDamage> list=new ArrayList<>();
                PlayModel.RoleIdDamage.Builder damageU=PlayModel.RoleIdDamage.newBuilder();
                damageU.setFromRoleId(npcId);
                damageU.setToRoleId(targetRoleId);
                damageU.setAttackStyle(AttackStyleCode.ATTACK.getCode());
                damageU.setBufferId(-1);
                damageU.setDamage(reduce);
                damageU.setDamageType(ConsuMeTypeCode.HP.getCode());
                damageU.setMp(mmoSimpleRole.getNowMp());
                damageU.setNowblood(mmoSimpleRole.getNowBlood());
                damageU.setSkillId(skillBean.getId());
                damageU.setState(mmoSimpleRole.getStatus());
                list.add(damageU.build());
                //生成数据包
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
                ConcurrentHashMap<Integer, MmoSimpleRole> roleMap=MmoCache.getInstance().getMmoSimpleRoleConcurrentHashMap();
                Integer sceneId=mmoSimpleRole.getMmosceneid();
                ArrayList<Integer> players=new ArrayList<>();
                for (Integer npcId:roleMap.keySet()) {
                    MmoSimpleRole role=roleMap.get(npcId);
                    if (role.getMmosceneid().equals(sceneId)&&role.getType().equals(RoleTypeCode.PLAYER.getCode())){
                        players.add(role.getId());
                    }
                }
                for (Integer playerId:players){
                    ConcurrentHashMap<Integer,Channel> cMap=MmoCache.getInstance().getChannelConcurrentHashMap();
                    Channel c=cMap.get(playerId);
                    if (c!=null){
                        c.writeAndFlush(nettyResponse);
                    }
                }
            }
        }
    }

}
