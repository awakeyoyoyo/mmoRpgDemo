package com.liqihao.cache;

import com.liqihao.cache.base.CommonsCache;
import com.liqihao.pojo.baseMessage.ProfessionMessage;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 职业基础信息缓存类
 * @author lqhao
 */
@Component
public class ProfessionMessageCache extends CommonsCache<ProfessionMessage> {
    private static String excel_file = "classpath:message/professionMessage.xlsx";
    private volatile static ProfessionMessageCache instance ;
    public static ProfessionMessageCache getInstance(){
        return instance;
    }
    public ProfessionMessageCache() {

    }
    @PostConstruct
    public  void init() throws IllegalAccessException, IOException, InstantiationException {
        instance=this;
        super.init(excel_file, ProfessionMessage.class);
    }
    private ProfessionMessageCache(ConcurrentHashMap<Integer,ProfessionMessage> map) {
        super(map);
    }
}
