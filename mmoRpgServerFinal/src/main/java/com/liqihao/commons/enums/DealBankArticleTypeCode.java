package com.liqihao.commons.enums;

/**
 * 交易物品模式
 * @author lqhao
 */

public enum DealBankArticleTypeCode {
    //
    SELL(0,"一口价"),AUCTION(1,"竞价");
    private  int code;
    private  String value;
    DealBankArticleTypeCode(int code,String name)
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
        for (DealBankArticleTypeCode ele : values()) {
            if(ele.getCode()==code){
                return ele.getValue();
            }
        }
        return null;
    }
}
