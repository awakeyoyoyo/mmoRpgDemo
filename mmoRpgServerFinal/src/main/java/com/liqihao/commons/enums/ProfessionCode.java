package com.liqihao.commons.enums;

/**
 * 职业id
 * @author lqhao
 */
public enum ProfessionCode {
    //
    SOLDIER(1,"战士"),

    PRIEST(2,"牧师"),
    MAGE(3,"法师"),
    TRAINER(4,"召唤师"),
    HELPER(5,"召唤兽");
    private  int code;
    private  String value;
    ProfessionCode(int code,String name)
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
        for (ProfessionCode ele : values()) {
            if(ele.getCode()==code){
                return ele.getValue();
            }
        }
        return null;
    }
}
