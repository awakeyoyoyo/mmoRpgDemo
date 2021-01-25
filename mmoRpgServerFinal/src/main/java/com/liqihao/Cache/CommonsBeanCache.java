package com.liqihao.Cache;

import com.liqihao.util.ExcelReaderUtil;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 游戏内容缓存基类
 * @author lqhao
 */
public class CommonsBeanCache <T>{
    protected ConcurrentHashMap<Integer,T> concurrentHashMap;
    public CommonsBeanCache() {
    }
    public CommonsBeanCache(ConcurrentHashMap<Integer, T> concurrentHashMap) {
        this.concurrentHashMap = concurrentHashMap;
    }
    public T get(Integer id){
        return concurrentHashMap.get(id);
    }
    public T put(Integer id,T object){
        return concurrentHashMap.put(id,object);
    }
    public void remove(Integer id){
        concurrentHashMap.remove(id);
    }
    public boolean contains(Integer id){
        return concurrentHashMap.containsKey(id);
    }
    public Collection<T> values(){return concurrentHashMap.values();}
}
