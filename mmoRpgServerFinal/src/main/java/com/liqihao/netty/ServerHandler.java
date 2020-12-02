package com.liqihao.netty;

import com.liqihao.commons.NettyRequest;
import com.liqihao.commons.NettyResponse;
import com.liqihao.handler.Dispatcherservlet;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ServerHandler extends ChannelInboundHandlerAdapter {
    private Dispatcherservlet dispatcherservlet;
    private static final Logger log = LoggerFactory.getLogger(ServerHandler.class);

    public ServerHandler() {
    }

    public ServerHandler(Dispatcherservlet dispatcherservlet) {
        this.dispatcherservlet = dispatcherservlet;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("Server:channelActive");
        log.info("["+ctx.channel().remoteAddress()+"] connected");
        System.out.println("["+ctx.channel().remoteAddress()+"] connected");
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println("Server:channelRead");
        NettyRequest request= (NettyRequest) msg;
        NettyResponse response=dispatcherservlet.handler(request);
        ctx.writeAndFlush(response);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("ServerHandler exception message: "+cause.getMessage());
    }
}
