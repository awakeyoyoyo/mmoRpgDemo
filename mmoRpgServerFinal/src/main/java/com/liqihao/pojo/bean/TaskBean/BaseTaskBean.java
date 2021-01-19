package com.liqihao.pojo.bean.TaskBean;

import com.liqihao.pojo.bean.roleBean.MmoSimpleRole;

/**
 *
 * @author lqhao
 */
public class BaseTask {
    /**
     * 任务信息类
     */
    private Integer taskMessageId;
    /**
     * 进展
     */
    private Integer progress;
    /**
     * 状态
     */
    private Integer status;

     /**
     * 数据库id
     */
    private Integer taskDbId;

    /**
     * 任务id
     */
    private Integer taskId;

    /**
     * 检测是否完成
     */
    private void check(MmoSimpleRole role){

    }

    public Integer getTaskMessageId() {
        return taskMessageId;
    }

    public void setTaskMessageId(Integer taskMessageId) {
        this.taskMessageId = taskMessageId;
    }

    public Integer getProgress() {
        return progress;
    }

    public void setProgress(Integer progress) {
        this.progress = progress;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getTaskDbId() {
        return taskDbId;
    }

    public void setTaskDbId(Integer taskDbId) {
        this.taskDbId = taskDbId;
    }

    public Integer getTaskId() {
        return taskId;
    }

    public void setTaskId(Integer taskId) {
        this.taskId = taskId;
    }
}
