package com.liqihao.commons.enums;

/**
 * 副本状态
 * @author lqhao
 */

public enum CopySceneBeanStatusCode {
    //
    NO_START(0,"未开始挑战"), IS_START(1,"已开始挑战");
    private  int code;
    private  String value;
    CopySceneBeanStatusCode(int code,String name)
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
        for (CopySceneBeanStatusCode ele : values()) {
            if(ele.getCode()==code){
                return ele.getValue();
            }
        }
        return null;
    }
}
