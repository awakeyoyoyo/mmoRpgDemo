package com.liqihao.Cache;

import com.liqihao.pojo.baseMessage.BaseDetailMessage;
import com.liqihao.pojo.baseMessage.BaseRoleMessage;

/**
 * 基本信息缓存
 * @author lqhao
 */
public class MmoBaseMessageCache {
    //用户角色的基本信息
    private BaseRoleMessage baseRoleMessage;
    //基础配置信息
    private BaseDetailMessage baseDetailMessage;

    private volatile  static MmoBaseMessageCache instance;

    public static MmoBaseMessageCache getInstance(){
        return instance;
    }
    public MmoBaseMessageCache() {
    }

    public MmoBaseMessageCache(BaseRoleMessage baseRoleMessage, BaseDetailMessage baseDetailMessage) {
        this.baseRoleMessage = baseRoleMessage;
        this.baseDetailMessage = baseDetailMessage;
    }

    public static void init(BaseRoleMessage baseRoleMessage, BaseDetailMessage baseDetailMessage){
        if (instance==null){
            instance= new MmoBaseMessageCache(baseRoleMessage, baseDetailMessage);
        }
    }
    public BaseRoleMessage getBaseRoleMessage() {
        return baseRoleMessage;
    }

    public  void setBaseRoleMessage(BaseRoleMessage baseRoleMessage) {
        this.baseRoleMessage = baseRoleMessage;
    }

    public  BaseDetailMessage getBaseDetailMessage() {
        return baseDetailMessage;
    }

    public  void setBaseDetailMessage(BaseDetailMessage baseDetailMessage) {
        this.baseDetailMessage = baseDetailMessage;
    }
}
