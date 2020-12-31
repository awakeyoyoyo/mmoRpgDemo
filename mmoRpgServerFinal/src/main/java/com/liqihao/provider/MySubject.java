package com.liqihao.provider;

import com.liqihao.pojo.bean.MmoSimpleRole;
import com.liqihao.pojo.bean.Role;

/**
 * 通知中心接口
 * @author lqhao
 */
public interface MySubject {
    void registerObservier(Role o);
    void removeObservier(Role o);
    void notifyObserver(Role fromRole, String str);
    void notifyOne(Integer toRoleId, Role fromRole, String str);
}
