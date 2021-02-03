package com.liqihao.cache;

import com.liqihao.cache.base.CommonsCache;
import com.liqihao.pojo.baseMessage.BufferMessage;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

/**
 * buffer基本信息缓存
 * @author lqhao
 */
@Component
public class BufferMessageCache extends CommonsCache<BufferMessage> {
    private static String excel_file = "classpath:message/bufferMessage.xlsx";
    private volatile static BufferMessageCache instance ;
    public static BufferMessageCache getInstance(){
        return instance;
    }
    public BufferMessageCache() {

    }
    @PostConstruct
    public  void init() throws IllegalAccessException, IOException, InstantiationException {
        instance=this;
        super.init(excel_file, BufferMessage.class);
    }
    private BufferMessageCache(ConcurrentHashMap<Integer,BufferMessage> map) {
        super(map);
     }
}
