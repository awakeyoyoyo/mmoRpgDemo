package com.liqihao.commons.enums;

public enum AttackStyleCode {
    BUFFER(0,"buffer伤害"),ATTACK(1,"攻击&技能伤害"),USESKILL(2,"使用技能造成"),AUTORE(3,"自动恢复");
    private  int code;
    private  String value;
    AttackStyleCode(int code, String name)
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
        for (AttackStyleCode ele : values()) {
            if(ele.getCode()==code){
                return ele.getvalue();
            }
        }
        return null;
    }
}
