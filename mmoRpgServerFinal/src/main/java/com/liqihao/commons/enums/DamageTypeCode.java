package com.liqihao.commons.enums;

/**
 *伤害类型
 * @author LQHAO
 */
public enum  DamageTypeCode {
    //
    HP(0,"血量类型"),MP(1,"蓝量类型"),NOTHING(2,"无消耗");
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

    public void setValue(String name) {
        this.value = name;
    }

    public String getValue() {
        return value;
    }
    public static String getValue(int code) {
        for (DamageTypeCode ele : values()) {
            if(ele.getCode()==code){
                return ele.getValue();
            }
        }
        return null;
    }
}
