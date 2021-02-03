package com.liqihao.cache;

import com.liqihao.cache.base.CommonsBeanCache;
import com.liqihao.dao.MmoRolePOJOMapper;
import com.liqihao.pojo.MmoRolePOJO;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 角色表
 * @author lqhao
 */
@Component
public class RoleMessageCache extends CommonsBeanCache<MmoRolePOJO> implements ApplicationContextAware {
    private volatile static RoleMessageCache instance;

    public static RoleMessageCache getInstance() {
        return instance;
    }
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        instance=this;
        MmoRolePOJOMapper mmoRolePOJOMapper = (MmoRolePOJOMapper) applicationContext.getBean("mmoRolePOJOMapper");
        List<MmoRolePOJO> mmoRolePOJOS=mmoRolePOJOMapper.selectAll();
        this.concurrentHashMap=new ConcurrentHashMap<>();
        for (MmoRolePOJO m:mmoRolePOJOS) {
            this.concurrentHashMap.put(m.getId(),m);
        }
    }

    public RoleMessageCache() {

    }
    private RoleMessageCache(ConcurrentHashMap<Integer, MmoRolePOJO> map) {
        super(map);
    }


}
