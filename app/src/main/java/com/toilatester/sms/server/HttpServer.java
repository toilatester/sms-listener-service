package com.toilatester.sms.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

public class HttpServer {
    static final int PORT = 8181;
    private static Thread serverThread;

    public void startServer() {
        if (serverThread != null) {
            throw new IllegalStateException("Server is already running");
        }
        serverThread = new Thread() {
            @Override
            public void run() {
                EventLoopGroup bossGroup = new NioEventLoopGroup(1);
                EventLoopGroup workerGroup = new NioEventLoopGroup();
                try {
                    ServerBootstrap b = new ServerBootstrap();
                    b.option(ChannelOption.SO_BACKLOG, 1024);
                    b.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class)
                            .handler(new LoggingHandler(LogLevel.INFO))
                            .childHandler(new HttpHelloWorldServerInitializer());

                    Channel ch = b.bind(PORT).sync().channel();

                    System.err
                            .println("Open your web browser and navigate to " + "http" + "://127.0.0.1:" + PORT + '/');

                    ch.closeFuture().sync();
                } catch (InterruptedException e) {
                    // ignore
                } catch (Exception e) {
                    System.err.println(e.getMessage());
                } finally {
                    bossGroup.shutdownGracefully();
                    workerGroup.shutdownGracefully();
                }
            }
        };
        serverThread.start();
    }

    public void stopServer() {
        if (serverThread == null) {
            return;
        }

        serverThread.interrupt();
        serverThread = null;
    }
}
