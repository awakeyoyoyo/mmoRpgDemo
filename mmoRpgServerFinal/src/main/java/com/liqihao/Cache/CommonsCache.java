package com.liqihao.Cache;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
/**
 * Cache基类
 * @author lqhao
 */
public class CommonsCache<T>{
    protected ConcurrentHashMap<Integer,T> concurrentHashMap;
    public CommonsCache() {
    }
    public CommonsCache(ConcurrentHashMap<Integer, T> concurrentHashMap) {
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
