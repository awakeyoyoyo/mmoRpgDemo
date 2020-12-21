package com.liqihao.application;

import com.liqihao.Cache.*;
import com.liqihao.pojo.baseMessage.*;
import com.liqihao.pojo.bean.MmoSimpleNPC;
import com.liqihao.pojo.bean.SceneBean;
import com.liqihao.util.CommonsUtil;
import com.liqihao.util.ExcelReaderUtil;
import com.liqihao.util.ScheduledThreadPoolUtil;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
/**
 * 初始化服务器类
 */
public class ServerInit{
    private static String baseRoleMessage_file = "classpath:message/baseRoleMessage.xlsx";
    private static String npcMessage_file = "classpath:message/npcMessage.xlsx";
    private static String sceneMessage_file = "classpath:message/sceneMessage.xlsx";
    private static String skillMessage_file = "classpath:message/skillMessage.xlsx";
    private static String bufferMessage_file = "classpath:message/bufferMessage.xlsx";
    private static String medicineMessage_file = "classpath:message/medicineMessage.xlsx";
    private static String equipmentMessage_file = "classpath:message/equipmentMessage.xlsx";
    private static String baseDetailMessage_file = "classpath:message/baseDetailMessage.xlsx";


    public void init() throws IOException, IllegalAccessException, InstantiationException {
//      //初始化线程池
        //初始化缓存
        //基本信息
        BaseRoleMessage baseRoleMessage= ExcelReaderUtil.readExcelFromFileName(baseRoleMessage_file,BaseRoleMessage.class).get(0);
        BaseDetailMessage baseDetailMessage=ExcelReaderUtil.readExcelFromFileName(baseDetailMessage_file,BaseDetailMessage.class).get(0);
        MmoBaseMessageCache.init(baseRoleMessage,baseDetailMessage);
        //NPC
        ConcurrentHashMap<Integer, MmoSimpleNPC> npcMap=new ConcurrentHashMap<>();
        List<NPCMessage> npcMessageList=ExcelReaderUtil.readExcelFromFileName(npcMessage_file,NPCMessage.class);
        for (NPCMessage n:npcMessageList) {
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
        List<SceneMessage> sceneMessages=ExcelReaderUtil.readExcelFromFileName(sceneMessage_file,SceneMessage.class);
        for (SceneMessage m:sceneMessages){
            SceneBean sceneBean;
            sceneBean=CommonsUtil.sceneMessageToSceneBean(m);
            sceneBeanConcurrentHashMap.put(sceneBean.getId(),sceneBean);
        }
        SceneBeanMessageCache.init(sceneBeanConcurrentHashMap);
        //技能信息
        List<SkillMessage> skillMessages=ExcelReaderUtil.readExcelFromFileName(skillMessage_file,SkillMessage.class);
        for (SkillMessage s:skillMessages) {
            skillMessageConcurrentHashMap.put(s.getId(),s);
        }
        SkillMessageCache.init(skillMessageConcurrentHashMap);
        //buffer信息
        List<BufferMessage> bufferMessage=ExcelReaderUtil.readExcelFromFileName(bufferMessage_file,BufferMessage.class);
        for (BufferMessage b:bufferMessage) {
            bufferMessageConcurrentHashMap.put(b.getId(),b);
        }
        BufferMessageCache.init(bufferMessageConcurrentHashMap);
        //药品信息
        List<MedicineMessage> medicineMessages=ExcelReaderUtil.readExcelFromFileName(medicineMessage_file,MedicineMessage.class);
        for (MedicineMessage medicineMessage:medicineMessages) {
            medicineMessageConcurrentHashMap.put(medicineMessage.getId(),medicineMessage);
        }
        MediceneMessageCache.init(medicineMessageConcurrentHashMap);
        //装备信息
        List<EquipmentMessage> equipmentMessages=ExcelReaderUtil.readExcelFromFileName(equipmentMessage_file,EquipmentMessage.class);
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
