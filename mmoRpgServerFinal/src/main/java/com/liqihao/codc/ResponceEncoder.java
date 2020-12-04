package com.liqihao.codc;

import com.liqihao.commons.ConstantValue;
import com.liqihao.commons.NettyRequest;
import com.liqihao.commons.NettyResponse;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.apache.log4j.Logger;

/**
 * 响应编码器
 * 数据包格式
 * 包头（4byte）-----模块号（2byte）-----命令号（2byte）-----状态码（4byte）------长度（4byte）--------数据
 * 数据包基本长度 模块号+命令+状态码+长度
 */
public class ResponceEncoder extends MessageToByteEncoder<NettyResponse> {
    private static Logger logger=Logger.getLogger(ResponceEncoder.class);
    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, NettyResponse nettyResponse, ByteBuf byteBuf) throws Exception {
        logger.info("Server:ResponceEncoder");

//        //写入包头
        byteBuf.writeInt(ConstantValue.FLAG);
        //module
        byteBuf.writeShort(nettyResponse.getModule());
        //cmd
        byteBuf.writeShort(nettyResponse.getCmd());
        //状态码
        byteBuf.writeInt(nettyResponse.getStateCode());
        //长度
        byteBuf.writeInt(nettyResponse.getDataLength());
        //data
        byteBuf.writeBytes(nettyResponse.getData());

        logger.error("Server:ResponceEncoder"+byteBuf.readableBytes());
    }
}
