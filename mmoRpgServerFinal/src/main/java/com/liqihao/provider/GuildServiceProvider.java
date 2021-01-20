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
import com.liqihao.dao.MmoRolePOJOMapper;
import com.liqihao.pojo.MmoGuildApplyPOJO;
import com.liqihao.pojo.MmoGuildPOJO;
import com.liqihao.pojo.MmoGuildRolePOJO;
import com.liqihao.pojo.MmoRolePOJO;
import com.liqihao.pojo.baseMessage.GuildAuthorityMessage;
import com.liqihao.pojo.baseMessage.GuildPositionMessage;
import com.liqihao.pojo.bean.guildBean.GuildApplyBean;
import com.liqihao.pojo.bean.guildBean.GuildBean;
import com.liqihao.pojo.bean.guildBean.GuildRoleBean;
import com.liqihao.pojo.bean.roleBean.MmoSimpleRole;
import com.liqihao.util.CommonsUtil;
import com.liqihao.util.ScheduledThreadPoolUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 公会服务提供类
 * @author lqhao
 */
@Component
public class GuildServiceProvider  implements ApplicationContextAware {
    private final Logger log = LoggerFactory.getLogger(EmailServiceProvider.class);
    private static final ConcurrentHashMap<Integer, GuildBean> guildBeanConcurrentHashMap=new ConcurrentHashMap<>();
    @Autowired
    private MmoGuildPOJOMapper mmoGuildPOJOMapper;
    @Autowired
    private MmoGuildRolePOJOMapper mmoGuildRolePOJOMapper;
    @Autowired
    private MmoGuildApplyPOJOMapper mmoGuildApplyPOJOMapper;
    @Autowired
    private MmoRolePOJOMapper mmoRolePOJOMapper;
    private volatile static GuildServiceProvider instance;
    @Autowired
    private CommonsUtil commonsUtil;
    /**
     * 公会自增id
     */
    private static AtomicInteger guildIdAuto;
    /**
     * 公会人物中间表自增id
     */
    private static AtomicInteger guildRoleIdAuto;
    /**
     * 公会申请表自增id
     */
    private static AtomicInteger guildApplyIdAuto;
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        instance=this;
        MmoGuildPOJOMapper mmoGuildPOJOMapper=(MmoGuildPOJOMapper)applicationContext.getBean("mmoGuildPOJOMapper");
        Integer guildIndex = mmoGuildPOJOMapper.selectNextIndex();
        guildIdAuto = new AtomicInteger(guildIndex);
        log.info("GuildServiceProvider：mmoGuildPOJOMapper下一个主键index:" + guildIndex );
        MmoGuildRolePOJOMapper mmoGuildRolePOJOMapper=(MmoGuildRolePOJOMapper)applicationContext.getBean("mmoGuildRolePOJOMapper");
        Integer guildRoleIndex = mmoGuildRolePOJOMapper.selectNextIndex();
        guildRoleIdAuto = new AtomicInteger(guildRoleIndex);
        log.info("GuildServiceProvider：mmoRolePOJOMapper下一个主键index:" + guildRoleIndex );
        MmoGuildApplyPOJOMapper mmoGuildApplyPOJOMapper=(MmoGuildApplyPOJOMapper)applicationContext.getBean("mmoGuildApplyPOJOMapper");
        Integer guildApplyIndex = mmoGuildApplyPOJOMapper.selectNextIndex();
        guildApplyIdAuto = new AtomicInteger(guildApplyIndex);
        log.info("GuildServiceProvider：mmoGuildApplyPOJOMapper下一个主键index:" + guildApplyIndex );
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
        mmoGuildPOJO.setMoney(0);
        mmoGuildPOJO.setId(guildIdAuto.incrementAndGet());
        GuildBean guildBean= CommonsUtil.mmoGuildPOJOToGuildBean(mmoGuildPOJO);
        //插入成员bean
        List<GuildRoleBean> roleBeans=new ArrayList<>();
        GuildRoleBean guildRoleBean=new GuildRoleBean();
        guildRoleBean.setRoleId(role.getId());
        guildRoleBean.setGuildId(guildBean.getId());
        guildRoleBean.setContribution(0);
        guildRoleBean.setGuildPositionId(GuildRolePositionCode.HUI_ZHANG.getCode());
        guildRoleBean.setId(guildRoleIdAuto.incrementAndGet());
        guildBean.getGuildRoleBeans().add(guildRoleBean);
        roleBeans.add(guildRoleBean);
        guildBean.setGuildRoleBeans(roleBeans);
        //放入提供者中
        guildBeanConcurrentHashMap.put(guildBean.getId(),guildBean);
        //用户持有公会引用
        role.setGuildBean(guildBean);
        //数据入库
        ScheduledThreadPoolUtil.addTask(() -> {
            mmoGuildPOJOMapper.insert(mmoGuildPOJO);
            insertGuildRolePOJO(guildRoleBean);
        });
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
        guildApplyBean.setId(guildApplyIdAuto.incrementAndGet());
        //放入公会bean中
        guildBean.addGuildApplyBean(guildApplyBean);
        //数据入库
        ScheduledThreadPoolUtil.addTask(() -> insertGuildApplyPOJO(guildApplyBean));
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
     * @param guildApplyBean
     * @return
     */
    public void insertGuildApplyPOJO(GuildApplyBean guildApplyBean) {
        MmoGuildApplyPOJO guildApplyPOJO=new MmoGuildApplyPOJO();
        guildApplyPOJO.setGuildId(guildApplyBean.getGuildId());
        guildApplyPOJO.setCreateTime(guildApplyBean.getCreateTime());
        guildApplyPOJO.setEndTime(guildApplyBean.getEndTime());
        guildApplyPOJO.setRoleId(guildApplyBean.getRoleId());
        guildApplyPOJO.setId(guildApplyBean.getId());
        mmoGuildApplyPOJOMapper.insert(guildApplyPOJO);
    }
    /**
     * 插入数据库角色申请表记录
     * @param guildRoleBean
     * @return
     */
    public Integer insertGuildRolePOJO(GuildRoleBean guildRoleBean) {
        MmoGuildRolePOJO guildRolePOJO=new MmoGuildRolePOJO();
        guildRolePOJO.setGuildId(guildRoleBean.getGuildId());
        guildRolePOJO.setGuildPositionId(guildRoleBean.getGuildPositionId());
        guildRolePOJO.setContribution(guildRoleBean.getContribution());
        guildRolePOJO.setRoleId(guildRoleBean.getRoleId());
        guildRoleBean.setId(guildRoleBean.getId());
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

    public void updateRolePOJO(MmoRolePOJO mmoRolePOJO) {
        mmoRolePOJOMapper.updateByPrimaryKeySelective(mmoRolePOJO);
    }

    public void updateGuildPOJO(GuildBean guildBean) {
        MmoGuildPOJO guildPOJO=new MmoGuildPOJO();
        guildPOJO.setId(guildBean.getId());
        guildPOJO.setName(guildBean.getName());
        guildPOJO.setCreateTime(guildBean.getCreateTime());
        guildPOJO.setLevel(guildBean.getLevel());
        guildPOJO.setPeopleNum(guildBean.getPeopleNum());
        guildPOJO.setChairmanId(guildBean.getChairmanId());
        guildBean.setMoney(guildBean.getMoney());
        mmoGuildPOJOMapper.updateByPrimaryKey(guildPOJO);
    }

    public void updateGuildRole(GuildRoleBean roleBean) {
        MmoGuildRolePOJO mmoGuildRolePOJO=new MmoGuildRolePOJO();
        mmoGuildRolePOJO.setId(roleBean.getId());
        mmoGuildRolePOJO.setRoleId(roleBean.getRoleId());
        mmoGuildRolePOJO.setContribution(roleBean.getContribution());
        mmoGuildRolePOJO.setGuildId(roleBean.getGuildId());
        mmoGuildRolePOJO.setGuildPositionId(roleBean.getGuildPositionId());
        mmoGuildRolePOJOMapper.updateByPrimaryKey(mmoGuildRolePOJO);
    }
}
