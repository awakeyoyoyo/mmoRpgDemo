package com.liqihao.handler;

import com.google.protobuf.InvalidProtocolBufferException;
import com.liqihao.annotation.HandlerCmdTag;
import com.liqihao.annotation.HandlerServiceTag;
import com.liqihao.commons.NettyRequest;
import com.liqihao.commons.NettyResponse;
import com.liqihao.commons.enums.StateCode;
import io.netty.channel.Channel;
import org.apache.log4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

/**
 * 根据module分发请求
 */
@Component
public class Dispatcherservlet implements ApplicationContextAware {
    private  ApplicationContext applicationContext;
    private static Logger logger=Logger.getLogger(Dispatcherservlet.class);
    private Map<String, Object>  services;
    private ArrayList<Method> methods=new ArrayList<>();
    /**
     * 根据model转发到不同的handler
     * @param nettyRequest
     * @return
     */
    public NettyResponse handler(NettyRequest nettyRequest, Channel channel) throws InvalidProtocolBufferException, InvocationTargetException, IllegalAccessException {
        int cmd=nettyRequest.getCmd();
        for (Method m:methods) {
            if (m.getAnnotation(HandlerCmdTag.class)!=null&&m.getAnnotation(HandlerCmdTag.class).cmd()==cmd){
                String beanName=m.getAnnotation(HandlerCmdTag.class).module();
                return (NettyResponse)m.invoke(services.get(beanName),nettyRequest,channel);
            }
        }
        return new NettyResponse(StateCode.FAIL,444,"传入错误的cmd".getBytes());
    }
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext=applicationContext;
        Map<String, Object>  serviceMap=applicationContext.getBeansWithAnnotation(HandlerServiceTag.class);
        services=serviceMap;
        for (Object o:serviceMap.values()) {
            methods.addAll(Arrays.asList(o.getClass().getMethods()));
        }
    }
}
