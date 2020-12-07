package com.liqihao.commons;

import com.liqihao.pojo.MmoScene;
import com.liqihao.pojo.baseMessage.NPCMessage;
import com.liqihao.pojo.baseMessage.SceneMessage;
import com.liqihao.protobufObject.PlayModel;

import java.util.concurrent.ConcurrentHashMap;

public class MmoCacheCilent {
    private static  MmoCacheCilent instance;
    private  Integer nowSceneId;
    private  PlayModel.MmoSimpleRole nowRoles;
    private ConcurrentHashMap<Integer, SceneMessage> sceneMessageConcurrentHashMap;
    private ConcurrentHashMap<Integer, NPCMessage> npcMessageConcurrentHashMap;

    public MmoCacheCilent(Integer nowSceneId, PlayModel.MmoSimpleRole nowRoles, ConcurrentHashMap<Integer, SceneMessage> sceneMessageConcurrentHashMap, ConcurrentHashMap<Integer, NPCMessage> npcMessageConcurrentHashMap) {
        this.nowSceneId = nowSceneId;
        this.nowRoles = nowRoles;
        this.sceneMessageConcurrentHashMap = sceneMessageConcurrentHashMap;
        this.npcMessageConcurrentHashMap = npcMessageConcurrentHashMap;
    }
    public static MmoCacheCilent getInstance(){
        return instance;
    }
    public static void init(Integer nowSceneId, PlayModel.MmoSimpleRole nowRoles, ConcurrentHashMap<Integer, SceneMessage> sceneMessageConcurrentHashMap, ConcurrentHashMap<Integer, NPCMessage> npcMessageConcurrentHashMap){
        instance=new MmoCacheCilent(nowSceneId,nowRoles,sceneMessageConcurrentHashMap,npcMessageConcurrentHashMap);
    }
    public Integer getNowSceneId() {
        return nowSceneId;
    }

    public void setNowSceneId(Integer nowSceneId) {
        this.nowSceneId = nowSceneId;
    }

    public PlayModel.MmoSimpleRole getNowRoles() {
        return nowRoles;
    }

    public void setNowRoles(PlayModel.MmoSimpleRole nowRoles) {
        this.nowRoles = nowRoles;
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
}
