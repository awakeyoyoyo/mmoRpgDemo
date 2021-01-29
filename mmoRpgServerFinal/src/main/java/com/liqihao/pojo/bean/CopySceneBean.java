package com.liqihao.pojo.bean;

import com.liqihao.Cache.ChannelMessageCache;
import com.liqihao.Cache.CopySceneMessageCache;
import com.liqihao.Cache.OnlineRoleMessageCache;
import com.liqihao.commons.ConstantValue;
import com.liqihao.commons.NettyResponse;
import com.liqihao.commons.RpgServerException;
import com.liqihao.commons.StateCode;
import com.liqihao.commons.enums.*;
import com.liqihao.pojo.baseMessage.CopySceneMessage;
import com.liqihao.pojo.bean.articleBean.Article;
import com.liqihao.pojo.bean.articleBean.EquipmentBean;
import com.liqihao.pojo.bean.articleBean.MedicineBean;
import com.liqihao.pojo.bean.roleBean.BossBean;
import com.liqihao.pojo.bean.roleBean.MmoHelperBean;
import com.liqihao.pojo.bean.roleBean.MmoSimpleRole;
import com.liqihao.pojo.bean.roleBean.Role;
import com.liqihao.pojo.bean.taskBean.copySceneSuccessTask.CopySceneTaskAction;
import com.liqihao.pojo.bean.taskBean.killTask.KillTaskAction;
import com.liqihao.pojo.bean.teamBean.TeamBean;
import com.liqihao.protobufObject.CopySceneModel;
import com.liqihao.protobufObject.SceneModel;
import com.liqihao.provider.ArticleServiceProvider;
import com.liqihao.provider.CopySceneProvider;
import com.liqihao.provider.TaskServiceProvider;
import com.liqihao.provider.TeamServiceProvider;
import com.liqihao.util.CommonsUtil;
import com.liqihao.util.DbUtil;
import com.liqihao.util.LogicThreadPool;
import com.liqihao.util.ScheduledThreadPoolUtil;
import io.netty.channel.Channel;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.atomic.AtomicInteger;


/**
 * 副本bean
 * @author lqhao
 */
public class CopySceneBean{
    /**
     * 副本基本信息Id
     */
    private Integer copySceneMessageId;

    /**
     * 开始时间
     */
    private long createTime;
    /**
     * 结束时间
     */
    private long endTime;
    /**
     * BOSS列表
     */
    private LinkedList<BossBean> bossBeans;
    /**
     * 副本角色包括 召唤兽
     */
    private List<Role> roles;
    /**
     * 副本状态
     */
    private Integer status;
    /**
     * 副本实例id
     */
    private Integer copySceneBeanId;
    /**
     * 副本对应队伍id
     */
    private Integer teamId;
    /**
     * 当前boss
     */
    private BossBean nowBoss;
    /**
     * 地面掉落物品
     */
    private ConcurrentHashMap<Integer, Article> articlesMap=new ConcurrentHashMap<>();
    /**
     *
     * 掉落物品的下标
     */
    private AtomicInteger floorArticleIdAuto=new AtomicInteger(0);

    public Integer getCopySceneMessageId() {
        return copySceneMessageId;
    }
    public void setCopySceneMessageId(Integer copySceneMessageId) {
        this.copySceneMessageId = copySceneMessageId;
    }

    public Integer getFloorIndex(){
        return floorArticleIdAuto.incrementAndGet();
    }

    public ConcurrentHashMap<Integer, Article> getArticlesMap() {
        return articlesMap;
    }

    public void setArticlesMap(ConcurrentHashMap<Integer, Article> articlesMap) {
        this.articlesMap = articlesMap;
    }


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
        Iterator<Role> iterator=roles.iterator();
        synchronized (roles) {
            while (iterator.hasNext()) {
                Role role = iterator.next();
                if (role.getType().equals(RoleTypeCode.PLAYER.getCode())) {
                    MmoSimpleRole mmoSimpleRole= (MmoSimpleRole) role;
                    if (mmoSimpleRole.getId().equals(roleId)) {
                        //副本角色删除
                        if (mmoSimpleRole.getProfessionId().equals(ProfessionCode.TRAINER.getCode())){
                            //召唤兽也离开
                            MmoHelperBean helperBean=mmoSimpleRole.getMmoHelperBean();
                            if (helperBean!=null){
                                roles.remove(helperBean);
                            }
                        }
                        roles.remove(mmoSimpleRole);
                        Channel c = ChannelMessageCache.getInstance().get(roleId);
                        //从副本回到原来场景
                        Integer nextSceneId = mmoSimpleRole.getLastSceneId();
                        mmoSimpleRole.setLastSceneId(null);
                        List<Role> nextRoles = mmoSimpleRole.wentScene(nextSceneId);
                        //修改人物
                        mmoSimpleRole.setCopySceneId(null);
                        NettyResponse nettyResponse = new NettyResponse();
                        nettyResponse.setCmd(ConstantValue.WENT_RESPONSE);
                        nettyResponse.setStateCode(StateCode.SUCCESS);

                        SceneModel.SceneModelMessage.Builder builder = SceneModel.SceneModelMessage.newBuilder();
                        builder.setDataType(SceneModel.SceneModelMessage.DateType.WentResponse);
                        SceneModel.WentResponse.Builder wentResponseBuilder = SceneModel.WentResponse.newBuilder();
                        //simpleRole
                        List<SceneModel.RoleDTO> roleDTOS = new ArrayList<>();
                        for (Role mmoRole : nextRoles) {
                            SceneModel.RoleDTO.Builder msr = CommonsUtil.roleToSceneModelRoleDto(mmoRole);
                            SceneModel.RoleDTO msrObject = msr.build();
                            roleDTOS.add(msrObject);
                        }
                        wentResponseBuilder.setSceneId(nextSceneId);
                        wentResponseBuilder.addAllRoleDTO(roleDTOS);
                        builder.setWentResponse(wentResponseBuilder.build());
                        byte[] data2 = builder.build().toByteArray();
                        nettyResponse.setData(data2);
                        if (c != null) {
                            c.writeAndFlush(nettyResponse);
                        }
                        break;
                    }
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
     * 副本结束
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
            List<Role> nextRoles=mmoSimpleRole.wentScene(nextSceneId);
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
            for (Role mmoRole :nextRoles){
                SceneModel.RoleDTO.Builder msr=CommonsUtil.roleToSceneModelRoleDto(mmoRole);
                SceneModel.RoleDTO msrObject=msr.build();
                roleDTOS.add(msrObject);
            }
            wentResponseBuilder.setSceneId(nextSceneId);
            wentResponseBuilder.addAllRoleDTO(roleDTOS);
            builder.setWentResponse(wentResponseBuilder.build());
            byte[] data2=builder.build().toByteArray();
            nettyResponse.setData(data2);
            c.writeAndFlush(nettyResponse);
        }
        //copySceneProvider删除该副本
        CopySceneProvider.deleteNewCopySceneById(getCopySceneBeanId());
        //广播给队伍中的人 副本解散了
        sendCopySceneDelete(teamBean,cause);
    }

    /**
     * 挑战成功
     */
    public void changeResult(TeamBean teamBean) {
        CopySceneMessage copySceneMessage= CopySceneMessageCache.getInstance().get(getCopySceneMessageId());
        //将副本中的人物全部状态改为存活
        for (Role role:roles) {
            //增加任务
            if (role.getType().equals(RoleTypeCode.PLAYER.getCode())) {
                MmoSimpleRole mmoSimpleRole= (MmoSimpleRole) role;
                CopySceneTaskAction copySceneTaskAction = new CopySceneTaskAction();
                copySceneTaskAction.setCopySceneId(copySceneBeanId);
                copySceneTaskAction.setTaskTargetType(TaskTargetTypeCode.COPY_SCENE.getCode());
                mmoSimpleRole.getTaskManager().handler(copySceneTaskAction, mmoSimpleRole);
            }
            if (role.getType().equals(RoleTypeCode.PLAYER.getCode())) {
                role.setStatus(RoleStatusCode.ALIVE.getCode());
                role.setNowHp(role.getHp());
            }
        }
        //发奖励了
        List<MedicineBean> medicineBeans= ArticleServiceProvider.productMedicineToCopyScene(this,CommonsUtil.split(copySceneMessage.getMedicineIds()));
        List<EquipmentBean> equipmentBeans= ArticleServiceProvider.productEquipmentToCopyScene(this,CommonsUtil.split(copySceneMessage.getEquipmentIds()));
        if (medicineBeans.size()>0) {
            for (MedicineBean m:medicineBeans) {
                articlesMap.put(m.getFloorIndex(),m);
            }
        }
        if (equipmentBeans.size()>0){
            for (EquipmentBean e:equipmentBeans) {
                articlesMap.put(e.getFloorIndex(),e);
            }
        }
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
            /**
             * 队伍中玩家全部加金币 上锁，防止购买物品与副本挑战成功获取金币冲突
             */
            Integer money=copySceneMessage.getMoney();
            Integer index=CommonsUtil.getIndexByChannel(role.getChannel());
            LogicThreadPool.getInstance().execute(() -> {
                role.setMoney(role.getMoney() + money);
                DbUtil.updateRole(role);
            }, index);

            if (c!=null) {
                c.writeAndFlush(nettyResponse);
            }
        }
    }

    /**
     * 挑战失败-人物全部死亡
     */
    public void changeFailPeopleDie(TeamBean teamBean){
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
        //判断副本状态
        if (getStatus().equals(CopySceneStatusCode.FINISH.getCode())){
            return;
        }
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

    /**
     * 发送副本解散消息
     * @param teamBean
     * @param cause
     */
    public void sendCopySceneDelete(TeamBean teamBean,Integer cause){
        NettyResponse nettyResponse=new NettyResponse();
        nettyResponse.setCmd(ConstantValue.COPY_SCENE_FINISH_RESPONSE);
        nettyResponse.setStateCode(StateCode.SUCCESS);
        CopySceneModel.CopySceneModelMessage.Builder builder=CopySceneModel.CopySceneModelMessage.newBuilder();
        builder.setDataType(CopySceneModel.CopySceneModelMessage.DateType.CopySceneDeleteResponse);
        builder.setCopySceneDeleteResponse(CopySceneModel.CopySceneDeleteResponse.newBuilder()
                .setCopySceneId(getCopySceneMessageId()).setCause(cause).build());
        nettyResponse.setData(builder.build().toByteArray());
        for (MmoSimpleRole role:teamBean.getMmoSimpleRoles()) {
            Channel c=ChannelMessageCache.getInstance().get(role.getId());
            if (c!=null) {
                c.writeAndFlush(nettyResponse);
            }
        }
    }

    /**
     * BOSS挑战是否完成
     */
    public void bossComeOrFinish()  {
        //修改副本状态已经搞完
        setStatus(CopySceneStatusCode.FINISH.getCode());
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

    /**
     * 根据index获取副本物品
     */
    public Article getArticleByIndex(Integer index){
        synchronized (articlesMap) {
            Article article = articlesMap.get(index);
            if (article!=null){
                articlesMap.remove(index);
            }
            return article;
        }
    }
}
