package com.liqihao.netModule.team.request;

/**
 * 用户接受邀请or 队长同意玩家申请请求
 * @author lqhao
 */
public class EntryPeopleRequest {
    /**
     *  若当前channle的role与传入的roleId相等,则表示用户接受了该队伍的邀请
     *  若当前channle的role与传入的roleId不相等，则是队长同意该用户的申请，需要判断当前channel role是否为队长
     */
    Integer roleId;

    Integer teamId;
}
