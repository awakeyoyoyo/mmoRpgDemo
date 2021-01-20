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

/**
 * 客户端初始化类
 * @author lqhao
 */
public class Main {
    private static String baseRoleMessage_file = "classpath:message/baseRoleMessage.xlsx";
    private static String npcMessage_file = "classpath:message/npcMessage.xlsx";
    private static String sceneMessage_file = "classpath:message/sceneMessage.xlsx";
    private static String skillMessage_file = "classpath:message/skillMessage.xlsx";
    private static String bufferMessage_file = "classpath:message/bufferMessage.xlsx";
    private static String medicineMessage_file = "classpath:message/medicineMessage.xlsx";
    private static String equipmentMessage_file = "classpath:message/equipmentMessage.xlsx";
    private static String copySceneMessage_file = "classpath:message/copySceneMessage.xlsx";
    private static String bossMessage_file = "classpath:message/bossMessage.xlsx";
    private static String baseDetailMessage_file = "classpath:message/baseDetailMessage.xlsx";
    private static String goodsMessage_file = "classpath:message/goodsMessage.xlsx";
    private static String professionMessage_file = "classpath:message/professionMessage.xlsx";
    private static String guildAuthorityMessage_file = "classpath:message/guildAuthorityMessage.xlsx";
    private static String guildBaseMessage_file = "classpath:message/guildBaseMessage.xlsx";
    private static String guildPositionMessage_file = "classpath:message/guildPositionMessage.xlsx";
    private static String taskMessage_file = "classpath:message/taskMessage.xlsx";
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
        ConcurrentHashMap<Integer, BossMessage> bossMessageConcurrentHashMap=new ConcurrentHashMap<>();
        List<BossMessage> bossMessages= ExcelReaderUtil.readExcelFromFileName(bossMessage_file,BossMessage.class);
        for (BossMessage bossMessage:bossMessages) {
            bossMessageConcurrentHashMap.put(bossMessage.getId(),bossMessage);
        }
        ConcurrentHashMap<Integer, CopySceneMessage> copySceneMessageConcurrentHashMap=new ConcurrentHashMap<>();
        List<CopySceneMessage> copySceneMessages= ExcelReaderUtil.readExcelFromFileName(copySceneMessage_file,CopySceneMessage.class);
        for (CopySceneMessage copySceneMessage:copySceneMessages) {
            copySceneMessageConcurrentHashMap.put(copySceneMessage.getId(),copySceneMessage);
        }
        ConcurrentHashMap<Integer, GoodsMessage> goodsMessageConcurrentHashMap=new ConcurrentHashMap<>();
        List<GoodsMessage> goodsMessages= ExcelReaderUtil.readExcelFromFileName(goodsMessage_file,GoodsMessage.class);
        for (GoodsMessage goodsMessage:goodsMessages) {
            goodsMessageConcurrentHashMap.put(goodsMessage.getId(),goodsMessage);
        }
        ConcurrentHashMap<Integer, ProfessionMessage> professionMessageConcurrentHashMap=new ConcurrentHashMap<>();
        List<ProfessionMessage> professionMessages= ExcelReaderUtil.readExcelFromFileName(professionMessage_file,ProfessionMessage.class);
        for (ProfessionMessage professionMessage:professionMessages) {
            professionMessageConcurrentHashMap.put(professionMessage.getId(),professionMessage);
        }

        ConcurrentHashMap<Integer, TaskMessage> taskMessageConcurrentHashMap=new ConcurrentHashMap<>();
        List<TaskMessage> taskMessages= ExcelReaderUtil.readExcelFromFileName(taskMessage_file,TaskMessage.class);
        for (TaskMessage taskMessage:taskMessages) {
            taskMessageConcurrentHashMap.put(taskMessage.getId(),taskMessage);
        }
        MmoCacheCilent.getInstance().setTaskMessageConcurrentHashMap(taskMessageConcurrentHashMap);
        MmoCacheCilent.getInstance().setProfessionMessageConcurrentHashMap(professionMessageConcurrentHashMap);
        MmoCacheCilent.getInstance().setGoodsMessageConcurrentHashMap(goodsMessageConcurrentHashMap);
        MmoCacheCilent.getInstance().setCopySceneMessageConcurrentHashMap(copySceneMessageConcurrentHashMap);
        MmoCacheCilent.getInstance().setBossMessageConcurrentHashMap(bossMessageConcurrentHashMap);
        MmoCacheCilent.getInstance().setMedicineMessageConcurrentHashMap(medicineMessageConcurrentHashMap);
        MmoCacheCilent.getInstance().setEquipmentMessageConcurrentHashMap(equipmentMessageConcurrentHashMap);
        MmoCacheCilent.getInstance().setBufferMessageConcurrentHashMap(bufferMessageConcurrentHashMap);
        MmoCacheCilent.getInstance().setSkillMessageConcurrentHashMap(skillMessageConcurrentHashMap);
        BaseDetailMessage baseDetailMessage=ExcelReaderUtil.readExcelFromFileName(baseDetailMessage_file,BaseDetailMessage.class).get(0);
        MmoCacheCilent.getInstance().setBaseDetailMessage(baseDetailMessage);

        ConcurrentHashMap<Integer, GuildPositionMessage> guildPositionMessageConcurrentHashMap=new ConcurrentHashMap<>();
        List<GuildPositionMessage> guildPositionMessages=ExcelReaderUtil.readExcelFromFileName(guildPositionMessage_file,GuildPositionMessage.class);
        for (GuildPositionMessage guildPositionMessage:guildPositionMessages) {
            guildPositionMessageConcurrentHashMap.put(guildPositionMessage.getId(),guildPositionMessage);
        }
        MmoCacheCilent.getInstance().setGuildPositionMessageConcurrentHashMap(guildPositionMessageConcurrentHashMap);

        ConcurrentHashMap<Integer, GuildAuthorityMessage> guildAuthorityMessageConcurrentHashMap=new ConcurrentHashMap<>();
        List<GuildAuthorityMessage> guildAuthorityMessages=ExcelReaderUtil.readExcelFromFileName(guildPositionMessage_file,GuildAuthorityMessage.class);
        for (GuildAuthorityMessage guildAuthorityMessage:guildAuthorityMessages) {
            guildAuthorityMessageConcurrentHashMap.put(guildAuthorityMessage.getId(),guildAuthorityMessage);
        }
        MmoCacheCilent.getInstance().setGuildAuthorityMessageConcurrentHashMap(guildAuthorityMessageConcurrentHashMap);

        GuildBaseMessage guildBaseMessage=ExcelReaderUtil.readExcelFromFileName(guildBaseMessage_file,GuildBaseMessage.class).get(0);
        MmoCacheCilent.getInstance().setGuildBaseMessage(guildBaseMessage);

        nettyTcpServer.run();
    }
}
