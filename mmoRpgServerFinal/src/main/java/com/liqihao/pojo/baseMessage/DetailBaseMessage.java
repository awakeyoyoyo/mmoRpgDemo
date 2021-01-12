package com.liqihao.pojo.baseMessage;

/**
 * 基础信息类
 * @author lqhao
 */
public class DetailBaseMessage {
    private Integer bagSize;
    private Integer reduceDurability;
    private Integer teamApplyOrInviteSize;
    private Integer teamRoleSize;

    public Integer getTeamRoleSize() {
        return teamRoleSize;
    }

    public void setTeamRoleSize(Integer teamRoleSize) {
        this.teamRoleSize = teamRoleSize;
    }

    public Integer getTeamApplyOrInviteSize() {
        return teamApplyOrInviteSize;
    }

    public void setTeamApplyOrInviteSize(Integer teamApplyOrInviteSize) {
        this.teamApplyOrInviteSize = teamApplyOrInviteSize;
    }

    @Override
    public String toString() {
        return "BaseDetailMessage{" +
                "bagSize=" + bagSize +
                ", reduceDurability=" + reduceDurability +
                '}';
    }

    public Integer getBagSize() {
        return bagSize;
    }

    public void setBagSize(Integer bagSize) {
        this.bagSize = bagSize;
    }

    public Integer getReduceDurability() {
        return reduceDurability;
    }

    public void setReduceDurability(Integer reduceDurability) {
        this.reduceDurability = reduceDurability;
    }
}
