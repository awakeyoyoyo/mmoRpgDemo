package com.liqihao.handler;

import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.Parser;
import com.liqihao.annotation.HandlerCmdTag;
import com.liqihao.annotation.HandlerServiceTag;
import com.liqihao.commons.NettyRequest;
import com.liqihao.commons.NettyResponse;
import com.liqihao.commons.StateCode;
import com.liqihao.protobufObject.BackPackModel;
import io.netty.channel.Channel;
import org.apache.log4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * 根据module分发请求
 *
 * @author lqhao
 */
@Component
public class Dispatcherservlet implements ApplicationContextAware {
    private ApplicationContext applicationContext;
    private static Logger logger = Logger.getLogger(Dispatcherservlet.class);
    private Map<String, ServiceObject> services;
    private HashMap<Integer, Method> methodHashMap = new HashMap<>();
    private final static String packet = "com.liqihao.protobufObject.";

    /**
     * 根据model转发到不同的handler
     *
     * @param nettyRequest
     * @return
     */
    public void handler(NettyRequest nettyRequest, Channel channel) throws InvalidProtocolBufferException, InvocationTargetException, IllegalAccessException, ClassNotFoundException, NoSuchMethodException {
        logger.info("线程：" + Thread.currentThread().getName() + " 正在处理该请求");
        int cmd = nettyRequest.getCmd();
        Method m = methodHashMap.get(cmd);
        if (m != null) {
            String beanName = m.getAnnotation(HandlerCmdTag.class).module();
            ServiceObject serviceObject = services.get(beanName);
            Object object = serviceObject.getParser().parseFrom(nettyRequest.getData());
            m.invoke(serviceObject.getService(), object, channel);
            return;
        }
        channel.writeAndFlush(new NettyResponse(StateCode.FAIL, 444, "传入错误的cmd".getBytes()));
        return;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
        Map<String, Object> serviceMap = applicationContext.getBeansWithAnnotation(HandlerServiceTag.class);
        services=new HashMap<>();
        for (String key : serviceMap.keySet()) {
            Object o = serviceMap.get(key);
            Method[] methods = o.getClass().getMethods();
            for (int i = 0; i < methods.length; i++) {
                String protobufModel = o.getClass().getAnnotation(HandlerServiceTag.class).protobufModel();
                Class clazz = null;
                Parser parser=null;
                try {
                    clazz = Class.forName(packet + protobufModel);
                    Method method = clazz.getMethod("parser");
                    parser = (Parser) method.invoke(null);
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
                ServiceObject serviceObject = new ServiceObject();
                serviceObject.setService(o);
                serviceObject.setClazz(clazz);
                serviceObject.setParser(parser);
                services.put(key, serviceObject);
                if (methods[i].getAnnotation(HandlerCmdTag.class) != null) {
                    methodHashMap.put(methods[i].getAnnotation(HandlerCmdTag.class).cmd(), methods[i]);
                }
            }
        }
    }
}
