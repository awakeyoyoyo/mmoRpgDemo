package com.liqihao.netty;

import com.liqihao.dao.MmoPersonMapper;
import com.liqihao.netty.codec.MmoStudentPOJO;
import com.liqihao.netty.codec.MyDataInfo;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.codec.serialization.ClassResolver;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class HelloServer {
    @Autowired
    private MmoPersonMapper mmoPersonMapper;
    private int port=6666;

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public  void run() throws Exception {
        //创建两个线程吃 boosGroup、workerGroup
        //负责监听端口
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        //负责读写任务
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            //创建服务端的启动对象，设置参数
            ServerBootstrap bootstrap = new ServerBootstrap();
            //设置两个线程组boosGroup和workerGroup
            bootstrap.group(bossGroup, workerGroup)
                    //设置服务端通道实现类型
                    .channel(NioServerSocketChannel.class)
//                    //设置线程队列得到连接个数
//                    .option(ChannelOption.SO_BACKLOG, 128)
//                    //设置保持活动连接状态
//                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                    //使用匿名内部类的形式初始化通道对象
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            //给pipeline管道设置处理器 childhandler是写游戏业务处理逻辑的
                            socketChannel.pipeline()
                                    //对象的序列化
//                                    .addLast(new ObjectEncoder())
                                    //对象的反序列化
//                                    .addLast(new ObjectDecoder(ClassResolvers.cacheDisabled(null)))
//                            加入protoful解码器 需要指定对于哪种对象进行解码
                                    .addLast("encoder",new ProtobufEncoder())
                                    .addLast("decoder",new ProtobufDecoder(MyDataInfo.MyMessage.getDefaultInstance()))
                                    .addLast(new HelloHandler(mmoPersonMapper))
                            ;
                        }
                    });//给workerGroup的EventLoop对应的管道设置处理器
            System.out.println("hello服务端已经准备就绪...");
            //绑定端口号，启动服务端
            ChannelFuture channelFuture = bootstrap.bind(port).sync();
            //对关闭通道进行监听
            channelFuture.channel().closeFuture().sync();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}
