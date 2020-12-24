package com.liqihao.commons.enums;

/**
 * 技能类型 单体或者AOE
 * @author lqhao
 */
public enum  SkillAttackTypeCode {
    SINGLE(0,"单体"),
    ALLPEOPLE(1,"群攻");
    private  int code;
    private  String value;
    SkillAttackTypeCode(int code, String name)
    {
        this.code=code;
        this.value = name;
    }
    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public void setvalue(String name) {
        this.value = name;
    }

    public String getvalue() {
        return value;
    }
    public static String getValue(int code) {
        for (SkillAttackTypeCode ele : values()) {
            if(ele.getCode()==code){
                return ele.getvalue();
            }
        }
        return null;
    }
}
