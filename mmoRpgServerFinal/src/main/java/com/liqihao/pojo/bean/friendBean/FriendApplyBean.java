package com.liqihao.pojo.bean.friendBean;

/**
 * @Classname FriendApplyBean
 * @Description 好友申请bean
 * @Author lqhao
 * @Date 2021/1/27 10:47
 * @Version 1.0
 */
public class FriendApplyBean {
    Integer id;
    Integer roleId;
    String name;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getRoleId() {
        return roleId;
    }

    public void setRoleId(Integer roleId) {
        this.roleId = roleId;
    }
}
