package com.toilatester.sms.server;

import android.content.ContentResolver;
import android.content.Context;

import java.util.logging.LogManager;
import java.util.logging.Logger;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

public class HttpServer {
    private final Logger LOG = Logger.getLogger(HttpServer.class.getName());
    private int serverPort;
    private static Thread serverThread;
    private Context context;
    private ContentResolver content;
    private static boolean serverRunning = false;


    public HttpServer(Context context, ContentResolver content, int serverPort) {
        this.content = content;
        this.context = context;
        this.serverPort = serverPort;
    }

    public void startServer() {
        if (serverThread != null) {
            throw new IllegalStateException("Server is already running");
        }
        Runnable httpServerRunnable = new HttpServerRunnable(this.context, this.content, this.serverPort);
        serverThread = new Thread(httpServerRunnable);
        serverThread.start();
        serverRunning = true;
    }

    public void stopServer() {
        if (serverThread == null) {
            return;
        }

        serverThread.interrupt();
        serverThread = null;
        serverRunning = false;
    }

    public boolean isServerRunning() {
        return serverRunning;
    }

    private class HttpServerRunnable implements Runnable {
        private Context context;
        private ContentResolver content;
        private int serverPort;

        public HttpServerRunnable(Context context, ContentResolver content, int serverPort) {
            this.content = content;
            this.context = context;
            this.serverPort = serverPort;
        }

        public void run() {
            LOG.info("Server Port: " + this.serverPort);
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

                Channel ch = b.bind("0.0.0.0", this.serverPort).sync().channel();

                LOG.info("Open your web browser and navigate to " + "http" + "://0.0.0.0:" + this.serverPort + '/');

                ch.closeFuture().sync();
            } catch (InterruptedException e) {
                LOG.warning("Stop SMS server");
                serverRunning = false;
            } catch (Exception e) {
                LOG.severe(e.getMessage());
            } finally {
                bossGroup.shutdownGracefully();
                workerGroup.shutdownGracefully();
            }
        }
    }
}
