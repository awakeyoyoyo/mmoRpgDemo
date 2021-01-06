package com.liqihao.netty;

import com.google.protobuf.InvalidProtocolBufferException;
import com.liqihao.commons.ConstantValue;
import com.liqihao.commons.NettyRequest;
import com.liqihao.commons.NettyResponse;
import com.liqihao.commons.StateCode;
import com.liqihao.handler.DispatcherServlet;
import com.liqihao.service.GameSystemService;
import com.liqihao.util.CommonsUtil;
import com.liqihao.util.LogicThreadPool;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleStateEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.lang.reflect.InvocationTargetException;
import java.nio.charset.StandardCharsets;

/**
 * nettyHandler
 * @author lqhao
 */
public class ServerHandler extends ChannelInboundHandlerAdapter {
    private DispatcherServlet dispatcherservlet;
    private GameSystemService gameSystemService;
    /**
     * 计数----未读次数
     */
    private int readIdleTimes=0;
    private static final Logger log = LoggerFactory.getLogger(ServerHandler.class);

    public ServerHandler() {
    }

    public ServerHandler(DispatcherServlet dispatcherservlet,GameSystemService gameSystemService) {
        this.dispatcherservlet = dispatcherservlet;
        this.gameSystemService=gameSystemService;
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
        //根据channel计算index
        Integer index= CommonsUtil.getIndexByChannel(ctx.channel());
        LogicThreadPool.getInstance().execute(new Runnable() {
            @Override
            public void run() {
                dispatcherservlet.handler(request,ctx.channel());
            }
        },index);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("ServerHandler exception message: "+cause);
        cause.printStackTrace();
        sendException(ctx,cause.getMessage());
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
            gameSystemService.netIoOutTime(ctx.channel());
            ctx.channel().close();
        }
    }
    private void sendException(ChannelHandlerContext ctx,String  cause){
        NettyResponse nettyResponse=new NettyResponse();
        nettyResponse.setCmd(ConstantValue.FAIL_RESPONSE);
        String message="服务端抛出异常："+cause;
        nettyResponse.setData(message.getBytes(StandardCharsets.UTF_8));
        nettyResponse.setStateCode(StateCode.FAIL);
        ctx.channel().writeAndFlush(nettyResponse);
    }
}
