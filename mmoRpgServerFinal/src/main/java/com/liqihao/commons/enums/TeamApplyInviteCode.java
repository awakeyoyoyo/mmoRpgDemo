package com.liqihao.commons.enums;
public enum  TeamApplyInviteCode {
    //
    APPLY(0,"入队申请"),
    INVITE(1,"入队邀请");
    private  int code;
    private  String value;
    TeamApplyInviteCode(int code, String name)
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
        for (TeamApplyInviteCode ele : values()) {
            if(ele.getCode()==code){
                return ele.getvalue();
            }
        }
        return null;
    }
}
