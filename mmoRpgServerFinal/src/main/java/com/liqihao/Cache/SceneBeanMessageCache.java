package com.liqihao.Cache;

import com.liqihao.pojo.bean.SceneBean;
import io.netty.channel.Channel;

import java.util.concurrent.ConcurrentHashMap;

/**
 * 场景实例Cache
 * @author lqhao
 */
public class SceneBeanMessageCache extends CommonsCache<SceneBean>{
    private volatile static SceneBeanMessageCache instance ;
    public static SceneBeanMessageCache getInstance(){
        return instance;
    }
    public SceneBeanMessageCache() {

    }
    public static void init(ConcurrentHashMap<Integer, SceneBean> map){
        if (instance==null){
            instance= new SceneBeanMessageCache(map);
        }
    }
    private SceneBeanMessageCache(ConcurrentHashMap<Integer,SceneBean> map) {
        super(map);
    }
}
