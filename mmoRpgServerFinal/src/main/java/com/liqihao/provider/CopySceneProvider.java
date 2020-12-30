package com.liqihao.provider;

import com.liqihao.Cache.BossMessageCache;
import com.liqihao.Cache.CopySceneMessageCache;
import com.liqihao.commons.enums.CopySceneBeanStatusCode;
import com.liqihao.pojo.baseMessage.BossMessage;
import com.liqihao.pojo.baseMessage.CopySceneMessage;
import com.liqihao.pojo.bean.BossBean;
import com.liqihao.pojo.bean.CopySceneBean;
import com.liqihao.pojo.bean.TeamBean;
import com.liqihao.util.CommonsUtil;
import com.liqihao.util.ScheduledThreadPoolUtil;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 游戏副本提供者
 * @author lqhao
 */
public class CopySceneProvider {
    private static ConcurrentHashMap<Integer,CopySceneBean> copySceneBeans=new ConcurrentHashMap<>();
    private static AtomicInteger copySceneBeanIdAuto=new AtomicInteger(0);
    private static AtomicInteger bossBeanIdAuto=new AtomicInteger(0);

    public static CopySceneBean createNewCopyScene(Integer copySceneId, TeamBean teamBean){
        CopySceneMessage copySceneMessage=CopySceneMessageCache.getInstance().get(copySceneId);
        Integer copyBeanId=copySceneBeanIdAuto.incrementAndGet();
        CopySceneBean copySceneBean= CommonsUtil.copySceneMessageToCopySceneBean(copySceneMessage);
        copySceneBean.setCopySceneBeanId(copyBeanId);
        List<Integer> bossIds=CommonsUtil.split(copySceneMessage.getBossIds());
        List<BossBean> bossBeans=new ArrayList<>();
        for (Integer id:bossIds) {
            BossMessage bossMessage=BossMessageCache.getInstance().get(id);
            BossBean bossBean=CommonsUtil.bossMessageToBossBean(bossMessage);
            Integer bossBeanId=bossBeanIdAuto.incrementAndGet();
            bossBean.setBossBeanId(bossBeanId);
            bossBeans.add(bossBean);
        }
        copySceneBean.setBossBeans(bossBeans);
        //开启定时任务
        ScheduledThreadPoolUtil.getScheduledExecutorService().schedule(
                new ScheduledThreadPoolUtil.CopySceneOutTimeTask(teamBean,copySceneBean)
                , copySceneBean.getLastTime(), TimeUnit.SECONDS);

        copySceneBeans.put(copySceneBean.getCopySceneBeanId(),copySceneBean);
        return copySceneBean;
    }

    public static void deleteNewCopySceneById(Integer copySceneBeanId){
        Iterator<CopySceneBean> it = copySceneBeans.values().iterator();
        while(it.hasNext()){
            CopySceneBean copySceneBean = it.next();
            if(copySceneBean.getCopySceneBeanId().equals(copySceneBeanId)){
                //删除延时任务
                ConcurrentHashMap<Integer, ScheduledFuture<?>> copySceneTaskMap=ScheduledThreadPoolUtil.getCopySceneTaskMap();
                if (copySceneTaskMap.containsKey(copySceneBean.getCopySceneBeanId())){
                    copySceneTaskMap.get(copySceneBean.getCopySceneBeanId()).cancel(false);
                    copySceneTaskMap.remove(copySceneBean.getCopySceneBeanId());
                }

                copySceneBeans.remove(copySceneBean.getCopySceneBeanId());
            }
        }
    }

    public static CopySceneBean getCopySceneBeanById(Integer copySceneBeanId) {
        return copySceneBeans.get(copySceneBeanId);
    }
}
