package com.liqihao.commons.enums;

/**
 * buffer的生效方式，间隔还是持续
 * @author lqhao
 */
public enum BufferStyleCode {
    //
    SPACE_DO(0,"间隔生效"),
    EVERYTIME_DO(1,"持续生效");
    private  int code;
    private  String value;
    BufferStyleCode(int code,String name)
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
        for (BufferStyleCode ele : values()) {
            if(ele.getCode()==code){
                return ele.getValue();
            }
        }
        return null;
    }
}
