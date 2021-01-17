package com.liqihao.commons.enums;

/**
 * 公会橘色
 * @author lqhao
 */

public enum GuildRolePositionCode {
    //
    HUI_ZHANG(1,"会长"),FU_HUI_ZHANG(2,"副会长")
    ,JIN_YING(3,"精英"), COMMON_PEOPLE(4,"普通会员");
    private  int code;
    private  String value;
    GuildRolePositionCode(int code,String name)
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
        for (GuildRolePositionCode ele : values()) {
            if(ele.getCode()==code){
                return ele.getValue();
            }
        }
        return null;
    }
}
