package com.liqihao.netty;

import com.liqihao.codc.ResponceEncoder;
import com.liqihao.commons.NettyRequest;
import com.liqihao.commons.NettyResponse;
import com.liqihao.handler.Dispatcherservlet;
import com.liqihao.protobufObject.SceneModel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class ClientHandler extends ChannelInboundHandlerAdapter {
    private static Logger logger=Logger.getLogger(ClientHandler.class);
    private Dispatcherservlet dispatcherservlet;

    public ClientHandler() {
    }

    public ClientHandler(Dispatcherservlet dispatcherservlet) {
        this.dispatcherservlet = dispatcherservlet;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        logger.info("CLIENT:channelActive");
        //发送一个当前askCanRequest

    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        logger.info("CLIENT:channelRead");
        NettyResponse response= (NettyResponse) msg;
        dispatcherservlet.handler(response);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        logger.error("ClientHandler exception message: "+cause);
    }
}
