package com.liqihao.commons.enums;

/**
 * buffer枚举类型类
 * @author LQHAO
 */

public enum BufferTypeCode {
    //
    ADD_HP(0,"加血buffer"),
    REDUCE_HP(1,"扣血buffer"),
    REDUCE_MP(2,"扣蓝buffer"),
    ADD_MP(3,"加蓝buffer"),
    ADD_ATTACK(4,"增加攻击力"),
    REDUCE_ATTACK(5,"减少攻击力"),
    GG_ATTACK(6,"嘲讽");

    private  int code;
    private  String value;
    BufferTypeCode(int code,String name)
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
        for (BufferTypeCode ele : values()) {
            if(ele.getCode()==code){
                return ele.getValue();
            }
        }
        return null;
    }
}
