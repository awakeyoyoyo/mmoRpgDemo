package com.liqihao.pojo.bean.taskBean.killTask;

import com.liqihao.pojo.bean.taskBean.BaseTaskAction;

/**
 * @Classname KillTaskAction
 * @Description 击杀怪物的事件
 * @Author lqhao
 * @Date 2021/1/26 10:41
 * @Version 1.0
 */
public class KillTaskAction extends BaseTaskAction {
    private Integer targetRoleId;
    private Integer roleType;
    private Integer num;
    public Integer getTargetRoleId() {
        return targetRoleId;
    }

    public Integer getNum() {
        return num;
    }

    public void setNum(Integer num) {
        this.num = num;
    }

    public void setTargetRoleId(Integer targetRoleId) {
        this.targetRoleId = targetRoleId;
    }

    public Integer getRoleType() {
        return roleType;
    }

    public void setRoleType(Integer roleType) {
        this.roleType = roleType;
    }
}
