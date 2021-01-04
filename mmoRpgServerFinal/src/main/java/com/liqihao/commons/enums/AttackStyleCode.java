package com.liqihao.commons.enums;

/**
 * 伤害来源枚举类
 * @author lqhao
 */

public enum AttackStyleCode {
    //
    BUFFER(0,"buffer伤害"),
    ATTACK(1,"攻击&技能伤害"),
    USE_SKILL(2,"使用技能造成"),
    AUTO_RE(3,"自动恢复"),
    MEDICINE(4,"药品恢复");
    private  int code;
    private  String value;
    AttackStyleCode(int code,String name)
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
        for (AttackStyleCode ele : values()) {
            if(ele.getCode()==code){
                return ele.getValue();
            }
        }
        return null;
    }
}
