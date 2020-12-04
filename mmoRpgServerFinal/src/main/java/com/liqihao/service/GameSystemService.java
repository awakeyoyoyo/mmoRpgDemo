package com.liqihao.service;

import com.liqihao.commons.NettyRequest;
import com.liqihao.commons.NettyResponse;
import io.netty.channel.Channel;

public interface GameSystemService {
    NettyResponse netIoOutTime(NettyRequest nettyRequest, Channel channel);
}
