package com.liqihao.commons.enums;

/**
 * 技能伤害是加还是扣
 * @author lqhao
 */
public enum SkillDamageTypeCode {
    //
    ADD(1,"增加"),
    REDUCE(0,"扣减");
    private  int code;
    private  String value;
    SkillDamageTypeCode(int code, String name)
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
        for (SkillDamageTypeCode ele : values()) {
            if(ele.getCode()==code){
                return ele.getValue();
            }
        }
        return null;
    }
}
