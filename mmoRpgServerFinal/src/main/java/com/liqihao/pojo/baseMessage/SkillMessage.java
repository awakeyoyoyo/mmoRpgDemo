package com.liqihao.pojo.baseMessage;

/**
 * 技能信息类
 * @author lqhao
 */
public class SkillMessage extends BaseMessage{
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

    @Override
    public Integer getTheId() {
        return getId();
    }

    public Integer getChantTime() {
        return chantTime;
    }

    public Integer getSkillDamageType() {
        return skillDamageType;
    }

    public Integer getSkillAttackType() {
        return skillAttackType;
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

    public Integer getSkillType() {
        return skillType;
    }

    public Integer getId() {
        return id;
    }

    public String getSkillName() {
        return skillName;
    }

    public Integer getBaseDamage() {
        return baseDamage;
    }

    public Integer getCd() {
        return cd;
    }

    public Integer getConsumeType() {
        return consumeType;
    }

    public Integer getConsumeNum() {
        return consumeNum;
    }

    public String getBufferIds() {
        return bufferIds;
    }
}
