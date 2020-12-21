package com.liqihao.service;

import com.liqihao.annotation.HandlerCmdTag;
import com.liqihao.annotation.HandlerServiceTag;
import com.liqihao.commons.ConstantValue;
import com.liqihao.commons.NettyRequest;
import com.liqihao.commons.NettyResponse;
import io.netty.channel.Channel;
public interface GameSystemService {
    void netIoOutTime(NettyRequest nettyRequest, Channel channel);
}
