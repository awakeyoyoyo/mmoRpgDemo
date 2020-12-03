package com.liqihao.service;

import com.google.protobuf.InvalidProtocolBufferException;
import com.liqihao.commons.NettyResponse;

public interface GameService {
    void outTimeResponse(NettyResponse nettyResponse) throws InvalidProtocolBufferException;
}
