package com.liqihao.handler;

import com.google.protobuf.Parser;
import com.liqihao.annotation.HandlerCmdTag;
import com.liqihao.annotation.HandlerServiceTag;
import com.liqihao.commons.ConstantValue;
import com.liqihao.commons.NettyRequest;
import com.liqihao.commons.NettyResponse;
import com.liqihao.commons.StateCode;
import com.liqihao.pojo.bean.roleBean.MmoSimpleRole;
import com.liqihao.util.CommonsUtil;
import io.netty.channel.Channel;
import org.apache.log4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * 根据module分发请求
 *
 * @author lqhao
 */
@Component
public class DispatcherServlet implements ApplicationContextAware {
    private static final Logger logger = Logger.getLogger(DispatcherServlet.class);
    private Map<String, ServiceObject> services;
    private final HashMap<Integer, Method> methodHashMap = new HashMap<>();
    private final static String PACKET = "com.liqihao.protobufObject.";

    /**
     * 根据model转发到不同的handler
     *
     * @param nettyRequest
     * @return
     */
    public void handler(NettyRequest nettyRequest, Channel channel) {
        int cmd = nettyRequest.getCmd();
        MmoSimpleRole mmoSimpleRole = null;
        //根据指令获取方法对象
        Method m = methodHashMap.get(cmd);
        //特殊的登陆以及注册接口
        if (cmd == ConstantValue.LOGIN_REQUEST || cmd == ConstantValue.REGISTER_REQUEST
                || cmd == ConstantValue.LOGOUT_REQUEST || cmd == ConstantValue.OUT_RIME_RESPONSE) {
            String beanName = m.getAnnotation(HandlerCmdTag.class).module();
            ServiceObject serviceObject = services.get(beanName);
            try {
                Object object = serviceObject.getParser().parseFrom(nettyRequest.getData());
                m.invoke(serviceObject.getService(), object, channel);
            } catch (InvocationTargetException e) {
                e.printStackTrace();
                sendException(channel, e.getTargetException().getMessage());
            } catch (Exception e) {
                e.printStackTrace();
                sendException(channel, e.getMessage());
            }
            return;
        }

        //业务逻辑处理
        if (m != null) {
            String beanName = m.getAnnotation(HandlerCmdTag.class).module();
            ServiceObject serviceObject = services.get(beanName);
            try {
                mmoSimpleRole = CommonsUtil.checkLogin(channel);
                Object object = serviceObject.getParser().parseFrom(nettyRequest.getData());
                m.invoke(serviceObject.getService(), object, mmoSimpleRole);
            } catch (InvocationTargetException e) {
                e.printStackTrace();
                sendException(channel, e.getTargetException().getMessage());
            } catch (Exception e) {
                e.printStackTrace();
                sendException(channel, e.getMessage());
            }
            return;
        }
        channel.writeAndFlush(new NettyResponse(StateCode.FAIL, ConstantValue.FAIL_RESPONSE, "传入错误的cmd".getBytes()));
    }

    /**
     * 返回错误信息
     * @param ctx
     * @param cause
     */
    private void sendException(Channel ctx, String cause) {
        NettyResponse nettyResponse = new NettyResponse();
        nettyResponse.setCmd(ConstantValue.FAIL_RESPONSE);
        String message = "服务端抛出异常：" + cause;
        nettyResponse.setData(message.getBytes(StandardCharsets.UTF_8));
        nettyResponse.setStateCode(StateCode.FAIL);
        ctx.writeAndFlush(nettyResponse);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        //key是beanName value是对象
        Map<String, Object> serviceMap = applicationContext.getBeansWithAnnotation(HandlerServiceTag.class);
        services = new HashMap<>(32);
        for (String key : serviceMap.keySet()) {
            Object o = serviceMap.get(key);
            Method[] methods = o.getClass().getMethods();
            String protobufModel = o.getClass().getAnnotation(HandlerServiceTag.class).protobufModel();
            for (Method value : methods) {
                if (value.getAnnotation(HandlerCmdTag.class) != null) {
                    Class clazz = null;
                    Parser parser = null;
                    try {
                        clazz = Class.forName(PACKET + protobufModel);
                        Method method = clazz.getMethod("parser");
                        parser = (Parser) method.invoke(null);
                    } catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                        e.printStackTrace();
                    }
                    ServiceObject serviceObject = new ServiceObject();
                    serviceObject.setService(o);
                    serviceObject.setParser(parser);
                    //将serviceObject对象存储起来
                    services.put(key, serviceObject);
                    //将cmd与其映射的方法存储起来
                    methodHashMap.put(value.getAnnotation(HandlerCmdTag.class).cmd(), value);
                }
            }
        }
    }
}
