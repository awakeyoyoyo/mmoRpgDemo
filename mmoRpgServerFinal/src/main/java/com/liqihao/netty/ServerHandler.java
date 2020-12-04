package com.liqihao.netty;

import com.liqihao.commons.ConstantValue;
import com.liqihao.commons.NettyRequest;
import com.liqihao.commons.NettyResponse;
import com.liqihao.handler.Dispatcherservlet;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleStateEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ServerHandler extends ChannelInboundHandlerAdapter {
    private Dispatcherservlet dispatcherservlet;
    private int readIdleTimes=0;
    public int RolesId;
    private static final Logger log = LoggerFactory.getLogger(ServerHandler.class);

    public ServerHandler() {
    }

    public ServerHandler(Dispatcherservlet dispatcherservlet) {
        this.dispatcherservlet = dispatcherservlet;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        log.info("Server:channelActive");
        log.info("["+ctx.channel().remoteAddress()+"] connected");
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        log.info("Server:channelRead");
        readIdleTimes=0;
        NettyRequest request= (NettyRequest) msg;
        NettyResponse response=dispatcherservlet.handler(request,ctx.channel());
        ctx.writeAndFlush(response);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("ServerHandler exception message: "+cause);
        NettyRequest nettyRequest=new NettyRequest();
        nettyRequest.setModule(ConstantValue.GAME_SYSTEM_MODULE);
        nettyRequest.setCmd(ConstantValue.NET_IO_OUTTIME);
        dispatcherservlet.handler(nettyRequest,ctx.channel());
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        IdleStateEvent event = (IdleStateEvent)evt;
        String eventType = null;
        switch (event.state()){
            case READER_IDLE:
                eventType = "读空闲";
                break;
            case WRITER_IDLE:
                eventType = "写空闲";
                // 不处理
                break;
            case ALL_IDLE:
                eventType ="读写空闲";
                // 不处理
                readIdleTimes ++;
                break;
        }
        if(readIdleTimes > 3){
            log.error(" [server]读空闲超过3次，关闭连接");
            NettyRequest nettyRequest=new NettyRequest();
            nettyRequest.setModule(ConstantValue.GAME_SYSTEM_MODULE);
            nettyRequest.setCmd(ConstantValue.NET_IO_OUTTIME);
            NettyResponse response=dispatcherservlet.handler(nettyRequest,ctx.channel());
            ctx.channel().writeAndFlush(response);
            ctx.channel().close();
        }
    }
}
