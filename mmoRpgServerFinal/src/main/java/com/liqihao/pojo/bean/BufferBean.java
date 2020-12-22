package com.liqihao.pojo.bean;

import com.liqihao.pojo.baseMessage.BufferMessage;

/**
 * buffer Bean
 * @author lqhao
 */
public class BufferBean extends BufferMessage {
    private Integer fromRoleId;
    private Integer toRoleId;
    private Long createTime;

    public Long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
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

}
