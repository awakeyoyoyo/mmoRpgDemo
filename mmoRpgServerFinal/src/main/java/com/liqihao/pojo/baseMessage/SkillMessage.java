package com.liqihao.pojo.baseMessage;

/**
 * 技能信息类
 * @author lqhao
 */
public class SkillMessage {
    private Integer id;
    private String  skillName;
    private Integer baseDamage;
    private Integer cd;
    private Integer consumeType;
    private Integer consumeNum;
    private String bufferIds;
    private Integer skillType;
    private double addPerson;
    private Integer skillAttackType;

    public Integer getSkillAttackType() {
        return skillAttackType;
    }

    public void setSkillAttackType(Integer skillAttackType) {
        this.skillAttackType = skillAttackType;
    }

    @Override
    public String toString() {
        return "SkillMessage{" +
                "id=" + id +
                ", skillName='" + skillName + '\'' +
                ", baseDamage=" + baseDamage +
                ", cd=" + cd +
                ", consumeType=" + consumeType +
                ", consumeNum=" + consumeNum +
                ", bufferIds='" + bufferIds + '\'' +
                ", skillType=" + skillType +
                ", addPerson=" + addPerson +
                '}';
    }

    public double getAddPerson() {
        return addPerson;
    }

    public void setAddPerson(double addPerson) {
        this.addPerson = addPerson;
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
