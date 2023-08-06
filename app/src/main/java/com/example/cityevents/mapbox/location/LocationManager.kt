package com.example.cityevents.mapbox.location

import android.content.Context
import com.mapbox.android.gestures.MoveGestureDetector
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.MapView
import com.mapbox.maps.plugin.compass.compass
import com.mapbox.maps.plugin.gestures.OnMoveListener
import com.mapbox.maps.plugin.gestures.gestures
import com.mapbox.maps.plugin.locationcomponent.OnIndicatorBearingChangedListener
import com.mapbox.maps.plugin.locationcomponent.OnIndicatorPositionChangedListener
import com.mapbox.maps.plugin.locationcomponent.location

class LocationManager(private val mapView: MapView, private val context: Context) {
    private val mapboxMap = mapView.getMapboxMap()
    private var isCameraCentered = false
    private val locationPlugin = mapView.location

    private val bearingChangedListener = OnIndicatorBearingChangedListener {
        mapboxMap.setCamera(CameraOptions.Builder().bearing(it).build())
    }

    private val positionChangedListener = OnIndicatorPositionChangedListener {
        mapboxMap.setCamera(CameraOptions.Builder().center(it).zoom(ZOOM_LEVEL).build())
    }

    private val onMoveListener = object : OnMoveListener {
        override fun onMoveBegin(detector: MoveGestureDetector) {
            onCameraTrackingDismissed()
            isCameraCentered = false
        }

        override fun onMove(detector: MoveGestureDetector): Boolean {
            return false
        }

        override fun onMoveEnd(detector: MoveGestureDetector) {}
    }

    fun start() {
        setup()

        mapView.compass.addCompassClickListener {
            if (mapView.getMapboxMap().cameraState.bearing == 0.0) {
                if (isCameraCentered)
                    locationPlugin.addOnIndicatorBearingChangedListener(bearingChangedListener)

                locationPlugin.addOnIndicatorPositionChangedListener(positionChangedListener)
                isCameraCentered = true
            } else
                locationPlugin.removeOnIndicatorBearingChangedListener(bearingChangedListener)
        }
    }

    private fun setup() {
        mapView.gestures.addOnMoveListener(onMoveListener)
        locationPlugin.addOnIndicatorPositionChangedListener(positionChangedListener)
        mapboxMap.setCamera(CameraOptions.Builder().bearing(0.0).build())

        mapView.compass.fadeWhenFacingNorth = false
        mapView.compass.marginTop = 600F
    }


    private fun onCameraTrackingDismissed() {
        locationPlugin.removeOnIndicatorPositionChangedListener(positionChangedListener)
        locationPlugin.removeOnIndicatorBearingChangedListener(bearingChangedListener)
    }

    companion object {
        const val ZOOM_LEVEL = 13.0
    }
}