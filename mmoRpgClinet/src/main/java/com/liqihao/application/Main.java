package com.liqihao.application;

import com.liqihao.netty.HelloClientServer;

public class Main {
//    private static Logger logger=Logger.getLogger(Main.class);
    public static void main(String[] args) throws Exception {
        HelloClientServer helloClientServer=new HelloClientServer("127.0.0.1",6666);
        helloClientServer.run();
        System.out.println("客户端已经启动");
    }
}
