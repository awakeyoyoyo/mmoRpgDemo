package com.liqihao.provider;

import com.liqihao.Cache.OnlineRoleMessageCache;
import com.liqihao.commons.enums.ChatTypeCode;
import com.liqihao.pojo.bean.MmoSimpleRole;
import com.liqihao.pojo.bean.Role;

import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 聊天功能提供者
 * @author lqhao
 */

public class ChatServiceProvider implements MySubject{
    private OnlineRoleMessageCache onlineRoleMessageCache;
    private volatile static ChatServiceProvider instance;
    private ChatServiceProvider() {
        onlineRoleMessageCache=OnlineRoleMessageCache.getInstance();
    }
    public static ChatServiceProvider getInstance() {
        //第一次检测
        if (instance == null) {
            synchronized (ChatServiceProvider.class) {
                //第二次检测
                if (instance == null) {
                    instance = new ChatServiceProvider();
                }
            }
        }
        return instance;
    }
    @Override
    public void registerObservier(Role o) {
        onlineRoleMessageCache.put(o.getId(),(MmoSimpleRole) o);
    }

    @Override
    public void removeObservier(Role o) {
        if (onlineRoleMessageCache.contains((o.getId()))){
            onlineRoleMessageCache.remove(o.getId());
        }
    }

    @Override
    public void notifyObserver(Role fromRole,String str) {
        Iterator<MmoSimpleRole> iterator = onlineRoleMessageCache.values().iterator();
        while(iterator.hasNext()){
            MmoSimpleRole observer=iterator.next();
            observer.update(fromRole,str, ChatTypeCode.ALLPEOPLE.getCode());
        }
    }

    @Override
    public void notifyOne(Integer toRoleId, Role fromRole, String str) {
        if (onlineRoleMessageCache.contains(toRoleId)){
            onlineRoleMessageCache.get(toRoleId).update(fromRole,str,ChatTypeCode.SINGLEPEOPLE.getCode());
        }
    }

}
