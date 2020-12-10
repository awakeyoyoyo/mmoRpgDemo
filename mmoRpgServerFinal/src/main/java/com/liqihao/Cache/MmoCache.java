package com.liqihao.Cache;

import com.liqihao.annotation.HandlerCmdTag;
import com.liqihao.pojo.MmoRolePOJO;
import com.liqihao.pojo.baseMessage.BaseRoleMessage;
import com.liqihao.pojo.baseMessage.NPCMessage;
import com.liqihao.pojo.baseMessage.SceneMessage;
import com.liqihao.pojo.baseMessage.SkillMessage;
import com.liqihao.pojo.bean.MmoSimpleNPC;
import com.liqihao.pojo.bean.MmoSimpleRole;
import io.netty.channel.Channel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 缓存
 */
public class MmoCache {
    private volatile static MmoCache instance ;
    private ConcurrentHashMap<Integer, MmoSimpleRole> mmoSimpleRoleConcurrentHashMap;
    private ConcurrentHashMap<Integer, Channel> channelConcurrentHashMap;
    private ConcurrentHashMap<Integer, SceneMessage> sceneMessageConcurrentHashMap;
    private ConcurrentHashMap<Integer, MmoSimpleNPC> npcMessageConcurrentHashMap;
    private BaseRoleMessage baseRoleMessage;
    private ConcurrentHashMap<Integer,SkillMessage> skillMessageConcurrentHashMap;
    private CopyOnWriteArrayList<Integer> NoMpRole;

    public CopyOnWriteArrayList<Integer> getNoMpRole() {
        return NoMpRole;
    }

    public void setNoMpRole(CopyOnWriteArrayList<Integer> noMpRole) {
        NoMpRole = noMpRole;
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
        this.mmoSimpleRoleConcurrentHashMap = new ConcurrentHashMap<>();;
        this.sceneMessageConcurrentHashMap=sceneMessageConcurrentHashMap;
        this.npcMessageConcurrentHashMap=npcMessageConcurrentHashMap;
        this.channelConcurrentHashMap=new ConcurrentHashMap<>();
        this.NoMpRole=new CopyOnWriteArrayList<>();
    }


    public ConcurrentHashMap<Integer, MmoSimpleRole> getMmoSimpleRoleConcurrentHashMap() {
        return mmoSimpleRoleConcurrentHashMap;
    }

    public void setMmoSimpleRoleConcurrentHashMap(ConcurrentHashMap<Integer, MmoSimpleRole> mmoSimpleRoleConcurrentHashMap) {
        this.mmoSimpleRoleConcurrentHashMap = mmoSimpleRoleConcurrentHashMap;
    }
}
