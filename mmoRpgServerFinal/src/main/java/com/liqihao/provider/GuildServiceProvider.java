package com.liqihao.provider;

import com.liqihao.commons.RpgServerException;
import com.liqihao.commons.StateCode;
import com.liqihao.dao.MmoEmailPOJOMapper;
import com.liqihao.dao.MmoGuildPOJOMapper;
import com.liqihao.dao.MmoGuildRolePOJOMapper;
import com.liqihao.dao.MmoUserPOJOMapper;
import com.liqihao.pojo.MmoGuildPOJO;
import com.liqihao.pojo.bean.GoodsBean;
import com.liqihao.pojo.bean.guildBean.GuildBean;
import com.liqihao.pojo.bean.guildBean.WareHouseManager;
import com.liqihao.pojo.bean.roleBean.MmoSimpleRole;
import com.liqihao.pojo.bean.roleBean.Role;
import com.liqihao.service.GuildService;
import com.liqihao.util.CommonsUtil;
import org.springframework.beans.BeansException;
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
    private static final ConcurrentHashMap<Integer, GuildBean> guildBeanConcurrentHashMap=new ConcurrentHashMap<>();
    private MmoGuildPOJOMapper mmoGuildPOJOMapper;
    private MmoGuildRolePOJOMapper mmoGuildRolePOJOMapper;
    private List<Integer> needDeleteGuildIds;
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        MmoGuildPOJOMapper mmoGuildPOJOMapper=(MmoGuildPOJOMapper)applicationContext.getBean("mmoGuildPOJOMapper");
        MmoGuildRolePOJOMapper mmoGuildRolePOJOMapper=(MmoGuildRolePOJOMapper)applicationContext.getBean("mmoGuildRolePOJOMapper");
        this.mmoGuildPOJOMapper=mmoGuildPOJOMapper;
        this.mmoGuildRolePOJOMapper=mmoGuildRolePOJOMapper;
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
        List<Integer> roleIds=new ArrayList<>();
        roleIds.add(role.getId());
        guildBean.setRoleIds(roleIds);
        //放入提供者中
        guildBeanConcurrentHashMap.put(guildBean.getId(),guildBean);
        //用户持有公会引用
        role.setGuildBean(guildBean);
        return guildBean;
    }
    /**
     * 申请加入公会
     */
    public void applyGuild(MmoSimpleRole role,Integer guildBeanId){

    }
}
