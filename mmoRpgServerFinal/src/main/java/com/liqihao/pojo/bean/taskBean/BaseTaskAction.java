package com.liqihao.pojo.bean.taskBean;

/**
 * @Classname BaseTaskAction
 * @Description 基础的任务执行动作消息
 * @Author lqhao
 * @Date 2021/1/21 12:19
 * @Version 1.0
 */
public abstract class BaseTaskAction {
    private Integer taskTargetType;
    /**
     * description 返回任务目标类型
     * @return {@link Integer }
     * @author lqhao
     * @createTime 2021/1/21 12:34
     */
    public Integer getTaskTargetType(){
        return taskTargetType;
    }

    public void setTaskTargetType(Integer taskTargetType) {
        this.taskTargetType = taskTargetType;
    }
}
