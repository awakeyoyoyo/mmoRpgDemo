package com.liqihao.netty.tcp;



import com.sun.org.apache.xpath.internal.operations.String;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.nio.charset.Charset;
import java.util.UUID;


public class HelloHandler extends SimpleChannelInboundHandler<ByteBuf>{
    private int count;
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        //发生异常，关闭通道
        cause.printStackTrace();
        ctx.close();
    }


    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) throws Exception {
        byte[] buffer=new byte[msg.readableBytes()];
        msg.readBytes(buffer);
        //将buffer转成字符串
        java.lang.String message = new java.lang.String(buffer, Charset.forName("UTF-8"));
        System.out.println("服务器接收到的数据 "+message);
        System.out.println("服务器接收到的消息量="+ count++);
        //回复数据
        ByteBuf respond=Unpooled
                .copiedBuffer(UUID.randomUUID().toString()+" ",
                        Charset.forName("utf-8"));
        ctx.writeAndFlush(respond);
    }
}