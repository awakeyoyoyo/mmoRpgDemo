package com.liqihao.netty;

import com.liqihao.dao.MmoPersonMapper;
import com.liqihao.netty.codec.MyDataInfo;
import io.netty.channel.*;





public class HelloHandler extends ChannelInboundHandlerAdapter{
    private MmoPersonMapper mmoPersonMapper;
    public HelloHandler(MmoPersonMapper mmoPersonMapper){
        this.mmoPersonMapper=mmoPersonMapper;
    }
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
//        MmoStudentPOJO.MmoStudent student= (MmoStudentPOJO.MmoStudent) msg;
//        System.out.println("Server received: "+student.getName());
//        MmoStudentPOJO.MmoStudent student02=MmoStudentPOJO.MmoStudent.newBuilder()
//                .setId(1001)
//                .setName("张学友")
//                .build();
//        ctx.writeAndFlush(student02);
        MyDataInfo.MyMessage myMessage= (MyDataInfo.MyMessage) msg;
        //根据dataType来显示不同的信息
        MyDataInfo.MyMessage.DateType dateType=myMessage.getDataType();
        if (dateType==MyDataInfo.MyMessage.DateType.StudentType){
            System.out.println("学生id："+myMessage.getStudent().getId()+
                    "学生名字："+myMessage.getStudent().getName());
        }else if (dateType==MyDataInfo.MyMessage.DateType.WorkerType){
            System.out.println("工作者名字："+myMessage.getWorker().getName()+
                    "工作者年龄："+myMessage.getWorker().getAge());
        }else {
            System.out.println("传输的类型不正确");
        }
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
//        MmoPerson mmoPerson=new MmoPerson();
//        mmoPerson.setId(2);
//        mmoPerson.setName("张学友");
//        ctx.channel().writeAndFlush(mmoPerson);
    }
}