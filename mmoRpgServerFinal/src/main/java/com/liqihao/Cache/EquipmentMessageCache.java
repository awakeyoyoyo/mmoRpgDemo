package com.liqihao.Cache;

import com.liqihao.pojo.baseMessage.BufferMessage;
import com.liqihao.pojo.baseMessage.EquipmentMessage;

import java.util.concurrent.ConcurrentHashMap;

/**
 * 装备基本信息类
 * @author lqhao
 */
public class EquipmentMessageCache extends CommonsCache<EquipmentMessage>{
    private volatile static EquipmentMessageCache instance ;
    public static EquipmentMessageCache getInstance(){
        return instance;
    }
    public EquipmentMessageCache() {

    }
    public static void init(ConcurrentHashMap<Integer, EquipmentMessage> map){
        if (instance==null){
            instance= new EquipmentMessageCache(map);
        }
    }
    private EquipmentMessageCache(ConcurrentHashMap<Integer,EquipmentMessage> map) {
        super(map);
    }
}
