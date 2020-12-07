package com.liqihao.application;

import com.liqihao.Cache.MmoCache;
import com.liqihao.pojo.baseMessage.BaseMessage;
import com.liqihao.pojo.baseMessage.NPCMessage;
import com.liqihao.pojo.baseMessage.SceneMessage;
import com.liqihao.util.CommonsUtil;
import com.liqihao.util.YmlUtils;
import org.springframework.stereotype.Component;
import java.io.FileNotFoundException;
import java.util.concurrent.ConcurrentHashMap;

@Component
/**
 * 初始化服务器类
 */
public class ServerInit{
    public void init() throws FileNotFoundException {
//        //初始化线程池
//        ThreadPools.init();
        //分解出可用scene
        //初始化缓存
        ConcurrentHashMap<Integer, SceneMessage> scmMap=new ConcurrentHashMap<>();
        ConcurrentHashMap<Integer, NPCMessage> npcMap=new ConcurrentHashMap<>();
        //读取配置文件
        BaseMessage baseMessage=YmlUtils.getBaseMessage();
        for (SceneMessage s:baseMessage.getSceneMessages()) {
            scmMap.put(s.getId(),s);
        }
        for (NPCMessage n:baseMessage.getNpcMessages()) {
            npcMap.put(n.getId(),n);
        }
        //初始化NPC 和场景
        MmoCache.init(scmMap,npcMap);
    }


    public static void main(String[] args) {
        String str=null;
        System.out.println(CommonsUtil.split(str));

    }

}
