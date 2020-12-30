package com.toilatester.sms.server;

import android.content.ContentResolver;
import android.content.Context;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpServerCodec;

public class HttpHelloWorldServerInitializer extends ChannelInitializer<SocketChannel> {
    private Context context;
    private ContentResolver content;
    private ServiceCallbacks serviceCallbacks;

    public HttpHelloWorldServerInitializer(Context context, ContentResolver content, ServiceCallbacks serviceCallbacks) {
        this.content = content;
        this.context = context;
        this.serviceCallbacks = serviceCallbacks;
    }

    @Override
    public void initChannel(SocketChannel ch) {
        ChannelPipeline p = ch.pipeline();
        p.addLast(new HttpServerCodec());
        p.addLast(new HttpHelloWorldServerHandler(this.context, this.content, this.serviceCallbacks));
    }
}
