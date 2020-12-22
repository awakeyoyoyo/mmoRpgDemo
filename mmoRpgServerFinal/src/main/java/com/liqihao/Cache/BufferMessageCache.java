package com.liqihao.Cache;

import com.liqihao.pojo.baseMessage.BufferMessage;
import com.liqihao.pojo.baseMessage.SkillMessage;
import com.liqihao.util.ExcelReaderUtil;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * buffer基本信息缓存
 * @author lqhao
 */
@Component
public class BufferMessageCache extends CommonsCache<BufferMessage>{
    private static String bufferMessage_file = "classpath:message/bufferMessage.xlsx";
    private volatile static BufferMessageCache instance ;
    public static BufferMessageCache getInstance(){
        return instance;
    }
    public BufferMessageCache() {

    }
    @PostConstruct
    public  void init() throws IllegalAccessException, IOException, InstantiationException {
        instance=this;
        this.concurrentHashMap=new ConcurrentHashMap<>();
        List<BufferMessage> bufferMessage= ExcelReaderUtil.readExcelFromFileName(bufferMessage_file,BufferMessage.class);
        for (BufferMessage b:bufferMessage) {
            concurrentHashMap.put(b.getId(),b);
        }
    }
    private BufferMessageCache(ConcurrentHashMap<Integer,BufferMessage> map) {
        super(map);
     }
}
