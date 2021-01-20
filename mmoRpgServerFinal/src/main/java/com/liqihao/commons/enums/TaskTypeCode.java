package com.liqihao.commons.enums;

/**
 * 任务类型
 * @author lqhao
 */

public enum  TaskTypeCode {
    //
    MEDICINE(0,"成就类型"),
    EQUIPMENT(1,"任务类型");
    private  int code;
    private  String value;
    TaskTypeCode(int code, String name)
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
        for (TaskTypeCode ele : values()) {
            if(ele.getCode()==code){
                return ele.getValue();
            }
        }
        return null;
    }
}
