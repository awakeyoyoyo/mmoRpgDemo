package com.liqihao.pojo.bean.taskBean.talkTask;

import com.liqihao.pojo.bean.taskBean.BaseTaskAction;

/**
 * @Classname TalkAction
 * @Description 对话动作
 * @Author lqhao
 * @Date 2021/1/26 11:18
 * @Version 1.0
 */
public class TalkTaskAction extends BaseTaskAction {
    private Integer roleId;
    public Integer getRoleId() {
        return roleId;
    }

    public void setRoleId(Integer roleId) {
        this.roleId = roleId;
    }
}
