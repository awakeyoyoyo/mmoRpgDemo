package com.liqihao.netty;

import com.liqihao.codc.RequestEncoder;
import com.liqihao.codc.ResponceDecoder;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;


import java.net.InetSocketAddress;


public class NettyTcpClient {
    private int port;

    private String host;

    public NettyTcpClient(String host,int port) {
        this.port = port;
        this.host = host;
    }
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
                                    .addLast(new ClientHandler());
                        }
                    });//给workerGroup的EventLoop对应的管道设置处理器
            //绑定端口号，启动服务端
            ChannelFuture channelFuture = bootstrap.connect().sync();
            //对关闭通道进行监听
            System.out.println("客户端已经启动.....");
            System.out.println("---------------------------------------------------------");
            //开始游戏
//            ClientWorker clientWorker=new ClientWorker();
//            clientWorker.gameBegim(channelFuture);
            channelFuture.channel().closeFuture().sync();
        } finally {
            worker.shutdownGracefully();
        }
    }
}
