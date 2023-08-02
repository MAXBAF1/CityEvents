package com.example.cityevents.mapbox.location.serviceNotification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.cityevents.mapbox.location.LocationService
import com.example.cityevents.MainActivity
import com.example.cityevents.R

class Notification(private val service: Service) {
    fun startNotification() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val nChannel = NotificationChannel(
                LocationService.CHANNEL_ID,
                "Location Service",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            val nManager =
                service.getSystemService(NotificationManager::class.java) as NotificationManager
            nManager.createNotificationChannel(nChannel)
        }

        val nIntent = Intent(service, MainActivity::class.java)
        val pIntent = when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ->
                PendingIntent.getActivity(service, 10, nIntent, PendingIntent.FLAG_IMMUTABLE)
            else -> PendingIntent.getActivity(service, 10, nIntent, 0)
        }

        val stopIntent = Intent(service, StopServiceReceiver::class.java)
        val stopAction = PendingIntent.getBroadcast(
            service,
            0,
            stopIntent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val notification = NotificationCompat.Builder(service, LocationService.CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(service.getString(R.string.around_running))
            .addAction(R.mipmap.ic_launcher, service.getString(R.string.stop_and_exit), stopAction)
            .setContentIntent(pIntent).setAutoCancel(true).build()

        service.startForeground(99, notification)
    }
}