package com.liqihao.pojo.bean;

import com.liqihao.Cache.ChannelMessageCache;
import com.liqihao.commons.ConstantValue;
import com.liqihao.commons.NettyResponse;
import com.liqihao.commons.StateCode;
import com.liqihao.commons.enums.CopySceneDeleteCauseCode;
import com.liqihao.commons.enums.TeamApplyInviteCode;
import com.liqihao.protobufObject.TeamModel;
import com.liqihao.provider.CopySceneProvider;
import com.liqihao.provider.TeamServiceProvider;
import io.netty.channel.Channel;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Collectors;

/**
 * Team bean
 * @author lqhao
 */
public class TeamBean {
    private Integer teamId;
    private String teamName;
    private ConcurrentHashMap<Integer,MmoSimpleRole> mmoSimpleRolesMap;
    private Integer leaderId;
    private Integer copySceneId;
    private Integer copySceneBeanId;
    private ConcurrentLinkedQueue<TeamApplyOrInviteBean> teamApplyOrInviteBeans;
    private Integer teamApplyOrInviteSize;
    private Integer teamRoleSize;

    public String getTeamName() {
        return teamName;
    }

    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }

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
        TeamModel.TeamModelMessage.Builder teamMessageBuilder=TeamModel.TeamModelMessage.newBuilder();
        teamMessageBuilder.setDataType(TeamModel.TeamModelMessage.DateType.DeleteTeamResponse);
        teamMessageBuilder.setDeleteTeamResponse(TeamModel.DeleteTeamResponse.newBuilder().build());
        NettyResponse nettyResponse=new NettyResponse();
        nettyResponse.setStateCode(StateCode.SUCCESS);
        nettyResponse.setCmd(ConstantValue.DELETE_TEAM_RESPONSE);
        nettyResponse.setData(teamMessageBuilder.build().toByteArray());
        for (MmoSimpleRole role:mmoSimpleRoles){
            Channel c= ChannelMessageCache.getInstance().get(role.getId());
            role.setTeamId(null);
            // 广播队伍解散
            c.writeAndFlush(nettyResponse);
            role.setCopySceneId(null);
        }
        //副本解散
        if (getCopySceneBeanId()!=null) {
            CopySceneBean copySceneBean = CopySceneProvider.getCopySceneBeanById(getCopySceneBeanId());
            copySceneBean.end(this, CopySceneDeleteCauseCode.TEAM_END.getCode());
        }
        TeamServiceProvider.deleteTeamById(teamId);
    }

    /**
     * t人
     * @param roleId
     */
    public void banPeople(Integer roleId) {
        MmoSimpleRole mmoSimpleRole=mmoSimpleRolesMap.get(roleId);
        //队伍中删除该玩家
        mmoSimpleRolesMap.remove(roleId);
        //判断退出队伍者是否在副本中
        checkInCopyScene(mmoSimpleRole);
        //玩家队伍id设置为空
        mmoSimpleRole.setTeamId(null);
        // 发送信息给被t者
        TeamModel.TeamModelMessage.Builder teamMessageBuilder=TeamModel.TeamModelMessage.newBuilder();
        teamMessageBuilder.setDataType(TeamModel.TeamModelMessage.DateType.BanPeopleResponse);
        teamMessageBuilder.setBanPeopleResponse(TeamModel.BanPeopleResponse.newBuilder().build());
        NettyResponse nettyResponse=new NettyResponse();
        nettyResponse.setStateCode(StateCode.SUCCESS);
        nettyResponse.setCmd(ConstantValue.BAN_PEOPLE_RESPONSE);
        nettyResponse.setData(teamMessageBuilder.build().toByteArray());
        Channel ccc=ChannelMessageCache.getInstance().get(mmoSimpleRole.getId());
        if (ccc!=null){
            ccc.writeAndFlush(nettyResponse);
        }
        // 广播给队伍里面的人少了人
        exitTeamNotification(mmoSimpleRole);
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
            checkInCopyScene(mmoSimpleRole);
            mmoSimpleRole.setTeamId(null);
            if (mmoSimpleRolesMap.values().isEmpty()){
                //解散队伍
                TeamServiceProvider.deleteTeamById(getTeamId());
                return;
            }else {
                //获取第一个,成员作为leader
                MmoSimpleRole nextLeader = mmoSimpleRolesMap.values().iterator().next();
                setLeaderId(nextLeader.getId());
                //成为leader信息
                TeamModel.TeamModelMessage.Builder teamMessageBuilder=TeamModel.TeamModelMessage.newBuilder();
                teamMessageBuilder.setDataType(TeamModel.TeamModelMessage.DateType.LeaderTeamResponse);
                teamMessageBuilder.setLeaderTeamResponse(TeamModel.LeaderTeamResponse.newBuilder().setTeamId(teamId).setTeamName(teamName).build());
                NettyResponse nettyResponse=new NettyResponse();
                nettyResponse.setStateCode(StateCode.SUCCESS);
                nettyResponse.setCmd(ConstantValue.LEADER_TEAM_RESPONSE);
                nettyResponse.setData(teamMessageBuilder.build().toByteArray());
                Channel ccc=ChannelMessageCache.getInstance().get(nextLeader.getId());
                if (ccc!=null){
                    ccc.writeAndFlush(nettyResponse);
                }
                exitTeamNotification(mmoSimpleRole);
                return;
            }
        }
        mmoSimpleRolesMap.remove(mmoSimpleRole.getId());
        //判断退出队伍者是否在副本中
        checkInCopyScene(mmoSimpleRole);
        mmoSimpleRole.setTeamId(null);
        exitTeamNotification(mmoSimpleRole);
    }

    /**
     * 检查是否在副本中
     * @param mmoSimpleRole
     */
    private void checkInCopyScene(MmoSimpleRole mmoSimpleRole){
        if (mmoSimpleRole.getCopySceneId()!=null&&mmoSimpleRole.getCopySceneId().equals(getCopySceneId())){
            //回到原来场景
            CopySceneBean copySceneBean=CopySceneProvider.getCopySceneBeanById(getCopySceneBeanId());
            copySceneBean.peopleExit(mmoSimpleRole.getId());
        }
    }

    /**
     * 增加队伍申请
     * @param teamApplyOrInviteBean
     */
    public void addTeamApplyOrInviteBean(TeamApplyOrInviteBean teamApplyOrInviteBean) {
        //每次插入的时候删除过时的
        checkOutTime();
        //邀请的大小，先进先出咯
        if (teamApplyOrInviteBeans.size()>=teamApplyOrInviteSize){
            teamApplyOrInviteBeans.poll();
        }
        teamApplyOrInviteBeans.add(teamApplyOrInviteBean);
    }

    /**
     * 检查申请是否过时
     */
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
    public TeamApplyOrInviteBean invitePeople(MmoSimpleRole mmoSimpleRole) {

        TeamApplyOrInviteBean teamApplyOrInviteBean=new TeamApplyOrInviteBean();
        teamApplyOrInviteBean.setCreateTime(System.currentTimeMillis());
        teamApplyOrInviteBean.setEndTime(System.currentTimeMillis()+5*1000*60);
        teamApplyOrInviteBean.setType(TeamApplyInviteCode.INVITE.getCode());
        teamApplyOrInviteBean.setRoleId(mmoSimpleRole.getId());
        teamApplyOrInviteBean.setTeamId(teamId);
        //用户方面添加邀请
        mmoSimpleRole.addTeamApplyOrInviteBean(teamApplyOrInviteBean);
        return teamApplyOrInviteBean;
    }

    /**
     * 申请入队
     */
    public TeamApplyOrInviteBean applyTeam(MmoSimpleRole mmoSimpleRole) {
        TeamApplyOrInviteBean t;
        //判断队伍申请之前还有无该用户申请 有就删除
        Iterator iterator=teamApplyOrInviteBeans.iterator();
        while (iterator.hasNext()){
            t= (TeamApplyOrInviteBean) iterator.next();
            if (t.getRoleId().equals(mmoSimpleRole.getId())
                    &&t.getType().equals(TeamApplyInviteCode.APPLY.getCode())){
                teamApplyOrInviteBeans.remove(t);
            }
        }
        //队伍增加申请
        TeamApplyOrInviteBean teamApplyOrInviteBean=new TeamApplyOrInviteBean();
        addTeamApplyOrInviteBean(teamApplyOrInviteBean);
        teamApplyOrInviteBean.setCreateTime(System.currentTimeMillis());
        teamApplyOrInviteBean.setEndTime(System.currentTimeMillis()+5*1000*60);
        teamApplyOrInviteBean.setType(TeamApplyInviteCode.APPLY.getCode());
        teamApplyOrInviteBean.setRoleId(mmoSimpleRole.getId());
        teamApplyOrInviteBean.setTeamId(teamId);
        return  teamApplyOrInviteBean;
    }

    /**
     * 拒绝申请入队
     * @param
     */
    public TeamApplyOrInviteBean refuseApply(Integer roleId) {
        checkOutTime();
       Iterator iterator=teamApplyOrInviteBeans.iterator();
       TeamApplyOrInviteBean teamApplyOrInviteBean=null;
       while (iterator.hasNext()){
          teamApplyOrInviteBean= (TeamApplyOrInviteBean) iterator.next();
          if (teamApplyOrInviteBean.getRoleId().equals(roleId)
                  &&teamApplyOrInviteBean.getType().equals(TeamApplyInviteCode.APPLY.getCode())){
              teamApplyOrInviteBeans.remove(teamApplyOrInviteBean);
              return teamApplyOrInviteBean;
          }
       }
       return null;
    }
    /**
     * 获取申请入队的列表
     */
    public List<TeamApplyOrInviteBean> getTeamApplyBean(){
        checkOutTime();
        return teamApplyOrInviteBeans.stream().filter(e->e.getType().equals(TeamApplyInviteCode.APPLY.getCode())).collect(Collectors.toList());
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
        // 广播给各个队友
        TeamModel.RoleDto roleDto=TeamModel.RoleDto.newBuilder().setId(mmoSimpleRole.getId()).setHp(mmoSimpleRole.getHp())
                .setMp(mmoSimpleRole.getMp()).setName(mmoSimpleRole.getName()).setNowHp(mmoSimpleRole.getNowHp())
                .setNowMP(mmoSimpleRole.getNowMp()).setTeamId(mmoSimpleRole.getTeamId()).build();
        TeamModel.EntryPeopleResponse entryPeopleResponse=TeamModel.EntryPeopleResponse.newBuilder().setRoleDto(roleDto).build();
        TeamModel.TeamModelMessage.Builder teamMessageBuilder=TeamModel.TeamModelMessage.newBuilder();
        teamMessageBuilder.setDataType(TeamModel.TeamModelMessage.DateType.EntryPeopleResponse);
        teamMessageBuilder.setEntryPeopleResponse(entryPeopleResponse);
        NettyResponse nettyResponse=new NettyResponse();
        nettyResponse.setStateCode(StateCode.SUCCESS);
        nettyResponse.setCmd(ConstantValue.ENTRY_PEOPLE_RESPONSE);
        nettyResponse.setData(teamMessageBuilder.build().toByteArray());
        for (MmoSimpleRole m:mmoSimpleRolesMap.values()) {
            Channel c=ChannelMessageCache.getInstance().get(m.getId());
            if (c!=null){
                c.writeAndFlush(nettyResponse);
            }
        }
    }

    /**
     * 是否包含某申请
     * @param roleId
     * @return
     */
    public TeamApplyOrInviteBean containsInvite(Integer roleId) {
        checkOutTime();
        Iterator iterator=teamApplyOrInviteBeans.iterator();
        TeamApplyOrInviteBean teamApplyOrInviteBean=null;
        while (iterator.hasNext()){
            teamApplyOrInviteBean= (TeamApplyOrInviteBean) iterator.next();
            if (teamApplyOrInviteBean.getRoleId().equals(roleId)
                    &&teamApplyOrInviteBean.getType().equals(TeamApplyInviteCode.APPLY.getCode())
                    ){
                return teamApplyOrInviteBean;
            }
        }
        return teamApplyOrInviteBean;
    }

    /**
     * 离开队伍通知
     * @param mmoSimpleRole
     */
    private void exitTeamNotification(MmoSimpleRole mmoSimpleRole){
        TeamModel.RoleDto roleDto=TeamModel.RoleDto.newBuilder().setId(mmoSimpleRole.getId()).setHp(mmoSimpleRole.getHp())
                .setMp(mmoSimpleRole.getMp()).setName(mmoSimpleRole.getName()).setNowHp(mmoSimpleRole.getNowHp())
                .setNowMP(mmoSimpleRole.getNowMp()).setTeamId(-1).build();
        TeamModel.ExitTeamResponse exitTeamResponse=TeamModel.ExitTeamResponse.newBuilder().setRoleDto(roleDto).build();
        TeamModel.TeamModelMessage.Builder teamMessageBuilder=TeamModel.TeamModelMessage.newBuilder();
        teamMessageBuilder.setDataType(TeamModel.TeamModelMessage.DateType.ExitTeamResponse);
        teamMessageBuilder.setExitTeamResponse(exitTeamResponse);
        NettyResponse nettyResponse=new NettyResponse();
        nettyResponse.setStateCode(StateCode.SUCCESS);
        nettyResponse.setCmd(ConstantValue.EXIT_TEAM_RESPONSE);
        nettyResponse.setData(teamMessageBuilder.build().toByteArray());
        // 发送信息给退出队伍者
        Channel c=ChannelMessageCache.getInstance().get(mmoSimpleRole.getId());
        c.writeAndFlush(nettyResponse);
        // 广播队伍里面的人少了人
        for (MmoSimpleRole m:mmoSimpleRolesMap.values()) {
            c=ChannelMessageCache.getInstance().get(m.getId());
            c.writeAndFlush(nettyResponse);
        }
    }

    /**
     * 是否是相同场景
     * @return
     */
    public boolean inSameScene() {
        Integer sceneId=mmoSimpleRolesMap.get(leaderId).getMmoSceneId();
        for (MmoSimpleRole m:mmoSimpleRolesMap.values()) {
            if (!m.getMmoSceneId().equals(sceneId)){
                return false;
            }
        }
        return true;
    }
}
