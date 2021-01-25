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
import io.netty.handler.timeout.IdleState;
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
     * 计数----未有客户端可读次数
     */
    private int lossConnectCount=0;
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
        lossConnectCount=0;
        NettyRequest request= (NettyRequest) msg;
        if (request.getCmd()==ConstantValue.HEART_BEAT) {
            //心跳包则不处理 直接返回
            log.info("收到客户端端的心跳包");
           return;
        }
        //根据channel计算index
        Integer index= CommonsUtil.getIndexByChannel(ctx.channel());
        LogicThreadPool.getInstance().execute(() -> dispatcherservlet.handler(request,ctx.channel()),index);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("ServerHandler exception message: "+cause);
        cause.printStackTrace();
        sendException(ctx,cause.getMessage());
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        log.info("已经5秒未收到客户端的消息了！ 第几次？"+lossConnectCount);
        if (evt instanceof IdleStateEvent){
            IdleStateEvent event = (IdleStateEvent)evt;
            if (event.state()== IdleState.READER_IDLE){
                lossConnectCount++;
                if (lossConnectCount>3){
                    log.info("关闭这个不活跃通道！");
                    ctx.channel().close();
                }
            }
        }else {
            super.userEventTriggered(ctx,evt);
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
