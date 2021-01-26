package com.liqihao.pojo.bean.taskBean.useTask;

import com.liqihao.pojo.bean.taskBean.BaseTaskAction;

/**
 * @Classname UseTaskAction
 * @Description 使用物品的任务
 * @Author lqhao
 * @Date 2021/1/21 20:10
 * @Version 1.0
 */
public class UseTaskAction extends BaseTaskAction {

    /**
     * 目标物品类型
     */
    private Integer articleType;
    /**
     * 目标id
     */
    private Integer targetId;
    /**
     * 进度
     */
    private Integer progress;


    public Integer getArticleType() {
        return articleType;
    }

    public void setArticleType(Integer articleType) {
        this.articleType = articleType;
    }

    public Integer getTargetId() {
        return targetId;
    }

    public void setTargetId(Integer targetId) {
        this.targetId = targetId;
    }

    public Integer getProgress() {
        return progress;
    }

    public void setProgress(Integer progress) {
        this.progress = progress;
    }
}
