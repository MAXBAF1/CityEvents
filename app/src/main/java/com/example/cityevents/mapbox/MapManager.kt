package com.example.cityevents.mapbox

import android.location.Location
import androidx.fragment.app.activityViewModels
import com.example.cityevents.MainApp
import com.example.cityevents.MainViewModel
import com.example.cityevents.R
import com.example.cityevents.fragments.mainFragment.MainFragment
import com.example.cityevents.mapbox.location.*
import com.mapbox.geojson.Point
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
        val pManager = LocationPermissionManager(mainFragment, ::startLocUpdate)
        pManager.registerPermissionLauncher()
        pManager.checkLocationPermission()
        onMapStyleReady()
    }

    private fun onMapStyleReady() {
        map.loadStyle(style(mainFragment.getString(R.string.globe3dKey)) { })
    }

    private var isFirstStart = true

    private fun updateMap(location: Location) {
        if (isFirstStart) {
            Compass(mapView, location).add()
            isFirstStart = false
        }
    }

    private fun startLocUpdate() {
        if (isServiceRunning) return
        isServiceRunning = true

        mapView.location.apply {
            enabled = true
            pulsingEnabled = true
        }
        LocationManager(mainFragment.requireContext(), ::updateMap).start()
    }
}