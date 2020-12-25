package com.liqihao.service;

import com.google.protobuf.InvalidProtocolBufferException;
import com.liqihao.commons.NettyResponse;

public interface TeamService {
    void teamMessageResponse(NettyResponse nettyResponse) throws InvalidProtocolBufferException;

    void applyForTeamResponse(NettyResponse nettyResponse) throws InvalidProtocolBufferException ;

    void invitePeopleResponse(NettyResponse nettyResponse)throws InvalidProtocolBufferException ;

    void applyMessageResponse(NettyResponse nettyResponse)throws InvalidProtocolBufferException ;

    void inviteMessageResponse(NettyResponse nettyResponse)throws InvalidProtocolBufferException ;
}
