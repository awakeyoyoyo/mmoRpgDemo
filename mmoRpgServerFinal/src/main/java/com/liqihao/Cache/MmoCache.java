package com.liqihao.Cache;

import com.liqihao.pojo.MmoRolePOJO;
import com.liqihao.pojo.MmoScene;
import com.liqihao.pojo.baseMessage.BaseMessage;
import com.liqihao.pojo.baseMessage.NPCMessage;
import com.liqihao.pojo.baseMessage.SceneMessage;
import io.netty.channel.Channel;

import java.util.concurrent.ConcurrentHashMap;

public class MmoCache {
    private volatile static MmoCache instance ;
    private ConcurrentHashMap<Integer, MmoRolePOJO> mmoSimpleRoleConcurrentHashMap;
    private ConcurrentHashMap<Integer, Channel> channelConcurrentHashMap;
    private ConcurrentHashMap<Integer, SceneMessage> sceneMessageConcurrentHashMap;
    private ConcurrentHashMap<Integer, NPCMessage> npcMessageConcurrentHashMap;
    public ConcurrentHashMap<Integer, Channel> getChannelConcurrentHashMap() {
        return channelConcurrentHashMap;
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

    public void setChannelConcurrentHashMap(ConcurrentHashMap<Integer, Channel> channelConcurrentHashMap) {
        this.channelConcurrentHashMap = channelConcurrentHashMap;
    }

    public static void init(
                            ConcurrentHashMap<Integer, SceneMessage> smc, ConcurrentHashMap<Integer, NPCMessage> npc
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
                    ConcurrentHashMap<Integer, NPCMessage> npcMessageConcurrentHashMap
    )
    {
        this.mmoSimpleRoleConcurrentHashMap = new ConcurrentHashMap<>();;
        this.sceneMessageConcurrentHashMap=sceneMessageConcurrentHashMap;
        this.npcMessageConcurrentHashMap=npcMessageConcurrentHashMap;
        this.channelConcurrentHashMap=new ConcurrentHashMap<>();
    }


    public ConcurrentHashMap<Integer, MmoRolePOJO> getMmoSimpleRoleConcurrentHashMap() {
        return mmoSimpleRoleConcurrentHashMap;
    }

    public void setMmoSimpleRoleConcurrentHashMap(ConcurrentHashMap<Integer, MmoRolePOJO> mmoSimpleRoleConcurrentHashMap) {
        this.mmoSimpleRoleConcurrentHashMap = mmoSimpleRoleConcurrentHashMap;
    }
}
