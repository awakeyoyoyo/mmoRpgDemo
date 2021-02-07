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

    public String getName() {
        return name;
    }

    public String getSkillIds() {
        return skillIds;
    }
}
