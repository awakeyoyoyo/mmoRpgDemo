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

    public String getName() {
        return name;
    }

    public String getAuthorityIds() {
        return authorityIds;
    }
}
