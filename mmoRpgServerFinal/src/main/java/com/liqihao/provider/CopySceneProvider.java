package com.liqihao.provider;

import com.liqihao.Cache.BossMessageCache;
import com.liqihao.Cache.CopySceneMessageCache;
import com.liqihao.pojo.baseMessage.BossMessage;
import com.liqihao.pojo.baseMessage.CopySceneMessage;
import com.liqihao.pojo.bean.BossBean;
import com.liqihao.pojo.bean.CopySceneBean;
import com.liqihao.util.CommonsUtil;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 游戏副本提供者
 * @author lqhao
 */
public class CopySceneProvider {
    private static ConcurrentHashMap<Integer,CopySceneBean> copySceneBeans;
    private static AtomicInteger copySceneBeanIdAuto=new AtomicInteger(0);
    private static AtomicInteger bossBeanIdAuto=new AtomicInteger(0);

    public static CopySceneBean createNewCopyScene(Integer copySceneId){
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
        copySceneBeans.put(copySceneBean.getCopySceneBeanId(),copySceneBean);
        return copySceneBean;
    }

    public static void deleteNewCopySceneById(Integer copySceneBeanId){
        Iterator<CopySceneBean> it = copySceneBeans.values().iterator();
        while(it.hasNext()){
            CopySceneBean copySceneBean = it.next();
            if(copySceneBean.getCopySceneBeanId().equals(copySceneBeanId)){
                copySceneBeans.remove(copySceneBean.getCopySceneBeanId());
            }
        }
    }

    public static CopySceneBean getCopySceneBeanById(Integer copySceneBeanId) {
        return copySceneBeans.get(copySceneBeanId);
    }
}
