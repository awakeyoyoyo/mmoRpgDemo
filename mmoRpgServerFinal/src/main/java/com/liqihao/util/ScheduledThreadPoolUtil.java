package com.liqihao.util;

import com.liqihao.Cache.MmoCache;
import com.liqihao.commons.ConstantValue;
import com.liqihao.commons.NettyResponse;
import com.liqihao.commons.enums.*;
import com.liqihao.pojo.bean.BufferBean;
import com.liqihao.pojo.bean.BufferManager;
import com.liqihao.pojo.bean.MmoSimpleNPC;
import com.liqihao.pojo.bean.MmoSimpleRole;
import com.liqihao.protobufObject.PlayModel;
import io.netty.channel.Channel;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;

/**
 * @author awakeyoyoyo
 * @className ScheduledThreadPoolUtil
 * @description TODO
 * @date 2020-12-13 18:59
 */
public class ScheduledThreadPoolUtil {
    private static ScheduledThreadPoolExecutor scheduledExecutorService;

    public static void init() {
        scheduledExecutorService = new ScheduledThreadPoolExecutor(4);
    }

    public static ScheduledThreadPoolExecutor getScheduledExecutorService() {
        return scheduledExecutorService;
    }

    public static void setScheduledExecutorService(ScheduledThreadPoolExecutor scheduledExecutorService) {
        ScheduledThreadPoolUtil.scheduledExecutorService = scheduledExecutorService;
    }

    public class ReplyMpTask implements Runnable {
        private Logger logger = Logger.getLogger(ReplyMpTask.class);
        private Integer roleId;

        public ReplyMpTask(Integer roleId) {
            this.roleId = roleId;
        }

        @Override
        public void run() {
            logger.info("回蓝线程-------------------");
            MmoSimpleRole mmoSimpleRole = MmoCache.getInstance().getMmoSimpleRoleConcurrentHashMap().get(roleId);
            Integer oldMp = mmoSimpleRole.getNowMp();
            Integer addMp = (int) Math.ceil(oldMp * 0.1);
            Integer newMp = oldMp + addMp;
            PlayModel.RoleIdDamage.Builder damageU = PlayModel.RoleIdDamage.newBuilder();
            if (newMp > mmoSimpleRole.getMp()) {
                mmoSimpleRole.setNowMp(mmoSimpleRole.getMp());
                //发送数据包
            } else {
                mmoSimpleRole.setNowMp(newMp);
            }
            damageU.setFromRoleId(mmoSimpleRole.getId());
            damageU.setToRoleId(mmoSimpleRole.getId());
            damageU.setAttackStyle(AttackStyleCode.AUTORE.getCode());
            damageU.setBufferId(-1);
            damageU.setDamage(mmoSimpleRole.getMp() - oldMp);
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
                scheduledExecutorService.remove(this);
            }
        }
    }

    public class BufferTask implements Runnable {
        private Logger logger = Logger.getLogger(BufferTask.class);
        private BufferManager bufferManager;
//        private
        public BufferTask() {

        }

        public BufferTask(BufferManager bufferManager) {
            this.bufferManager = bufferManager;
        }

        @Override
        public void run() {
            logger.info("buffer线程-------------------");
            //处理每个buffer
            List<BufferBean> beans = bufferManager.getBufferBeans();
            for (BufferBean bean : beans) {
                Integer bufferType = bean.getBuffType();
                if (bufferType.equals(BufferTypeCode.ADDHP.getCode()) ||
                        bufferType.equals(BufferTypeCode.REDUCEHP.getCode()) ||
                        bufferType.equals(BufferTypeCode.ADDMP.getCode()) ||
                        bufferType.equals(BufferTypeCode.REDUCEMP.getCode())) {
                    //需要间隔性更改信息的
                    //判断cdMap有无该buffer时间 即开始了没
                    ConcurrentHashMap<Integer, Long> cdMap = bufferManager.getCdMap();
                    if (cdMap.containsKey(bean.getId())) {
                        //该buffer已经开始计算
                        Long targetTime = cdMap.get(bean.getId());
                        Long nowTime = System.currentTimeMillis();
                        if (targetTime <= nowTime) {
                            //扣伤害
                            Integer ToroleId = bean.getToRoleId();
                            MmoSimpleNPC npc = MmoCache.getInstance().getNpcMessageConcurrentHashMap().get(ToroleId);
                            if (npc == null) {
                                continue;
                            } else {
                                //根据buffer类型扣血扣蓝
                                if (bean.getBuffType().equals(BufferTypeCode.REDUCEHP.getCode())) {
                                    if (npc.getNowBlood() <= 0) {
                                        cdMap.remove(bean.getId());
                                        bufferManager.getBufferBeans().remove(bean);
                                        if (bufferManager.getBufferBeans().size() == 0) {
                                            bufferManagerConcurrentHashMap.remove(manager);
                                        }
                                    }
                                    Integer hp = npc.getNowBlood() - bean.getBuffNum();
                                    if (hp <= 0) {
                                        hp = 0;
                                    }
                                    npc.setNowBlood(hp);
                                } else if (bean.getBuffType().equals(BufferTypeCode.REDUCEMP.getCode())) {
                                    Integer mp = npc.getNowMp() - bean.getBuffNum();
                                    if (mp <= 0) {
                                        mp = 0;
                                    }
                                    npc.setNowMp(mp);
                                }
                                //设置下一个间隔的时间放入cdMap
                                targetTime = nowTime + bean.getSpaceTime() * 1000;
                                if (targetTime <= (bean.getCreateTime() + bean.getLastTime() * 1000)) {
                                    //buffer下个间隔时间还会生效
                                    cdMap.put(bean.getId(), targetTime);
                                } else {
                                    cdMap.remove(bean.getId());
                                    manager.getBufferBeans().remove(bean);
                                    if (manager.getBufferBeans().size() == 0) {
                                        bufferManagerConcurrentHashMap.remove(manager);
                                    }
                                }
                                //广播信息
                                ConcurrentHashMap<Integer, MmoSimpleRole> roleMap = MmoCache.getInstance().getMmoSimpleRoleConcurrentHashMap();
                                Integer sceneId = roleMap.get(bean.getFromRoleId()).getMmosceneid();
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
                                        .setDamage(bean.getBuffNum()).setFromRoleId(bean.getFromRoleId()).setToRoleId(bean.getToRoleId())
                                        .setState(npc.getStatus()).setMp(npc.getNowMp()).setBufferId(bean.getId()).setNowblood(npc.getNowBlood());
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
                            }
                        } else {
                            //buffer到期了
                        }
                    } else {
                        //buffer未开始
                        MmoSimpleNPC npc = MmoCache.getInstance().getNpcMessageConcurrentHashMap().get(bean.getToRoleId());
                        if (npc == null) {
                            continue;
                        } else {
                            //根据buffer类型扣血扣蓝
                            //下一次buffer再次生效时间
                            Long targetTime = bean.getCreateTime() + bean.getSpaceTime() * 1000;
                            if (bean.getBuffType().equals(BufferTypeCode.REDUCEHP.getCode())) {
                                if (npc.getNowBlood() <= 0) {
                                    cdMap.remove(bean.getId());
                                    manager.getBufferBeans().remove(bean);
                                    if (manager.getBufferBeans().size() == 0) {
                                        bufferManagerConcurrentHashMap.remove(manager);
                                    }
                                }
                                Integer hp = npc.getNowBlood() - bean.getBuffNum();
                                if (hp <= 0) {
                                    hp = 0;
                                    npc.setStatus(RoleStatusCode.DIE.getCode());
                                }
                                npc.setNowBlood(hp);
                            } else if (bean.getBuffType().equals(BufferTypeCode.REDUCEMP.getCode())) {
                                Integer mp = npc.getNowMp() - bean.getBuffNum();
                                if (mp <= 0) {
                                    mp = 0;
                                }
                                npc.setNowMp(mp);
                            }
                            //设置下一个间隔的时间放入cdMap
                            if (targetTime <= (bean.getCreateTime() + bean.getLastTime() * 1000)) {
                                cdMap.put(bean.getId(), targetTime);
                            } else {
                                cdMap.remove(bean.getId());
                                manager.getBufferBeans().remove(bean);
                                if (manager.getBufferBeans().size() == 0) {
                                    bufferManagerConcurrentHashMap.remove(manager);
                                }
                            }
                            //广播信息
                            ConcurrentHashMap<Integer, MmoSimpleRole> roleMap = MmoCache.getInstance().getMmoSimpleRoleConcurrentHashMap();
                            Integer sceneId = roleMap.get(bean.getFromRoleId()).getMmosceneid();
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
                                    .setDamage(bean.getBuffNum()).setFromRoleId(bean.getFromRoleId()).setToRoleId(bean.getToRoleId())
                                    .setState(npc.getStatus()).setMp(npc.getNowMp()).setBufferId(bean.getId()).setNowblood(npc.getNowBlood());
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
                        }
                    }
                } else {
                    //需要持续性增加属性的buffer
                }
            }
        }
    }
}
