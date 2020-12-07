package com.liqihao.handler;

import com.google.protobuf.InvalidProtocolBufferException;
import com.liqihao.annotation.HandlerModuleTag;
import com.liqihao.codc.ResponceEncoder;
import com.liqihao.commons.ConstantValue;
import com.liqihao.commons.NettyRequest;
import com.liqihao.commons.NettyResponse;
import com.liqihao.commons.StateCode;
import com.liqihao.service.GameSystemService;
import com.liqihao.service.PlayService;
import com.liqihao.service.SceneService;
import io.netty.channel.Channel;
import org.apache.log4j.Logger;
import org.omg.CORBA.PRIVATE_MEMBER;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;

/**
 * 根据module分发请求
 */
@Component
public class Dispatcherservlet {
    @Autowired
    private List<ModuleHandler> moduleHandlers;
    private static Logger logger=Logger.getLogger(Dispatcherservlet.class);
    /**
     * 根据model转发到不同的handler
     * @param nettyRequest
     * @return
     */
    public NettyResponse handler(NettyRequest nettyRequest, Channel channel) throws InvalidProtocolBufferException, InvocationTargetException, IllegalAccessException {
        short module=nettyRequest.getModule();
        for (ModuleHandler m:moduleHandlers) {
            if (m.getClass().getAnnotation(HandlerModuleTag.class).module()==module){
                return m.handler(nettyRequest,channel);
            }
        }
        return new NettyResponse(StateCode.FAIL,(short)444,(short)444,"传入错误的module".getBytes());
    }
}
