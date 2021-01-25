package com.liqihao.netty;

import com.liqihao.application.GameStart;
import com.liqihao.codc.RequestEncoder;
import com.liqihao.codc.ResponceDecoder;
import com.liqihao.handler.Dispatcherservlet;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;

@Component
public class NettyTcpClient {
    private static Logger logger=Logger.getLogger(NettyTcpClient.class);
    @Autowired
    private Dispatcherservlet dispatcherservlet;
    private int port=6666;

    private String host="127.0.0.1";

    public  void run() throws Exception {
        //创建一个workerGroup
        EventLoopGroup worker = new NioEventLoopGroup();
        try {
            //创建Bootstrap 初始化客户端
            Bootstrap bootstrap = new Bootstrap();
            //设置工作线程组
            bootstrap.group(worker)
                    //设置服务端通道实现类型
                    .channel(NioSocketChannel.class)
                    .remoteAddress(new InetSocketAddress(host,port))
                    //使用匿名内部类的形式初始化通道对象
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            //给pipeline管道设置处理器
                            socketChannel.pipeline()
                                    .addLast("decoder",new ResponceDecoder()) //解码器
                                    .addLast("encoder",new RequestEncoder())  //编码器
                                    .addLast(new IdleStateHandler(0,5,0, TimeUnit.SECONDS))
                                    .addLast(new ClientHandler(dispatcherservlet));
                        }
                    });//给workerGroup的EventLoop对应的管道设置处理器
            //绑定端口号，启动服务端
            ChannelFuture channelFuture = bootstrap.connect().sync();
            //对关闭通道进行监听

            logger.info("客户端已经启动.....");
            logger.info("---------------------------------------------------------");
            //开始游戏
            GameStart gameStart=new GameStart(channelFuture.channel());
            gameStart.play();
            channelFuture.channel().closeFuture().sync();
        } finally {
            worker.shutdownGracefully();
        }
    }
}
