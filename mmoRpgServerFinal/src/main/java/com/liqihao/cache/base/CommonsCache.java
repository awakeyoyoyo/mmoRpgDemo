package com.liqihao.cache.base;

import com.liqihao.pojo.baseMessage.BaseMessage;
import com.liqihao.util.ExcelReaderUtil;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
/**
 * Cache基类   读取批量的配置文件
 * @author lqhao
 */
public class CommonsCache<T extends BaseMessage>{
    private ConcurrentHashMap<Integer,T> concurrentHashMap;
    public CommonsCache() {
    }
    public CommonsCache(ConcurrentHashMap<Integer, T> concurrentHashMap) {
        this.concurrentHashMap = concurrentHashMap;
    }
    public T get(Integer id){
        return concurrentHashMap.get(id);
    }
    private T put(Integer id,T object){
        return concurrentHashMap.put(id,object);
    }
    private void remove(Integer id){
        concurrentHashMap.remove(id);
    }
    public boolean contains(Integer id){
        return concurrentHashMap.containsKey(id);
    }
    public Collection<T> values(){return concurrentHashMap.values();}

    /**
     * 初始化
     * @param excel_file
     * @param clazz
     * @throws IllegalAccessException
     * @throws IOException
     * @throws InstantiationException
     */
    public  void init(String excel_file,Class clazz) throws IllegalAccessException, IOException, InstantiationException {
        this.concurrentHashMap=new ConcurrentHashMap<>();
        List<T> messages= ExcelReaderUtil.readExcelFromFileName(excel_file,clazz);
        for (T message:messages) {
            concurrentHashMap.put(message.getTheId(),message);
        }
    }
}
