package com.example.cityevents.mapbox.location

import android.Manifest
import android.app.Service
import android.content.Intent
import android.content.pm.PackageManager
import android.os.IBinder
import android.os.Looper
import androidx.core.app.ActivityCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.example.cityevents.MainViewModel
import com.example.cityevents.mapbox.location.serviceNotification.Notification
import com.google.android.gms.location.*

class LocationService : Service() {
    private lateinit var locProvider: FusedLocationProviderClient
    private lateinit var locRequestBuilder: LocationRequest.Builder

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Notification(this).startNotification()
        isRunning = true
        return START_STICKY
    }

    override fun onCreate() {
        super.onCreate()
        initLocation()
        startLocationUpdates()
    }

    override fun onDestroy() {
        super.onDestroy()
        isRunning = false
        locProvider.removeLocationUpdates(locCallBack)
    }

    private val locCallBack = object : LocationCallback() {
        override fun onLocationResult(lresult: LocationResult) {
            super.onLocationResult(lresult)
            val currentLocation = lresult.lastLocation
            if (currentLocation != null) {
                lastLocation = LocationSerializable(
                    currentLocation.latitude, currentLocation.longitude, currentLocation.speed
                )
                sendLocData(LocationModel(lastLocation))
            }
        }
    }

    private fun sendLocData(locModel: LocationModel) {
        val intent = Intent(LOC_MODEL_INTENT)
        intent.putExtra(LOC_MODEL_INTENT, locModel)
        LocalBroadcastManager.getInstance(applicationContext).sendBroadcast(intent)
    }

    private fun initLocation() {
        val updateInterval = 100L
        locRequestBuilder = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, updateInterval)
        locProvider = LocationServices.getFusedLocationProviderClient(baseContext)
    }

    private fun startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) return

        locProvider.requestLocationUpdates(
            locRequestBuilder.build(), locCallBack, Looper.myLooper()
        )
    }

    companion object {
        lateinit var lastLocation: LocationSerializable
        lateinit var model: MainViewModel
        const val LOC_MODEL_INTENT = "loc_intent"
        const val CHANNEL_ID = "channel_1"
        var isRunning = false
    }
}