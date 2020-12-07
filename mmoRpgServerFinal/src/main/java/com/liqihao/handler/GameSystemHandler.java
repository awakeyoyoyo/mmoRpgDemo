package com.liqihao.handler;

import com.google.protobuf.InvalidProtocolBufferException;
import com.liqihao.annotation.HandlerModuleTag;
import com.liqihao.annotation.HandlerServiceTag;
import com.liqihao.commons.ConstantValue;
import com.liqihao.commons.NettyRequest;
import com.liqihao.commons.NettyResponse;
import com.liqihao.commons.StateCode;
import com.liqihao.service.GameSystemService;
import io.netty.channel.Channel;
import org.omg.CORBA.PRIVATE_MEMBER;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
@HandlerModuleTag(module = ConstantValue.GAME_SYSTEM_MODULE)
public class GameSystemHandler implements ModuleHandler{
    private List<Method> gameSystemServiceMethods=null;
    @Autowired
    private GameSystemService gameSystemService;
    /**
     * 根据不同的cmd 转发到不同的service
     * @param nettyRequest
     * @return
     * @throws InvalidProtocolBufferException
     */
    @Override
    public NettyResponse handler(NettyRequest nettyRequest, Channel channel) throws InvalidProtocolBufferException, InvocationTargetException, IllegalAccessException {
        if (null==gameSystemServiceMethods){
            List<Method> methods=new ArrayList<>(Arrays.asList(GameSystemService.class.getMethods()));
            gameSystemServiceMethods=methods;
        }
        for (Method m:gameSystemServiceMethods) {
            if (m.getAnnotation(HandlerServiceTag.class).cmd()==nettyRequest.getCmd()){
                return (NettyResponse) m.invoke(gameSystemService,nettyRequest,channel);
            }
        }
        return  new NettyResponse(StateCode.FAIL,(short)444,(short)444,"传入错误的cmd".getBytes());
    }
}
