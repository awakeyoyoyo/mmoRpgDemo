package com.liqihao.service;

import com.liqihao.annotation.HandlerCmdTag;
import com.liqihao.annotation.HandlerServiceTag;
import com.liqihao.commons.ConstantValue;
import com.liqihao.commons.NettyRequest;
import com.liqihao.commons.NettyResponse;
import io.netty.channel.Channel;

/**
 *游戏系统模块
 * @author lqhao
 */
public interface GameSystemService {
    /**
     * 长期未通信 断线
     * @param nettyRequest
     * @param channel
     */
    void netIoOutTime(NettyRequest nettyRequest, Channel channel);
}
