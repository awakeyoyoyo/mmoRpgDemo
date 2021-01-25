package com.liqihao.pojo.bean.TaskBean.UseTask;

import com.liqihao.pojo.bean.TaskBean.BaseTaskAction;

/**
 * @Classname UseTaskAction
 * @Description TODO
 * @Author lqhao
 * @Date 2021/1/21 20:10
 * @Version 1.0
 */
public class UseTaskAction extends BaseTaskAction {
    /**
     * 目标类型
     */
    private Integer targetType;
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

    public Integer getTargetType() {
        return targetType;
    }

    public void setTargetType(Integer targetType) {
        this.targetType = targetType;
    }

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

    @Override
    public Integer getTaskTargetType() {
        return getTargetType();
    }
}
