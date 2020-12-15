package com.liqihao.Cache;

import com.liqihao.pojo.baseMessage.*;
import com.liqihao.pojo.bean.MmoSimpleNPC;
import com.liqihao.pojo.bean.MmoSimpleRole;
import com.liqihao.pojo.bean.SceneBean;
import io.netty.channel.Channel;
import java.util.concurrent.ConcurrentHashMap;


/**
 * 缓存
 */
public class MmoCache {
    private volatile static MmoCache instance ;
    //在线用户
    private ConcurrentHashMap<Integer, MmoSimpleRole> mmoSimpleRoleConcurrentHashMap;
    //id--channle
    private ConcurrentHashMap<Integer, Channel> channelConcurrentHashMap;
    //实例场景
    private ConcurrentHashMap<Integer, SceneBean> sceneBeanConcurrentHashMap;
    //场景基本信息
    private ConcurrentHashMap<Integer, SceneMessage> sceneMessageConcurrentHashMap;
    //npc
    private ConcurrentHashMap<Integer, MmoSimpleNPC> npcMessageConcurrentHashMap;
    //用户角色的基本信息
    private BaseRoleMessage baseRoleMessage;
    //技能的基本信息
    private ConcurrentHashMap<Integer,SkillMessage> skillMessageConcurrentHashMap;
    //buffer的基本信息
    private ConcurrentHashMap<Integer, BufferMessage> bufferMessageConcurrentHashMap;
    //药品基本信息
    private ConcurrentHashMap<Integer, MedicineMessage> medicineMessageConcurrentHashMap;
    //装备基本信息
    private ConcurrentHashMap<Integer, EquipmentMessage> equipmentMessageConcurrentHashMap;

    public ConcurrentHashMap<Integer, SceneBean> getSceneBeanConcurrentHashMap() {
        return sceneBeanConcurrentHashMap;
    }

    public void setSceneBeanConcurrentHashMap(ConcurrentHashMap<Integer, SceneBean> sceneBeanConcurrentHashMap) {
        this.sceneBeanConcurrentHashMap = sceneBeanConcurrentHashMap;
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

    public BaseRoleMessage getBaseRoleMessage() {
        return baseRoleMessage;
    }

    public void setBaseRoleMessage(BaseRoleMessage baseRoleMessage) {
        this.baseRoleMessage = baseRoleMessage;
    }

    public ConcurrentHashMap<Integer, Channel> getChannelConcurrentHashMap() {
        return channelConcurrentHashMap;
    }

    public ConcurrentHashMap<Integer, SceneMessage> getSceneMessageConcurrentHashMap() {
        return sceneMessageConcurrentHashMap;
    }

    public void setSceneMessageConcurrentHashMap(ConcurrentHashMap<Integer, SceneMessage> sceneMessageConcurrentHashMap) {
        this.sceneMessageConcurrentHashMap = sceneMessageConcurrentHashMap;
    }

    public ConcurrentHashMap<Integer, MmoSimpleNPC> getNpcMessageConcurrentHashMap() {
        return npcMessageConcurrentHashMap;
    }

    public void setNpcMessageConcurrentHashMap(ConcurrentHashMap<Integer, MmoSimpleNPC> npcMessageConcurrentHashMap) {
        this.npcMessageConcurrentHashMap = npcMessageConcurrentHashMap;
    }

    public void setChannelConcurrentHashMap(ConcurrentHashMap<Integer, Channel> channelConcurrentHashMap) {
        this.channelConcurrentHashMap = channelConcurrentHashMap;
    }

    public static void init(
                            ConcurrentHashMap<Integer, SceneMessage> smc, ConcurrentHashMap<Integer, MmoSimpleNPC> npc
    ){
        instance=new MmoCache(smc,npc);
    }
    public static MmoCache getInstance(){
        return instance;
    }
    public MmoCache() {
    }

    public MmoCache(
                    ConcurrentHashMap<Integer, SceneMessage> sceneMessageConcurrentHashMap,
                    ConcurrentHashMap<Integer, MmoSimpleNPC> npcMessageConcurrentHashMap
    )
    {
        this.mmoSimpleRoleConcurrentHashMap = new ConcurrentHashMap<>();
        this.sceneMessageConcurrentHashMap=sceneMessageConcurrentHashMap;
        this.npcMessageConcurrentHashMap=npcMessageConcurrentHashMap;
        this.channelConcurrentHashMap=new ConcurrentHashMap<>();
    }


    public ConcurrentHashMap<Integer, MmoSimpleRole> getMmoSimpleRoleConcurrentHashMap() {
        return mmoSimpleRoleConcurrentHashMap;
    }

    public void setMmoSimpleRoleConcurrentHashMap(ConcurrentHashMap<Integer, MmoSimpleRole> mmoSimpleRoleConcurrentHashMap) {
        this.mmoSimpleRoleConcurrentHashMap = mmoSimpleRoleConcurrentHashMap;
    }
}
