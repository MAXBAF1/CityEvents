package com.example.cityevents.mapbox.location.serviceNotification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Process.killProcess
import android.os.Process.myPid

class StopServiceReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        killProcess(myPid())
    }
}