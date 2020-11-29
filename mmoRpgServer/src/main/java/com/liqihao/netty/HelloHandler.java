package com.liqihao.netty;

import com.liqihao.dao.MmoPersonMapper;
import com.liqihao.entity.MmoPerson;
import com.liqihao.netty.codec.MmoStudentPOJO;
import io.netty.channel.*;





public class HelloHandler extends ChannelInboundHandlerAdapter{
    private MmoPersonMapper mmoPersonMapper;
    public HelloHandler(MmoPersonMapper mmoPersonMapper){
        this.mmoPersonMapper=mmoPersonMapper;
    }
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        MmoStudentPOJO.MmoStudent student= (MmoStudentPOJO.MmoStudent) msg;
        System.out.println("Server received: "+student.getName());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        //发生异常，关闭通道
        cause.printStackTrace();
        ctx.close();
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        System.out.println("有一个客户端连接");
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        MmoPerson mmoPerson=new MmoPerson();
        mmoPerson.setId(2);
        mmoPerson.setName("张学友");
        ctx.channel().writeAndFlush(mmoPerson);
    }
}