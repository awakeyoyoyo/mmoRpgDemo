package com.liqihao.commons.enums;

/**
 * 角色生存状态
 * @author LQHAO
 */
public enum  RoleStatusCode {
    //
    DIE(0,"死亡"),ALIVE(1,"存活");
    private  int code;
    private  String value;
    RoleStatusCode(int code,String name)
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
        for (RoleStatusCode ele : values()) {
            if(ele.getCode()==code){
                return ele.getValue();
            }
        }
        return null;
    }
}
