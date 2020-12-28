package com.liqihao.pojo.dto;

import java.util.List;

/**
 * 副本的基本信息传输类
 * @author lqhao
 */
public class CopySceneMessageDto {
    private Integer id;
    private String name;
    private Integer lastTime;
    private List<Integer> bossIds;

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

    public Integer getLastTime() {
        return lastTime;
    }

    public void setLastTime(Integer lastTime) {
        this.lastTime = lastTime;
    }

    public List<Integer> getBossIds() {
        return bossIds;
    }

    public void setBossIds(List<Integer> bossIds) {
        this.bossIds = bossIds;
    }
}
