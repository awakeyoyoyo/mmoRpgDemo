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
    private ApplicationContext applicationContext;
    private static Logger logger = Logger.getLogger(DispatcherServlet.class);
    private Map<String, ServiceObject> services;
    private HashMap<Integer, Method> methodHashMap = new HashMap<>();
    private final static String packet = "com.liqihao.protobufObject.";

    /**
     * 根据model转发到不同的handler
     *
     * @param nettyRequest
     * @return
     */
    public void handler(NettyRequest nettyRequest, Channel channel) {
        logger.info("线程：" + Thread.currentThread().getName() + " 正在处理该请求");
        int cmd = nettyRequest.getCmd();
        Method m = methodHashMap.get(cmd);
        MmoSimpleRole mmoSimpleRole=null;

        //特殊的登陆以及注册接口
        if (cmd==ConstantValue.LOGIN_REQUEST||cmd==ConstantValue.REGISTER_REQUEST
                ||cmd==ConstantValue.LOGOUT_REQUEST||cmd==ConstantValue.OUT_RIME_RESPONSE) {
            String beanName = m.getAnnotation(HandlerCmdTag.class).module();
            ServiceObject serviceObject = services.get(beanName);
            try {
            Object object = serviceObject.getParser().parseFrom(nettyRequest.getData());
            m.invoke(serviceObject.getService(), object, channel);
            }catch (InvocationTargetException e){
                e.printStackTrace();
                sendException(channel,e.getTargetException().getMessage());
            } catch (Exception e) {
                e.printStackTrace();
                sendException(channel,e.getMessage());
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
            }catch (InvocationTargetException e){
                e.printStackTrace();
                sendException(channel,e.getTargetException().getMessage());
            } catch (Exception e) {
                e.printStackTrace();
                sendException(channel,e.getMessage());
            }
            return;
        }
        channel.writeAndFlush(new NettyResponse(StateCode.FAIL, ConstantValue.FAIL_RESPONSE, "传入错误的cmd".getBytes()));
    }

    private void sendException(Channel ctx, String  cause){
        NettyResponse nettyResponse=new NettyResponse();
        nettyResponse.setCmd(ConstantValue.FAIL_RESPONSE);
        String message="服务端抛出异常："+cause;
        nettyResponse.setData(message.getBytes(StandardCharsets.UTF_8));
        nettyResponse.setStateCode(StateCode.FAIL);
        ctx.writeAndFlush(nettyResponse);
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
                } catch (ClassNotFoundException | NoSuchMethodException e) {
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
