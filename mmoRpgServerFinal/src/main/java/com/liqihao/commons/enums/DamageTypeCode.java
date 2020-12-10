package com.liqihao.commons.enums;

public enum  DamageTypeCode {
    HP(0,"血量类型"),MP(1,"蓝量类型");
    private  int code;
    private  String value;
    DamageTypeCode(int code,String name)
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
        for (DamageTypeCode ele : values()) {
            if(ele.getCode()==code){
                return ele.getvalue();
            }
        }
        return null;
    }
}
