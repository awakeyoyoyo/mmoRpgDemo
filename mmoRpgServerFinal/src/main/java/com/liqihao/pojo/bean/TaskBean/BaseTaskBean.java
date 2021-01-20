package com.liqihao.pojo.bean.TaskBean;

import com.liqihao.commons.RpgServerException;
import com.liqihao.commons.enums.TaskTargetTypeCode;
import com.liqihao.pojo.bean.articleBean.Article;
import com.liqihao.pojo.bean.roleBean.MmoSimpleRole;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author lqhao
 */
public abstract class BaseTaskBean {
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
     * 接收事件
     */
    private long createTime;
    /**
     * 检测是否完成
     */
    public abstract void update(ActionDto dto,MmoSimpleRole role) throws RpgServerException;

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public Integer getTaskMessageId() {
        return taskMessageId;
    }

    public void sendTaskFinish(BaseTaskBean taskBean,MmoSimpleRole role) {
        //todo
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

}
