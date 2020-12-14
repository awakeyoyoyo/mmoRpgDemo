package com.liqihao.pojo.baseMessage;

public class SkillMessage {
    private Integer id;
    private String  skillName;
    private Integer baseDamage;
    private Integer cd;
    private Integer consumeType;
    private Integer consumeNum;
    private String bufferIds;
    private Integer skillType;
    private double addPercon;

    public double getAddPercon() {
        return addPercon;
    }

    public void setAddPercon(double addPercon) {
        this.addPercon = addPercon;
    }

    public Integer getSkillType() {
        return skillType;
    }

    public void setSkillType(Integer skillType) {
        this.skillType = skillType;
    }
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

    public String getBufferIds() {
        return bufferIds;
    }

    public void setBufferIds(String bufferIds) {
        this.bufferIds = bufferIds;
    }
}
