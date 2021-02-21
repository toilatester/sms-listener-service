package com.toilatester.sms.listener;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.IBinder;
import android.os.PowerManager;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.toilatester.sms.MainActivity;
import com.toilatester.sms.server.HttpServer;
import com.toilatester.smslistener.R;

import java.util.logging.Logger;

public class NettyServerService extends Service {
    private static final Logger LOG = Logger.getLogger(NettyServerService.class.getName());
    public static final String CHANNEL_ID = "SMSListenerForegroundServiceChannel";
    private static HttpServer server;
    private int serverPort;
    private PowerManager.WakeLock wakeLock;
    private WifiManager.WifiLock wifiLock;

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
                .setPriority(NotificationManager.IMPORTANCE_HIGH)
                .build();
        startForeground(1, notification);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (server == null) {
            this.serverPort = intent.getIntExtra("serverPort", 8181);
            startNettyServer(this.serverPort);
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
        if (!server.isServerRunning()) {
            startNettyServer(this.serverPort);
        }
    }

    private synchronized void startNettyServer(int serverPort) {
        if (server != null && server.isServerRunning())
            return;
        server = new HttpServer(this.getApplicationContext(), this.getContentResolver(), serverPort);
        server.startServer();
        LOG.info("Completed start Netty server");
        createWakeLock();
        createWifiLock();

    }

    private synchronized void stopNettyServer() {
        if (server != null) {
            server.stopServer();
            server = null;
            this.wifiLock.release();
            this.wakeLock.release();
            LOG.warning("Stop Netty server");
        }
    }

    private void createWakeLock() {
        Context appContext = getApplicationContext();
        PowerManager pm = (PowerManager) appContext.getSystemService(Context.POWER_SERVICE);
        this.wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, this.getClass().getSimpleName());
        this.wakeLock.acquire();
        LOG.info("Completed create wake lock");
    }

    private void createWifiLock() {
        Context appContext = getApplicationContext();
        WifiManager wm = (WifiManager) appContext.getSystemService(Context.WIFI_SERVICE);
        if (!wm.isWifiEnabled()) {
            LOG.severe("Wifi is disabled");
            throw new IllegalStateException("Wifi is disabled");
        }
        this.wifiLock = wm.createWifiLock(WifiManager.WIFI_MODE_FULL_HIGH_PERF, this.getClass().getSimpleName());
        this.wifiLock.acquire();
        LOG.info("Completed create wifi lock");
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
