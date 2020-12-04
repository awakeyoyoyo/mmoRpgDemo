package com.liqihao.codc;

import com.liqihao.commons.ConstantValue;
import com.liqihao.commons.NettyRequest;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import org.apache.log4j.Logger;


import java.util.List;

/**
 * 解码器
 */
public class RequestDecoder  extends ByteToMessageDecoder {
    private static Logger logger=Logger.getLogger(RequestDecoder.class);
    /**
     * //数据包基本长度 包头+模块号+命令+长度
     * 数据包基本长度 模块号+命令+长度
     */
    public static int BASE_LENGTH=4+2+2+4;
    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {
        logger.info("Clinet:RequestDecoder");
        if (byteBuf.readableBytes()>=BASE_LENGTH){
            //记录开始读取的index
            int beginReader =byteBuf.writerIndex();
            //可以处理

            //读到不正确包头 断开通道连接 避免恶意telnet或者攻击
            if (byteBuf.readInt() != ConstantValue.FLAG) {
                channelHandlerContext.channel().close();
                logger.info("Client：包头错误关闭通道");
            }
            short module=byteBuf.readShort();
            short cmd=byteBuf.readShort();
            int len=byteBuf.readInt();
            if (byteBuf.readableBytes()<len){
                //等待后面的数据包来
                //但需要将之前读取的12字节的东西还原回去
                byteBuf.readerIndex(beginReader);
                return;
            }
            byte[] data=new byte[len];
            byteBuf.readBytes(data);
            NettyRequest nettyRequest=new NettyRequest();
            nettyRequest.setModule(module);
            nettyRequest.setCmd(cmd);
            nettyRequest.setData(data);
            list.add(nettyRequest);
        }else{
            //数据包不完整，需要等待数据包来齐
            return;
        }
    }
}
