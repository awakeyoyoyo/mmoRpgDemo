package com.liqihao.pojo.baseMessage;

/**
 * 任务信息类
 * @author lqhao
 */
public class TaskMessage extends BaseMessage{
    private Integer id;
    private String name;
    private String description;
    private Integer targetProgress;
    private Integer targetType;
    private Integer rewardArticleType;
    private Integer rewardArticleMessageId;
    private Integer rewardNum;
    private Integer targetId;
    private Integer type;
    private Integer articleType;
    private Integer preTaskId;
    private Integer nextTaskId;

    public Integer getPreTaskId() {
        return preTaskId;
    }

    public Integer getNextTaskId() {
        return nextTaskId;
    }

    public Integer getArticleType() {
        return articleType;
    }

    public Integer getTargetType() {
        return targetType;
    }

    public Integer getId() {
        return id;
    }

    @Override
    public Integer getTheId() {
        return getId();
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Integer getTargetProgress() {
        return targetProgress;
    }

    public Integer getRewardArticleType() {
        return rewardArticleType;
    }

    public Integer getRewardArticleMessageId() {
        return rewardArticleMessageId;
    }

    public Integer getRewardNum() {
        return rewardNum;
    }

    public Integer getTargetId() {
        return targetId;
    }

    public Integer getType() {
        return type;
    }
}
