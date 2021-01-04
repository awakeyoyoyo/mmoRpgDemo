package com.liqihao.service;

import com.liqihao.protobufObject.GameSystemModel;
import io.netty.channel.Channel;

/**
 *游戏系统模块
 * @author lqhao
 */
public interface GameSystemService {
    /**
     * 长期未通信 断线
     * @param channel
     */
    void netIoOutTime(Channel channel);
}
