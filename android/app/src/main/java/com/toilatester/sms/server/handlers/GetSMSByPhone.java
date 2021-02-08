package com.toilatester.sms.server.handlers;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.toilatester.sms.models.SMSData;
import com.toilatester.sms.utils.ReadSMS;

import java.util.List;
import java.util.Map;

import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;

public class GetSMSByPhone implements Handler {
    private FullHttpResponse response;
    private ReadSMS readSMS;
    private String rawRequestData = "{}";

    public GetSMSByPhone(ReadSMS readSMS) {
        this.readSMS = readSMS;
    }

    @Override
    public void setRawRequestData(String requestData) {
        this.rawRequestData = requestData;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void setResponseContent() {
        try {
            Gson gson = new Gson();
            Map<String, String> jsonObject = gson.fromJson(this.rawRequestData, Map.class);
            List<SMSData> smsMessages = this.readSMS.getSMSByPhoneNumber(jsonObject.get("phone"), jsonObject.get("limit"));
            String json = new Gson().toJson(smsMessages);
            this.response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
            this.response.content().writeBytes(Unpooled.wrappedBuffer(json.getBytes()));
            this.response.headers().set(HttpHeaderNames.CONTENT_LENGTH, this.response.content().readableBytes());
        } catch (JsonSyntaxException e) {
            this.response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.BAD_REQUEST);
            this.response.content().writeBytes(Unpooled.wrappedBuffer("Invalid request data".getBytes()));
            this.response.headers().set(HttpHeaderNames.CONTENT_LENGTH, this.response.content().readableBytes());
        }
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
