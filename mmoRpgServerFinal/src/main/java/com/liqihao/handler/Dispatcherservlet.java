package com.liqihao.handler;

import com.google.protobuf.InvalidProtocolBufferException;
import com.liqihao.commons.ConstantValue;
import com.liqihao.commons.NettyRequest;
import com.liqihao.commons.NettyResponse;
import com.liqihao.commons.StateCode;
import com.liqihao.service.PlayService;
import com.liqihao.service.SceneService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class Dispatcherservlet {
    @Autowired
    private SceneService sceneService;
    @Autowired
    private PlayService playService;

    /**
     * 根据model和cmd转发到不同的service
     * @param nettyRequest
     * @return
     */
    public NettyResponse handler(NettyRequest nettyRequest) throws InvalidProtocolBufferException {
        short module=nettyRequest.getModule();
        short cmd=nettyRequest.getCmd();
        NettyResponse nettyResponse=null;
        switch (module){
            case ConstantValue.SCENE_MODULE:
                switch (cmd){
                    case ConstantValue.ASK_CAN_REQUEST:
                        nettyResponse=sceneService.askCanRequest(nettyRequest);
                        break;
                    case ConstantValue.WENT_REQUEST:
                        nettyResponse=sceneService.wentRequest(nettyRequest);
                        break;
                    case ConstantValue.WHERE_REQUEST:
                        nettyResponse=sceneService.whereRequest(nettyRequest);
                        break;
                    case ConstantValue.FIND_ALL_ROLES_REQUEST:
                        nettyResponse=sceneService.findAllRolesRequest(nettyRequest);
                        break;
                    default:
                        nettyResponse=new NettyResponse(StateCode.FAIL,(short)444,(short)444,"传入错误的cmd".getBytes());
                }
                break;
            case ConstantValue.PLAY_MODULE:
                switch (cmd){
                    case ConstantValue.LOGIN_REQUEST:
                        nettyResponse=playService.loginRequest(nettyRequest);
                        break;
                    case ConstantValue.REGISTER_REQUEST:
                        nettyResponse=playService.registerRequest(nettyRequest);
                        break;
                    case ConstantValue.LOGOUT_REQUEST:
                        nettyResponse=playService.logoutRequest(nettyRequest);
                    default:
                       nettyResponse=new NettyResponse(StateCode.FAIL,(short)444,(short)444,"传入错误的cmd".getBytes());
                }
                break;
            default:
                nettyResponse=new NettyResponse(StateCode.FAIL,(short)444,(short)444,"传入错误的module".getBytes());

        }
        return  nettyResponse;
    }
}
