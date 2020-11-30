package com.liqihao.application;

import com.liqihao.commons.RoleStatusCode;
import com.liqihao.commons.RoleTypeCode;
import com.liqihao.dao.MmoRolePOJOMapper;
import com.liqihao.dao.MmoScenePOJOMapper;
import com.liqihao.pojo.*;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class ServerInit implements ApplicationContextAware {
    @Autowired
    private MmoScenePOJOMapper scenePOJOMapper;
    @Autowired
    private MmoRolePOJOMapper rolePOJOMapper;
    private ApplicationContext applicationContext;
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
    public void init() {
        List<MmoScenePOJO> mmoScenePOJOS = scenePOJOMapper.selectAll();
        //分解出可用scene
        for (MmoScenePOJO mmoScenePOJO : mmoScenePOJOS) {
            MmoScene mmoScene=new MmoScene();
            mmoScene.setId(mmoScenePOJO.getId());
            mmoScene.setPlaceName(mmoScenePOJO.getPlacename());
            //构建场景可通的下一个场景
            List<Integer> canScene=split(mmoScenePOJO.getCanscene());
            List<MmoSimpleScene> mmoSimpleScenes=new ArrayList<>();
            for (Integer id: canScene) {
                MmoSimpleScene mmoSimpleScene=new MmoSimpleScene();
                mmoSimpleScene.setId(id);
                for (MmoScenePOJO temp : mmoScenePOJOS) {
                    if (temp.getId().equals(id)) {
                        mmoSimpleScene.setPalceName(temp.getPlacename());
                        break;
                    }
                }
                mmoSimpleScenes.add(mmoSimpleScene);
            }
            mmoScene.setCanScene(mmoSimpleScenes);
            //构建场景中的角色
            List<Integer> roles=split(mmoScenePOJO.getRoles());
            List<MmoSimpleRole> mmoSimpleRoles=new ArrayList<>();
            for (Integer id:roles){
                MmoRolePOJO mmoRolePOJO=rolePOJOMapper.selectByPrimaryKey(id);
                MmoSimpleRole mmoSimpleRole=new MmoSimpleRole();
                mmoSimpleRole.setId(mmoRolePOJO.getId());
                mmoSimpleRole.setName(mmoRolePOJO.getName());
                mmoSimpleRole.setStatus(RoleStatusCode.getValue(mmoRolePOJO.getStatus()));
                mmoSimpleRole.setType(RoleTypeCode.getValue(mmoRolePOJO.getType()));
                mmoSimpleRoles.add(mmoSimpleRole);
            }
            mmoScene.setRoles(mmoSimpleRoles);
            //放入spring容器中进行管理
            //将applicationContext转换为ConfigurableApplicationContext
            ConfigurableApplicationContext configurableApplicationContext = (ConfigurableApplicationContext) applicationContext;
// 获取bean工厂并转换为DefaultListableBeanFactory
            DefaultListableBeanFactory defaultListableBeanFactory = (DefaultListableBeanFactory) configurableApplicationContext.getBeanFactory();
            defaultListableBeanFactory.registerSingleton("scene"+mmoScene.getId(),mmoScene);
        }
    }

    private  static List<Integer> split(String str) {
        if (null == str || str.trim().length() == 0) {
            return new ArrayList<Integer>();
        }
        String[] strings=str.split(",");
        List<Integer> res=new ArrayList<>();
        for (String s:strings){
            if (s.isEmpty()){
                continue;
            }
            Integer i=Integer.parseInt(s);
            res.add(i);
        }
        return res;
    }

    public static void main(String[] args) {
        String str=null;
        System.out.println(split(str));

    }

}
