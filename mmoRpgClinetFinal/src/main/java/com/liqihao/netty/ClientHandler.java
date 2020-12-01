package com.liqihao.netty;

import com.liqihao.commons.NettyRequest;
import com.liqihao.commons.NettyResponse;
import com.liqihao.protobufObject.SceneModel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.util.List;

public class ClientHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("CLIENT:channelActive");
        //发送一个当前askCanRequest
        NettyRequest nettyRequest=new NettyRequest();
        nettyRequest.setCmd((short)1);
        nettyRequest.setModule((short)1);
        SceneModel.SceneModelMessage myMessage;
        myMessage=SceneModel.SceneModelMessage.newBuilder()
                .setDataType(SceneModel.SceneModelMessage.DateType.AskCanRequest)
                .setAskCanRequest(SceneModel.AskCanRequest.newBuilder().setSceneId(2).build()).build();
        byte[] data=myMessage.toByteArray();
        nettyRequest.setData(data);
        ctx.writeAndFlush(nettyRequest);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println("CLIENT:channelRead");
        NettyResponse response= (NettyResponse) msg;
        byte[] data=response.getData();
        SceneModel.SceneModelMessage myMessage;
        myMessage=SceneModel.SceneModelMessage.parseFrom(data);
        List<SceneModel.MmoSimpleScene> mmoSimpleScenes=myMessage.getAskCanResponse().getMmoSimpleScenesList();
        for (SceneModel.MmoSimpleScene mmoSimpleScene:mmoSimpleScenes){
            System.out.println("当前可以进入的场景："+mmoSimpleScene.getPalceName());
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        System.out.println("ClientHandler exception message: "+cause.getMessage());
    }
}
