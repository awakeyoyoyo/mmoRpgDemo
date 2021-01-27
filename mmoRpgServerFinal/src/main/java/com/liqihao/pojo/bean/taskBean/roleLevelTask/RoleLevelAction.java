package com.liqihao.pojo.bean.taskBean.roleLevelTask;

import com.liqihao.pojo.bean.taskBean.BaseTaskAction;
import com.liqihao.pojo.bean.taskBean.BaseTaskBean;

/**
 * @Classname RoleLevelAction
 * @Description 角色升级
 * @Author lqhao
 * @Date 2021/1/27 15:35
 * @Version 1.0
 */
public class RoleLevelAction extends BaseTaskAction {
    Integer level;

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }
}
