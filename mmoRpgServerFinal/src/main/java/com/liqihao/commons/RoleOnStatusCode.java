package com.liqihao.commons;

/**
 * 角色在线状态
 */
public enum  RoleOnStatusCode {
    ONLINE(9527,"在线"),EXIT(7259,"离线");
    private  int code;
    private  String value;
    RoleOnStatusCode(int code,String name)
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
        for (RoleOnStatusCode ele : values()) {
            if(ele.getCode()==code){
                return ele.getvalue();
            }
        }
        return null;
    }
}
