package com.liqihao.netty;

import com.liqihao.commons.NettyRequest;
import com.liqihao.commons.NettyResponse;
import com.liqihao.protobufObject.SceneModel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class ServerHandler extends ChannelInboundHandlerAdapter {
    private static final Logger log = LoggerFactory.getLogger(ServerHandler.class);

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("Server:channelActive");
        log.info("["+ctx.channel().remoteAddress()+"] connected");
        System.out.println("["+ctx.channel().remoteAddress()+"] connected");
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println("Server:channelRead");
        NettyRequest request= (NettyRequest) msg;
        byte[] data=request.getData();
        SceneModel.SceneModelMessage myMessage;
        myMessage=SceneModel.SceneModelMessage.parseFrom(data);
        System.out.println("收到的SceneId是："+myMessage.getAskCanRequest().getSceneId());
        //返回数据
        List<SceneModel.MmoSimpleScene> mmoSimpleScenes=new ArrayList<>();
        SceneModel.MmoSimpleScene mmoSimpleScene=SceneModel.MmoSimpleScene.newBuilder().setId(1).setPalceName("天上").build();
        mmoSimpleScenes.add(mmoSimpleScene);
        mmoSimpleScene=SceneModel.MmoSimpleScene.newBuilder().setId(1).setPalceName("天下").build();
        mmoSimpleScenes.add(mmoSimpleScene);
        //new 一个AskCanRsponse
        SceneModel.SceneModelMessage myMessage2;
        myMessage2=SceneModel.SceneModelMessage.newBuilder()
                .setDataType(SceneModel.SceneModelMessage.DateType.AskCanResponse)
                .setAskCanResponse(SceneModel.AskCanResponse.newBuilder().addAllMmoSimpleScenes(mmoSimpleScenes)).build();
        //封装到Response中
        NettyResponse response=new NettyResponse();
        response.setCmd((short)1);
        response.setStateCode(200);
        response.setModule((short)1);
        byte[] data2=myMessage2.toByteArray();
        response.setData(data2);
        ctx.writeAndFlush(response);


    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("ServerHandler exception message: "+cause.getMessage());
    }
}
