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
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 根据module分发请求
 */
@Component
public class Dispatcherservlet implements ApplicationContextAware {
    private ApplicationContext applicationContext;
    private static Logger logger = Logger.getLogger(Dispatcherservlet.class);
    private Map<String, Object> services;
    private HashMap<Integer, Method> methodHashMap = new HashMap<>();

    /**
     * 根据model转发到不同的handler
     *
     * @param nettyRequest
     * @return
     */
    public void handler(NettyRequest nettyRequest, Channel channel) throws InvalidProtocolBufferException, InvocationTargetException, IllegalAccessException {
        logger.info("线程："+Thread.currentThread().getName()+" 正在处理该请求");
        int cmd = nettyRequest.getCmd();
        Method m = methodHashMap.get(cmd);
        if (m != null) {
            String beanName = m.getAnnotation(HandlerCmdTag.class).module();
            m.invoke(services.get(beanName), nettyRequest, channel);
            return;
        }
        channel.writeAndFlush(new NettyResponse(StateCode.FAIL, 444, "传入错误的cmd".getBytes()));
        return;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
        Map<String, Object> serviceMap = applicationContext.getBeansWithAnnotation(HandlerServiceTag.class);
        services = serviceMap;
        for (Object o : serviceMap.values()) {
            Method[] methods = o.getClass().getMethods();
            for (int i = 0; i < methods.length; i++) {
                if (methods[i].getAnnotation(HandlerCmdTag.class) != null) {
                    methodHashMap.put(methods[i].getAnnotation(HandlerCmdTag.class).cmd(), methods[i]);
                }
            }
        }
    }
}
