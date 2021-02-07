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

    public Integer getTeamApplyOrInviteSize() {
        return teamApplyOrInviteSize;
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

    public Integer getReduceDurability() {
        return reduceDurability;
    }
}
