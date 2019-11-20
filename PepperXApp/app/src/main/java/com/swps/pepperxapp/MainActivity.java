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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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

import static java.lang.System.currentTimeMillis;

public class MainActivity  extends RobotActivity implements UiNotifier {

    private Robot robot;

    private Advertiser advertiser;
    private HttpNanolets httpd;

    private Button btnStart;
    private Button btnStop;
    private TextView txtStatus;
    private TextView txtDetection;
    private TextView txtHints;

    private  ImageView imgAvatar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        btnStart = findViewById(R.id.button_start);
//        btnStop = findViewById(R.id.button_stop);
        txtStatus = findViewById(R.id.txtStaus);
        txtDetection = findViewById(R.id.txtDetection);
        txtHints = findViewById(R.id.txtHints);
        imgAvatar = findViewById(R.id.imgAvatar);


        ImageView imgSPWS = findViewById(R.id.imgSWPS);
        imgSPWS.setOnClickListener(new View.OnClickListener() {
            int _cntr = 0;
            long _ts = 0;
            public void onClick(View v) {
                // Code here executes on main thread after user presses button
                long ts = System.currentTimeMillis();
                if (ts - _ts < 500) {
                    _cntr++;
                }
                else {
                    _cntr = 0;
                }
                _ts = ts;

                if (_cntr > 4) {
                    _cntr=0;
                    //robot.performAction();
                    Toast.makeText(getApplicationContext(), "ACTION!", Toast.LENGTH_SHORT).show();
                }
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
