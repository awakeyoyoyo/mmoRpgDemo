package com.liqihao.pojo.bean.taskBean.skillTask;

import com.liqihao.pojo.bean.taskBean.BaseTaskAction;

/**
 * @Classname SkillTaskAction
 * @Description 使用技能动作
 * @Author lqhao
 * @Date 2021/1/26 11:12
 * @Version 1.0
 */
public class SkillTaskAction extends BaseTaskAction {
    private Integer skillId;

    public Integer getSkillId() {
        return skillId;
    }

    public void setSkillId(Integer skillId) {
        this.skillId = skillId;
    }
}
