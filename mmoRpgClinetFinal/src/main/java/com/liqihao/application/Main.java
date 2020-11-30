package com.liqihao.application;

import com.liqihao.netty.NettyTcpClient;

public class Main {
    public static void main(String[] args) throws Exception {
        NettyTcpClient nettyTcpClient=new NettyTcpClient("127.0.0.1",6666);
        nettyTcpClient.run();
    }
}
