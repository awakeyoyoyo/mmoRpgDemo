package com.liqihao.netty;
import com.liqihao.netty.codec.MmoStudentPOJO;
import com.liqihao.netty.codec.MyDataInfo;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.util.Random;

public class HelloClientHandler extends ChannelInboundHandlerAdapter {
    //当通道就绪时就会触发
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        //发送一个Student对象到服务器
//        MmoStudentPOJO.MmoStudent student=MmoStudentPOJO.MmoStudent.newBuilder()
//                .setId(1000)
//                .setName("刘德华")
//                .build();
//        ctx.writeAndFlush(student);
        //随机发送Student或者Worker对象
        int random=new Random().nextInt(3);
        MyDataInfo.MyMessage myMessage=null;
        if (0==random){
            //发送studnet
            myMessage=MyDataInfo.MyMessage.newBuilder()
                    .setDataType(MyDataInfo.MyMessage.DateType.StudentType)
                    .setStudent(MyDataInfo.Studnet.newBuilder().setId(5).setName("小学僧").build()).build();
        }else{
            //发送worker
            myMessage=MyDataInfo.MyMessage.newBuilder()
                    .setDataType(MyDataInfo.MyMessage.DateType.WorkerType)
                    .setWorker(MyDataInfo.Worker.newBuilder().setAge(18).setName("搬砖仔").build()).build();
        }
        ctx.writeAndFlush(myMessage);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        MmoStudentPOJO.MmoStudent student= (MmoStudentPOJO.MmoStudent) msg;
        System.out.println("Client received: "+student.getName());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
            throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
