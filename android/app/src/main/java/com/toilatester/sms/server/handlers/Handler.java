package com.toilatester.sms.server.handlers;

import io.netty.handler.codec.http.FullHttpResponse;

public interface Handler {

    default void setRawRequestData(String requestData) {

    }

    void setResponseContent();

    void setResponseHeader();

    FullHttpResponse getResponse();
}
