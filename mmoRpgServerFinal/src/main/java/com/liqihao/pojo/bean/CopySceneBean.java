package com.liqihao.pojo.bean;

import com.liqihao.Cache.ChannelMessageCache;
import com.liqihao.Cache.OnlineRoleMessageCache;
import com.liqihao.commons.ConstantValue;
import com.liqihao.commons.NettyResponse;
import com.liqihao.commons.StateCode;
import com.liqihao.commons.enums.CopySceneDeleteCauseCode;
import com.liqihao.commons.enums.RoleStatusCode;
import com.liqihao.commons.enums.RoleTypeCode;
import com.liqihao.pojo.baseMessage.CopySceneMessage;
import com.liqihao.protobufObject.CopySceneModel;
import com.liqihao.protobufObject.SceneModel;
import com.liqihao.provider.CopySceneProvider;
import com.liqihao.provider.TeamServiceProvider;
import com.liqihao.util.ScheduledThreadPoolUtil;
import io.netty.channel.Channel;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ScheduledFuture;


/**
 * 副本bean
 * @author lqhao
 */
public class CopySceneBean extends CopySceneMessage {
    private long createTime;
    private long endTime;
    private LinkedList<BossBean> bossBeans;
    private List<Role> roles;
    private Integer status;
    private Integer copySceneBeanId;
    private Integer teamId;
    private BossBean nowBoss;

    public BossBean getNowBoss() {
        return nowBoss;
    }

    public void setNowBoss(BossBean nowBoss) {
        this.nowBoss = nowBoss;
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

    public LinkedList<BossBean> getBossBeans() {
        return bossBeans;
    }

    public void setBossBeans(LinkedList<BossBean> bossBeans) {
        this.bossBeans = bossBeans;
    }


    public List<Role> getRoles() {
        return roles;
    }

    public void setRoles(List<Role> roles) {
        this.roles = roles;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    /**
     * 玩家退出副本
     * @param roleId
     */
    public void  peopleExit(Integer roleId) {
        Iterator iterator=roles.iterator();
        synchronized (roles) {
            while (iterator.hasNext()) {
                MmoSimpleRole mmoSimpleRole = (MmoSimpleRole) iterator.next();
                if (mmoSimpleRole.getId().equals(roleId)) {
                    //副本角色删除
                    roles.remove(mmoSimpleRole);
                    Channel c = ChannelMessageCache.getInstance().get(roleId);
                    //从副本回到原来场景
                    Integer nextSceneId=mmoSimpleRole.getLastSceneId();
                    mmoSimpleRole.setLastSceneId(null);
                    List<MmoSimpleRole> nextRoles=mmoSimpleRole.wentScene(nextSceneId);
                    //修改人物
                    mmoSimpleRole.setCopySceneId(null);
                    NettyResponse nettyResponse=new NettyResponse();
                    nettyResponse.setCmd(ConstantValue.WENT_RESPONSE);
                    nettyResponse.setStateCode(StateCode.SUCCESS);
                    SceneModel.SceneModelMessage.Builder builder=SceneModel.SceneModelMessage.newBuilder();
                    builder.setDataType(SceneModel.SceneModelMessage.DateType.WentResponse);
                    SceneModel.WentResponse.Builder wentResponseBuilder=SceneModel.WentResponse.newBuilder();
                    //simpleRole
                    List<SceneModel.RoleDTO> roleDTOS=new ArrayList<>();
                    for (MmoSimpleRole mmoRole :nextRoles){
                        SceneModel.RoleDTO.Builder msr=SceneModel.RoleDTO.newBuilder();
                        msr.setId(mmoRole.getId());
                        msr.setName(mmoRole.getName());
                        msr.setType(mmoRole.getType());
                        msr.setStatus(mmoRole.getStatus());
                        msr.setOnStatus(mmoRole.getOnStatus());
                        msr.setBlood(mmoRole.getHp());
                        msr.setNowBlood(mmoRole.getNowHp());
                        msr.setMp(mmoRole.getMp());
                        msr.setNowMp(mmoRole.getNowMp());
                        msr.setAttack(mmoRole.getAttack());
                        msr.setAttackAdd(mmoRole.getDamageAdd());
                        SceneModel.RoleDTO msrObject=msr.build();
                        roleDTOS.add(msrObject);
                    }
                    wentResponseBuilder.setSceneId(nextSceneId);
                    wentResponseBuilder.addAllRoleDTO(roleDTOS);
                    builder.setWentResponse(wentResponseBuilder.build());
                    byte[] data2=builder.build().toByteArray();
                    nettyResponse.setData(data2);
                    c.writeAndFlush(nettyResponse);
                    break;
                }
            }
        }
        //如果副本没人
        //copySceneProvider删除该副本
        if (roles.size()<=0) {
            CopySceneProvider.deleteNewCopySceneById(copySceneBeanId);
            MmoSimpleRole mmoSimpleRole= OnlineRoleMessageCache.getInstance().get(roleId);
            TeamBean teamBean= TeamServiceProvider.getTeamBeanByTeamId(mmoSimpleRole.getTeamId());
            teamBean.setCopySceneBeanId(null);
            teamBean.setCopySceneId(null);
            sendCopySceneDelete(teamBean,CopySceneDeleteCauseCode.NO_PEOPLE.getCode());
        }
    }


    /**
     *     副本结束
     */
    public void end(TeamBean teamBean,Integer cause) {
        Iterator iterator=roles.iterator();
        teamBean.setCopySceneBeanId(null);
        teamBean.setCopySceneId(null);
        while (iterator.hasNext()){
            MmoSimpleRole mmoSimpleRole= (MmoSimpleRole) iterator.next();
            //让用户回到原来的场景EE
            //副本角色删除
            roles.remove(mmoSimpleRole);
            Channel c = ChannelMessageCache.getInstance().get(mmoSimpleRole.getId());
            //从副本回到原来场景
            Integer nextSceneId=mmoSimpleRole.getLastSceneId();
            mmoSimpleRole.setLastSceneId(null);
            List<MmoSimpleRole> nextRoles=mmoSimpleRole.wentScene(nextSceneId);
            //修改人物
            mmoSimpleRole.setCopySceneId(null);
            NettyResponse nettyResponse=new NettyResponse();
            nettyResponse.setCmd(ConstantValue.WENT_RESPONSE);
            nettyResponse.setStateCode(StateCode.SUCCESS);
            SceneModel.SceneModelMessage.Builder builder=SceneModel.SceneModelMessage.newBuilder();
            builder.setDataType(SceneModel.SceneModelMessage.DateType.WentResponse);
            SceneModel.WentResponse.Builder wentResponsebuilder=SceneModel.WentResponse.newBuilder();
            //simpleRole
            List<SceneModel.RoleDTO> roleDTOS=new ArrayList<>();
            for (MmoSimpleRole mmoRole :nextRoles){
                SceneModel.RoleDTO.Builder msr=SceneModel.RoleDTO.newBuilder();
                msr.setId(mmoRole.getId());
                msr.setName(mmoRole.getName());
                msr.setType(mmoRole.getType());
                msr.setStatus(mmoRole.getStatus());
                msr.setOnStatus(mmoRole.getOnStatus());
                msr.setBlood(mmoRole.getHp());
                msr.setNowBlood(mmoRole.getNowHp());
                msr.setMp(mmoRole.getMp());
                msr.setNowMp(mmoRole.getNowMp());
                msr.setAttack(mmoRole.getAttack());
                msr.setAttackAdd(mmoRole.getDamageAdd());
                SceneModel.RoleDTO msrobject=msr.build();
                roleDTOS.add(msrobject);
            }
            wentResponsebuilder.setSceneId(nextSceneId);
            wentResponsebuilder.addAllRoleDTO(roleDTOS);
            builder.setWentResponse(wentResponsebuilder.build());
            byte[] data2=builder.build().toByteArray();
            nettyResponse.setData(data2);
            c.writeAndFlush(nettyResponse);
        }
        //copySceneProvider删除该副本
        CopySceneProvider.deleteNewCopySceneById(getCopySceneBeanId());
        //定时任务取消
        ScheduledFuture<?> t=ScheduledThreadPoolUtil.getCopySceneTaskMap().get(getCopySceneBeanId());
        if (t!=null){
            t.cancel(false);
            ScheduledThreadPoolUtil.getCopySceneTaskMap().remove(getCopySceneBeanId());
        }
        //广播给队伍中的人 副本解散了
        sendCopySceneDelete(teamBean,cause);
    }

    /**
     * 挑战成功
     */
    public void changeResult(TeamBean teamBean){
        //将副本中的人物全部状态改为存活
        for (Role role:roles) {
            if (role.getType().equals(RoleTypeCode.PLAYER.getCode())) {
                role.setStatus(RoleStatusCode.ALIVE.getCode());
                role.setNowHp(role.getHp());
            }
        }
        //副本解散
        end(teamBean,CopySceneDeleteCauseCode.SUCCESS.getCode());
        // 广播队伍副本挑战成功
        NettyResponse nettyResponse=new NettyResponse();
        nettyResponse.setCmd(ConstantValue.CHANGE_SUCCESS_RESPONSE);
        nettyResponse.setStateCode(StateCode.SUCCESS);
        CopySceneModel.CopySceneModelMessage.Builder builder=CopySceneModel.CopySceneModelMessage.newBuilder();
        builder.setDataType(CopySceneModel.CopySceneModelMessage.DateType.ChangeSuccessResponse);
        builder.setChangeSuccessResponse(CopySceneModel.ChangeSuccessResponse.newBuilder().build());
        nettyResponse.setData(builder.build().toByteArray());
        for (MmoSimpleRole role:teamBean.getMmoSimpleRoles()) {
            Channel c=ChannelMessageCache.getInstance().get(role.getId());
            if (c!=null) {
                c.writeAndFlush(nettyResponse);
            }
        }
    }
    /**
     * 挑战失败-人物全部死亡
     */
    public void changePeopleDie(TeamBean teamBean){
        //将副本中的人物全部状态改为存活
        for (Role role:roles) {
            role.setStatus(RoleStatusCode.ALIVE.getCode());
            role.setNowHp(role.getHp());
        }
        //副本解散
        end(teamBean,CopySceneDeleteCauseCode.PEOPLE_DIE.getCode());

        // 广播队伍副本挑战失败
        NettyResponse nettyResponse=new NettyResponse();
        nettyResponse.setCmd(ConstantValue.CHANGE_FAIL_RESPONSE);
        nettyResponse.setStateCode(StateCode.SUCCESS);
        CopySceneModel.CopySceneModelMessage.Builder builder=CopySceneModel.CopySceneModelMessage.newBuilder();
        builder.setDataType(CopySceneModel.CopySceneModelMessage.DateType.ChangeFailResponse);
        builder.setChangeFailResponse(CopySceneModel.ChangeFailResponse.newBuilder().setCause(CopySceneDeleteCauseCode.PEOPLE_DIE.getCode()).build());
        nettyResponse.setData(builder.build().toByteArray());
        for (MmoSimpleRole role:teamBean.getMmoSimpleRoles()) {
            Channel c=ChannelMessageCache.getInstance().get(role.getId());
            if (c!=null) {
                c.writeAndFlush(nettyResponse);
            }
        }
    }
    /**
     * 挑战失败-时间过时
     */
    public void changeFailTimeOut(TeamBean teamBean){
        //将副本中的人物全部状态改为存活
        for (Role role:roles) {
            role.setStatus(RoleStatusCode.ALIVE.getCode());
            role.setNowHp(role.getHp());
        }
        //副本解散
        end(teamBean,CopySceneDeleteCauseCode.TIMEOUT.getCode());
        //广播队伍副本挑战失败
        NettyResponse nettyResponse=new NettyResponse();
        nettyResponse.setCmd(ConstantValue.CHANGE_FAIL_RESPONSE);
        nettyResponse.setStateCode(StateCode.SUCCESS);
        CopySceneModel.CopySceneModelMessage.Builder builder=CopySceneModel.CopySceneModelMessage.newBuilder();
        builder.setDataType(CopySceneModel.CopySceneModelMessage.DateType.ChangeFailResponse);
        builder.setChangeFailResponse(CopySceneModel.ChangeFailResponse.newBuilder().setCause(CopySceneDeleteCauseCode.TIMEOUT.getCode()).build());
        nettyResponse.setData(builder.build().toByteArray());
        for (MmoSimpleRole role:teamBean.getMmoSimpleRoles()) {
            Channel c=ChannelMessageCache.getInstance().get(role.getId());
            if (c!=null) {
                c.writeAndFlush(nettyResponse);
            }
        }
    }
    public void sendCopySceneDelete(TeamBean teamBean,Integer cause){
        NettyResponse nettyResponse=new NettyResponse();
        nettyResponse.setCmd(ConstantValue.COPY_SCENE_FINISH_RESPONSE);
        nettyResponse.setStateCode(StateCode.SUCCESS);
        CopySceneModel.CopySceneModelMessage.Builder builder=CopySceneModel.CopySceneModelMessage.newBuilder();
        builder.setDataType(CopySceneModel.CopySceneModelMessage.DateType.CopySceneDeleteResponse);
        builder.setCopySceneDeleteResponse(CopySceneModel.CopySceneDeleteResponse.newBuilder()
                .setCopySceneId(getId()).setCause(cause).build());
        nettyResponse.setData(builder.build().toByteArray());
        for (MmoSimpleRole role:teamBean.getMmoSimpleRoles()) {
            Channel c=ChannelMessageCache.getInstance().get(role.getId());
            if (c!=null) {
                c.writeAndFlush(nettyResponse);
            }
        }
    }

    public void bossComeOrFinish() {
        if (bossBeans.size()>0){
            BossBean bossBean=bossBeans.pop();
            setNowBoss(bossBean);
            //发信息第二个boss出现
        }else{
            // 挑战成功
            TeamBean teamBean= TeamServiceProvider.getTeamBeanByTeamId(getTeamId());
            changeResult(teamBean);
        }
    }
}
