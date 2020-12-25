package com.liqihao.service;

import com.google.protobuf.InvalidProtocolBufferException;
import com.liqihao.commons.NettyResponse;

public interface TeamService {
    void teamMessageResponse(NettyResponse nettyResponse) throws InvalidProtocolBufferException;
}
