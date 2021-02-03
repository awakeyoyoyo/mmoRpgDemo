package com.liqihao.cache;

import com.liqihao.cache.base.CommonsCache;
import com.liqihao.pojo.baseMessage.GuildAuthorityMessage;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 读取权限信息
 * @author lqhao
 */
@Component
public class GuildAuthorityMessageCache extends CommonsCache<GuildAuthorityMessage> {
    private static String excel_file = "classpath:message/guildAuthorityMessage.xlsx";
    private volatile static GuildAuthorityMessageCache instance ;
    public static GuildAuthorityMessageCache getInstance(){
        return instance;
    }
    public GuildAuthorityMessageCache() {

    }
    @PostConstruct
    public  void init() throws IllegalAccessException, IOException, InstantiationException {
        instance=this;
        super.init(excel_file, GuildAuthorityMessage.class);
    }
    private GuildAuthorityMessageCache(ConcurrentHashMap<Integer,GuildAuthorityMessage> map) {
        super(map);
    }
}
