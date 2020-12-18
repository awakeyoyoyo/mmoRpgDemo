package com.liqihao.Cache;

import com.liqihao.pojo.baseMessage.SkillMessage;
import com.liqihao.pojo.bean.SceneBean;
import com.liqihao.pojo.bean.SkillBean;

import java.util.concurrent.ConcurrentHashMap;

/**
 * 技能的基本信息Cache
 * @author lqhao
 */
public class SkillMessageCache extends CommonsCache<SkillMessage> {
    private volatile static SkillMessageCache instance ;
    public static SkillMessageCache getInstance(){
        return instance;
    }
    public SkillMessageCache() {

    }
    public static void init(ConcurrentHashMap<Integer, SkillMessage> map){
        if (instance==null){
            instance= new SkillMessageCache(map);
        }
    }
    private SkillMessageCache(ConcurrentHashMap<Integer,SkillMessage> map) {
        super(map);
    }
}
