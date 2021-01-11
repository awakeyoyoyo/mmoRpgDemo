package com.liqihao.provider;

import com.liqihao.Cache.OnlineRoleMessageCache;
import com.liqihao.Cache.SceneBeanMessageCache;
import com.liqihao.commons.RpgServerException;
import com.liqihao.commons.StateCode;
import com.liqihao.commons.enums.ChatTypeCode;
import com.liqihao.commons.enums.RoleTypeCode;
import com.liqihao.pojo.bean.*;
import com.liqihao.pojo.bean.roleBean.MmoSimpleRole;
import com.liqihao.pojo.bean.roleBean.Role;
import com.liqihao.pojo.bean.teamBean.TeamBean;

import java.util.Iterator;
import java.util.List;

/**
 * 聊天功能提供者
 * @author lqhao
 */
public class ChatServiceProvider implements MySubject{
    private OnlineRoleMessageCache onlineRoleMessageCache;
    private volatile static ChatServiceProvider instance;
    private ChatServiceProvider() {
        onlineRoleMessageCache=OnlineRoleMessageCache.getInstance();
    }
    public static ChatServiceProvider getInstance() {
        //第一次检测
        if (instance == null) {
            synchronized (ChatServiceProvider.class) {
                //第二次检测
                if (instance == null) {
                    instance = new ChatServiceProvider();
                }
            }
        }
        return instance;
    }
    @Override
    public void registerObserver(Role o) {
        onlineRoleMessageCache.put(o.getId(),(MmoSimpleRole) o);
    }

    @Override
    public void removeObserver(Role o) {
        if (onlineRoleMessageCache.contains((o.getId()))){
            onlineRoleMessageCache.remove(o.getId());
        }
    }

    /**
     * 世界广播
     * @param fromRole
     * @param str
     */
    @Override
    public void notifyObserver(Role fromRole,String str) {
        Iterator<MmoSimpleRole> iterator = onlineRoleMessageCache.values().iterator();
        while(iterator.hasNext()){
            MmoSimpleRole observer=iterator.next();
            observer.update(fromRole,str, ChatTypeCode.ALL_PEOPLE.getCode());
        }
    }
    /**
     * 私聊
     * @param fromRole
     * @param str
     */
    @Override
    public void notifyOne(Integer toRoleId, Role fromRole, String str) {
        if (onlineRoleMessageCache.contains(toRoleId)){
            onlineRoleMessageCache.get(toRoleId).update(fromRole,str,ChatTypeCode.SINGLE_PEOPLE.getCode());
        }

    }

    /**
     * 地图or副本广播
     * @param fromRole
     * @param str
     */
    public void notifyScene(Role fromRole, String str) throws Exception {
        Integer sceneId=fromRole.getMmoSceneId();
        if (sceneId!=null) {
            SceneBean sceneBean = SceneBeanMessageCache.getInstance().get(sceneId);
            if (sceneBean == null) {
                throw new RpgServerException(StateCode.FAIL,"错误的sceneId");
            }
            List<Integer> roleIds = sceneBean.getRoles();
            for (Integer roleId : roleIds) {
                MmoSimpleRole role = OnlineRoleMessageCache.getInstance().get(roleId);
                if (role != null) {
                    role.update(fromRole, str, ChatTypeCode.SCENE_PEOPLE.getCode());
                }
            }
        }else{
            Integer copySceneBeanId=fromRole.getCopySceneBeanId();
            CopySceneBean copySceneBean=CopySceneProvider.getCopySceneBeanById(copySceneBeanId);
            List<Role> roles=copySceneBean.getRoles();
            for (Role role:roles) {
                if (role.getType().equals(RoleTypeCode.PLAYER.getCode())){
                    MmoSimpleRole mmoSimpleRole= (MmoSimpleRole) role;
                    mmoSimpleRole.update(fromRole,str,ChatTypeCode.SCENE_PEOPLE.getCode());
                }
            }
        }
    }
    /**
     * 队伍广播
     * @param fromRole
     * @param str
     */
    public void notifyTeam(Integer teamBeanId, Role fromRole, String str) throws Exception {
        TeamBean teamBean=TeamServiceProvider.getTeamBeanByTeamId(teamBeanId);
        if (teamBean==null){
            throw new RpgServerException(StateCode.FAIL,"错误的copySceneBeanId");
        }
        for (MmoSimpleRole role:teamBean.getMmoSimpleRoles()) {
            role.update(fromRole,str,ChatTypeCode.TEAM_PEOPLE.getCode());
        }
    }
}
