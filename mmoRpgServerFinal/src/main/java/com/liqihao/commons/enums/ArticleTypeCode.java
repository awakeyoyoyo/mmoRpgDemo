package com.liqihao.commons.enums;

public enum  ArticleTypeCode {
    //
    MEDICINE(0,"药品"),EQUIPMENT(1,"装备"),ACTIVITY(2,"活动道具"),OTHERS(3,"其他");
    private  int code;
    private  String value;
    ArticleTypeCode(int code,String name)
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
        for (ArticleTypeCode ele : values()) {
            if(ele.getCode()==code){
                return ele.getValue();
            }
        }
        return null;
    }
}
