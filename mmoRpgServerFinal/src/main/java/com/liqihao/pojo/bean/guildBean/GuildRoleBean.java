package com.liqihao.pojo.bean.guildBean;

import com.liqihao.pojo.bean.roleBean.MmoSimpleRole;

/**
 * 公会成员记录bean
 * @author lqhao
 */
public class GuildRoleBean {
    private Integer id;

    private Integer guildId;

    private Integer roleId;

    private Integer guildPositionId;

    private Integer contribution;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getGuildId() {
        return guildId;
    }

    public void setGuildId(Integer guildId) {
        this.guildId = guildId;
    }

    public Integer getRoleId() {
        return roleId;
    }

    public void setRoleId(Integer roleId) {
        this.roleId = roleId;
    }

    public Integer getGuildPositionId() {
        return guildPositionId;
    }

    public void setGuildPositionId(Integer guildPositionId) {
        this.guildPositionId = guildPositionId;
    }

    public Integer getContribution() {
        return contribution;
    }

    public void setContribution(Integer contribution) {
        this.contribution = contribution;
    }
}
