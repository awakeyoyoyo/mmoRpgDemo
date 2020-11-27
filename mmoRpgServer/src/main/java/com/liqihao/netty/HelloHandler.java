package com.liqihao.netty;

import com.liqihao.dao.MmoPersonMapper;
import com.liqihao.entity.MmoPerson;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.util.CharsetUtil;


/**
 * 自定义的Handler需要继承Netty规定好的HandlerAdapter
 * 才能被Netty框架所关联，有点类似SpringMVC的适配器模式
 **/

public class HelloHandler extends ChannelInboundHandlerAdapter{
    private MmoPersonMapper mmoPersonMapper;
    public HelloHandler(MmoPersonMapper mmoPersonMapper){
        this.mmoPersonMapper=mmoPersonMapper;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf in = (ByteBuf) msg;
        System.out.println("Server received: " + in.toString(CharsetUtil.UTF_8));


    }
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        //发送消息给客户端
        MmoPerson mmoPerson=mmoPersonMapper.selectByPrimaryKey(1000);
        System.out.println(mmoPerson.getName());
        //将接收到的消息写给发送者，而不冲刷出站消息
        ctx.write(mmoPerson.getName());
        ctx.write("Hello Client!");
        ctx.flush();
    }
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        //发生异常，关闭通道
        cause.printStackTrace();
        ctx.close();
    }
}