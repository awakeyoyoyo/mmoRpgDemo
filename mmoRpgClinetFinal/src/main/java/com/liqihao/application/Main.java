package com.liqihao.application;

import com.liqihao.commons.MmoCacheCilent;
import com.liqihao.netty.NettyTcpClient;
import com.liqihao.pojo.MmoRole;
import com.liqihao.pojo.baseMessage.*;
import com.liqihao.utils.ExcelReaderUtil;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class Main {
    private static String baseRoleMessage_file = "classpath:message/baseRoleMessage.xlsx";
    private static String npcMessage_file = "classpath:message/npcMessage.xlsx";
    private static String sceneMessage_file = "classpath:message/sceneMessage.xlsx";
    private static String skillMessage_file = "classpath:message/skillMessage.xlsx";
    private static String bufferMessage_file = "classpath:message/bufferMessage.xlsx";
    private static String medicineMessage_file = "classpath:message/medicineMessage.xlsx";
    private static String equipmentMessage_file = "classpath:message/equipmentMessage.xlsx";
    private static String baseDetailMessage_file = "classpath:message/baseDetailMessage.xlsx";
    public static void main(String[] args) throws Exception {
//查询类路径 加载配置文件
        ApplicationContext applicationContext = new ClassPathXmlApplicationContext("spring.xml");
        NettyTcpClient nettyTcpServer=(NettyTcpClient)applicationContext.getBean("nettyTcpClient");
        ConcurrentHashMap<Integer, SceneMessage> scmMap=new ConcurrentHashMap<>();
        ConcurrentHashMap<Integer, NPCMessage> npcMap=new ConcurrentHashMap<>();
        //读取配置文件
        List<SceneMessage> sceneMessages= ExcelReaderUtil.readExcelFromFileName(sceneMessage_file,SceneMessage.class);
        for (SceneMessage s:sceneMessages) {
            scmMap.put(s.getId(),s);
        }
        List<NPCMessage> npcMessageList=ExcelReaderUtil.readExcelFromFileName(npcMessage_file,NPCMessage.class);
        for (NPCMessage n:npcMessageList) {
            npcMap.put(n.getId(),n);
        }
        ConcurrentHashMap<Integer, SkillMessage> skillMessageConcurrentHashMap=new ConcurrentHashMap<>();
        List<SkillMessage> skillMessages=ExcelReaderUtil.readExcelFromFileName(skillMessage_file,SkillMessage.class);
        for (SkillMessage s:skillMessages) {
            skillMessageConcurrentHashMap.put(s.getId(),s);
        }
        MmoCacheCilent.init(null,null,new HashMap<Integer, MmoRole>(),scmMap,npcMap);

        ConcurrentHashMap<Integer, BufferMessage> bufferMessageConcurrentHashMap=new ConcurrentHashMap<>();
        List<BufferMessage> bufferMessage=ExcelReaderUtil.readExcelFromFileName(bufferMessage_file,BufferMessage.class);
        for (BufferMessage b:bufferMessage) {
            bufferMessageConcurrentHashMap.put(b.getId(),b);
        }
        ConcurrentHashMap<Integer, MedicineMessage> medicineMessageConcurrentHashMap=new ConcurrentHashMap<>();
        List<MedicineMessage> medicineMessages=ExcelReaderUtil.readExcelFromFileName(medicineMessage_file,MedicineMessage.class);
        for (MedicineMessage m:medicineMessages) {
            medicineMessageConcurrentHashMap.put(m.getId(),m);
        }
        ConcurrentHashMap<Integer, EquipmentMessage> equipmentMessageConcurrentHashMap=new ConcurrentHashMap<>();
        List<EquipmentMessage> equipmentMessages=ExcelReaderUtil.readExcelFromFileName(equipmentMessage_file,EquipmentMessage.class);
        for (EquipmentMessage e:equipmentMessages) {
            equipmentMessageConcurrentHashMap.put(e.getId(),e);
        }
        MmoCacheCilent.getInstance().setMedicineMessageConcurrentHashMap(medicineMessageConcurrentHashMap);
        MmoCacheCilent.getInstance().setEquipmentMessageConcurrentHashMap(equipmentMessageConcurrentHashMap);
        MmoCacheCilent.getInstance().setBufferMessageConcurrentHashMap(bufferMessageConcurrentHashMap);
        MmoCacheCilent.getInstance().setSkillMessageConcurrentHashMap(skillMessageConcurrentHashMap);
        BaseDetailMessage baseDetailMessage=ExcelReaderUtil.readExcelFromFileName(baseDetailMessage_file,BaseDetailMessage.class).get(0);
        MmoCacheCilent.getInstance().setBaseDetailMessage(baseDetailMessage);
        nettyTcpServer.run();

    }
}
