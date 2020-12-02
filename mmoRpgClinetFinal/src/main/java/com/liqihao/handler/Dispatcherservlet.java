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
     * @param nettyResponse
     * @return
     */
    public void handler(NettyResponse nettyResponse) throws InvalidProtocolBufferException {
        short module=nettyResponse.getModule();
        short cmd=nettyResponse.getCmd();
        switch (module){
            case ConstantValue.SCENE_MODULE:
                switch (cmd){
                    case ConstantValue.ASK_CAN_RESPONSE:
                        sceneService.askCanResponse(nettyResponse);
                        break;
                    case ConstantValue.WENT_RESPONSE:
                        sceneService.wentResponse(nettyResponse);
                        break;
                    case ConstantValue.WHERE_RESPONSE:
                        sceneService.whereResponse(nettyResponse);
                        break;
                    case ConstantValue.FIND_ALL_ROLES_RESPONSE:
                        sceneService.findAllRolesResponse(nettyResponse);
                        break;
                    default:
                       System.out.println("handler:收到错误的数据包");
                }
                break;
            case ConstantValue.PLAY_MODULE:
                switch (cmd){
                    case ConstantValue.LOGIN_RESPONSE:
                        playService.loginResponse(nettyResponse);
                        break;
                    case ConstantValue.REGISTER_RESPONSE:
                        playService.registerResponse(nettyResponse);
                        break;
                    case ConstantValue.LOGOUT_RESPONSE:
                        playService.logoutResponse(nettyResponse);
                        break;
                    default:
                        System.out.println("handler:收到错误的数据包");
                }
                break;
            default:
                System.out.println("handler:收到错误的数据包");

        }
    }
}
