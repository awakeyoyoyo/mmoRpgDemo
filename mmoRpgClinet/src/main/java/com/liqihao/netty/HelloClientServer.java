package com.liqihao.netty;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import java.net.InetSocketAddress;

public class HelloClientServer {
    private int port;

    private String host;

    public HelloClientServer(String host,int port) {
        this.port = port;
        this.host = host;
    }
    public  void run() throws Exception {
        //创建两个线程组 boosGroup
        EventLoopGroup group = new NioEventLoopGroup();

        try {
            //创建Bootstrap 初始化客户端
            Bootstrap bootstrap = new Bootstrap();
            //设置两个线程组boosGroup和workerGroup
            bootstrap.group(group)
                    //设置服务端通道实现类型
                    .channel(NioSocketChannel.class)
                    .remoteAddress(new InetSocketAddress(host,port))
                    //使用匿名内部类的形式初始化通道对象
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            //给pipeline管道设置处理器
                            socketChannel.pipeline().addLast(new HelloClientHandler());
                        }
                    });//给workerGroup的EventLoop对应的管道设置处理器
            //绑定端口号，启动服务端
            ChannelFuture channelFuture = bootstrap.connect().sync();
            //对关闭通道进行监听
            channelFuture.channel().closeFuture().sync();
        } finally {
            group.shutdownGracefully();
        }
    }
}
