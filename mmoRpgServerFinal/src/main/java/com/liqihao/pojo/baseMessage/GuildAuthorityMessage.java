package com.liqihao.pojo.baseMessage;

/**
 * 权限信息
 * @author lqhao
 */
public class GuildAuthorityMessage extends BaseMessage{
    private Integer id;
    private String description;

    @Override
    public Integer getTheId() {
        return getId();
    }

    public Integer getId() {
        return id;
    }

    private void setId(Integer id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    private void setDescription(String description) {
        this.description = description;
    }
}
