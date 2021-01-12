package com.liqihao.Cache;


import com.liqihao.pojo.baseMessage.GuildPositionMessage;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 工会职位
 * @author lqhao
 */
@Component
public class GuildPositionMessageCache extends CommonsCache<GuildPositionMessage>{
    private static String excel_file = "classpath:message/guildPositionMessage.xlsx";
    private volatile static GuildPositionMessageCache instance ;
    public static GuildPositionMessageCache getInstance(){
        return instance;
    }
    public GuildPositionMessageCache() {

    }
    @PostConstruct
    public  void init() throws IllegalAccessException, IOException, InstantiationException {
        instance=this;
        super.init(excel_file, GuildPositionMessage.class);
    }
    private GuildPositionMessageCache(ConcurrentHashMap<Integer,GuildPositionMessage> map) {
        super(map);
    }
}
