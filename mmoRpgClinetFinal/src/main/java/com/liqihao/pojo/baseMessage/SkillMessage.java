package com.liqihao.pojo.baseMessage;

/**
 * 技能信息类
 * @author lqhao
 */
public class SkillMessage {
    /**
     * 基本信息id
     */
    private Integer id;
    /**
     * 技能名称
     */
    private String  skillName;
    /**
     * 基础伤害
     */
    private Integer baseDamage;
    /**
     * cd时间
     */
    private Integer cd;
    /**
     * 消耗类型
     */
    private Integer consumeType;
    /**
     * 消耗
     */
    private Integer consumeNum;
    /**
     * 所造成buffer
     */
    private String bufferIds;
    /**
     * 技能类型
     */
    private Integer skillType;
    /**
     * 按照攻击力加成
     */
    private double addPerson;
    /**
     * 技能攻击类型
     */
    private Integer skillAttackType;
    /**
     * 吟唱时间
     */
    private Integer chantTime;
    /**
     * 伤害类型
     */
    private Integer skillDamageType;

    public Integer getChantTime() {
        return chantTime;
    }

    public void setChantTime(Integer chantTime) {
        this.chantTime = chantTime;
    }

    public Integer getSkillDamageType() {
        return skillDamageType;
    }

    public void setSkillDamageType(Integer skillDamageType) {
        this.skillDamageType = skillDamageType;
    }

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
