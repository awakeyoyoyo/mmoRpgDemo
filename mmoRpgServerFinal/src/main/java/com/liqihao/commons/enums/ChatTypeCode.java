package com.liqihao.commons.enums;

/**
 * 聊天频道
 * @author lqhao
 */

public enum ChatTypeCode {
    //
    ALL_PEOPLE(0,"全服频道"),SINGLE_PEOPLE(1,"私聊频道"),SCENE_PEOPLE(2,"场景频道"),TEAM_PEOPLE(3,"队伍频道");
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
    public void setValue(String name) {
        this.value = name;
    }
    public String getValue() {
        return value;
    }
    public static String getValue(int code) {
        for (ChatTypeCode ele : values()) {
            if(ele.getCode()==code){
                return ele.getValue();
            }
        }
        return null;
    }
}
