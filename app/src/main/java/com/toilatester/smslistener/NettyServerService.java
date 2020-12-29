package com.toilatester.smslistener;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.Nullable;

import com.toilatester.smsserver.HttpServer;

public class NettyServerService extends Service {

    private static HttpServer server;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private final int port = 8181;
    private Thread serverThread;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (server == null) {
            server = new HttpServer();
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
