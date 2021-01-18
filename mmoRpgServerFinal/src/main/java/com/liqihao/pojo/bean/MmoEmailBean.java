package com.liqihao.pojo.bean;

/**
 * 邮件实例bean
 * @author lqhao
 */
public class MmoEmailBean {
    private Integer id;

    private String title;

    private String context;

    private Integer fromRoleId;

    private Integer toRoleId;

    private Integer articleType;

    private Integer articleMessageId;

    private Integer articleNum;

    private Boolean hasArticle;

    private Long createTime;

    private Boolean checked;

    private Boolean fromDelete;

    private Boolean toDelete;

    private Boolean intoDataBase;

    private Integer equipmentId;
    /**
     * 是否已经被收取物品
     */
    private Boolean isGet=false;
    /**
     * 是否已经被收取物品
     */
    private Boolean isGetMoney=false;

    private Integer money;

    public Boolean getGetMoney() {
        return isGetMoney;
    }

    public void setGetMoney(Boolean getMoney) {
        isGetMoney = getMoney;
    }

    public Integer getMoney() {
        return money;
    }

    public void setMoney(Integer money) {
        this.money = money;
    }

    public Integer getEquipmentId() {
        return equipmentId;
    }

    public void setEquipmentId(Integer equipmentId) {
        this.equipmentId = equipmentId;
    }

    public Boolean getGet() {
        return isGet;
    }

    public void setGet(Boolean get) {
        isGet = get;
    }

    public Integer getArticleMessageId() {
        return articleMessageId;
    }

    public void setArticleMessageId(Integer articleMessageId) {
        this.articleMessageId = articleMessageId;
    }

    public Boolean getIntoDataBase() {
        return intoDataBase;
    }

    public void setIntoDataBase(Boolean intoDataBase) {
        this.intoDataBase = intoDataBase;
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

    public Boolean getHasArticle() {
        return hasArticle;
    }

    public void setHasArticle(Boolean hasArticle) {
        this.hasArticle = hasArticle;
    }

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
        this.title = title;
    }

    public String getContext() {
        return context;
    }

    public void setContext(String context) {
        this.context = context;
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
}
