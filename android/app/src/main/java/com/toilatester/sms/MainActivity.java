package com.toilatester.sms;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.toilatester.sms.listener.NettyServerService;
import com.toilatester.smslistener.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static final Logger LOG = Logger.getLogger(MainActivity.class.getName());
    /**
     * permissions request code
     */
    private static final int REQUEST_CODE_ASK_PERMISSIONS = 1;

    /**
     * Permissions that need to be explicitly requested from end user.
     */
    private static final String[] REQUIRED_SDK_PERMISSIONS = new String[]{
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.READ_SMS,
            Manifest.permission.RECEIVE_SMS,
            Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.INTERNET,
            Manifest.permission.CHANGE_WIFI_STATE,
            Manifest.permission.ACCESS_WIFI_STATE,
            Manifest.permission.FOREGROUND_SERVICE,
            Manifest.permission.REQUEST_IGNORE_BATTERY_OPTIMIZATIONS,
            Manifest.permission.WAKE_LOCK
    };

    private Button startServer;
    private Button stopServer;
    private int serverPort;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.serverPort = this.getIntent().getIntExtra("serverPort", 8181);
        checkPermissions();
        setContentView(R.layout.activity_main);
        ignoreDozeMode();
        startSmsService(this.serverPort);
        bindActionListener();
        moveTaskToBack(true);
    }

    @Override
    public void onClick(View view) {
        if (view == startServer) {
            startService(createSmsServerIntent(this.serverPort));
        } else if (view == stopServer) {
            stopService(new Intent(this, NettyServerService.class));
        }
    }

    protected void checkPermissions() {
        final List<String> missingPermissions = new ArrayList<>();
        // check all required dynamic permissions
        for (final String permission : REQUIRED_SDK_PERMISSIONS) {
            final int result = ContextCompat.checkSelfPermission(this, permission);
            if (result != PackageManager.PERMISSION_GRANTED) {
                missingPermissions.add(permission);
            }
        }
        if (!missingPermissions.isEmpty()) {
            // request all missing permissions
            final String[] permissions = missingPermissions
                    .toArray(new String[missingPermissions.size()]);
            ActivityCompat.requestPermissions(this, permissions, REQUEST_CODE_ASK_PERMISSIONS);
        } else {
            final int[] grantResults = new int[REQUIRED_SDK_PERMISSIONS.length];
            Arrays.fill(grantResults, PackageManager.PERMISSION_GRANTED);
            onRequestPermissionsResult(REQUEST_CODE_ASK_PERMISSIONS, REQUIRED_SDK_PERMISSIONS,
                    grantResults);
        }
    }

    private void startSmsService(int serverPort) {
        Intent smsServiceIntent = createSmsServerIntent(serverPort);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(smsServiceIntent);
        } else {
            startService(smsServiceIntent);
        }
    }

    private Intent createSmsServerIntent(int serverPort) {
        Intent smsIntent = new Intent(this, NettyServerService.class);
        smsIntent.putExtra("serverPort", serverPort);
        return smsIntent;
    }

    private void bindActionListener() {
        this.startServer = (Button) findViewById(R.id.startServer);
        this.stopServer = (Button) findViewById(R.id.stopServer);

        this.startServer.setOnClickListener(this);
        this.stopServer.setOnClickListener(this);
    }

    private void ignoreDozeMode() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Intent intent = new Intent();
            String packageName = getPackageName();
            LOG.info(String.format("Get Package Name To Ignore Doze Mode: %s", packageName));
            PowerManager pm = (PowerManager) getSystemService(POWER_SERVICE);
            if (!pm.isIgnoringBatteryOptimizations(packageName)) {
                intent.setAction(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
                intent.setData(Uri.parse("package:" + packageName));
                startActivity(intent);
                LOG.info(String.format("Completed Add Package Name To Ignore Doze Mode: %s", packageName));
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CODE_ASK_PERMISSIONS) {
            grantPermission(permissions, grantResults);
        } else
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void grantPermission(String[] permissions, int[] grantResults) {
        for (int index = permissions.length - 1; index >= 0; --index) {
            if (grantResults[index] != PackageManager.PERMISSION_GRANTED) {
                // exit the app if one permission is not granted
                Toast.makeText(this, "Required permission '" + permissions[index]
                        + "' not granted, exiting", Toast.LENGTH_LONG).show();
                finish();
                return;
            }
        }
    }
}