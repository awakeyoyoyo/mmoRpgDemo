package com.liqihao.pojo.baseMessage;

/**
 * 公会基本配置信息
 * @author Administrator
 */
public class GuildBaseMessage {
    private Integer maxRoleNumber;
    private Integer maxWareHouseNumber;
    private Integer applyLastTime;

    public Integer getApplyLastTime() {
        return applyLastTime;
    }

    private void setApplyLastTime(Integer applyLastTime) {
        this.applyLastTime = applyLastTime;
    }

    public Integer getMaxRoleNumber() {
        return maxRoleNumber;
    }

    private void setMaxRoleNumber(Integer maxRoleNumber) {
        this.maxRoleNumber = maxRoleNumber;
    }

    public Integer getMaxWareHouseNumber() {
        return maxWareHouseNumber;
    }

    private void setMaxWareHouseNumber(Integer maxWareHouseNumber) {
        this.maxWareHouseNumber = maxWareHouseNumber;
    }
}
