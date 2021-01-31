package com.liqihao.commons.enums;

/**
 *
 * 装备栏部位
 * @author lqhao
 */
public enum PositionCode {
    //
    CAP(1,"帽子"),
    COAT(2,"上衣"),
    BELT(3,"腰带"),
    PANTS(4,"裤子"),
    SHOES(5,"鞋子"),
    ARMS(6,"武器");
    private  int code;
    private  String value;
    PositionCode(int code,String name)
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
        for (PositionCode ele : values()) {
            if(ele.getCode()==code){
                return ele.getValue();
            }
        }
        return null;
    }
}
