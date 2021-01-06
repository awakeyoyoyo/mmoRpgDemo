package com.liqihao.commons.enums;

/**
 * 技能类型 单体或者AOE或者召唤
 * @author lqhao
 */
public enum  SkillAttackTypeCode {
    //
    SINGLE(0,"单体"),
    ALL_PEOPLE(1,"群攻"),
    CALL(2,"召唤");
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

    public void setValue(String name) {
        this.value = name;
    }

    public String getValue() {
        return value;
    }
    public static String getValue(int code) {
        for (SkillAttackTypeCode ele : values()) {
            if(ele.getCode()==code){
                return ele.getValue();
            }
        }
        return null;
    }
}
