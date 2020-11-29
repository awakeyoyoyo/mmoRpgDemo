package com.liqihao.netty.tcp;
import com.liqihao.netty.codec.MmoStudentPOJO;
import com.liqihao.netty.codec.MyDataInfo;
import com.sun.org.apache.xpath.internal.operations.String;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.SimpleChannelInboundHandler;

import java.nio.charset.Charset;
import java.util.Random;

public class HelloClientHandler extends SimpleChannelInboundHandler<ByteBuf> {
    private int count;
    //当通道就绪时就会触发
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        //使用客户端发送十条数据 hello server 编号
        for (int i=0;i<10;i++){
            ByteBuf byteBuf=Unpooled.copiedBuffer("hello,server "+i, Charset.forName("utf-8"));
            ctx.writeAndFlush(byteBuf);
        }

    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, ByteBuf msg) throws Exception {
        byte[] buffer=new byte[msg.readableBytes()];
        msg.readBytes(buffer);
        java.lang.String message=new java.lang.String(buffer,Charset.forName("utf-8"));
        System.out.println("客户端接收到的信息="+message);
        System.out.println("客户端接收到的数量="+count++);


    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
            throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
