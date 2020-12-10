package com.liqihao.util;


import com.liqihao.Cache.MmoCache;
import com.liqihao.commons.ConstantValue;
import com.liqihao.commons.NettyResponse;
import com.liqihao.commons.enums.AttackStyleCode;
import com.liqihao.commons.enums.DamageTypeCode;
import com.liqihao.commons.enums.StateCode;
import com.liqihao.pojo.bean.MmoSimpleRole;
import com.liqihao.protobufObject.PlayModel;
import io.netty.channel.Channel;

import java.util.concurrent.*;


/**
 * @author awakeyoyoyo
 * @className ScheduledPool
 * @description 线程池
 * @date 2020-12-03 22:52
 */
public class ThreadPools {
    private static ExecutorService executorService;
    //线程池初始化大小
    private static int poolSize=8;
    //任务队列长度
    public static void init(){
        /**
         * 核心线程数量大小
         * 最大活跃线程数量大小
         * 空闲线程超时时间
         * 时间单位
         * 有限的阻塞工作队列
         * 丢弃策略
         */
        //创建了一个最大线程数和核心线程数是8的线程池，工作队列长度为100的有限队列，线程池策略采取丢弃抛异常
        executorService = new ThreadPoolExecutor(
                poolSize,
                poolSize,
                0,
                TimeUnit.SECONDS,
                new LinkedBlockingDeque<Runnable>()
                );
        autoReMp();
    }
    public static void submit(Runnable task){
        executorService.submit(task);
    }
    public static void shutdown(){
        executorService.shutdown();;
    }
    private static void autoReMp(){
        executorService.submit(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    System.out.println("回蓝任务执行");
                    CopyOnWriteArrayList<Integer> list = MmoCache.getInstance().getNoMpRole();
                    try {
                        if (list.size() == 0) {

                        }else{
                            for (Integer id: list) {
                                MmoSimpleRole mmoSimpleRole=MmoCache.getInstance().getMmoSimpleRoleConcurrentHashMap().get(id);
                                Integer oldMp=mmoSimpleRole.getNowMp();
                                Integer addMp=(int)Math.ceil(oldMp*0.1);
                                Integer newMp=oldMp+addMp;
                                PlayModel.RoleIdDamage.Builder damageU=PlayModel.RoleIdDamage.newBuilder();
                                if (newMp>mmoSimpleRole.getMp()){
                                    mmoSimpleRole.setNowMp(mmoSimpleRole.getMp());
                                    list.remove(mmoSimpleRole.getId());
                                    //发送数据包
                                    damageU.setFromRoleId(mmoSimpleRole.getId());
                                    damageU.setToRoleId(mmoSimpleRole.getId());
                                    damageU.setAttackStyle(AttackStyleCode.AUTORE.getCode());
                                    damageU.setBufferId(-1);
                                    damageU.setDamage(mmoSimpleRole.getMp()-oldMp);
                                    damageU.setDamageType(DamageTypeCode.MP.getCode());
                                    damageU.setMp(mmoSimpleRole.getNowMp());
                                    damageU.setNowblood(mmoSimpleRole.getNowBlood());
                                    damageU.setSkillId(-1);
                                    damageU.setState(mmoSimpleRole.getStatus());
                                    //todo
                                }else {
                                    mmoSimpleRole.setNowMp(newMp);
                                    //发送数据包
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
                                }
                                PlayModel.PlayModelMessage.Builder myMessageBuilder=PlayModel.PlayModelMessage.newBuilder();
                                myMessageBuilder.setDataType(PlayModel.PlayModelMessage.DateType.DamagesNoticeResponse);
                                PlayModel.DamagesNoticeResponse.Builder damagesNoticeBuilder=PlayModel.DamagesNoticeResponse.newBuilder();
                                damagesNoticeBuilder.setRoleIdDamage(damageU);
                                myMessageBuilder.setDamagesNoticeResponse(damagesNoticeBuilder.build());
                                NettyResponse nettyResponse=new NettyResponse();
                                nettyResponse.setCmd(ConstantValue.DAMAGES_NOTICE_RESPONSE);
                                nettyResponse.setStateCode(StateCode.SUCCESS);
                                nettyResponse.setData(myMessageBuilder.build().toByteArray());
                                ConcurrentHashMap<Integer, Channel> c=MmoCache.getInstance().getChannelConcurrentHashMap();
                                c.get(mmoSimpleRole.getId()).writeAndFlush(nettyResponse);
                            }
                        }
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

            }
        });
    }
}
