package com.toilatester.sms.listener;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import androidx.annotation.Nullable;

import com.toilatester.sms.server.HttpServer;
import com.toilatester.sms.server.ServiceCallbacks;

public class NettyServerService extends Service {
    private final IBinder binder = new LocalBinder();
    private ServiceCallbacks serviceCallbacks;
    private static HttpServer server;
    private final int port = 8181;
    private Thread serverThread;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    public void setCallbacks(ServiceCallbacks callbacks) {
        System.out.println("================= set service callbacks " + callbacks);
        this.serviceCallbacks = callbacks;
        if (callbacks != null && server == null) {
            System.out.println("========== Start server");
            server = new HttpServer(this.getApplicationContext(), this.getContentResolver(), this.serviceCallbacks);
            server.startServer();

        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (server == null) {
            System.out.println("================= start service check service callback " + this.serviceCallbacks);
//            server = new HttpServer(this.getApplicationContext(), this.getContentResolver(), this.serviceCallbacks);
//            server.startServer();
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

    public class LocalBinder extends Binder {
        NettyServerService getService() {
            // Return this instance of MyService so clients can call public methods
            return NettyServerService.this;
        }
    }
}
