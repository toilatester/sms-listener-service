package com.toilatester.sms.server.handlers;

import com.google.gson.Gson;
import com.toilatester.sms.utils.ReadSMS;
import com.toilatester.sms.models.SMSData;

import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;

public class GetLatestSMS implements Handler {
    private FullHttpResponse response;
    private ReadSMS readSMS;

    public GetLatestSMS(ReadSMS readSMS) {
        this.readSMS = readSMS;
    }

    @Override
    public void setResponseContent() {
        SMSData smsMessages = this.readSMS.getLatestSMSMessage();
        String json = new Gson().toJson(smsMessages);
        this.response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
        this.response.content().writeBytes(Unpooled.wrappedBuffer(json.getBytes()));
        this.response.headers().set(HttpHeaderNames.CONTENT_LENGTH, this.response.content().readableBytes());
    }

    @Override
    public void setResponseHeader() {
        this.response.headers().set(HttpHeaderNames.CONTENT_TYPE, "application/json");
    }

    @Override
    public FullHttpResponse getResponse() {
        this.setResponseContent();
        this.setResponseHeader();
        return this.response;
    }
}
