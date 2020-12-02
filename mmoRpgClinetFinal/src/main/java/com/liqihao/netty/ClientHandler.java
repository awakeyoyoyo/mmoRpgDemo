package com.liqihao.netty;


import com.liqihao.commons.NettyResponse;
import com.liqihao.handler.Dispatcherservlet;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;



public class ClientHandler extends ChannelInboundHandlerAdapter {
    private Dispatcherservlet dispatcherservlet;

    public ClientHandler() {
    }

    public ClientHandler(Dispatcherservlet dispatcherservlet) {
        this.dispatcherservlet = dispatcherservlet;
    }


    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println("CLIENT:channelRead");
        NettyResponse response= (NettyResponse) msg;
        dispatcherservlet.handler(response);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        System.out.println("ClientHandler exception message: "+cause.getMessage());
    }
}
