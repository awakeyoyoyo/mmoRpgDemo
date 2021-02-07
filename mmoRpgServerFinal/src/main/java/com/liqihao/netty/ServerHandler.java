package com.liqihao.netty;

import com.google.protobuf.InvalidProtocolBufferException;
import com.liqihao.cache.ChannelMessageCache;
import com.liqihao.commons.*;
import com.liqihao.handler.DispatcherServlet;
import com.liqihao.pojo.bean.roleBean.MmoSimpleRole;
import com.liqihao.provider.PlayServiceProvider;
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
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * nettyHandler
 * @author lqhao
 */
public class ServerHandler extends ChannelInboundHandlerAdapter {
    private DispatcherServlet dispatcherservlet;
    private PlayServiceProvider playServiceProvider;
    /**
     * 计数----未有客户端可读次数
     */
    private int lossConnectCount=0;
    private static final int MAX_LOSS_CONNECT_COUNT=3;
    private static final Logger log = LoggerFactory.getLogger(ServerHandler.class);

    public ServerHandler() {
    }

    public ServerHandler(DispatcherServlet dispatcherservlet,PlayServiceProvider playServiceProvider) {
        this.dispatcherservlet = dispatcherservlet;
        this.playServiceProvider=playServiceProvider;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {

    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        lossConnectCount=0;
        NettyRequest request= (NettyRequest) msg;
        if (request.getCmd()==ConstantValue.HEART_BEAT) {
            //心跳包则不处理 直接返回
           return;
        }
        //根据channel计算index
        MmoSimpleRole role= CommonsUtil.getRoleByChannel(ctx.channel());
        if(role!=null) {
            role.execute(() -> dispatcherservlet.handler(request, ctx.channel()));
        }else{
            dispatcherservlet.handler(request, ctx.channel());
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("ServerHandler exception message: "+cause);
        cause.printStackTrace();
        MmoSimpleRole role=CommonsUtil.checkLogin(ctx.channel());
        if (role!=null){
            playServiceProvider.logout(role);
        }
        sendException(ctx,cause.getMessage());
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent){
            IdleStateEvent event = (IdleStateEvent)evt;
            if (event.state()== IdleState.READER_IDLE){
                lossConnectCount++;
                if (lossConnectCount>MAX_LOSS_CONNECT_COUNT){
                    log.info("关闭这个不活跃通道！");
                    MmoSimpleRole mmoSimpleRole= CommonsUtil.getRoleByChannel(ctx.channel());
                    if (mmoSimpleRole==null){
                        playServiceProvider.logout(mmoSimpleRole);
                    }
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
