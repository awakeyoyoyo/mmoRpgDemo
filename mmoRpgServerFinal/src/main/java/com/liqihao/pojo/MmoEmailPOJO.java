package com.liqihao.pojo;

public class MmoEmailPOJO {
    private Integer id;

    private String title;

    private String context;

    private Integer fromRoleId;

    private Integer toRoleId;

    private Integer articleType;

    private Integer articleMessageId;

    private Integer articleNum;

    private Long createTime;

    private Boolean checked;

    private Boolean fromDelete;

    private Boolean toDelete;

    private Boolean isGet;

    private Integer equipmentId;

    private Integer money;

    private Boolean isGetMoney;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title == null ? null : title.trim();
    }

    public String getContext() {
        return context;
    }

    public void setContext(String context) {
        this.context = context == null ? null : context.trim();
    }

    public Integer getFromRoleId() {
        return fromRoleId;
    }

    public void setFromRoleId(Integer fromRoleId) {
        this.fromRoleId = fromRoleId;
    }

    public Integer getToRoleId() {
        return toRoleId;
    }

    public void setToRoleId(Integer toRoleId) {
        this.toRoleId = toRoleId;
    }

    public Integer getArticleType() {
        return articleType;
    }

    public void setArticleType(Integer articleType) {
        this.articleType = articleType;
    }

    public Integer getArticleMessageId() {
        return articleMessageId;
    }

    public void setArticleMessageId(Integer articleMessageId) {
        this.articleMessageId = articleMessageId;
    }

    public Integer getArticleNum() {
        return articleNum;
    }

    public void setArticleNum(Integer articleNum) {
        this.articleNum = articleNum;
    }

    public Long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }

    public Boolean getChecked() {
        return checked;
    }

    public void setChecked(Boolean checked) {
        this.checked = checked;
    }

    public Boolean getFromDelete() {
        return fromDelete;
    }

    public void setFromDelete(Boolean fromDelete) {
        this.fromDelete = fromDelete;
    }

    public Boolean getToDelete() {
        return toDelete;
    }

    public void setToDelete(Boolean toDelete) {
        this.toDelete = toDelete;
    }

    public Boolean getIsGet() {
        return isGet;
    }

    public void setIsGet(Boolean isGet) {
        this.isGet = isGet;
    }

    public Integer getEquipmentId() {
        return equipmentId;
    }

    public void setEquipmentId(Integer equipmentId) {
        this.equipmentId = equipmentId;
    }

    public Integer getMoney() {
        return money;
    }

    public void setMoney(Integer money) {
        this.money = money;
    }

    public Boolean getIsGetMoney() {
        return isGetMoney;
    }

    public void setIsGetMoney(Boolean isGetMoney) {
        this.isGetMoney = isGetMoney;
    }
}