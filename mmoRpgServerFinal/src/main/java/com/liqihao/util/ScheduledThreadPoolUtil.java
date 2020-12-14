package com.liqihao.util;

import com.liqihao.Cache.MmoCache;
import com.liqihao.commons.ConstantValue;
import com.liqihao.commons.NettyResponse;
import com.liqihao.commons.enums.*;
import com.liqihao.pojo.bean.BufferBean;
import com.liqihao.pojo.bean.MmoSimpleNPC;
import com.liqihao.pojo.bean.MmoSimpleRole;
import com.liqihao.protobufObject.PlayModel;
import io.netty.channel.Channel;
import org.apache.log4j.Logger;
import java.util.ArrayList;
import java.util.concurrent.*;

/**
 * @author awakeyoyoyo
 * @className ScheduledThreadPoolUtil
 * @description TODO
 * @date 2020-12-13 18:59
 */
public class ScheduledThreadPoolUtil {
    private static ScheduledThreadPoolExecutor scheduledExecutorService;
    //存储了正在调度线程池中执行的回蓝的角色id
    private static ConcurrentHashMap<Integer, ScheduledFuture<?>> replyMpRole = new ConcurrentHashMap<>();
    //存储了正在调度线程池中执行的buffer的角色id
    private static ConcurrentHashMap<Integer, ScheduledFuture<?>> bufferRole = new ConcurrentHashMap<>();

    public static void init() {
        replyMpRole = new ConcurrentHashMap<>();
        bufferRole = new ConcurrentHashMap<>();
        scheduledExecutorService = new ScheduledThreadPoolExecutor(4);
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

    public static ConcurrentHashMap<Integer, ScheduledFuture<?>> getReplyMpRole() {
        return replyMpRole;
    }

    public static void setReplyMpRole(ConcurrentHashMap<Integer, ScheduledFuture<?>> replyMpRole) {
        ScheduledThreadPoolUtil.replyMpRole = replyMpRole;
    }

    public static class ReplyMpTask implements Runnable {
        private Logger logger = Logger.getLogger(ReplyMpTask.class);
        private Integer roleId;
        private Integer number;

        public Logger getLogger() {
            return logger;
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

        public ReplyMpTask(Integer roleId, Integer number) {
            this.roleId = roleId;
            this.number = number;
        }

        @Override
        public void run() {
            logger.info("回蓝线程-------------------"+Thread.currentThread().getName());
            MmoSimpleRole mmoSimpleRole = MmoCache.getInstance().getMmoSimpleRoleConcurrentHashMap().get(roleId);
            Integer oldMp = mmoSimpleRole.getNowMp();
            Integer addMp;
            if (number == null) {
                //number没有传入 代表着这是自动回蓝
                addMp = (int) Math.ceil(mmoSimpleRole.getMp() * 0.05);
            } else {
                //传入则代表着是吃药
                addMp = number;
            }
            Integer newMp = oldMp + addMp;
            PlayModel.RoleIdDamage.Builder damageU = PlayModel.RoleIdDamage.newBuilder();
            if (newMp > mmoSimpleRole.getMp()) {
                mmoSimpleRole.setNowMp(mmoSimpleRole.getMp());
                addMp=mmoSimpleRole.getMp()-oldMp;
                //发送数据包
            } else {
                mmoSimpleRole.setNowMp(newMp);
            }
            damageU.setFromRoleId(mmoSimpleRole.getId());
            damageU.setToRoleId(mmoSimpleRole.getId());
            damageU.setAttackStyle(AttackStyleCode.AUTORE.getCode());
            damageU.setBufferId(-1);
            damageU.setDamage(addMp);
            damageU.setDamageType(DamageTypeCode.MP.getCode());
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
            if (mmoSimpleRole.getNowMp().equals(mmoSimpleRole.getMp())) {
                replyMpRole.get(roleId).cancel(false);
                replyMpRole.remove(roleId);
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
}
