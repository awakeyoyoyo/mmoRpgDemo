package com.liqihao.Cache;

import com.liqihao.pojo.bean.MmoSimpleNPC;
import com.liqihao.pojo.bean.MmoSimpleRole;

import java.util.concurrent.ConcurrentHashMap;

/**
 * 在线用户缓存
 * @author lqhao
 */
public class OnlineRoleMessageCache extends CommonsCache<MmoSimpleRole> {
    private volatile static OnlineRoleMessageCache instance;

    public static OnlineRoleMessageCache getInstance() {
        return instance;
    }

    public OnlineRoleMessageCache() {

    }
    public static void init(ConcurrentHashMap<Integer, MmoSimpleRole> map) {
        if (instance == null) {
            instance = new OnlineRoleMessageCache(map);
        }
    }
    private OnlineRoleMessageCache(ConcurrentHashMap<Integer, MmoSimpleRole> map) {
        super(map);

    }
}
