package com.toilatester.sms.server.handlers;

import io.netty.handler.codec.http.FullHttpResponse;

public interface Handler {
    public void setResponseContent();

    public void setResponseHeader();

    public FullHttpResponse getResponse();
}
