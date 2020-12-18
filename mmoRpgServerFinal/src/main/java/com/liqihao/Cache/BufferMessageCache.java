package com.liqihao.Cache;

import com.liqihao.pojo.baseMessage.BufferMessage;
import com.liqihao.pojo.baseMessage.SkillMessage;

import java.util.concurrent.ConcurrentHashMap;

/**
 * buffer基本信息缓存
 * @author lqhao
 */
public class BufferMessageCache extends CommonsCache<BufferMessage>{
    private volatile static BufferMessageCache instance ;
    public static BufferMessageCache getInstance(){
        return instance;
    }
    public BufferMessageCache() {

    }
    public static void init(ConcurrentHashMap<Integer, BufferMessage> map){
        if (instance==null){
            instance= new BufferMessageCache(map);
        }
    }
    private BufferMessageCache(ConcurrentHashMap<Integer,BufferMessage> map) {
        super(map);
     }
}
