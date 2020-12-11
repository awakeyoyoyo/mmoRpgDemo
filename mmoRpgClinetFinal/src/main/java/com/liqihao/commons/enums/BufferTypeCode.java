package com.liqihao.commons.enums;

public enum BufferTypeCode {
    ADDHP(0,"加血buffer"),
    REDUCEHP(1,"扣血buffer"),
    REDUCEMP(2,"扣蓝buffer"),
    ADDMP(3,"加蓝buffer"),
    ADDATTACK(4,"增加攻击力"),
    REDUCEATTACK(5,"减少攻击力");
    private  int code;
    private  String value;
    BufferTypeCode(int code, String name)
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
        for (BufferTypeCode ele : values()) {
            if(ele.getCode()==code){
                return ele.getvalue();
            }
        }
        return null;
    }
}
