package com.liqihao.pojo;

import com.liqihao.commons.enums.RoleOnStatusCode;
import com.liqihao.commons.enums.RoleStatusCode;
import com.liqihao.commons.enums.RoleTypeCode;

public class MmoRolePOJO {
    private Integer id;

    private Integer status;

    private String name;

    private Integer type;

    private Integer mmoSceneId;

    private Integer onStatus;

    private String skillIds;

    private Integer money;

    private Integer professionId;

    private Integer guildId;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Integer getMmoSceneId() {
        return mmoSceneId;
    }

    public void setMmoSceneId(Integer mmoSceneId) {
        this.mmoSceneId = mmoSceneId;
    }

    public Integer getOnStatus() {
        return onStatus;
    }

    public void setOnStatus(Integer onStatus) {
        this.onStatus = onStatus;
    }

    public String getSkillIds() {
        return skillIds;
    }

    public void setSkillIds(String skillIds) {
        this.skillIds = skillIds == null ? null : skillIds.trim();
    }

    public Integer getMoney() {
        return money;
    }

    public void setMoney(Integer money) {
        this.money = money;
    }

    public Integer getProfessionId() {
        return professionId;
    }

    public void setProfessionId(Integer professionId) {
        this.professionId = professionId;
    }

    public Integer getGuildId() {
        return guildId;
    }

    public void setGuildId(Integer guildId) {
        this.guildId = guildId;
    }
    /**
     * description 初始化方法
     * @param roleName
     * @return
     * @author lqhao
     * @createTime 2021/1/21 15:18
     */
    public void init(String roleName) {
        setName(roleName);
        setMmoSceneId(1);
        setStatus(RoleStatusCode.ALIVE.getCode());
        setOnStatus(RoleOnStatusCode.EXIT.getCode());
        setType(RoleTypeCode.PLAYER.getCode());
        //职业
        setProfessionId(1);
        setGuildId(-1);
    }
}