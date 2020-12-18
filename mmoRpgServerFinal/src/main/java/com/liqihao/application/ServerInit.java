package com.liqihao.application;

import com.liqihao.Cache.*;
import com.liqihao.pojo.baseMessage.*;
import com.liqihao.pojo.bean.MmoSimpleNPC;
import com.liqihao.pojo.bean.SceneBean;
import com.liqihao.util.CommonsUtil;
import com.liqihao.util.ScheduledThreadPoolUtil;
import com.liqihao.util.YmlUtils;
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
        //初始化缓存
        //基本信息
        BaseRoleMessage baseRoleMessage=YmlUtils.getBaseRoleMessage();
        BaseDetailMessage baseDetailMessage=YmlUtils.getBaseDetailMessage();
        MmoBaseMessageCache.init(baseRoleMessage,baseDetailMessage);
        //NPC
        ConcurrentHashMap<Integer, MmoSimpleNPC> npcMap=new ConcurrentHashMap<>();
        for (NPCMessage n:YmlUtils.getNpcMessage()) {
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
            npc.setAttack(n.getAttack());
            npc.setBufferBeans(new CopyOnWriteArrayList<>());
            npcMap.put(n.getId(),npc);
        }
        NpcMessageCache.init(npcMap);
        ConcurrentHashMap<Integer, SceneBean> sceneBeanConcurrentHashMap=new ConcurrentHashMap<>();
        ConcurrentHashMap<Integer, SkillMessage> skillMessageConcurrentHashMap=new ConcurrentHashMap<>();
        ConcurrentHashMap<Integer, BufferMessage> bufferMessageConcurrentHashMap=new ConcurrentHashMap<>();
        ConcurrentHashMap<Integer, MedicineMessage> medicineMessageConcurrentHashMap=new ConcurrentHashMap<>();
        ConcurrentHashMap<Integer, EquipmentMessage> equipmentMessageConcurrentHashMap=new ConcurrentHashMap<>();
        //场景今昔
        List<SceneMessage> sceneMessages=YmlUtils.getSceneMessage();
        for (SceneMessage m:sceneMessages){
            SceneBean sceneBean;
            sceneBean=CommonsUtil.sceneMessageToSceneBean(m);
            sceneBeanConcurrentHashMap.put(sceneBean.getId(),sceneBean);
        }
        SceneBeanMessageCache.init(sceneBeanConcurrentHashMap);
        //技能信息
        List<SkillMessage> skillMessages=YmlUtils.getSkillMessage();
        for (SkillMessage s:skillMessages) {
            skillMessageConcurrentHashMap.put(s.getId(),s);
        }
        SkillMessageCache.init(skillMessageConcurrentHashMap);
        //buffer信息
        List<BufferMessage> bufferMessage=YmlUtils.getBufferMessage();
        for (BufferMessage b:bufferMessage) {
            bufferMessageConcurrentHashMap.put(b.getId(),b);
        }
        BufferMessageCache.init(bufferMessageConcurrentHashMap);
        //药品信息
        List<MedicineMessage> medicineMessages=YmlUtils.getMedicineMessages();
        for (MedicineMessage medicineMessage:medicineMessages) {
            medicineMessageConcurrentHashMap.put(medicineMessage.getId(),medicineMessage);
        }
        MediceneMessageCache.init(medicineMessageConcurrentHashMap);
        //装备信息
        List<EquipmentMessage> equipmentMessages=YmlUtils.getEquipmentMessages();
        for (EquipmentMessage equipmentMessage:equipmentMessages) {
            equipmentMessageConcurrentHashMap.put(equipmentMessage.getId(),equipmentMessage);
        }
        EquipmentMessageCache.init(equipmentMessageConcurrentHashMap);
        //在线用户
        OnlineRoleMessageCache.init(new ConcurrentHashMap<>());
        //channel
        ChannelMessageCache.init(new ConcurrentHashMap<>());
        ScheduledThreadPoolUtil.init();
    }


    public static void main(String[] args) {
        String str=null;
        System.out.println(CommonsUtil.split(str));

    }

}
