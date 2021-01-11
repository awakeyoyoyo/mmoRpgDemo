package com.liqihao.commons;

/**
 * 自定义异常
 * @author lqhao
 */
public class RpgServerException extends Exception{
    private Integer code;

    private String message;

    public RpgServerException(Integer code,String message) {
        super(message);
        this.code = code;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }
}
