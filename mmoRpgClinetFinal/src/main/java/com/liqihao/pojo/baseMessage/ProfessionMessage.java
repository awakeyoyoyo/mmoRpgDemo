package com.liqihao.pojo.baseMessage;

/**
 * 职业基本信息类
 * @author lqhao
 */
public class ProfessionMessage {
    private Integer id;
    private String name;
    private String skillIds;

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

    public String getSkillIds() {
        return skillIds;
    }

    public void setSkillIds(String skillIds) {
        this.skillIds = skillIds;
    }
}
