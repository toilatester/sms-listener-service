package com.toilatester.sms.listener;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.toilatester.smslistener.R;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private Button startSMS, stopSMS, startServer, stopServer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        startSMS = (Button) findViewById(R.id.startService);
        stopSMS = (Button) findViewById(R.id.stopService);
        startServer = (Button) findViewById(R.id.startServer);
        stopServer = (Button) findViewById(R.id.stopServer);

        startSMS.setOnClickListener(this);
        stopSMS.setOnClickListener(this);
        startServer.setOnClickListener(this);
        stopServer.setOnClickListener(this);
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
    }
}