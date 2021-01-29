package com.liqihao.commons.enums;

/**
 * @author lqhao
 * 任务状态
 */
public enum TaskStateCode {
    //
    ON_DOING(0,"正在进行中"),
    FINISH(1,"已完成"),
    END(2,"已领取奖励");
    private  int code;
    private  String value;

    TaskStateCode(int code, String name)
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
        for (TaskStateCode ele : values()) {
            if(ele.getCode()==code){
                return ele.getValue();
            }
        }
        return null;
    }
}
