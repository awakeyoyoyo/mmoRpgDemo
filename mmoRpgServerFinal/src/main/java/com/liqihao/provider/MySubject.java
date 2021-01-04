package com.liqihao.provider;

import com.liqihao.pojo.bean.MmoSimpleRole;
import com.liqihao.pojo.bean.Role;

/**
 * 通知中心接口
 * @author lqhao
 */
public interface MySubject {
    /**
     * 注册角色
     * @param o
     */
    void registerObserver(Role o);

    /**
     * 删除角色
     * @param o
     */
    void removeObserver(Role o);

    /**
     * 通知所有
     * @param fromRole
     * @param str
     */
    void notifyObserver(Role fromRole, String str);

    /**
     * 通知一人
     * @param toRoleId
     * @param fromRole
     * @param str
     */
    void notifyOne(Integer toRoleId, Role fromRole, String str);
}
