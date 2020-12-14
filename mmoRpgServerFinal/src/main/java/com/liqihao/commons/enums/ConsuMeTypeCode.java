package com.liqihao.commons.enums;

public enum  ConsuMeTypeCode {
    HP(0,"消耗血量"),MP(1,"消耗蓝量"),NOTHING(2,"无消耗");
    private  int code;
    private  String value;
    ConsuMeTypeCode(int code,String name)
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
        for (ConsuMeTypeCode ele : values()) {
            if(ele.getCode()==code){
                return ele.getvalue();
            }
        }
        return null;
    }
}
