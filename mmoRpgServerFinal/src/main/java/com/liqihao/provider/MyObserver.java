package com.liqihao.provider;

import com.liqihao.pojo.bean.roleBean.Role;

/**
 * 观察者接口
 * @author lqhao
 */
public interface MyObserver {
    /**
     * 有信息则触发
     * @param fromRole
     * @param str
     * @param chatType
     */
    void update(Role fromRole,String str,Integer chatType);

    /**
     * 返回roleId
     * @return
     */
    Integer returnRoleId();
}
