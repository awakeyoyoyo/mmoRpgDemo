package com.liqihao.codc;

import com.liqihao.commons.ConstantValue;
import com.liqihao.commons.NettyResponse;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import org.apache.log4j.Logger;

import java.util.List;

/**
 * 响应解码器
 * 数据包格式
 * 包头（4byte）-----命令号（2byte）-----状态码（4byte）------长度（4byte）--------数据
 */
public class ResponceDecoder extends ByteToMessageDecoder {
    /**
     * 数据包基本长度 包头+命令+状态码+长度
     */
    public static int BASE_LENGTH=4+2+4+4;

    private static Logger logger=Logger.getLogger(ResponceDecoder.class);
    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {
        logger.info("Client:ResponceDecoder readableBytes:"+byteBuf.readableBytes());
        if (byteBuf.readableBytes()>=BASE_LENGTH){
            //记录开始读取的index
            int beginReader =byteBuf.writerIndex();
            //可以处理
            //读到不正确包头 断开通道连接 避免恶意telnet或者攻击
            Integer flag=byteBuf.readInt();
            logger.info("Client:ResponceDecoder flag:"+flag);
            if (!flag.equals(ConstantValue.FLAG)) {
                channelHandlerContext.channel().close();
                logger.info("Server：包头错误关闭通道 flag:"+flag);
            }
            int cmd=byteBuf.readInt();
            int stateCode=byteBuf.readInt();
            int len=byteBuf.readInt();
            //判断请求数据包数据是否到齐
            if (byteBuf.readableBytes()<len){
                //等待后面的数据包来
                //但需要将之前读取的12字节的东西还原回去
                byteBuf.readerIndex(beginReader);
                return;
            }
            byte[] data=new byte[len];
            byteBuf.readBytes(data);
            NettyResponse nettyResponse=new NettyResponse();
            nettyResponse.setCmd(cmd);
            nettyResponse.setStateCode(stateCode);
            nettyResponse.setData(data);
            list.add(nettyResponse);
        }else{
            //数据包不完整，需要等待数据包来齐
            return;
        }

    }
}
