package com.liqihao.codc;

import com.liqihao.commons.ConstantValue;
import com.liqihao.commons.NettyRequest;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * RequestEncoder编码器
 */
public class RequestEncoder extends MessageToByteEncoder<NettyRequest> {
    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, NettyRequest nettyRequest, ByteBuf byteBuf) throws Exception {
        System.out.println("Clinet:RequestEncoder");
        //写入包头
//        byteBuf.writeInt(ConstantValue.FLAG);
        //module
        byteBuf.writeShort(nettyRequest.getModule());
        //cmd
        byteBuf.writeShort(nettyRequest.getCmd());
        //长度
        byteBuf.writeInt(nettyRequest.getDataLength());
        //data
        byteBuf.writeBytes(nettyRequest.getData());
    }
}
