package com.toilatester.sms.listener;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.toilatester.sms.manager.ReadSMS;
import com.toilatester.sms.models.SMSData;
import com.toilatester.smslistener.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    /**
     * permissions request code
     */
    private final static int REQUEST_CODE_ASK_PERMISSIONS = 1;

    /**
     * Permissions that need to be explicitly requested from end user.
     */
    private static final String[] REQUIRED_SDK_PERMISSIONS = new String[]{
            Manifest.permission.READ_SMS,
            Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.RECEIVE_SMS,
            Manifest.permission.READ_SMS};

    private Button startSMS, stopSMS, startServer, stopServer, fetchSMS;
    private ReadSMS readSMS;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //this.checkPermissions();
        setContentView(R.layout.activity_main);
        this.readSMS = new ReadSMS(this.getApplicationContext(), this.getContentResolver());
        this.startSMS = (Button) findViewById(R.id.startService);
        this.stopSMS = (Button) findViewById(R.id.stopService);
        this.startServer = (Button) findViewById(R.id.startServer);
        this.stopServer = (Button) findViewById(R.id.stopServer);
        this.fetchSMS = (Button) findViewById(R.id.fetchSMS);

        this.startSMS.setOnClickListener(this);
        this.stopSMS.setOnClickListener(this);
        this.startServer.setOnClickListener(this);
        this.stopServer.setOnClickListener(this);
        this.fetchSMS.setOnClickListener(this);
    }

    protected void checkPermissions() {
        final List<String> missingPermissions = new ArrayList<String>();
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


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_PERMISSIONS:
                for (int index = permissions.length - 1; index >= 0; --index) {
                    if (grantResults[index] != PackageManager.PERMISSION_GRANTED) {
                        // exit the app if one permission is not granted
                        Toast.makeText(this, "Required permission '" + permissions[index]
                                + "' not granted, exiting", Toast.LENGTH_LONG).show();
                        finish();
                        return;
                    }
                }
                // all permissions were granted
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    public void onClick(View view) {
        if (view == startSMS)
            startService(new Intent(this, SMSListenerService.class));
        else if (view == stopSMS)
            stopService(new Intent(this, SMSListenerService.class));
        else if (view == startServer)
            startService(new Intent(this, NettyServerService.class));
        else if (view == stopServer)
            stopService(new Intent(this, NettyServerService.class));
        else if (view == fetchSMS) {
            List<SMSData> allSMSMessages = this.readSMS.getAllSMSMessages();
            for (SMSData data : allSMSMessages) {
                System.out.println("====================DEBUG: " + data.getReceiveDate().toString() + " - " + data.getMobile() + " [" + data.getMessage() + "]");
            }
        }
    }
}