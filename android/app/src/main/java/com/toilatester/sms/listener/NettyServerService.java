package com.toilatester.sms.listener;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.toilatester.sms.server.HttpServer;
import com.toilatester.smslistener.R;

public class NettyServerService extends Service {
    private static final int NOTIFY_ID = 1;
    private static final String NOTIFY_CHANNEL_ID = "Channel_Id";
    private static HttpServer server;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (server == null) {
            int serverPort = intent.getIntExtra("serverPort", 8181);
            server = new HttpServer(this.getApplicationContext(), this.getContentResolver(), serverPort);
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
