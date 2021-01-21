package com.liqihao.pojo.bean.TaskBean;

/**
 * @author 人物动作
 */
public class BaseTaskAction {
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

    public Integer getArticleType() {
        return articleType;
    }

    public void setArticleType(Integer articleType) {
        this.articleType = articleType;
    }

    public Integer getTargetType() {
        return targetType;
    }

    public void setTargetType(Integer targetType) {
        this.targetType = targetType;
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
