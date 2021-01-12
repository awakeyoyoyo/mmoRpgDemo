package com.liqihao.pojo.baseMessage;

/**
 * 职位信息读取
 * @author lqhao
 */
public class GuildPositionMessage extends BaseMessage{
    private Integer id;
    private String name;
    private String authorityIds;
    @Override
    public Integer getTheId() {
        return getId();
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

    public String getAuthorityIds() {
        return authorityIds;
    }

    public void setAuthorityIds(String authorityIds) {
        this.authorityIds = authorityIds;
    }
}
