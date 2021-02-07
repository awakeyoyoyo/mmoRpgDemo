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

    private void setPreTaskId(Integer preTaskId) {
        this.preTaskId = preTaskId;
    }

    public Integer getNextTaskId() {
        return nextTaskId;
    }

    private void setNextTaskId(Integer nextTaskId) {
        this.nextTaskId = nextTaskId;
    }

    public Integer getArticleType() {
        return articleType;
    }

    private void setArticleType(Integer articleType) {
        this.articleType = articleType;
    }

    public Integer getTargetType() {
        return targetType;
    }

    private void setTargetType(Integer targetType) {
        this.targetType = targetType;
    }

    public Integer getId() {
        return id;
    }

    @Override
    public Integer getTheId() {
        return getId();
    }

    private void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    private void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    private void setDescription(String description) {
        this.description = description;
    }

    public Integer getTargetProgress() {
        return targetProgress;
    }

    private void setTargetProgress(Integer targetProgress) {
        this.targetProgress = targetProgress;
    }

    public Integer getRewardArticleType() {
        return rewardArticleType;
    }

    private void setRewardArticleType(Integer rewardArticleType) {
        this.rewardArticleType = rewardArticleType;
    }

    public Integer getRewardArticleMessageId() {
        return rewardArticleMessageId;
    }

    private void setRewardArticleMessageId(Integer rewardArticleMessageId) {
        this.rewardArticleMessageId = rewardArticleMessageId;
    }

    public Integer getRewardNum() {
        return rewardNum;
    }

    private void setRewardNum(Integer rewardNum) {
        this.rewardNum = rewardNum;
    }

    public Integer getTargetId() {
        return targetId;
    }

    private void setTargetId(Integer targetId) {
        this.targetId = targetId;
    }

    public Integer getType() {
        return type;
    }

    private void setType(Integer type) {
        this.type = type;
    }
}
