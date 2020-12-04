package com.liqihao.handler;

import com.google.protobuf.InvalidProtocolBufferException;
import com.liqihao.codc.ResponceEncoder;
import com.liqihao.commons.ConstantValue;
import com.liqihao.commons.NettyRequest;
import com.liqihao.commons.NettyResponse;
import com.liqihao.commons.StateCode;
import com.liqihao.service.PlayService;
import com.liqihao.service.SceneService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class Dispatcherservlet {
    @Autowired
    private PlayerHandler playerHandler;
    @Autowired
    private SceneHandler sceneHandler;
    private static Logger logger=Logger.getLogger(Dispatcherservlet.class);
    /**
     * 根据model转发到不同的handler
     * @param nettyRequest
     * @return
     */
    public NettyResponse handler(NettyRequest nettyRequest) throws InvalidProtocolBufferException {
        short module=nettyRequest.getModule();
        switch (module){
            case ConstantValue.SCENE_MODULE:
                return sceneHandler.handler(nettyRequest);
            case ConstantValue.PLAY_MODULE:
                return playerHandler.handler(nettyRequest);
            default:
                return new NettyResponse(StateCode.FAIL,(short)444,(short)444,"传入错误的module".getBytes());
        }
    }
}
