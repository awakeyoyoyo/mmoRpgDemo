package com.liqihao.pojo.baseMessage;

/**
 * @author lqhao
 */
public class TaskMessage{
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

    public Integer getId() {
        return id;
    }


    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getTargetProgress() {
        return targetProgress;
    }

    public void setTargetProgress(Integer targetProgress) {
        this.targetProgress = targetProgress;
    }

    public Integer getRewardArticleType() {
        return rewardArticleType;
    }

    public void setRewardArticleType(Integer rewardArticleType) {
        this.rewardArticleType = rewardArticleType;
    }

    public Integer getRewardArticleMessageId() {
        return rewardArticleMessageId;
    }

    public void setRewardArticleMessageId(Integer rewardArticleMessageId) {
        this.rewardArticleMessageId = rewardArticleMessageId;
    }

    public Integer getRewardNum() {
        return rewardNum;
    }

    public void setRewardNum(Integer rewardNum) {
        this.rewardNum = rewardNum;
    }

    public Integer getTargetId() {
        return targetId;
    }

    public void setTargetId(Integer targetId) {
        this.targetId = targetId;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }
}
