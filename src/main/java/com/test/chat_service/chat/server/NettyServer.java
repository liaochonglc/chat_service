package com.test.chat_service.chat.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class NettyServer {

    @Autowired
    private NettyServerChannelInitializer nettyServerChannelInitializer;

    @Value("${netty.port}")
    private int nettyPort;

    public void run() throws Exception {


        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();


        try{
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(nettyServerChannelInitializer);

            ChannelFuture channelFuture = serverBootstrap.bind(nettyPort).sync();
            channelFuture.channel().closeFuture().sync();
        }finally{

            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

}
