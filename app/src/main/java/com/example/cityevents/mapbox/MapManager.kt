package com.example.cityevents.mapbox

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.activityViewModels
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.example.cityevents.MainApp
import com.example.cityevents.MainViewModel
import com.example.cityevents.R
import com.example.cityevents.fragments.mainFragment.MainFragment
import com.example.cityevents.mapbox.location.LocationModel
import com.example.cityevents.mapbox.location.LocationPermissionManager
import com.example.cityevents.mapbox.location.LocationService
import com.example.cityevents.utils.serializable
import com.mapbox.maps.MapView
import com.mapbox.maps.extension.style.style
import com.mapbox.maps.plugin.locationcomponent.location

class MapManager(private val mainFragment: MainFragment, private val mapView: MapView) {
    private val map = mapView.getMapboxMap()
    private var isServiceRunning = false

    private val model: MainViewModel by mainFragment.activityViewModels {
        MainViewModel.ViewModelFactory((mainFragment.requireContext().applicationContext as MainApp).database)
    }

    fun initMap() {
        val pManager = LocationPermissionManager(mainFragment, ::startLocService)
        pManager.registerPermissionLauncher()
        pManager.checkLocationPermission()
        onMapStyleReady()
        checkServiceState()
        registerLocReceiver()
        locUpdates()
    }

    private fun onMapStyleReady() {
        map.loadStyle(style(mainFragment.getString(R.string.globe3dKey)) { })
    }

    private fun locUpdates() {
        model.locUpdates.observe(mainFragment.viewLifecycleOwner) {
            updateMap(it)
        }
    }

    private var isFirstStart = true

    private fun updateMap(locModel: LocationModel) {
        if (isFirstStart) {
            Compass(mapView, locModel.lastLocation).add()
            isFirstStart = false
        }
    }

    private fun startLocService() {
        if (isServiceRunning) return
        isServiceRunning = true

        mapView.location.apply {
            enabled = true
            pulsingEnabled = true
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mainFragment.activity?.startForegroundService(
                Intent(mainFragment.activity, LocationService::class.java)
            )
        } else mainFragment.activity?.startService(
            Intent(mainFragment.activity, LocationService::class.java)
        )
        LocationService.model = model
    }

    private fun checkServiceState() {
        isServiceRunning = LocationService.isRunning
    }

    val broadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, i: Intent?) {
            if (i?.action == LocationService.LOC_MODEL_INTENT) {
                val locModel = i.serializable(LocationService.LOC_MODEL_INTENT) as? LocationModel
                model.locUpdates.value = locModel
            }
        }
    }

    private fun registerLocReceiver() {
        val locFilter = IntentFilter(LocationService.LOC_MODEL_INTENT)
        LocalBroadcastManager.getInstance(mainFragment.activity as AppCompatActivity)
            .registerReceiver(broadcastReceiver, locFilter)
    }
}