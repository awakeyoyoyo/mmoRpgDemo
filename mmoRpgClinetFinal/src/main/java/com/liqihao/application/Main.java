package com.liqihao.application;

import com.liqihao.commons.MmoCacheCilent;
import com.liqihao.netty.NettyTcpClient;
import com.liqihao.pojo.baseMessage.BaseMessage;
import com.liqihao.pojo.baseMessage.NPCMessage;
import com.liqihao.pojo.baseMessage.SceneMessage;
import com.liqihao.utils.YmlUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

public class Main {
    public static void main(String[] args) throws Exception {
//查询类路径 加载配置文件
        ApplicationContext applicationContext = new ClassPathXmlApplicationContext("spring.xml");
        NettyTcpClient nettyTcpServer=(NettyTcpClient)applicationContext.getBean("nettyTcpClient");
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
        MmoCacheCilent.init(null,null,new HashMap<>(),scmMap,npcMap);
        nettyTcpServer.run();
    }
}
