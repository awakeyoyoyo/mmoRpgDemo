package com.liqihao.commons;

import com.liqihao.pojo.MmoRole;
import com.liqihao.pojo.baseMessage.*;
import com.liqihao.protobufObject.PlayModel;

import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

public class MmoCacheCilent {
    private static  MmoCacheCilent instance;
    private  Integer nowSceneId;
    private MmoRole nowRole;
    private HashMap<Integer,MmoRole> roleHashMap;
    private ConcurrentHashMap<Integer, SceneMessage> sceneMessageConcurrentHashMap;
    private ConcurrentHashMap<Integer, NPCMessage> npcMessageConcurrentHashMap;
    private ConcurrentHashMap<Integer, SkillMessage> skillMessageConcurrentHashMap;
    //buffer的基本信息
    private ConcurrentHashMap<Integer, BufferMessage> bufferMessageConcurrentHashMap;
    //药品基本信息
    private ConcurrentHashMap<Integer, MedicineMessage> medicineMessageConcurrentHashMap;
    //装备基本信息
    private ConcurrentHashMap<Integer, EquipmentMessage> equipmentMessageConcurrentHashMap;
    //副本基本信息
    private ConcurrentHashMap<Integer, CopySceneMessage> copySceneMessageConcurrentHashMap;
    //boss基本信息
    private ConcurrentHashMap<Integer, BossMessage> bossMessageConcurrentHashMap;
    //基础配置信息
    private BaseDetailMessage baseDetailMessage;

    public ConcurrentHashMap<Integer, CopySceneMessage> getCopySceneMessageConcurrentHashMap() {
        return copySceneMessageConcurrentHashMap;
    }

    public void setCopySceneMessageConcurrentHashMap(ConcurrentHashMap<Integer, CopySceneMessage> copySceneMessageConcurrentHashMap) {
        this.copySceneMessageConcurrentHashMap = copySceneMessageConcurrentHashMap;
    }

    public ConcurrentHashMap<Integer, BossMessage> getBossMessageConcurrentHashMap() {
        return bossMessageConcurrentHashMap;
    }

    public void setBossMessageConcurrentHashMap(ConcurrentHashMap<Integer, BossMessage> bossMessageConcurrentHashMap) {
        this.bossMessageConcurrentHashMap = bossMessageConcurrentHashMap;
    }

    public BaseDetailMessage getBaseDetailMessage() {
        return baseDetailMessage;
    }

    public void setBaseDetailMessage(BaseDetailMessage baseDetailMessage) {
        this.baseDetailMessage = baseDetailMessage;
    }

    public ConcurrentHashMap<Integer, MedicineMessage> getMedicineMessageConcurrentHashMap() {
        return medicineMessageConcurrentHashMap;
    }

    public void setMedicineMessageConcurrentHashMap(ConcurrentHashMap<Integer, MedicineMessage> medicineMessageConcurrentHashMap) {
        this.medicineMessageConcurrentHashMap = medicineMessageConcurrentHashMap;
    }

    public ConcurrentHashMap<Integer, EquipmentMessage> getEquipmentMessageConcurrentHashMap() {
        return equipmentMessageConcurrentHashMap;
    }

    public void setEquipmentMessageConcurrentHashMap(ConcurrentHashMap<Integer, EquipmentMessage> equipmentMessageConcurrentHashMap) {
        this.equipmentMessageConcurrentHashMap = equipmentMessageConcurrentHashMap;
    }

    public ConcurrentHashMap<Integer, BufferMessage> getBufferMessageConcurrentHashMap() {
        return bufferMessageConcurrentHashMap;
    }

    public void setBufferMessageConcurrentHashMap(ConcurrentHashMap<Integer, BufferMessage> bufferMessageConcurrentHashMap) {
        this.bufferMessageConcurrentHashMap = bufferMessageConcurrentHashMap;
    }

    public ConcurrentHashMap<Integer, SkillMessage> getSkillMessageConcurrentHashMap() {
        return skillMessageConcurrentHashMap;
    }

    public void setSkillMessageConcurrentHashMap(ConcurrentHashMap<Integer, SkillMessage> skillMessageConcurrentHashMap) {
        this.skillMessageConcurrentHashMap = skillMessageConcurrentHashMap;
    }

    public HashMap<Integer, MmoRole> getRoleHashMap() {
        return roleHashMap;
    }

    public void setRoleHashMap(HashMap<Integer, MmoRole> roleHashMap) {
        this.roleHashMap = roleHashMap;
    }

    public MmoCacheCilent(Integer nowSceneId, MmoRole nowRole, HashMap<Integer, MmoRole> roleHashMap, ConcurrentHashMap<Integer, SceneMessage> sceneMessageConcurrentHashMap, ConcurrentHashMap<Integer, NPCMessage> npcMessageConcurrentHashMap) {
        this.nowSceneId = nowSceneId;
        this.nowRole = nowRole;
        this.roleHashMap = roleHashMap;
        this.sceneMessageConcurrentHashMap = sceneMessageConcurrentHashMap;
        this.npcMessageConcurrentHashMap = npcMessageConcurrentHashMap;
    }

    public Integer getNowSceneId() {
        return nowSceneId;
    }

    public void setNowSceneId(Integer nowSceneId) {
        this.nowSceneId = nowSceneId;
    }

    public MmoRole getNowRole() {
        return nowRole;
    }

    public void setNowRole(MmoRole nowRole) {
        this.nowRole = nowRole;
    }

    public ConcurrentHashMap<Integer, SceneMessage> getSceneMessageConcurrentHashMap() {
        return sceneMessageConcurrentHashMap;
    }

    public void setSceneMessageConcurrentHashMap(ConcurrentHashMap<Integer, SceneMessage> sceneMessageConcurrentHashMap) {
        this.sceneMessageConcurrentHashMap = sceneMessageConcurrentHashMap;
    }

    public ConcurrentHashMap<Integer, NPCMessage> getNpcMessageConcurrentHashMap() {
        return npcMessageConcurrentHashMap;
    }

    public void setNpcMessageConcurrentHashMap(ConcurrentHashMap<Integer, NPCMessage> npcMessageConcurrentHashMap) {
        this.npcMessageConcurrentHashMap = npcMessageConcurrentHashMap;
    }

    public static MmoCacheCilent getInstance(){
        return instance;
    }
    public static void init(Integer nowSceneId, MmoRole nowRole,HashMap<Integer, MmoRole> roleHashMap, ConcurrentHashMap<Integer, SceneMessage> sceneMessageConcurrentHashMap, ConcurrentHashMap<Integer, NPCMessage> npcMessageConcurrentHashMap){
        instance=new MmoCacheCilent(nowSceneId,nowRole,roleHashMap,sceneMessageConcurrentHashMap,npcMessageConcurrentHashMap);
    }

}
