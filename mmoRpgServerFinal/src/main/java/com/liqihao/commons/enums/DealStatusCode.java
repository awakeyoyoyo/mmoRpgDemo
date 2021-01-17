package com.liqihao.commons.enums;

/**
 * 交易状态
 * @author lqhao
 */

public enum DealStatusCode {
    //
    WAIT(0,"等待接收交易邀请"),ON_DEAL(1,"交易进行中"),FINISH(2,"交易完成");
    private  int code;
    private  String value;
    DealStatusCode(int code,String name)
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
        for (DealStatusCode ele : values()) {
            if(ele.getCode()==code){
                return ele.getValue();
            }
        }
        return null;
    }
}
