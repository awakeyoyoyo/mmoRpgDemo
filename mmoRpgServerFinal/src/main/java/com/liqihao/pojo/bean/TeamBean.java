package com.liqihao.pojo.bean;

import com.liqihao.Cache.ChannelMessageCache;
import com.liqihao.commons.ConstantValue;
import com.liqihao.commons.NettyResponse;
import com.liqihao.commons.StateCode;
import com.liqihao.commons.enums.TeamApplyInviteCode;
import com.liqihao.provider.CopySceneProvider;
import com.liqihao.provider.TeamServiceProvider;
import io.netty.channel.Channel;
import org.apache.ibatis.annotations.Param;
import org.apache.poi.ss.formula.functions.T;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

/**
 * Team bean
 * @author lqhao
 */
public class TeamBean {
    private Integer teamId;
    private ConcurrentHashMap<Integer,MmoSimpleRole> mmoSimpleRolesMap;
    private Integer copySceneBeanId;
    private Integer leaderId;
    private Integer copySceneId;
    private ConcurrentLinkedQueue<TeamApplyOrInviteBean> teamApplyOrInviteBeans;
    private Integer teamApplyOrInviteSize;
    private Integer teamRoleSize;

    public Integer getTeamRoleSize() {
        return teamRoleSize;
    }

    public void setTeamRoleSize(Integer teamRoleSize) {
        this.teamRoleSize = teamRoleSize;
    }

    public ConcurrentLinkedQueue<TeamApplyOrInviteBean> getTeamApplyOrInviteBeans() {
        return teamApplyOrInviteBeans;
    }

    public void setTeamApplyOrInviteBeans(ConcurrentLinkedQueue<TeamApplyOrInviteBean> teamApplyOrInviteBeans) {
        this.teamApplyOrInviteBeans = teamApplyOrInviteBeans;
    }

    public Integer getTeamApplyOrInviteSize() {
        return teamApplyOrInviteSize;
    }

    public void setTeamApplyOrInviteSize(Integer teamApplyOrInviteSize) {
        this.teamApplyOrInviteSize = teamApplyOrInviteSize;
    }

    public Integer getCopySceneId() {
        return copySceneId;
    }

    public void setCopySceneId(Integer copySceneId) {
        this.copySceneId = copySceneId;
    }

    public ConcurrentHashMap<Integer, MmoSimpleRole> getMmoSimpleRolesMap() {
        return mmoSimpleRolesMap;
    }

    public void setMmoSimpleRolesMap(ConcurrentHashMap<Integer, MmoSimpleRole> mmoSimpleRolesMap) {
        this.mmoSimpleRolesMap = mmoSimpleRolesMap;
    }

    public Collection<MmoSimpleRole> getMmoSimpleRoles() {
        return mmoSimpleRolesMap.values();
    }



    public Integer getLeaderId() {
        return leaderId;
    }

    public void setLeaderId(Integer leaderId) {
        this.leaderId = leaderId;
    }

    public Integer getTeamId() {
        return teamId;
    }

    public void setTeamId(Integer teamId) {
        this.teamId = teamId;
    }


    public Integer getCopySceneBeanId() {
        return copySceneBeanId;
    }

    public void setCopySceneBeanId(Integer copySceneBeanId) {
        this.copySceneBeanId = copySceneBeanId;
    }
    /**
     *  队伍解散
     */
    public void breakUp() {
        Collection<MmoSimpleRole> mmoSimpleRoles= getMmoSimpleRoles();
        for (MmoSimpleRole role:mmoSimpleRoles){
            Channel c= ChannelMessageCache.getInstance().get(role.getId());
            role.setTeamId(null);
            //todo 广播队伍解散
//            c.writeAndFlush()
            role.setCopySceneId(null);
        }
        //副本解散
        if (getCopySceneBeanId()!=null) {
            CopySceneBean copySceneBean = CopySceneProvider.getCopySceneBeanById(getCopySceneBeanId());
            copySceneBean.end();
        }
    }

    /**
     * t人
     * @param roleId
     */
    public void banPeople(Integer roleId) {
        MmoSimpleRole mmoSimpleRole=mmoSimpleRolesMap.get(roleId);
        mmoSimpleRolesMap.remove(roleId);
        //判断退出队伍者是否在副本中
        checkInCopyScene(mmoSimpleRole);
        mmoSimpleRole.setTeamId(null);
        //TODO 发送信息给被t者

        //TODO 广播给队伍里面的人少了人
    }

    /**
     * 自动退队伍
     * @param roleId
     */
    public void exitPeople(Integer roleId){
        //判断退出队伍者是否是leader
        MmoSimpleRole mmoSimpleRole=mmoSimpleRolesMap.get(roleId);
        if (getLeaderId().equals(roleId)) {
            mmoSimpleRolesMap.remove(mmoSimpleRole.getId());
            if (mmoSimpleRolesMap.values().isEmpty()){
                //没人了
                //发送信息给退出队伍者 且队伍已经解散
                //TODO
                //判断是否在副本中
                checkInCopyScene(mmoSimpleRole);
                //解散队伍
                TeamServiceProvider.deleteTeamById(getTeamId());
                return;
            }else {
                //获取第一个,成员作为leader
                MmoSimpleRole nextLeader = mmoSimpleRolesMap.values().iterator().next();
                setLeaderId(nextLeader.getId());
            }
        }
        //判断退出队伍者是否在副本中
        checkInCopyScene(mmoSimpleRole);
        mmoSimpleRole.setTeamId(null);
        //TODO 发送信息给退出队伍者

        //TODO 广播队伍里面的人少了人
    }

    private void checkInCopyScene(MmoSimpleRole mmoSimpleRole){
        if (mmoSimpleRole.getCopySceneId()!=null&&mmoSimpleRole.getCopySceneId().equals(getCopySceneId())){
            //回到原来场景
            mmoSimpleRole.wentScene(mmoSimpleRole.getLastSceneId());
            mmoSimpleRole.setLastSceneId(null);
            mmoSimpleRole.setCopySceneId(null);
        }
    }
    public void addTeamApplyOrInviteBean(TeamApplyOrInviteBean teamApplyOrInviteBean) {
        //每次插入的时候删除过时的
        checkOutTime();
        //邀请的大小，先进先出咯
        if (teamApplyOrInviteBeans.size()>=teamApplyOrInviteSize){
            teamApplyOrInviteBeans.poll();
        }
        teamApplyOrInviteBeans.add(teamApplyOrInviteBean);
    }
    private void checkOutTime(){
        Iterator iterator=teamApplyOrInviteBeans.iterator();
        //每次插入都删除申请过时或者
        while (iterator.hasNext()){
            TeamApplyOrInviteBean bean= (TeamApplyOrInviteBean) iterator.next();
            if (bean.endTime<System.currentTimeMillis()){
                teamApplyOrInviteBeans.remove(bean);
            }
        }
    }
    /**
     * 邀请玩家进队伍
     */
    public void invitePeople(MmoSimpleRole mmoSimpleRole) {

        TeamApplyOrInviteBean teamApplyOrInviteBean=new TeamApplyOrInviteBean();
        teamApplyOrInviteBean.setCreateTime(System.currentTimeMillis());
        teamApplyOrInviteBean.setEndTime(System.currentTimeMillis()+5*1000*60);
        teamApplyOrInviteBean.setType(TeamApplyInviteCode.INVITE.getCode());
        teamApplyOrInviteBean.setRoleId(mmoSimpleRole.getId());
        //用户方面添加邀请
        mmoSimpleRole.addTeamApplyOrInviteBean(teamApplyOrInviteBean);
        //发送邀请给玩家 TODO
        //返回一个成功的响应给 邀请者
    }

    /**
     * 申请入队
     */
    public void applyTeam(MmoSimpleRole mmoSimpleRole) {
        TeamApplyOrInviteBean teamApplyOrInviteBean=new TeamApplyOrInviteBean();
        teamApplyOrInviteBean.setCreateTime(System.currentTimeMillis());
        teamApplyOrInviteBean.setEndTime(System.currentTimeMillis()+5*1000*60);
        teamApplyOrInviteBean.setType(TeamApplyInviteCode.APPLY.getCode());
        teamApplyOrInviteBean.setRoleId(mmoSimpleRole.getId());
        //队伍增加申请
        addTeamApplyOrInviteBean(teamApplyOrInviteBean);
        //发送邀请给玩家 TODO
        //返回一个成功的响应给 申请者
    }

    /**
     * 拒绝申请入队
     * @param
     */
    public void refuseApply(Integer roleId,Long createTime) {
        checkOutTime();
       Iterator iterator=teamApplyOrInviteBeans.iterator();
       TeamApplyOrInviteBean teamApplyOrInviteBean=null;
       while (iterator.hasNext()){
          teamApplyOrInviteBean= (TeamApplyOrInviteBean) iterator.next();
          if (teamApplyOrInviteBean.getRoleId().equals(roleId)
                  &&teamApplyOrInviteBean.getType().equals(TeamApplyInviteCode.APPLY.getCode())
          &&createTime.equals(teamApplyOrInviteBean.getCreateTime())){
              teamApplyOrInviteBeans.remove(teamApplyOrInviteBean);
              //todo 发送拒绝申请给role
              break;
          }
       }
    }
    /**
     * 获取申请入队的列表
     */
    public List<TeamApplyOrInviteBean> getTeamApplyBean(){
        checkOutTime();
        return teamApplyOrInviteBeans.stream().filter(e->e.getType().equals(TeamApplyInviteCode.APPLY)).collect(Collectors.toList());
    }

    /**
     * 增加队友
     * @param mmoSimpleRole
     */
    public void addRole(MmoSimpleRole mmoSimpleRole,Channel channel) {
        //判断队伍人数上限
        if (getTeamRoleSize()<=mmoSimpleRolesMap.size()){
            //队伍已经满了
            NettyResponse errotResponse=new NettyResponse(StateCode.FAIL, ConstantValue.FAIL_RESPONSE,"队伍已经满了".getBytes());
            channel.writeAndFlush(errotResponse);
            return;
        }
        mmoSimpleRolesMap.put(mmoSimpleRole.getId(),mmoSimpleRole);
        mmoSimpleRole.setTeamId(getTeamId());
        //todo 广播给各个队友
    }

    public TeamApplyOrInviteBean constainsInvite(Integer roleId, Long createTime) {
        checkOutTime();
        Iterator iterator=teamApplyOrInviteBeans.iterator();
        TeamApplyOrInviteBean teamApplyOrInviteBean=null;
        while (iterator.hasNext()){
            teamApplyOrInviteBean= (TeamApplyOrInviteBean) iterator.next();
            if (teamApplyOrInviteBean.getRoleId().equals(roleId)
                    &&teamApplyOrInviteBean.getType().equals(TeamApplyInviteCode.APPLY.getCode())
                    &&createTime.equals(teamApplyOrInviteBean.getCreateTime())){
                return teamApplyOrInviteBean;
            }
        }
        return null;
    }
}
