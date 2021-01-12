package com.liqihao.Cache;


import com.liqihao.pojo.bean.roleBean.MmoSimpleRole;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 在线用户缓存
 * @author lqhao
 */
@Component
public class OnlineRoleMessageCache extends CommonsBeanCache<MmoSimpleRole> {
    private volatile static OnlineRoleMessageCache instance;

    public static OnlineRoleMessageCache getInstance() {
        return instance;
    }

    public OnlineRoleMessageCache() {

    }
    @PostConstruct
    public void init() {
        instance=this;
        this.concurrentHashMap=new ConcurrentHashMap<>();
    }
    private OnlineRoleMessageCache(ConcurrentHashMap<Integer, MmoSimpleRole> map) {
        super(map);

    }
}
