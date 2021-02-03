package com.liqihao.cache;

import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Classname NodeCheckMessageCache
 * @Description 记录登陆的ip
 * @Author lqhao
 * @Date 2021/1/25 15:44
 * @Version 1.0
 */
@Component
public class NodeCheckMessageCache {
    private volatile static NodeCheckMessageCache instance;
    protected ConcurrentHashMap<String, Boolean> concurrentHashMap;
    public static NodeCheckMessageCache getInstance() {
        return instance;
    }
    public NodeCheckMessageCache() {
    }
    @PostConstruct
    public void init() {
        instance = this;
        this.concurrentHashMap = new ConcurrentHashMap<>();
    }

    public NodeCheckMessageCache(ConcurrentHashMap<String, Boolean> concurrentHashMap) {
        this.concurrentHashMap = concurrentHashMap;
    }

    public Boolean get(String ip) {
        return concurrentHashMap.get(ip);
    }

    public void put(String ip, Boolean isOk) {
        concurrentHashMap.put(ip, isOk);
    }

    public void remove(String ip) {
        concurrentHashMap.remove(ip);
    }

    public boolean contains(String ip) {
        return concurrentHashMap.containsKey(ip);
    }
}

