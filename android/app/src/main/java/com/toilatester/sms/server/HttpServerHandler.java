package com.toilatester.sms.server;

import com.toilatester.sms.server.handlers.Handler;

import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Map.Entry;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpUtil;
import io.netty.handler.codec.http.HttpVersion;

public class HttpServerHandler extends ChannelInboundHandlerAdapter {

    private Map<String, Handler> handlers;

    public HttpServerHandler(Map<String, Handler> handlers) {
        this.handlers = handlers;
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
        ctx.fireChannelReadComplete();
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (!(msg instanceof FullHttpRequest)) {
            errorBackendProcessingResponse(ctx, msg);
            super.channelRead(ctx, msg);
            return;
        }
        requestHandlerProcessing(ctx, msg);
        super.channelRead(ctx, msg);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
        super.exceptionCaught(ctx, cause);
    }

    private void errorBackendProcessingResponse(ChannelHandlerContext ctx, Object msg) {
        Handler errorHandler = this.handlers.get("error");
        ctx.write(errorHandler.getResponse());
    }

    private void requestHandlerProcessing(ChannelHandlerContext ctx, Object msg) {
        FullHttpRequest req = (FullHttpRequest) msg;
        switch (req.method().name()) {
            case "GET":
                getRequestProcessing(ctx, req);
                return;
            case "POST":
                postRequestProcessing(ctx, req);
                return;
            default:
                defaultRequestProcessing(ctx, req);
                return;
        }
    }

    private void defaultRequestProcessing(ChannelHandlerContext ctx, FullHttpRequest req) {
        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.METHOD_NOT_ALLOWED, Unpooled.wrappedBuffer(String.format("Method [%s] does not allow", req.method().name()).getBytes()));
        response.headers().set(HttpHeaderNames.CONTENT_LENGTH, response.content().readableBytes());
        ctx.write(response);
    }


    private void postRequestProcessing(ChannelHandlerContext ctx, FullHttpRequest req) {
        if (HttpUtil.is100ContinueExpected(req)) {
            ctx.write(new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.CONTINUE));
        }
        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.NO_CONTENT);
        String uri = req.uri();
        for (Entry<String, Handler> handler : handlers.entrySet()) {
            if (this.isMatchedHandler(uri, handler.getKey())) {
                handler.getValue().setRawRequestData(req.content().toString(StandardCharsets.UTF_8));
                ctx.write(handler.getValue().getResponse()).addListener(ChannelFutureListener.CLOSE);
                return;
            }
        }
        ctx.write(response).addListener(ChannelFutureListener.CLOSE);
    }

    private void getRequestProcessing(ChannelHandlerContext ctx, FullHttpRequest req) {
        if (HttpUtil.is100ContinueExpected(req)) {
            ctx.write(new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.CONTINUE));
        }
        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.NO_CONTENT);
        String uri = req.uri();
        for (Entry<String, Handler> handler : handlers.entrySet()) {
            if (this.isMatchedHandler(uri, handler.getKey())) {
                ctx.write(handler.getValue().getResponse()).addListener(ChannelFutureListener.CLOSE);
                return;
            }
        }
        ctx.write(response).addListener(ChannelFutureListener.CLOSE);
    }

    private boolean isMatchedHandler(String uri, String handlerUri) {
        return uri.equalsIgnoreCase(String.format("/%s", handlerUri));
    }
}