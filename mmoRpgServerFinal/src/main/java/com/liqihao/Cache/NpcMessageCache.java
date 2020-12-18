package com.liqihao.Cache;

import com.liqihao.pojo.bean.MmoSimpleNPC;
import java.util.concurrent.ConcurrentHashMap;
/**
 * Npc实例Cache类
 * @author lqhao
 */
public class NpcMessageCache extends CommonsCache<MmoSimpleNPC>{
    private volatile static NpcMessageCache instance ;
    public static NpcMessageCache getInstance(){
        return instance;
    }
    public NpcMessageCache() {

    }
    public static void init(ConcurrentHashMap<Integer,MmoSimpleNPC> map){
        if (instance==null){
            instance= new NpcMessageCache(map);
        }
    }
    private NpcMessageCache(ConcurrentHashMap<Integer,MmoSimpleNPC> map) {
        super(map);
    }
}
