package com.liqihao.commons.enums;

/**
 * @author 公会权限
 */

public enum GuildAuthorityCode {
    //
    GET_ARTICLE(1,"收取道具"),GET_MONEY(1,"收取金币"),PUT_ARTICLE(2,"放入道具"),
    PUT_MONEY(4,"放入金币"),SET_POSITION(5,"设置职位");
    private  int code;
    private  String value;
    GuildAuthorityCode(int code,String name)
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
        for (GuildAuthorityCode ele : values()) {
            if(ele.getCode()==code){
                return ele.getValue();
            }
        }
        return null;
    }
}
