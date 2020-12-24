package com.liqihao.pojo.baseMessage;

/**
 * 副本基本信息
 * @author lqhao
 */
public class CopySceneMessage {
    private Integer id;
    private String name;
    private Integer lastTime;
    private String bossIds;

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

    public String getBossIds() {
        return bossIds;
    }

    public void setBossIds(String bossIds) {
        this.bossIds = bossIds;
    }
}
