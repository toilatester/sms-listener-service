package com.toilatester.sms.listener;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.toilatester.sms.MainActivity;
import com.toilatester.sms.server.HttpServer;
import com.toilatester.smslistener.R;

import java.util.logging.Logger;

public class NettyServerService extends Service {
    private final Logger LOG = Logger.getLogger(HttpServer.class.getName());
    public static final String CHANNEL_ID = "SMSListenerForegroundServiceChannel";
    private static HttpServer server;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        LOG.info("NettyServerService created");
        createNotificationChannel();
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,
                0, notificationIntent, 0);
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("https://toilatester.blog")
                .setContentText("SMSListenerService")
                .setSmallIcon(R.mipmap.toilatester)
                .setContentIntent(pendingIntent)
                .setPriority(Notification.PRIORITY_HIGH)
                .build();
        startForeground(1, notification);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (server == null) {
            int serverPort = intent.getIntExtra("serverPort", 8181);
            startNettyServer(serverPort);
        }
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopNettyServer();
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        LOG.warning("onTaskRemoved remove task");
    }

    private void startNettyServer(int serverPort) {
        if (server != null)
            return;
        server = new HttpServer(this.getApplicationContext(), this.getContentResolver(), serverPort);
        server.startServer();
        LOG.info("Completed start Netty server");
    }

    private void stopNettyServer(){
        if (server != null) {
            server.stopServer();
            server = null;
            LOG.warning("Stop Netty server");
        }
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "Foreground Service Channel",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(serviceChannel);
        }
    }
}
