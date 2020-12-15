package com.liqihao.pojo.bean;

import java.util.List;

public class SceneBean {
    private  Integer id;
    private String name;
    private List<SceneBean> canScenes;

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

    public List<SceneBean> getCanScenes() {
        return canScenes;
    }

    public void setCanScenes(List<SceneBean> canScenes) {
        this.canScenes = canScenes;
    }
}
