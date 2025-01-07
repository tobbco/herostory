package org.herostory;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;

/**
 * @description:
 * @author：yexianchao
 * @date: 2025/1/6/006
 */
public class ServerMain {
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
                        new HttpObjectAggregator(65535),
                        new WebSocketServerProtocolHandler("/ws")
                );
            }
        });
        try {
            ChannelFuture future = bootstrap.bind(PORT).sync();
            if (future.isSuccess()) {
                System.out.println("服务启动成功,端口: " + PORT);
            }
            Channel channel = future.channel();
            channel.closeFuture();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

    }
}