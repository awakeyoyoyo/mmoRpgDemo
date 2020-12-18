package com.liqihao.Cache;

import com.liqihao.pojo.baseMessage.BufferMessage;
import com.liqihao.pojo.baseMessage.MedicineMessage;

import java.util.concurrent.ConcurrentHashMap;

/**
 * 药品基本信息缓存
 * @author lqhao
 */
public class MediceneMessageCache extends CommonsCache<MedicineMessage>{
    private volatile static MediceneMessageCache instance ;
    public static MediceneMessageCache getInstance(){
        return instance;
    }
    public MediceneMessageCache() {

    }
    public static void init(ConcurrentHashMap<Integer, MedicineMessage> map){
        if (instance==null){
            instance= new MediceneMessageCache(map);
        }
    }
    private MediceneMessageCache(ConcurrentHashMap<Integer,MedicineMessage> map) {
        super(map);
    }
}
