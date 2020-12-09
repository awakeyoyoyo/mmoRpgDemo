package com.liqihao.commons;

import com.liqihao.pojo.MmoRole;
import com.liqihao.pojo.baseMessage.NPCMessage;
import com.liqihao.pojo.baseMessage.SceneMessage;
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
