package com.toilatester.sms.listener;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.Nullable;

import com.toilatester.sms.server.HttpServer;

public class NettyServerService extends Service {
    private static HttpServer server;
    private final int port = 8181;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (server == null) {
            server = new HttpServer(this.getApplicationContext(), this.getContentResolver());
            server.startServer();
        }
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (server != null) {
            server.stopServer();
            server = null;
        }
    }
}
