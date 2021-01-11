package com.liqihao.pojo.dto;

import java.util.List;

/**
 * 副本bean传输类
 * @author lqhao
 */
public class CopySceneBeanDto {
    private long createTime;
    private long endTime;
    private List<BossBeanDto> bossBeans;
    private List<RoleDto> roleDto;
    private Integer status;
    private Integer copySceneBeanId;
    private Integer copySceneId;

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public List<BossBeanDto> getBossBeans() {
        return bossBeans;
    }

    public void setBossBeans(List<BossBeanDto> bossBeans) {
        this.bossBeans = bossBeans;
    }

    public List<RoleDto> getRoleDto() {
        return roleDto;
    }

    public void setRoleDto(List<RoleDto> roleDto) {
        this.roleDto = roleDto;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getCopySceneBeanId() {
        return copySceneBeanId;
    }

    public void setCopySceneBeanId(Integer copySceneBeanId) {
        this.copySceneBeanId = copySceneBeanId;
    }

    public Integer getCopySceneId() {
        return copySceneId;
    }

    public void setCopySceneId(Integer copySceneId) {
        this.copySceneId = copySceneId;
    }
}
