package com.liqihao.commons.enums;

/**
 * 消耗类型
 * @author LQHAO
 */

public enum ConsumeTypeCode {
    //
    HP(0,"消耗血量"),MP(1,"消耗蓝量"),NOTHING(2,"无消耗");
    private  int code;
    private  String value;
    ConsumeTypeCode(int code, String name)
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
        for (ConsumeTypeCode ele : values()) {
            if(ele.getCode()==code){
                return ele.getValue();
            }
        }
        return null;
    }
}
