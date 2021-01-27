package com.liqihao.util;


import com.liqihao.commons.NettyResponse;
import io.netty.channel.Channel;

/**
 * @Classname NotificationUtil
 * @Description 返回消息工具类
 * @Author lqhao
 * @Date 2021/1/27 17:11
 * @Version 1.0
 */
public class NotificationUtil {
    public static void sendMessage(Channel channel, NettyResponse nettyResponse,String json){
        channel.writeAndFlush(nettyResponse);
        //打印日志 todo

    }
}
