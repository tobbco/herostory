package org.herostory;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import org.herostory.handler.DefaultMessageDecoder;
import org.herostory.handler.DefaultMessageEncoder;
import org.herostory.handler.DefaultMessageHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ServerMain {
    private static final Logger logger = LoggerFactory.getLogger(ServerMain.class);
    public static final Integer PORT = 12345;

    public static void main(String[] args) {
        //负责接收客户端连接
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        //负责处理客户端连接
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        //创建服务端引导程序
        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.group(bossGroup, workerGroup);

        bootstrap.channel(NioServerSocketChannel.class);
        bootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel socketChannel) throws Exception {
                //获取当前客户端连接信道，并为信道添加处理器
                ChannelPipeline pipeline = socketChannel.pipeline();
                pipeline.addLast(
                        new HttpServerCodec(),
                        new HttpObjectAggregator(65535), //默认消息大小
                        new WebSocketServerProtocolHandler("/websocket"), //websocket协议
                        //打印netty 日志，用于调试，默认打印INFO级别
//                        new LoggingHandler(LogLevel.INFO),
                        new DefaultMessageDecoder(), //默认消息解码器
                        new DefaultMessageEncoder(), //默认消息编码器
                        new DefaultMessageHandler() //默认消息处理器
                );
            }
        });
        try {
            ChannelFuture future = bootstrap.bind(PORT).sync();
            if (future.isSuccess()) {
                logger.info("服务端启动成功,PORT: {}", PORT);
            }
            future.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            logger.error("服务端启动异常", e);
        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
            logger.info("服务端关闭");
        }
    }
}