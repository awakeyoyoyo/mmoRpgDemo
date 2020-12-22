package com.liqihao.Cache;

import com.liqihao.pojo.baseMessage.BufferMessage;
import com.liqihao.pojo.baseMessage.EquipmentMessage;
import com.liqihao.util.ExcelReaderUtil;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 装备基本信息类
 * @author lqhao
 */
@Component
public class EquipmentMessageCache extends CommonsCache<EquipmentMessage>{
    private static String equipmentMessage_file = "classpath:message/equipmentMessage.xlsx";
    private volatile static EquipmentMessageCache instance ;
    public static EquipmentMessageCache getInstance(){
        return instance;
    }
    public EquipmentMessageCache() {

    }
    @PostConstruct
    public  void init() throws IllegalAccessException, IOException, InstantiationException {
        instance=this;
        this.concurrentHashMap=new ConcurrentHashMap<>();
        List<EquipmentMessage> equipmentMessages= ExcelReaderUtil.readExcelFromFileName(equipmentMessage_file,EquipmentMessage.class);
        for (EquipmentMessage equipmentMessage:equipmentMessages) {
            concurrentHashMap.put(equipmentMessage.getId(),equipmentMessage);
        }
    }
    private EquipmentMessageCache(ConcurrentHashMap<Integer,EquipmentMessage> map) {
        super(map);
    }
}
