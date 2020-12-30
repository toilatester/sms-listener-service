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
    private ServiceCallbacks serviceCallbacks;

    public HttpServer(Context context, ContentResolver content, ServiceCallbacks serviceCallbacks) {
        this.content = content;
        this.context = context;
        this.serviceCallbacks = serviceCallbacks;
    }

    public void startServer() {
        if (serverThread != null) {
            throw new IllegalStateException("Server is already running");
        }
        Runnable httpServerRunnable = new HttpServerRunnable(this.context, this.content, this.serviceCallbacks);
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
        private ServiceCallbacks serviceCallbacks;

        public HttpServerRunnable(Context context, ContentResolver content, ServiceCallbacks serviceCallbacks) {
            this.content = content;
            this.context = context;
            this.serviceCallbacks = serviceCallbacks;
        }

        public void run() {
            EventLoopGroup bossGroup = new NioEventLoopGroup(1);
            EventLoopGroup workerGroup = new NioEventLoopGroup();
            try {
                ServerBootstrap b = new ServerBootstrap();
                b.option(ChannelOption.SO_BACKLOG, 1024);
                b.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class)
                        .handler(new LoggingHandler(LogLevel.INFO))
                        .childHandler(new HttpHelloWorldServerInitializer(this.context, this.content, this.serviceCallbacks));

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
    }
}
