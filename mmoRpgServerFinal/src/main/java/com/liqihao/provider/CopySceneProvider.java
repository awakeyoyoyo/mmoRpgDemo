package com.liqihao.provider;

import com.liqihao.Cache.BossMessageCache;
import com.liqihao.Cache.CopySceneMessageCache;
import com.liqihao.commons.enums.ArticleTypeCode;
import com.liqihao.pojo.baseMessage.BossMessage;
import com.liqihao.pojo.baseMessage.CopySceneMessage;
import com.liqihao.pojo.bean.articleBean.Article;
import com.liqihao.pojo.bean.articleBean.EquipmentBean;
import com.liqihao.pojo.bean.roleBean.BossBean;
import com.liqihao.pojo.bean.CopySceneBean;
import com.liqihao.pojo.bean.teamBean.TeamBean;
import com.liqihao.util.CommonsUtil;
import com.liqihao.util.DbUtil;
import com.liqihao.util.ScheduledThreadPoolUtil;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
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
        copySceneBean.setTeamId(teamBean.getTeamId());
        List<Integer> bossIds=CommonsUtil.split(copySceneMessage.getBossIds());
        LinkedList<BossBean> bossBeans=new LinkedList<>();
        for (Integer id:bossIds) {
            BossMessage bossMessage=BossMessageCache.getInstance().get(id);
            BossBean bossBean=CommonsUtil.bossMessageToBossBean(bossMessage);
            Integer bossBeanId=bossBeanIdAuto.incrementAndGet();
            bossBean.setBossBeanId(bossBeanId);
            bossBean.setCopySceneBeanId(copySceneBean.getCopySceneBeanId());
            bossBeans.push(bossBean);
        }
        copySceneBean.setBossBeans(bossBeans);
        //开启定时任务
        ScheduledFuture<?> t=ScheduledThreadPoolUtil.getScheduledExecutorService().schedule(
                new ScheduledThreadPoolUtil.CopySceneOutTimeTask(teamBean,copySceneBean)
                , copySceneMessage.getLastTime(), TimeUnit.SECONDS);
        ScheduledThreadPoolUtil.getCopySceneTaskMap().put(copySceneBean.getCopySceneBeanId(),t);
        copySceneBeans.put(copySceneBean.getCopySceneBeanId(),copySceneBean);
        return copySceneBean;
    }

    /**
     * 副本销毁
     * @param copySceneBeanId
     */
    public static void deleteNewCopySceneById(Integer copySceneBeanId){
        Iterator<CopySceneBean> it = copySceneBeans.values().iterator();
        while(it.hasNext()){
            CopySceneBean copySceneBean = it.next();
            if(copySceneBean.getCopySceneBeanId().equals(copySceneBeanId)){
                //删除装备
                for (Article value :copySceneBean.getArticlesMap().values()) {
                    if (value.getArticleTypeCode().equals(ArticleTypeCode.EQUIPMENT.getCode())){
                        EquipmentBean equipmentBean= (EquipmentBean) value;
                        ScheduledThreadPoolUtil.addTask(() -> DbUtil.deleteEquipmentById(equipmentBean.getEquipmentId()));
                    }
                }
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
