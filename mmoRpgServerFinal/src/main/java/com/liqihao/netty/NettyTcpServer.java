package com.liqihao.netty;

import com.liqihao.codc.RequestDecoder;
import com.liqihao.codc.ResponseEncoder;
import com.liqihao.handler.DispatcherServlet;
import com.liqihao.provider.PlayServiceProvider;
import com.liqihao.service.GameSystemService;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * nettyServer
 * @author lqhao
 */
@Component
public class NettyTcpServer {
    private static Logger logger=Logger.getLogger(NettyTcpServer.class);
    private int port=6666;
    @Autowired
    private DispatcherServlet dispatcherServlet;
    @Autowired
    private PlayServiceProvider playServiceProvider;
    public void run() throws Exception {
        //创建两个线程池 boosGroup、workerGroup
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
                    //使用匿名内部类的形式初始化通道对象
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            //给pipeline管道设置处理器 childHandler是写游戏业务处理逻辑的
                            socketChannel.pipeline()
                                    .addLast("decoder", new RequestDecoder())//解码器
                                    .addLast("encoder",new ResponseEncoder())//编码器
                                    .addLast(new IdleStateHandler(10,0,0, TimeUnit.SECONDS))//心跳
                                    .addLast(new ServerHandler(dispatcherServlet,playServiceProvider))//业务处理handler
                            ;
                        }
                    });//给workerGroup的EventLoop对应的管道设置处理器
            //绑定端口号，启动服务端
            ChannelFuture channelFuture = bootstrap.bind(port).sync();
            logger.info("hello服务端已经准备就绪...");
            //对关闭通道进行监听
            channelFuture.channel().closeFuture().sync();
        } catch (InterruptedException e){
            logger.error("bind "+":"+port+" failed",e.getCause());
        }finally{
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}
