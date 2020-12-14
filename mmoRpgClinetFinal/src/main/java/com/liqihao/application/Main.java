package com.liqihao.application;

import com.liqihao.commons.MmoCacheCilent;
import com.liqihao.netty.NettyTcpClient;
import com.liqihao.pojo.MmoRole;
import com.liqihao.pojo.baseMessage.*;
import com.liqihao.utils.YmlUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class Main {
    public static void main(String[] args) throws Exception {
//查询类路径 加载配置文件
        ApplicationContext applicationContext = new ClassPathXmlApplicationContext("spring.xml");
        NettyTcpClient nettyTcpServer=(NettyTcpClient)applicationContext.getBean("nettyTcpClient");
        ConcurrentHashMap<Integer, SceneMessage> scmMap=new ConcurrentHashMap<>();
        ConcurrentHashMap<Integer, NPCMessage> npcMap=new ConcurrentHashMap<>();
        //读取配置文件

        for (SceneMessage s:YmlUtils.getSceneMessage()) {
            scmMap.put(s.getId(),s);
        }
        for (NPCMessage n:YmlUtils.getNpcMessage()) {
            npcMap.put(n.getId(),n);
        }
        ConcurrentHashMap<Integer, SkillMessage> skillMessageConcurrentHashMap=new ConcurrentHashMap<>();
        List<SkillMessage> skillMessages=YmlUtils.getSkillMessage();
        for (SkillMessage s:skillMessages) {
            skillMessageConcurrentHashMap.put(s.getId(),s);
        }
        MmoCacheCilent.init(null,null,new HashMap<Integer, MmoRole>(),scmMap,npcMap);

        ConcurrentHashMap<Integer, BufferMessage> bufferMessageConcurrentHashMap=new ConcurrentHashMap<>();
        List<BufferMessage> bufferMessage=YmlUtils.getBufferMessage();
        for (BufferMessage b:bufferMessage) {
            bufferMessageConcurrentHashMap.put(b.getId(),b);
        }
        MmoCacheCilent.getInstance().setBufferMessageConcurrentHashMap(bufferMessageConcurrentHashMap);

        MmoCacheCilent.getInstance().setSkillMessageConcurrentHashMap(skillMessageConcurrentHashMap);
        nettyTcpServer.run();

    }
}
