package com.liqihao.provider;

import com.liqihao.cache.ProfessionMessageCache;
import com.liqihao.cache.SceneBeanMessageCache;
import com.liqihao.commons.enums.ProfessionCode;
import com.liqihao.commons.enums.RoleOnStatusCode;
import com.liqihao.commons.enums.RoleStatusCode;
import com.liqihao.commons.enums.RoleTypeCode;
import com.liqihao.pojo.baseMessage.ProfessionMessage;
import com.liqihao.pojo.bean.CopySceneBean;
import com.liqihao.pojo.bean.roleBean.MmoHelperBean;
import com.liqihao.pojo.bean.roleBean.MmoSimpleRole;
import com.liqihao.pojo.bean.SceneBean;
import com.liqihao.util.CommonsUtil;

import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 召唤兽服务提供
 * @author lqhao
 */
public class CallerServiceProvider {
    private static AtomicInteger callerBeanIdAuto=new AtomicInteger(0);

    /**
     * 召唤
     * @param mmoSimpleRole
     * @return
     */
    public static MmoHelperBean callHelper(MmoSimpleRole mmoSimpleRole){
        MmoHelperBean mmoHelperBean=new MmoHelperBean();
        ProfessionMessage professionMessage=ProfessionMessageCache.getInstance().get(ProfessionCode.HELPER.getCode());
        //初始化 召唤兽
        mmoHelperBean.setSkillIdList(CommonsUtil.split(professionMessage.getSkillIds()));
        mmoHelperBean.setSkillBeans(CommonsUtil.skillIdsToSkillBeans(mmoHelperBean.getSkillIdList()));
        mmoHelperBean.setName(mmoSimpleRole.getName()+"的"+professionMessage.getName());
        mmoHelperBean.setHp(mmoSimpleRole.getHp()/2);
        mmoHelperBean.setMp(mmoSimpleRole.getMp()/2);
        mmoHelperBean.setNowHp(mmoHelperBean.getHp());
        mmoHelperBean.setNowMp(mmoHelperBean.getMp());
        mmoHelperBean.setMasterId(mmoSimpleRole.getId());
        mmoHelperBean.setBufferBeans(new CopyOnWriteArrayList<>());
        mmoHelperBean.setAttack(mmoSimpleRole.getAttack()/4);
        mmoHelperBean.setType(RoleTypeCode.HELPER.getCode());
        mmoHelperBean.setCopySceneBeanId(mmoSimpleRole.getCopySceneBeanId());
        mmoHelperBean.setMmoSceneId(mmoSimpleRole.getMmoSceneId());
        mmoHelperBean.setStatus(RoleStatusCode.ALIVE.getCode());
        mmoHelperBean.setOnStatus(RoleOnStatusCode.ONLINE.getCode());
        mmoHelperBean.setDamageAdd(0);
        mmoHelperBean.setId(callerBeanIdAuto.incrementAndGet());
        mmoHelperBean.setTeamId(mmoSimpleRole.getTeamId());
        return mmoHelperBean;
    }

    /**
     * 取消召唤
     * @param mmoSimpleRole
     */
    public static void reduceCallHelper(MmoSimpleRole mmoSimpleRole){
       if (mmoSimpleRole.getMmoHelperBean()!=null){
           MmoHelperBean helperBean=mmoSimpleRole.getMmoHelperBean();
           //人物中删除
           mmoSimpleRole.setMmoHelperBean(null);
           if (helperBean.getMmoSceneId()!=null){
               //场景中去除
               SceneBean sceneBean=SceneBeanMessageCache.getInstance().get(helperBean.getMmoSceneId());
               sceneBean.getHelperBeans().remove(helperBean);
           }
           if (helperBean.getCopySceneBeanId()!=null){
               //副本中去除
               CopySceneBean copySceneBean=CopySceneProvider.getCopySceneBeanById(helperBean.getCopySceneBeanId());
               copySceneBean.getRoles().remove(helperBean);
           }
       }

    }

}
