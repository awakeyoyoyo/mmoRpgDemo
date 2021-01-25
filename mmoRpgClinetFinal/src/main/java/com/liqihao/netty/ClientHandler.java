package com.liqihao.netty;

import com.liqihao.commons.ConstantValue;
import com.liqihao.commons.NettyRequest;
import com.liqihao.commons.NettyResponse;
import com.liqihao.handler.Dispatcherservlet;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import org.apache.log4j.Logger;

import java.nio.charset.StandardCharsets;
import java.util.Date;

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
//        logger.info("CLIENT:channelActive");
        //发送一个当前askCanRequest

    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
//        logger.info("CLIENT:channelRead");
        NettyResponse response= (NettyResponse) msg;
        dispatcherservlet.handler(response);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        logger.error("ClientHandler exception message: "+cause);
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent){
            IdleStateEvent event = (IdleStateEvent)evt;
            if (event.state()== IdleState.WRITER_IDLE){
                if (ctx.channel().isActive()){
                    NettyRequest nettyRequest=new NettyRequest();
                    nettyRequest.setCmd(ConstantValue.HEART_BEAT);
                    nettyRequest.setData("I am alive".getBytes(StandardCharsets.UTF_8));
                    ctx.writeAndFlush(nettyRequest);
//                    System.out.println("给服务端发送心跳");
                }else{
                    ctx.channel().close();
                    System.out.println("服务器已经断开连接");
                }
            }
        }
    }
}
