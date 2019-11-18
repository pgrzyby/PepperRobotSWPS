package com.swps.pepperxapp.network;

import android.util.Log;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.concurrent.atomic.AtomicBoolean;

public class Advertiser implements Runnable {
    private Thread myThread=null;
    private final AtomicBoolean running = new AtomicBoolean(false);
    private int interval = 3500;
    private String message = "";


    public Advertiser() {
        createMessage();
    }

    public void start(){

        this.myThread = new Thread(this);
        this.myThread.setDaemon(true);
        this.myThread.setName("NanoHttpd Main Listener");
        this.myThread.start();

        Log.i("Robot", "Advertiser::start() done");
    }

    public void stop(){
        Log.i("Robot", "Advertiser::stop() ");
        myThread.interrupt();
        myThread = null;
    }

    private void createMessage() {
        message = "PEPPER:1";
    }

    private void broadcastMessage(String broadcastMessage, InetAddress address) throws IOException {
        DatagramSocket socket = new DatagramSocket();
        socket.setBroadcast(true);

        byte[] buffer = broadcastMessage.getBytes();

        DatagramPacket packet
                = new DatagramPacket(buffer, buffer.length, address, 4445);
        socket.send(packet);
        socket.close();
    }

    private void broadcastAll(String message) {

        try {
            broadcastMessage(message, InetAddress.getByName("255.255.255.255"));
        } catch (IOException e) {
            Log.e("Robot", "Advertiser::broadcastAll() exception: " + e.getMessage());

        }
    }

    @Override
    public void run() {
        running.set(true);
        while (running.get()) {
            try {
                Thread.sleep(interval);
            } catch (InterruptedException e){
                Thread.currentThread().interrupt();
            }
            // do something here
            broadcastAll(message);
        }
    }
}


