package com.example.cityevents.mapbox

import com.example.cityevents.R
import com.example.cityevents.fragments.MapFragment
import com.example.cityevents.mapbox.location.*
import com.mapbox.maps.MapView
import com.mapbox.maps.extension.style.style

class MapManager(private val mainFragment: MapFragment, private val mapView: MapView) {
    private val map = mapView.getMapboxMap()

    fun initMap() {
        val pManager = LocationPermissionManager(mainFragment, ::startLocUpdate)
        pManager.registerPermissionLauncher()
        pManager.checkLocationPermission()
        onMapStyleReady()
    }

    private fun onMapStyleReady() {
        map.loadStyle(style(mainFragment.getString(R.string.globe3dKey)) { })
    }

    private fun startLocUpdate() {
        LocationManager(mapView, mainFragment.requireContext()).start()
    }
}