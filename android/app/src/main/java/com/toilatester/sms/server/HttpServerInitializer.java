package com.toilatester.sms.server;

import android.content.ContentResolver;
import android.content.Context;
import android.util.ArrayMap;

import com.toilatester.sms.server.handlers.GetSMSByPhone;
import com.toilatester.sms.utils.ReadSMS;
import com.toilatester.sms.server.handlers.Error;
import com.toilatester.sms.server.handlers.GetAllSMS;
import com.toilatester.sms.server.handlers.GetLatestSMS;
import com.toilatester.sms.server.handlers.Handler;

import java.util.Map;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;

public class HttpServerInitializer extends ChannelInitializer<SocketChannel> {
    Map<String, Handler> handlers = new ArrayMap<>();

    public HttpServerInitializer(Context context, ContentResolver content) {
        this.initHandlers(context, content);
    }

    private void initHandlers(Context context, ContentResolver content) {
        this.handlers.put("error", new Error());
        this.handlers.put("all", new GetAllSMS(new ReadSMS(context, content)));
        this.handlers.put("latest", new GetLatestSMS(new ReadSMS(context, content)));
        this.handlers.put("byPhone", new GetSMSByPhone(new ReadSMS(context, content)));
    }

    @Override
    public void initChannel(SocketChannel ch) {
        ChannelPipeline p = ch.pipeline();
        p.addLast("codec", new HttpServerCodec());
        p.addLast("aggregator", new HttpObjectAggregator(65536));
        p.addLast("listener/handlers", new HttpServerHandler(this.handlers));
    }
}
