package com.liqihao.pojo.baseMessage;

/**
 * 职业基本信息类
 * @author lqhao
 */
public class ProfessionMessage extends BaseMessage{
    private Integer id;
    private String name;
    private String skillIds;
    @Override
    public Integer getTheId() {
        return getId();
    }
    public Integer getId() {
        return id;
    }

    private void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    private void setName(String name) {
        this.name = name;
    }

    public String getSkillIds() {
        return skillIds;
    }

    private void setSkillIds(String skillIds) {
        this.skillIds = skillIds;
    }
}
