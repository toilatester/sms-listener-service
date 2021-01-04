package com.toilatester.sms.server.handlers;

import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;


public class Error implements Handler {
    private FullHttpResponse response;

    public Error() {
    }

    @Override
    public void setResponseContent() {
        this.response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.BAD_REQUEST);
        this.response.content().writeBytes("Error in backend!".getBytes());
        this.response.headers().set(HttpHeaderNames.CONTENT_LENGTH, this.response.content().readableBytes());
    }

    @Override
    public void setResponseHeader() {
        this.response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/plain");
    }

    @Override
    public FullHttpResponse getResponse() {
        this.setResponseContent();
        this.setResponseHeader();
        return this.response;
    }

}
