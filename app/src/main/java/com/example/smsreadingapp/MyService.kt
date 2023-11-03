package com.example.smsreadingapp

import android.annotation.SuppressLint
import android.app.Service
import android.content.*
import android.os.IBinder
import android.util.Log


class MyService : Service() {

    private val restartService = Intent("RestartService")
    private val br = MyBroadcast()

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    @SuppressLint("SetTextI18n")
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.i("start", "Service started")
        registerReceiver(br, IntentFilter("android.provider.Telephony.SMS_RECEIVED"))
        return START_STICKY
    }

    override fun onDestroy() {
        Log.i("My service", "stopped")
        super.onDestroy()
        unregisterReceiver(br)
        sendBroadcast(restartService)
    }
}