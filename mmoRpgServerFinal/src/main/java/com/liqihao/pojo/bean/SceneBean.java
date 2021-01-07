package com.liqihao.pojo.bean;

import java.util.List;

/**
 * 场景bean
 * @author lqhao
 */
public class SceneBean {
    private  Integer id;
    private String name;
    private List<Integer> canScenes;
    private List<Integer> roles;
    private List<Integer> npcs;
    private List<MmoHelperBean> helperBeans;

    public List<MmoHelperBean> getHelperBeans() {
        return helperBeans;
    }

    public void setHelperBeans(List<MmoHelperBean> helperBeans) {
        this.helperBeans = helperBeans;
    }

    public List<Integer> getCanScenes() {
        return canScenes;
    }

    public void setCanScenes(List<Integer> canScenes) {
        this.canScenes = canScenes;
    }

    public List<Integer> getRoles() {
        return roles;
    }

    public void setRoles(List<Integer> roles) {
        this.roles = roles;
    }

    public List<Integer> getNpcs() {
        return npcs;
    }

    public void setNpcs(List<Integer> npcs) {
        this.npcs = npcs;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
