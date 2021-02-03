package com.liqihao.handler;

import com.google.protobuf.Parser;



/**
 * 服务类
 * @author lqhao
 */
public class ServiceObject {
    private Object service;
    private Parser parser;

    public Parser getParser() {
        return parser;
    }

    public void setParser(Parser parser) {
        this.parser = parser;
    }

    public Object getService() {
        return service;
    }

    public void setService(Object service) {
        this.service = service;
    }
}
