package com.toilatester.sms.server;

import android.content.ContentResolver;
import android.content.Context;

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
    private Context context;
    private ContentResolver content;

    public HttpServer(Context context, ContentResolver content) {
        this.content = content;
        this.context = context;
    }

    public void startServer() {
        if (serverThread != null) {
            throw new IllegalStateException("Server is already running");
        }
        Runnable httpServerRunnable = new HttpServerRunnable(this.context, this.content);
        serverThread = new Thread(httpServerRunnable);
        serverThread.start();
    }

    public void stopServer() {
        if (serverThread == null) {
            return;
        }

        serverThread.interrupt();
        serverThread = null;
    }

    private class HttpServerRunnable implements Runnable {
        private Context context;
        private ContentResolver content;

        public HttpServerRunnable(Context context, ContentResolver content) {
            this.content = content;
            this.context = context;
        }

        public void run() {
            EventLoopGroup bossGroup = new NioEventLoopGroup(1);
            EventLoopGroup workerGroup = new NioEventLoopGroup();
            try {
                ServerBootstrap b = new ServerBootstrap();
                b.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class)
                        .option(ChannelOption.SO_BACKLOG, 1024)
                        .option(ChannelOption.SO_REUSEADDR, true)
                        .option(ChannelOption.SO_KEEPALIVE, true)
                        .option(ChannelOption.TCP_NODELAY, true)
                        .handler(new LoggingHandler(LogLevel.INFO))
                        .childHandler(new HttpServerInitializer(this.context, this.content));

                Channel ch = b.bind("0.0.0.0",PORT).sync().channel();

                System.out
                        .println("Open your web browser and navigate to " + "http" + "://0.0.0.0:" + PORT + '/');

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
    }
}
