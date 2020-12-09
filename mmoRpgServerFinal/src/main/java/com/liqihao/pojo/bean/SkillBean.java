package com.liqihao.pojo.bean;

import java.util.List;

public class SkillBean {
    private Integer id;
    private String  skillName;
    private Integer baseDamage;
    private Integer cd;
    private Integer consumeType;
    private Integer consumeNum;
    private List<Integer> bufferIds;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getSkillName() {
        return skillName;
    }

    public void setSkillName(String skillName) {
        this.skillName = skillName;
    }

    public Integer getBaseDamage() {
        return baseDamage;
    }

    public void setBaseDamage(Integer baseDamage) {
        this.baseDamage = baseDamage;
    }

    public Integer getCd() {
        return cd;
    }

    public void setCd(Integer cd) {
        this.cd = cd;
    }

    public Integer getConsumeType() {
        return consumeType;
    }

    public void setConsumeType(Integer consumeType) {
        this.consumeType = consumeType;
    }

    public Integer getConsumeNum() {
        return consumeNum;
    }

    public void setConsumeNum(Integer consumeNum) {
        this.consumeNum = consumeNum;
    }

    public List<Integer> getBufferIds() {
        return bufferIds;
    }

    public void setBufferIds(List<Integer> bufferIds) {
        this.bufferIds = bufferIds;
    }
}
