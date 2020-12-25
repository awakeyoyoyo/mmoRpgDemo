package com.liqihao.provider;

import com.liqihao.Cache.MmoBaseMessageCache;
import com.liqihao.pojo.bean.CopySceneBean;
import com.liqihao.pojo.bean.MmoSimpleRole;
import com.liqihao.pojo.bean.TeamBean;
import org.apache.poi.ss.formula.functions.T;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 队伍服务提供类
 * @author lqhao
 */
public class TeamServiceProvider {
    private static ConcurrentHashMap<Integer,TeamBean> teamBeans=new ConcurrentHashMap<>();
    private static AtomicInteger teamBeanIdAuto=new AtomicInteger(0);
    public static TeamBean createNewTeamBean(MmoSimpleRole leader,String teamName){
        TeamBean teamBean=new TeamBean();
        teamBean.setTeamId(teamBeanIdAuto.incrementAndGet());
        teamBean.setCopySceneBeanId(null);
        teamBean.setTeamName(teamName);
        ConcurrentHashMap<Integer,MmoSimpleRole> mmoRoleMap=new ConcurrentHashMap<>();
        mmoRoleMap.put(leader.getId(),leader);
        teamBean.setMmoSimpleRolesMap(mmoRoleMap);
        teamBean.setCopySceneId(null);
        teamBean.setTeamApplyOrInviteSize(MmoBaseMessageCache.getInstance().getBaseDetailMessage().getTeamApplyOrInviteSize());
        teamBean.setTeamRoleSize(MmoBaseMessageCache.getInstance().getBaseDetailMessage().getTeamRoleSize());
        teamBean.setLeaderId(leader.getId());
        teamBeans.put(teamBean.getTeamId(),teamBean);
        return teamBean;
    }
    public static TeamBean getTeamBeanByTeamId(Integer teamBeanId){
        return teamBeans.get(teamBeanId);
    }

    public static void deleteTeamById(Integer teamId) {
        teamBeans.remove(teamId);
    }
}
