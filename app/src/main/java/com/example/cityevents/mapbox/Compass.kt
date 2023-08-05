package com.example.cityevents.mapbox

import com.example.cityevents.mapbox.location.LocationSerializable
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.MapView
import com.mapbox.maps.plugin.animation.MapAnimationOptions
import com.mapbox.maps.plugin.animation.easeTo
import com.mapbox.maps.plugin.compass.compass

class Compass(
    mapView: MapView,
    private val curLocation: LocationSerializable
) {
    private val compass = mapView.compass
    private val map = mapView.getMapboxMap()

    fun add() {
        compass.fadeWhenFacingNorth = false
        alignCameraToNorth()
        centerCamera()

        compass.addCompassClickListener {
            if (map.cameraState.bearing != 0.0) {
                alignCameraToNorth()
            } else {
                centerCamera()
            }
        }
    }

    private fun alignCameraToNorth() {
        map.easeTo(CameraOptions.Builder().bearing(0.0).pitch(0.0).build(),
            MapAnimationOptions.mapAnimationOptions {
                // Опциональная настройка анимации, если требуется
            })
    }

    private fun centerCamera() {
        val latLng = Point.fromLngLat(curLocation.longitude, curLocation.latitude)
        map.easeTo(CameraOptions.Builder().center(latLng).zoom(ZOOM_LEVEL).build(),
            MapAnimationOptions.mapAnimationOptions {
                // Опциональная настройка анимации, если требуется
            })
    }

    companion object {
        const val ZOOM_LEVEL = 13.0
    }
}