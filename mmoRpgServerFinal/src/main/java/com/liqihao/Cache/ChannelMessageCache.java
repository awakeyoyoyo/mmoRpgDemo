package com.liqihao.Cache;

import io.netty.channel.Channel;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 角色id与缓存
 * @author lqhao
 */
@Component
public class ChannelMessageCache extends CommonsCache<Channel>{
    private volatile static ChannelMessageCache instance ;
    public static ChannelMessageCache getInstance(){
        return instance;
    }
    public ChannelMessageCache() {
    }
    @PostConstruct
    public void init(){
        instance=this;
        this.concurrentHashMap=new ConcurrentHashMap<>();
    }
    private ChannelMessageCache(ConcurrentHashMap<Integer,Channel> map) {
        super(map);
    }
}
