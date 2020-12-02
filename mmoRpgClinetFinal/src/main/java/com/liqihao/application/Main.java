package com.liqihao.application;

import com.liqihao.netty.NettyTcpClient;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class Main {
    public static void main(String[] args) throws Exception {
//查询类路径 加载配置文件
        ApplicationContext applicationContext = new ClassPathXmlApplicationContext("spring.xml");
        NettyTcpClient nettyTcpServer=(NettyTcpClient)applicationContext.getBean("nettyTcpClient");
        nettyTcpServer.run();
    }
}
