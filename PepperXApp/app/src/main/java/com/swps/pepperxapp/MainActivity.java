/*
 *  Copyright (C) 2019 SmartLife Robotics, Poland
 *  See COPYING for the license
 */

package com.swps.pepperxapp;

import androidx.appcompat.app.AppCompatActivity;

import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.StrictMode;
import android.text.format.Formatter;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.aldebaran.qi.sdk.QiSDK;
import com.aldebaran.qi.sdk.design.activity.RobotActivity;
import com.aldebaran.qi.sdk.object.conversation.Phrase;
import com.swps.pepperxapp.network.Advertiser;
import com.swps.pepperxapp.network.HttpNanolets;
import com.swps.pepperxapp.robot.*;

import java.io.IOException;
import java.net.InetAddress;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class MainActivity  extends RobotActivity implements UiNotifier {

    private Robot robot;

    private Advertiser advertiser;
    private HttpNanolets httpd;

    private Button btnStart;
    private Button btnStop;
    private TextView txtStatus;
    private TextView txtDetection;
    private TextView txtHints;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnStart = findViewById(R.id.button_start);
        btnStop = findViewById(R.id.button_stop);
        txtStatus = findViewById(R.id.txtStaus);
        txtDetection = findViewById(R.id.txtDetection);
        txtHints = findViewById(R.id.txtHints);

        btnStart.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Code here executes on main thread after user presses button
            }
        });
        btnStop.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Code here executes on main thread after user presses button
                robot.performAction();
            }
        });


        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        robot = new Robot(this);
        QiSDK.register(this, robot);

        try {
            httpd = new HttpNanolets(getAssets());
            httpd.start();

            InetAddress hostAddr = InetAddress.getLocalHost();
            //WifiManager wm = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
            //String ipAddress = Formatter.formatIpAddress(wm.getConnectionInfo().getIpAddress());
            txtStatus.setText("Listening on: " + (hostAddr !=null ? hostAddr.getHostAddress() : "<null>"));
        }
        catch (IOException exc)
        {
            txtStatus.setText("Failed to open server: " + exc.getMessage());
        }

        //advertiser = new Advertiser();
        //advertiser.start();

    }

    @Override
    protected void onDestroy() {
        QiSDK.unregister(this, robot);
        httpd.stop();
        advertiser.stop();
        super.onDestroy();
    }


    /// UiNotifier Interface
    @Override
    public void setText(final String text) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                txtStatus.setText(text);
            }});

    }

    /// UiNotifier Interface
    @Override
    public void updateQiChatSuggestions(final List<Phrase> recommendation) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (recommendation.isEmpty()) {
                    txtHints.setText("");
                    return;
                }

                int[] indexes = getNRandomInt(4, recommendation.size());
                StringBuilder sb = new StringBuilder();
                for (int i=0;i<indexes.length;i++)
                    sb.append(recommendation.get(indexes[i]).getText()+"\n");
                txtHints.setText(sb.toString());
            }});
    }

    public int[] getNRandomInt(int N, int maxRange) {
        int[] result = new int[N];
        Set<Integer> used = new HashSet<>();
        Random gen = new Random();
        for (int i = 0; i < result.length; i++) {
            int newRandom;
            do {
                newRandom = gen.nextInt(maxRange);
            } while (used.contains(newRandom));
            result[i] = newRandom;
            used.add(newRandom);
        }
        return result;
    }
}
