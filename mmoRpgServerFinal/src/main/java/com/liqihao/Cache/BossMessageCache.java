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
    private static String excel_file = "classpath:message/bossMessage.xlsx";
    private volatile static BossMessageCache instance ;
    public static BossMessageCache getInstance(){
        return instance;
    }
    public BossMessageCache() {

    }
    @PostConstruct
    public  void init() throws IllegalAccessException, IOException, InstantiationException {
        instance=this;
        super.init(excel_file, BossMessage.class);
    }
    private BossMessageCache(ConcurrentHashMap<Integer,BossMessage> map) {
        super(map);
    }
}
