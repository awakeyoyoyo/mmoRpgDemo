package com.liqihao.service;

import com.google.protobuf.InvalidProtocolBufferException;
import com.liqihao.commons.NettyResponse;

public interface FriendService {
    void applyFriendResponse(NettyResponse nettyResponse) throws InvalidProtocolBufferException;

    void agreeFriendResponse(NettyResponse nettyResponse) throws InvalidProtocolBufferException;

    void refuseFriendResponse(NettyResponse nettyResponse) throws InvalidProtocolBufferException;

    void hasNewFriendsResponse(NettyResponse nettyResponse) throws InvalidProtocolBufferException;

    void beRefuseResponse(NettyResponse nettyResponse) throws InvalidProtocolBufferException;

    void getFriendsResponse(NettyResponse nettyResponse) throws InvalidProtocolBufferException;

    void friendApplyListResponse(NettyResponse nettyResponse) throws InvalidProtocolBufferException;

    void reduceFriendResponse(NettyResponse nettyResponse) throws InvalidProtocolBufferException;
}
