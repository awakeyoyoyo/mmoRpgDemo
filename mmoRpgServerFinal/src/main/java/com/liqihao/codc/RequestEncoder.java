package com.liqihao.codc;

import com.liqihao.commons.ConstantValue;
import com.liqihao.commons.NettyRequest;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.apache.log4j.Logger;


/**
 * 请求编码器
 * 数据包格式
 * 包头（4byte）-----模块号（2byte）-----状态码（4byte）------长度（4byte）--------数据
 * 数据包基本长度 模块号+命令+状态码+长度
 */
public class RequestEncoder extends MessageToByteEncoder<NettyRequest> {
    private static Logger logger=Logger.getLogger(RequestEncoder.class);
    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, NettyRequest nettyRequest, ByteBuf byteBuf) throws Exception {
       logger.info("Server:RequestEncoder");
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
