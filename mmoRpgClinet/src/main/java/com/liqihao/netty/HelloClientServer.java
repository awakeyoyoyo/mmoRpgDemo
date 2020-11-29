package com.liqihao.netty;
import com.liqihao.pojo.MmoPerson;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import java.net.InetSocketAddress;

public class HelloClientServer {
    private int port;

    private String host;

    public HelloClientServer(String host,int port) {
        this.port = port;
        this.host = host;
    }
    public  void run() throws Exception {
        //创建两个workerGroup
        EventLoopGroup worker = new NioEventLoopGroup();
        try {
            //创建Bootstrap 初始化客户端
            Bootstrap bootstrap = new Bootstrap();
            //设置两个线程组boosGroup和workerGroup
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
                                    //加入protoful编码器
                                    .addLast("encoder",new ProtobufEncoder())
//                                    .addLast(new ObjectEncoder())
                                    .addLast(new HelloClientHandler());
                        }
                    });//给workerGroup的EventLoop对应的管道设置处理器
            //绑定端口号，启动服务端
            ChannelFuture channelFuture = bootstrap.connect().sync();
//            MmoPerson mmoPerson=new MmoPerson();
//            mmoPerson.setId(1);
//            mmoPerson.setName("刘德华");
//            channelFuture.channel().writeAndFlush(mmoPerson);
            //对关闭通道进行监听
            System.out.println("客户端已经启动");
            channelFuture.channel().closeFuture().sync();
        } finally {
            worker.shutdownGracefully();
        }
    }
}
