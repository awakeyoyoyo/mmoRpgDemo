package com.liqihao.Cache;

import com.liqihao.pojo.baseMessage.CopySceneMessage;
import com.liqihao.pojo.baseMessage.EquipmentMessage;
import com.liqihao.util.ExcelReaderUtil;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 副本基本信息缓存
 * @author lqhao
 */
@Component
public class CopySceneMessageCache extends CommonsCache<CopySceneMessage>{
    private static String equipmentMessage_file = "classpath:message/copySceneMessage.xlsx";
    private volatile static CopySceneMessageCache instance ;
    public static CopySceneMessageCache getInstance(){
        return instance;
    }
    public CopySceneMessageCache() {

    }
    @PostConstruct
    public  void init() throws IllegalAccessException, IOException, InstantiationException {
        instance=this;
        this.concurrentHashMap=new ConcurrentHashMap<>();
        List<CopySceneMessage> copySceneMessages= ExcelReaderUtil.readExcelFromFileName(equipmentMessage_file,CopySceneMessage.class);
        for (CopySceneMessage copySceneMessage:copySceneMessages) {
            concurrentHashMap.put(copySceneMessage.getId(),copySceneMessage);
        }
    }
    private CopySceneMessageCache(ConcurrentHashMap<Integer,CopySceneMessage> map) {
        super(map);
    }
}