package com.liqihao.provider;

import com.liqihao.Cache.GuildAuthorityMessageCache;
import com.liqihao.Cache.GuildPositionMessageCache;
import com.liqihao.Cache.MmoBaseMessageCache;
import com.liqihao.commons.RpgServerException;
import com.liqihao.commons.StateCode;
import com.liqihao.commons.enums.GuildRolePositionCode;
import com.liqihao.dao.MmoGuildApplyPOJOMapper;
import com.liqihao.dao.MmoGuildPOJOMapper;
import com.liqihao.dao.MmoGuildRolePOJOMapper;
import com.liqihao.pojo.MmoGuildApplyPOJO;
import com.liqihao.pojo.MmoGuildPOJO;
import com.liqihao.pojo.MmoGuildRolePOJO;
import com.liqihao.pojo.baseMessage.GuildAuthorityMessage;
import com.liqihao.pojo.baseMessage.GuildPositionMessage;
import com.liqihao.pojo.bean.guildBean.GuildApplyBean;
import com.liqihao.pojo.bean.guildBean.GuildBean;
import com.liqihao.pojo.bean.guildBean.GuildRoleBean;
import com.liqihao.pojo.bean.roleBean.MmoSimpleRole;
import com.liqihao.util.CommonsUtil;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 公会服务提供类
 * @author lqhao
 */
@Component
public class GuildServiceProvider  implements ApplicationContextAware {
    private static final ConcurrentHashMap<Integer, GuildBean> guildBeanConcurrentHashMap=new ConcurrentHashMap<>();
    private MmoGuildPOJOMapper mmoGuildPOJOMapper;
    private MmoGuildRolePOJOMapper mmoGuildRolePOJOMapper;
    private MmoGuildApplyPOJOMapper mmoGuildApplyPOJOMapper;
    private volatile static GuildServiceProvider instance;
    @Autowired
    private CommonsUtil commonsUtil;
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        instance=this;
        MmoGuildPOJOMapper mmoGuildPOJOMapper=(MmoGuildPOJOMapper)applicationContext.getBean("mmoGuildPOJOMapper");
        MmoGuildRolePOJOMapper mmoGuildRolePOJOMapper=(MmoGuildRolePOJOMapper)applicationContext.getBean("mmoGuildRolePOJOMapper");
        MmoGuildApplyPOJOMapper mmoGuildApplyPOJOMapper=(MmoGuildApplyPOJOMapper)applicationContext.getBean("mmoGuildApplyPOJOMapper");
        this.mmoGuildPOJOMapper=mmoGuildPOJOMapper;
        this.mmoGuildRolePOJOMapper=mmoGuildRolePOJOMapper;
        this.mmoGuildApplyPOJOMapper=mmoGuildApplyPOJOMapper;
        init();
    }

    public void init(){
        List<MmoGuildPOJO> guildPOJOS=mmoGuildPOJOMapper.selectAll();
        for (MmoGuildPOJO mmoGuildPOJO:guildPOJOS) {
            GuildBean guildBean=CommonsUtil.MmoGuildPOJOToGuildBean(mmoGuildPOJO);
            guildBeanConcurrentHashMap.put(guildBean.getId(),guildBean);
        }
    }
    public static GuildServiceProvider getInstance() {
        return instance;
    }

    public static void setInstance(GuildServiceProvider instance) {
        GuildServiceProvider.instance = instance;
    }

    /**
     * 创建公会
     */
    public GuildBean createGuildBean(MmoSimpleRole role, String guildName) throws RpgServerException {
        //判断是否有重复名称
        for (GuildBean g:guildBeanConcurrentHashMap.values()){
            if (guildName.equals(g.getName())){
                throw new RpgServerException(StateCode.FAIL,"该公会名称重复");
            }
        }
        //建立插入数据库返回id
        MmoGuildPOJO mmoGuildPOJO=new MmoGuildPOJO();
        mmoGuildPOJO.setCreateTime(System.currentTimeMillis());
        mmoGuildPOJO.setName(guildName);
        mmoGuildPOJO.setChairmanId(role.getId());
        mmoGuildPOJO.setPeopleNum(1);
        mmoGuildPOJO.setLevel(1);
        mmoGuildPOJOMapper.insert(mmoGuildPOJO);
        GuildBean guildBean= CommonsUtil.mmoGuildPOJOToGuildBean(mmoGuildPOJO);
        //插入成员bean
        List<GuildRoleBean> roleBeans=new ArrayList<>();
        GuildRoleBean guildRoleBean=new GuildRoleBean();
        guildRoleBean.setRoleId(role.getId());
        guildRoleBean.setGuildId(guildBean.getId());
        guildRoleBean.setContribution(0);
        guildRoleBean.setGuildPositionId(GuildRolePositionCode.HUI_ZHANG.getCode());
        Integer id=insertGuildRolePOJO(guildRoleBean);
        guildRoleBean.setId(id);
        guildBean.getGuildRoleBeans().add(guildRoleBean);
        roleBeans.add(guildRoleBean);
        guildBean.setGuildRoleBeans(roleBeans);
        //放入提供者中
        guildBeanConcurrentHashMap.put(guildBean.getId(),guildBean);
        //用户持有公会引用
        role.setGuildBean(guildBean);
        return guildBean;
    }
    /**
     * 申请加入公会
     */
    public void applyGuild(MmoSimpleRole role,Integer guildBeanId) throws RpgServerException {
        GuildBean guildBean=guildBeanConcurrentHashMap.get(guildBeanId);
        if (guildBean==null){
            throw new RpgServerException(StateCode.FAIL,"传入无效公会id");
        }
        GuildApplyBean guildApplyBean=new GuildApplyBean();
        guildApplyBean.setGuildId(guildBeanId);
        guildApplyBean.setCreateTime(System.currentTimeMillis());
        Integer lastDay=MmoBaseMessageCache.getInstance().getGuildBaseMessage().getApplyLastTime();
        guildApplyBean.setEndTime(guildApplyBean.getCreateTime()+lastDay*24*60*60*1000);
        guildApplyBean.setRoleId(role.getId());
        //入库
        MmoGuildApplyPOJO guildApplyPOJO=new MmoGuildApplyPOJO();
        guildApplyPOJO.setGuildId(guildApplyBean.getGuildId());
        guildApplyPOJO.setCreateTime(guildApplyBean.getCreateTime());
        guildApplyPOJO.setEndTime(guildApplyBean.getEndTime());
        guildApplyPOJO.setRoleId(guildApplyBean.getRoleId());
        mmoGuildApplyPOJOMapper.insert(guildApplyPOJO);
        //入库后返回id
        guildApplyBean.setId(guildApplyPOJO.getId());
        //放入公会bean中
        guildBean.addGuildApplyBean(guildApplyBean);
    }


    /**
     * 删除指定的申请
     */
    public void deleteApply(List<Integer> guildApplyIds) {
        for (Integer id:guildApplyIds) {
            mmoGuildApplyPOJOMapper.deleteByPrimaryKey(id);
        }
    }

    /**
     * 删除数据库中 某人与公会的中间表记录
     * @param guildRoleId
     */
    public void deletePeople(Integer guildRoleId) {
        mmoGuildRolePOJOMapper.deleteByPrimaryKey(guildRoleId);
    }

    /**
     * 插入数据库某人与公会的中间表记录
     * @param guildRoleBean
     * @return
     */
    public Integer insertGuildRolePOJO(GuildRoleBean guildRoleBean) {
        MmoGuildRolePOJO guildRolePOJO=new MmoGuildRolePOJO();
        guildRolePOJO.setGuildId(guildRoleBean.getGuildId());
        guildRolePOJO.setGuildPositionId(guildRoleBean.getGuildPositionId());
        guildRolePOJO.setContribution(guildRoleBean.getContribution());
        guildRolePOJO.setRoleId(guildRoleBean.getRoleId());
        mmoGuildRolePOJOMapper.insert(guildRolePOJO);
        return guildRolePOJO.getId();
    }

    /**
     * 检测是否有权限
     */
    public boolean checkHasAuthority(MmoSimpleRole role,Integer authorityId) {
         GuildRoleBean guildRoleBean=role.getGuildBean().getRoleGuildMsg(role.getId());
         GuildPositionMessage guildPositionMessage= GuildPositionMessageCache.getInstance().get(guildRoleBean.getGuildPositionId());
         String authorityIdStr=guildPositionMessage.getAuthorityIds();
         List<Integer> authorityIds=CommonsUtil.split(authorityIdStr);
         if (authorityIds.contains(authorityId)){
             return true;
         }else {
             return false;
         }
    }

    public GuildBean getGuildBeanById(Integer guildId) {
        return  guildBeanConcurrentHashMap.get(guildId);
    }
}
