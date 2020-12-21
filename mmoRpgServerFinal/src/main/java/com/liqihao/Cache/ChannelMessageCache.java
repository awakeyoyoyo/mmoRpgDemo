package com.liqihao.Cache;

import com.liqihao.pojo.baseMessage.BaseDetailMessage;
import com.liqihao.pojo.baseMessage.BaseRoleMessage;
import com.liqihao.pojo.bean.MmoSimpleNPC;
import io.netty.channel.Channel;

import java.util.concurrent.ConcurrentHashMap;

/**
 * 角色id与缓存
 * @author lqhao
 */
public class ChannelMessageCache extends CommonsCache<Channel>{
    private volatile static ChannelMessageCache instance ;
    public static ChannelMessageCache getInstance(){
        return instance;
    }
    public ChannelMessageCache() {
    }
    public static void init(ConcurrentHashMap<Integer, Channel> map){
        if (instance==null){
            instance= new ChannelMessageCache(map);
        }
    }
    private ChannelMessageCache(ConcurrentHashMap<Integer,Channel> map) {
        super(map);
    }
}
