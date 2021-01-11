package com.liqihao.pojo.bean;

import com.liqihao.pojo.baseMessage.BufferMessage;

/**
 * buffer Bean
 * @author lqhao
 */
public class BufferBean {
    private Integer fromRoleId;
    private Integer toRoleId;
    private Long createTime;
    private Integer fromRoleType;
    private Integer toRoleType;
    private Integer BufferMessageId;

    public Integer getBufferMessageId() {
        return BufferMessageId;
    }

    public void setBufferMessageId(Integer bufferMessageId) {
        BufferMessageId = bufferMessageId;
    }

    public Integer getFromRoleType() {
        return fromRoleType;
    }

    public void setFromRoleType(Integer fromRoleType) {
        this.fromRoleType = fromRoleType;
    }

    public Integer getToRoleType() {
        return toRoleType;
    }

    public void setToRoleType(Integer toRoleType) {
        this.toRoleType = toRoleType;
    }

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
