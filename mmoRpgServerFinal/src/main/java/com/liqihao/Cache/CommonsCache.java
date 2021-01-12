package com.liqihao.Cache;

import com.liqihao.pojo.baseMessage.BaseMessage;
import com.liqihao.pojo.baseMessage.CopySceneMessage;
import com.liqihao.util.ExcelReaderUtil;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
/**
 * Cache基类
 * @author lqhao
 */
public class CommonsCache<T extends BaseMessage>{
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
    public  void init(String excel_file,Class clazz) throws IllegalAccessException, IOException, InstantiationException {
        this.concurrentHashMap=new ConcurrentHashMap<>();
        List<T> messages= ExcelReaderUtil.readExcelFromFileName(excel_file,clazz);
        for (T message:messages) {
            concurrentHashMap.put(message.getTheId(),message);
        }
    }
}
