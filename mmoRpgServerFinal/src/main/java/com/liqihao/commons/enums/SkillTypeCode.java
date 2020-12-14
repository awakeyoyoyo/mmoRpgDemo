package com.liqihao.commons.enums;

public enum SkillTypeCode {
    FIED(0,"固伤"),
    PERCENTAGE(1,"百分比");
    private  int code;
    private  String value;
    SkillTypeCode(int code, String name)
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
        for (SkillTypeCode ele : values()) {
            if(ele.getCode()==code){
                return ele.getvalue();
            }
        }
        return null;
    }
}
