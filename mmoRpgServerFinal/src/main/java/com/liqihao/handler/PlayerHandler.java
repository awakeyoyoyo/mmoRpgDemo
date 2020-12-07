package com.liqihao.handler;

import com.google.protobuf.InvalidProtocolBufferException;
import com.liqihao.annotation.HandlerModuleTag;
import com.liqihao.commons.ConstantValue;
import com.liqihao.commons.NettyRequest;
import com.liqihao.commons.NettyResponse;
import com.liqihao.commons.StateCode;
import com.liqihao.service.PlayService;
import io.netty.channel.Channel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@HandlerModuleTag(module = ConstantValue.PLAY_MODULE)
public class PlayerHandler implements ModuleHandler {
    @Autowired
    private PlayService playService;

    /**
     * 根据不同的cmd 转发到不同的service
     * @param nettyRequest
     * @return
     * @throws InvalidProtocolBufferException
     */
    @Override
    public NettyResponse handler(NettyRequest nettyRequest, Channel channel) throws InvalidProtocolBufferException {
        NettyResponse nettyResponse=null;
        switch (nettyRequest.getCmd()){
            case ConstantValue.LOGIN_REQUEST:
                nettyResponse=playService.loginRequest(nettyRequest,channel);
                break;
            case ConstantValue.REGISTER_REQUEST:
                nettyResponse=playService.registerRequest(nettyRequest, channel);
                break;
            case ConstantValue.LOGOUT_REQUEST:
                nettyResponse=playService.logoutRequest(nettyRequest,channel);
                break;
            default:
                nettyResponse=new NettyResponse(StateCode.FAIL,(short)444,(short)444,"传入错误的cmd".getBytes());
        }
        return nettyResponse;
    }

}
