package com.liqihao.handler;

import com.google.protobuf.InvalidProtocolBufferException;
import com.liqihao.annotation.HandlerModuleTag;
import com.liqihao.commons.ConstantValue;
import com.liqihao.commons.NettyRequest;
import com.liqihao.commons.NettyResponse;
import com.liqihao.commons.StateCode;
import com.liqihao.service.SceneService;
import io.netty.channel.Channel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
@HandlerModuleTag(module = ConstantValue.SCENE_MODULE)
public class SceneHandler implements ModuleHandler{
    @Autowired
    private SceneService sceneService;
    /**
     * 根据不同的cmd 转发到不同的service
     * @param nettyRequest
     * @return
     * @throws InvalidProtocolBufferException
     */
    @Override
    public NettyResponse handler(NettyRequest nettyRequest, Channel channel) throws InvalidProtocolBufferException {
        NettyResponse nettyResponse=null;
        switch (nettyRequest.getCmd()) {
            case ConstantValue.ASK_CAN_REQUEST:
                nettyResponse = sceneService.askCanRequest(nettyRequest,channel);
                break;
            case ConstantValue.WENT_REQUEST:
                nettyResponse = sceneService.wentRequest(nettyRequest,channel);
                break;
            case ConstantValue.FIND_ALL_ROLES_REQUEST:
                nettyResponse = sceneService.findAllRolesRequest(nettyRequest,channel);
                break;
            default:
                nettyResponse = new NettyResponse(StateCode.FAIL, (short) 444, (short) 444, "传入错误的cmd".getBytes());
        }
        return nettyResponse;
    }
}
