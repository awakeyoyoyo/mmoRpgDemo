package com.liqihao.service.impl;

import com.liqihao.Cache.CopySceneMessageCache;
import com.liqihao.commons.ConstantValue;
import com.liqihao.commons.NettyResponse;
import com.liqihao.commons.StateCode;
import com.liqihao.pojo.baseMessage.CopySceneMessage;
import com.liqihao.pojo.bean.CopySceneBean;
import com.liqihao.pojo.bean.MmoSimpleRole;
import com.liqihao.pojo.bean.TeamBean;
import com.liqihao.protobufObject.TeamModel;
import com.liqihao.provider.CopySceneProvider;
import com.liqihao.provider.TeamServiceProvider;
import com.liqihao.service.CopySceneService;
import com.liqihao.util.CommonsUtil;
import io.netty.channel.Channel;

import java.util.ArrayList;
import java.util.List;

/**
 * 副本模块
 * @author lqhao
 */
public class CopySceneServiceImpl implements CopySceneService {
    @Override
    public void askCanCopySceneRequest(TeamModel.TeamModelMessage myMessage, Channel channel) {
        //判断是否在线 并且返回玩家对象
        MmoSimpleRole mmoSimpleRole= CommonsUtil.checkLogin(channel);
        if (mmoSimpleRole==null) {
            return;
        }
        //roleId
        //TODO 增加限制条件
        //暂无限制，可以挑战所有副本
        List<Integer> copySceneId=new ArrayList<>();
        for (CopySceneMessage cMsg:CopySceneMessageCache.getInstance().values()) {
            copySceneId.add(cMsg.getId());
        }
        //todo 返回
    }

    @Override
    public void copySceneMessageRequest(TeamModel.TeamModelMessage myMessage, Channel channel) {
        //copySceneBeanId
        Integer copySceneBeanId=null;
        //判断是否在线 并且返回玩家对象
        MmoSimpleRole mmoSimpleRole= CommonsUtil.checkLogin(channel);
        if (mmoSimpleRole==null) {
            return;
        }
        //判断是否已经有进入副本
        Integer copySceneId=mmoSimpleRole.getCopySceneId();
        if (copySceneId==null){
            NettyResponse errotResponse=new NettyResponse(StateCode.FAIL, ConstantValue.FAIL_RESPONSE,"当前角色还未进入副本".getBytes());
            channel.writeAndFlush(errotResponse);
            return;
        }

        CopySceneBean copySceneBean=CopySceneProvider.getCopySceneBeanById(copySceneBeanId);
        //todo 返回
    }

    @Override
    public void enterCopySceneRequest(TeamModel.TeamModelMessage myMessage, Channel channel) {
        //copySceneId
        Integer copySceneId=null;
        MmoSimpleRole mmoSimpleRole= CommonsUtil.checkLogin(channel);
        if (mmoSimpleRole==null) {
            return;
        }
        //判断是否在组队状态
        Integer teamId=mmoSimpleRole.getTeamId();
        if (teamId==null){
            NettyResponse errotResponse=new NettyResponse(StateCode.FAIL, ConstantValue.FAIL_RESPONSE,"非组队状态不能进去".getBytes());
            channel.writeAndFlush(errotResponse);
            return;
        }
        //判断人物是否已经在副本
        if (mmoSimpleRole.getCopySceneId()!=null){
            NettyResponse errotResponse=new NettyResponse(StateCode.FAIL, ConstantValue.FAIL_RESPONSE,"人物已经在副本中，请退出当前你副本再尝试".getBytes());
            channel.writeAndFlush(errotResponse);
            return;
        }
        //判断队伍是否关联了副本
        TeamBean teamBean= TeamServiceProvider.getTeamBeanByTeamId(teamId);
        if (teamBean.getCopySceneBeanId()==null||!teamBean.getCopySceneId().equals(copySceneId)){
            NettyResponse errotResponse=new NettyResponse(StateCode.FAIL, ConstantValue.FAIL_RESPONSE,"当前队伍不在副本中或者队伍不是关联该副本".getBytes());
            channel.writeAndFlush(errotResponse);
            return;
        }
        CopySceneBean copySceneBean=CopySceneProvider.getCopySceneBeanById(teamBean.getCopySceneBeanId());
        mmoSimpleRole.wentCopyScene(copySceneBean);

        //todo 进行进入副本信息
        // 广播有人进入了队伍
    }

    @Override
    public void exitCopySceneRequest(TeamModel.TeamModelMessage myMessage, Channel channel) {
        MmoSimpleRole mmoSimpleRole= CommonsUtil.checkLogin(channel);
        if (mmoSimpleRole==null) {
            return;
        }
        //判断玩家是否在副本中
        if (mmoSimpleRole.getCopySceneId()==null){
            NettyResponse errotResponse=new NettyResponse(StateCode.FAIL, ConstantValue.FAIL_RESPONSE,"当前玩家不在副本中".getBytes());
            channel.writeAndFlush(errotResponse);
            return;
        }
        TeamBean teamBean=TeamServiceProvider.getTeamBeanByTeamId(mmoSimpleRole.getTeamId());
        Integer copySceneBeanId=teamBean.getCopySceneBeanId();
        CopySceneBean copySceneBean=CopySceneProvider.getCopySceneBeanById(copySceneBeanId);
        copySceneBean.peopleExit(mmoSimpleRole.getId());


        //todo 发送信息
    }

    @Override
    public void createCopySceneBeanRequest(TeamModel.TeamModelMessage myMessage, Channel channel) {
        Integer copySceneId=null;
        MmoSimpleRole mmoSimpleRole= CommonsUtil.checkLogin(channel);
        if (mmoSimpleRole==null) {
            return;
        }
        //判断是否在组队状态
        if (mmoSimpleRole.getTeamId()==null){
            NettyResponse errotResponse=new NettyResponse(StateCode.FAIL, ConstantValue.FAIL_RESPONSE,"请先组队".getBytes());
            channel.writeAndFlush(errotResponse);
            return;
        }
        TeamBean teamBean=TeamServiceProvider.getTeamBeanByTeamId(mmoSimpleRole.getTeamId());
        //判断是否是队长
        if (!teamBean.getLeaderId().equals(mmoSimpleRole.getId())){
            NettyResponse errotResponse=new NettyResponse(StateCode.FAIL, ConstantValue.FAIL_RESPONSE,"你不是队长没有该权利".getBytes());
            channel.writeAndFlush(errotResponse);
            return;
        }
        //判断队伍是否绑定了副本
        if (teamBean.getCopySceneBeanId()!=null){
            NettyResponse errotResponse=new NettyResponse(StateCode.FAIL, ConstantValue.FAIL_RESPONSE,"该队伍已经绑定副本，无法在创建".getBytes());
            channel.writeAndFlush(errotResponse);
            return;
        }
        CopySceneBean copySceneBean=CopySceneProvider.createNewCopyScene(copySceneId);
        teamBean.setCopySceneBeanId(copySceneBean.getCopySceneBeanId());
        //todo 创建成功 对队伍的人广播
    }
}
