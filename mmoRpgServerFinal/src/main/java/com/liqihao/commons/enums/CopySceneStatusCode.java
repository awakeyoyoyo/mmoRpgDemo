package com.liqihao.commons.enums;
/**
 * 副本状态枚举类
 * @author lqhao
 */

public enum  CopySceneStatusCode {
    //
    FINISH(0,"已结束"),ONDOING(1,"挑战中");
    private  int code;
    private  String value;
    CopySceneStatusCode(int code,String name)
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
        for (CopySceneStatusCode ele : values()) {
            if(ele.getCode()==code){
                return ele.getvalue();
            }
        }
        return null;
    }
}
