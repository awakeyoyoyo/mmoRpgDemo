package com.liqihao.Cache;

import com.liqihao.pojo.MmoRolePOJO;
import com.liqihao.pojo.MmoScene;
import io.netty.channel.Channel;

import java.util.concurrent.ConcurrentHashMap;

public class MmoCahe {
    private volatile static MmoCahe instance ;
    private ConcurrentHashMap<Integer,MmoScene> mmoSceneConcurrentHashMap;
    private ConcurrentHashMap<Integer, MmoRolePOJO> mmoSimpleRoleConcurrentHashMap;
    private ConcurrentHashMap<Integer, Channel> channelConcurrentHashMap;

    public ConcurrentHashMap<Integer, Channel> getChannelConcurrentHashMap() {
        return channelConcurrentHashMap;
    }

    public void setChannelConcurrentHashMap(ConcurrentHashMap<Integer, Channel> channelConcurrentHashMap) {
        this.channelConcurrentHashMap = channelConcurrentHashMap;
    }

    public static void init(ConcurrentHashMap<Integer,MmoScene> mms, ConcurrentHashMap<Integer,MmoRolePOJO> mmr){
        instance=new MmoCahe(mms,mmr);
    }
    public static MmoCahe getInstance(){
        return instance;
    }
    public MmoCahe() {
    }

    public MmoCahe(ConcurrentHashMap<Integer, MmoScene> mmoSceneConcurrentHashMap, ConcurrentHashMap<Integer, MmoRolePOJO> mmoSimpleRoleConcurrentHashMap) {
        this.mmoSceneConcurrentHashMap = mmoSceneConcurrentHashMap;
        this.mmoSimpleRoleConcurrentHashMap = mmoSimpleRoleConcurrentHashMap;
        this.channelConcurrentHashMap=new ConcurrentHashMap<>();
    }

    public ConcurrentHashMap<Integer, MmoScene> getMmoSceneConcurrentHashMap() {
        return mmoSceneConcurrentHashMap;
    }

    public void setMmoSceneConcurrentHashMap(ConcurrentHashMap<Integer, MmoScene> mmoSceneConcurrentHashMap) {
        this.mmoSceneConcurrentHashMap = mmoSceneConcurrentHashMap;
    }

    public ConcurrentHashMap<Integer, MmoRolePOJO> getMmoSimpleRoleConcurrentHashMap() {
        return mmoSimpleRoleConcurrentHashMap;
    }

    public void setMmoSimpleRoleConcurrentHashMap(ConcurrentHashMap<Integer, MmoRolePOJO> mmoSimpleRoleConcurrentHashMap) {
        this.mmoSimpleRoleConcurrentHashMap = mmoSimpleRoleConcurrentHashMap;
    }
}
