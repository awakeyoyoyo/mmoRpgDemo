package com.liqihao.commons.enums;

/**
 * 药品类型
 * @author LQHAO
 */

public enum MedicineTypeCode {
    //
    MOMENT(0,"瞬间恢复"),CONTINUED(1,"持续恢复");
    private  int code;
    private  String value;
    MedicineTypeCode(int code,String name)
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
        for (MedicineTypeCode ele : values()) {
            if(ele.getCode()==code){
                return ele.getValue();
            }
        }
        return null;
    }
}
