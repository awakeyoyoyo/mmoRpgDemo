package com.liqihao.handler;

import com.google.protobuf.InvalidProtocolBufferException;
import com.liqihao.commons.ConstantValue;
import com.liqihao.commons.NettyRequest;
import com.liqihao.commons.NettyResponse;
import com.liqihao.commons.StateCode;
import com.liqihao.service.PlayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PlayerHandler {
    @Autowired
    private PlayService playService;

    /**
     * 根据不同的cmd 转发到不同的service
     * @param nettyRequest
     * @return
     * @throws InvalidProtocolBufferException
     */
    public NettyResponse handler(NettyRequest nettyRequest) throws InvalidProtocolBufferException {
        NettyResponse nettyResponse=null;
        switch (nettyRequest.getCmd()){
            case ConstantValue.LOGIN_REQUEST:
                nettyResponse=playService.loginRequest(nettyRequest);
                break;
            case ConstantValue.REGISTER_REQUEST:
                nettyResponse=playService.registerRequest(nettyRequest);
                break;
            case ConstantValue.LOGOUT_REQUEST:
                nettyResponse=playService.logoutRequest(nettyRequest);
                break;
            default:
                nettyResponse=new NettyResponse(StateCode.FAIL,(short)444,(short)444,"传入错误的cmd".getBytes());
        }
        return nettyResponse;
    }
}
