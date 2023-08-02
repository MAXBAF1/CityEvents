package com.example.around.mapbox

import android.content.Context
import com.example.cityevents.R
import com.example.cityevents.mapbox.location.LocationSerializable
import com.example.cityevents.utils.setColor
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.MapView
import com.mapbox.maps.plugin.animation.MapAnimationOptions
import com.mapbox.maps.plugin.animation.easeTo
import com.mapbox.maps.plugin.compass.compass

class Compass(
    private val context: Context,
    mapView: MapView,
    private val curLocation: LocationSerializable
) {
    private val compass = mapView.compass
    private val map = mapView.getMapboxMap()

    fun add() {
        compass.fadeWhenFacingNorth = false
        alignCameraToNorth()
        centerCamera()
        val icNav = context.getDrawable(R.drawable.ic_navigation)

        compass.addCompassClickListener {
            if (map.cameraState.bearing != 0.0) {
                alignCameraToNorth()
                icNav?.setColor(context, R.color.black_gray)
                compass.image = icNav
            } else {
                centerCamera()
                icNav?.setColor(context, R.color.blue_500)
            }
        }
        map.addOnCameraChangeListener {
            if (map.cameraState.bearing != 0.0) {
                icNav?.setColor(context, R.color.black_gray)
                compass.image = null
            } else {
                compass.image = icNav
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