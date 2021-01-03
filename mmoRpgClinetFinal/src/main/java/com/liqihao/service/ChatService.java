package com.liqihao.service;

import com.google.protobuf.InvalidProtocolBufferException;
import com.liqihao.commons.NettyResponse;

public interface ChatService {
    void acceptMessageResopnse(NettyResponse nettyResponse) throws InvalidProtocolBufferException;
}
