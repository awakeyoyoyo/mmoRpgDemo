package com.liqihao.pojo.bean.friendBean;

/**
 * @Classname FriendBean
 * @Description 好友bean
 * @Author lqhao
 * @Date 2021/1/27 10:07
 * @Version 1.0
 */
public class FriendBean {
    private Integer roleId;
    private String  name;
    private Integer onStatus;
    private Integer professionId;

    public Integer getProfessionId() {
        return professionId;
    }

    public void setProfessionId(Integer professionId) {
        this.professionId = professionId;
    }

    public Integer getRoleId() {
        return roleId;
    }

    public void setRoleId(Integer roleId) {
        this.roleId = roleId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getOnStatus() {
        return onStatus;
    }

    public void setOnStatus(Integer onStatus) {
        this.onStatus = onStatus;
    }
}
