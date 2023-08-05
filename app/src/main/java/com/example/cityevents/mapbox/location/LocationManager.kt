package com.example.cityevents.mapbox.location

import android.content.Context
import android.location.Location
import android.os.Looper
import com.google.android.gms.location.*

class LocationManager(private val context: Context, private val locationFun: (Location) -> Unit) {
    private lateinit var locProvider: FusedLocationProviderClient
    private lateinit var locRequestBuilder: LocationRequest.Builder

    fun start() {
        initLocation()
        startLocationUpdates()
    }

    private val locCallBack = object : LocationCallback() {
        override fun onLocationResult(lresult: LocationResult) {
            super.onLocationResult(lresult)
            val currentLocation = lresult.lastLocation
            if (currentLocation != null) {
                locationFun(currentLocation)
            }
        }
    }

    private fun initLocation() {
        val updateInterval = 100L
        locRequestBuilder = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, updateInterval)
        locProvider = LocationServices.getFusedLocationProviderClient(context)
    }

    private fun startLocationUpdates() {
        locProvider.requestLocationUpdates(
            locRequestBuilder.build(), locCallBack, Looper.myLooper()
        )
    }
}