package com.liqihao.handler;

import com.google.protobuf.InvalidProtocolBufferException;
import com.liqihao.annotation.HandlerModuleTag;
import com.liqihao.annotation.HandlerServiceTag;
import com.liqihao.commons.ConstantValue;
import com.liqihao.commons.NettyRequest;
import com.liqihao.commons.NettyResponse;
import com.liqihao.commons.StateCode;
import com.liqihao.service.PlayService;
import io.netty.channel.Channel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 根据cmd分发请求
 */
@Component
@HandlerModuleTag(module = ConstantValue.PLAY_MODULE)
public class PlayerHandler implements ModuleHandler {
    @Autowired
    private PlayService playService;
    private List<Method> playServiceMethods=null;
    /**
     * 根据不同的cmd 转发到不同的service
     * @param nettyRequest
     * @return
     * @throws InvalidProtocolBufferException
     */
    @Override
    public NettyResponse handler(NettyRequest nettyRequest, Channel channel) throws InvalidProtocolBufferException, InvocationTargetException, IllegalAccessException {
        if (null==playServiceMethods){
            List<Method> methods=new ArrayList<>(Arrays.asList(PlayService.class.getMethods()));
            playServiceMethods=methods;
        }
        for (Method m:playServiceMethods) {
            if (m.getAnnotation(HandlerServiceTag.class).cmd()==nettyRequest.getCmd()){
                return (NettyResponse) m.invoke(playService,nettyRequest,channel);
            }
        }
        return new NettyResponse(StateCode.FAIL,(short)444,(short)444,"传入错误的cmd".getBytes());
    }

}
