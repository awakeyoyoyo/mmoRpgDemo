package com.liqihao.application;

import com.liqihao.Cache.MmoCache;
import com.liqihao.commons.enums.AttackStyleCode;
import com.liqihao.commons.enums.DamageTypeCode;
import com.liqihao.pojo.baseMessage.BaseMessage;
import com.liqihao.pojo.baseMessage.NPCMessage;
import com.liqihao.pojo.baseMessage.SceneMessage;
import com.liqihao.pojo.baseMessage.SkillMessage;
import com.liqihao.pojo.bean.MmoSimpleNPC;
import com.liqihao.pojo.bean.MmoSimpleRole;
import com.liqihao.pojo.bean.SkillBean;
import com.liqihao.protobufObject.PlayModel;
import com.liqihao.util.CommonsUtil;
import com.liqihao.util.ThreadPools;
import com.liqihao.util.YmlUtils;
import com.sun.javafx.image.IntPixelGetter;
import org.springframework.stereotype.Component;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
/**
 * 初始化服务器类
 */
public class ServerInit{
    public void init() throws FileNotFoundException {
//        //初始化线程池

        //新增一个自动回蓝的任务

        //分解出可用scene
        //初始化缓存
        ConcurrentHashMap<Integer, SceneMessage> scmMap=new ConcurrentHashMap<>();
        ConcurrentHashMap<Integer, MmoSimpleNPC> npcMap=new ConcurrentHashMap<>();
        //读取配置文件
        BaseMessage baseMessage=YmlUtils.getBaseMessage();
        for (SceneMessage s:baseMessage.getSceneMessages()) {
            scmMap.put(s.getId(),s);
        }
        for (NPCMessage n:baseMessage.getNpcMessages()) {
            MmoSimpleNPC npc=new MmoSimpleNPC();
            npc.setId(n.getId());
            npc.setType(n.getType());
            npc.setTalk(n.getTalk());
            npc.setOnstatus(n.getOnstatus());
            npc.setName(n.getName());
            npc.setMmosceneid(n.getMmosceneid());
            npc.setStatus(n.getStatus());
            npc.setBlood(n.getBlood());
            npc.setNowBlood(n.getBlood());
            npc.setMp(n.getMp());
            npc.setNowMp(n.getMp());
            npcMap.put(n.getId(),npc);
        }
        //初始化NPC 和场景
        MmoCache.init(scmMap,npcMap);
        ThreadPools.init();
        MmoCache.getInstance().setBaseRoleMessage(baseMessage.getBaseRoleMessage());
        ConcurrentHashMap<Integer, SkillMessage> skillMessageConcurrentHashMap=new ConcurrentHashMap<>();
        List<SkillMessage> skillMessages=baseMessage.getSkillMessages();
        for (SkillMessage s:skillMessages) {
            skillMessageConcurrentHashMap.put(s.getId(),s);
        }

        MmoCache.getInstance().setSkillMessageConcurrentHashMap(skillMessageConcurrentHashMap);
    }


    public static void main(String[] args) {
        String str=null;
        System.out.println(CommonsUtil.split(str));

    }

}
