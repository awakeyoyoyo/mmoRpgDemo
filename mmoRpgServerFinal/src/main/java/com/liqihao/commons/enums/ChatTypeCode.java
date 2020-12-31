package com.liqihao.commons.enums;

public enum ChatTypeCode {
    //
    ALLPEOPLE(0,"全服频道"),SINGLEPEOPLE(1,"私聊频道");
    private  int code;
    private  String value;
    ChatTypeCode(int code,String name)
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
        for (ChatTypeCode ele : values()) {
            if(ele.getCode()==code){
                return ele.getvalue();
            }
        }
        return null;
    }
}
