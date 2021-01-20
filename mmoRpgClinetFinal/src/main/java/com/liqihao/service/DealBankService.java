package com.liqihao.service;

import com.google.protobuf.InvalidProtocolBufferException;
import com.liqihao.commons.NettyResponse;
import com.liqihao.protobufObject.DealBankModel;

public interface DealBankService {
    /**
     * 上架一口价商品
     * @param nettyResponse
     */
    void addSellArticleRequest(NettyResponse nettyResponse) throws InvalidProtocolBufferException;
    /**
     * 下架一口价商品
     * @param nettyResponse
     */
    void reduceSellArticleRequest(NettyResponse nettyResponse) throws InvalidProtocolBufferException;

    /**
     * 下架拍卖品
     * @param nettyResponse
     */
    void reduceAuctionArticleRequest(NettyResponse nettyResponse) throws InvalidProtocolBufferException;
    /**
     * 购买一口价物品
     * @param nettyResponse
     */
    void buyArticleRequest(NettyResponse nettyResponse) throws InvalidProtocolBufferException;
    /**
     * 拍卖物品
     * @param nettyResponse
     */
    void auctionArticleRequest(NettyResponse nettyResponse) throws InvalidProtocolBufferException;
    /**
     * 获取所有商品物品
     * @param nettyResponse
     */
    void getArticleRequest(NettyResponse nettyResponse) throws InvalidProtocolBufferException;


}
