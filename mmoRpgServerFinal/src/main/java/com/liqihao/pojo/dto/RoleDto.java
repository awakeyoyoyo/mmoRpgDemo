package com.liqihao.pojo.dto;

/**
 * 角色简单信息传输类
 * @author lqhao
 */
public class RoleDto {
    Integer id;
    String name;
    Integer hp;
    Integer mp;
    Integer nowHp;
    Integer nowMP;
    Integer teamId;

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

    public Integer getHp() {
        return hp;
    }

    public void setHp(Integer hp) {
        this.hp = hp;
    }

    public Integer getMp() {
        return mp;
    }

    public void setMp(Integer mp) {
        this.mp = mp;
    }

    public Integer getNowHp() {
        return nowHp;
    }

    public void setNowHp(Integer nowHp) {
        this.nowHp = nowHp;
    }

    public Integer getNowMP() {
        return nowMP;
    }

    public void setNowMP(Integer nowMP) {
        this.nowMP = nowMP;
    }

    public Integer getTeamId() {
        return teamId;
    }

    public void setTeamId(Integer teamId) {
        this.teamId = teamId;
    }
}
