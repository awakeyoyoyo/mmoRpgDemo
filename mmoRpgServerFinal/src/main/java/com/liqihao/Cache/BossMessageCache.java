package com.liqihao.Cache;

import com.liqihao.pojo.baseMessage.BossMessage;
import com.liqihao.pojo.baseMessage.CopySceneMessage;
import com.liqihao.util.ExcelReaderUtil;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * boss信息缓存
 * @author lqhao
 */
@Component
public class BossMessageCache extends CommonsCache<BossMessage>{
    private static String bossMessage_file = "classpath:message/bossMessage.xlsx";
    private volatile static BossMessageCache instance ;
    public static BossMessageCache getInstance(){
        return instance;
    }
    public BossMessageCache() {

    }
    @PostConstruct
    public  void init() throws IllegalAccessException, IOException, InstantiationException {
        instance=this;
        this.concurrentHashMap=new ConcurrentHashMap<>();
        List<BossMessage> bossMessages= ExcelReaderUtil.readExcelFromFileName(bossMessage_file,BossMessage.class);
        for (BossMessage bossMessage:bossMessages) {
            concurrentHashMap.put(bossMessage.getId(),bossMessage);
        }
    }
    private BossMessageCache(ConcurrentHashMap<Integer,BossMessage> map) {
        super(map);
    }
}
