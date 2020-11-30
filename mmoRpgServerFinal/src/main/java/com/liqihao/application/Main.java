package com.liqihao.application;

import com.liqihao.netty.NettyTcpServer;
import com.liqihao.pojo.MmoScene;
import com.liqihao.pojo.MmoSimpleRole;
import com.liqihao.pojo.MmoSimpleScene;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class Main {

    public static void main(String[] args) throws Exception {
        //查询类路径 加载配置文件
        ApplicationContext applicationContext = new ClassPathXmlApplicationContext("spring.xml");
        //根据id获取bean
        //Spring就是一个大工厂（容器）专门生成bean bean就是对象
        //输出获取到的对象
        ServerInit serverInit = (ServerInit) applicationContext.getBean("serverInit");
        serverInit.init();
        NettyTcpServer nettyTcpServer=new NettyTcpServer();
        nettyTcpServer.run();
    }
}
