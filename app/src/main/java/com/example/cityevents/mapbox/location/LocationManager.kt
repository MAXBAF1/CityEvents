package com.example.cityevents.mapbox.location

import android.content.Context
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import com.mapbox.android.gestures.MoveGestureDetector
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.MapView
import com.mapbox.maps.plugin.animation.MapAnimationOptions
import com.mapbox.maps.plugin.animation.camera
import com.mapbox.maps.plugin.compass.compass
import com.mapbox.maps.plugin.gestures.OnMoveListener
import com.mapbox.maps.plugin.gestures.gestures
import com.mapbox.maps.plugin.locationcomponent.OnIndicatorBearingChangedListener
import com.mapbox.maps.plugin.locationcomponent.OnIndicatorPositionChangedListener
import com.mapbox.maps.plugin.locationcomponent.location
import com.mapbox.maps.plugin.scalebar.scalebar
import okhttp3.internal.wait

class LocationManager(private val mapView: MapView, private val context: Context) {
    private val mapboxMap = mapView.getMapboxMap()
    private var isCameraCentered = false
    private val locationPlugin = mapView.location
    private var isFirstStart = true

    private val bearingChangedListener = OnIndicatorBearingChangedListener {
        mapboxMap.setCamera(CameraOptions.Builder().bearing(it).build())
    }

    private val positionChangedListener = OnIndicatorPositionChangedListener {
        if (isFirstStart) {
            val cameraOptions = CameraOptions.Builder().center(it).zoom(ZOOM_LEVEL)
                .bearing(0.0) // Указываем желаемый угол поворота карты
                .pitch(0.0) // Указываем желаемый угол наклона карты
                .build()

            mapView.camera.flyTo(
                cameraOptions,
                MapAnimationOptions.Builder().duration(2000).interpolator(AccelerateInterpolator())
                    .build()
            )

            isFirstStart = false
        }
        else
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
                if (isCameraCentered) locationPlugin.addOnIndicatorBearingChangedListener(
                    bearingChangedListener
                )

                locationPlugin.addOnIndicatorPositionChangedListener(positionChangedListener)
                isCameraCentered = true
            } else locationPlugin.removeOnIndicatorBearingChangedListener(bearingChangedListener)
        }
    }

    private fun setup() {
        mapView.location.apply {
            enabled = true
            pulsingEnabled = true
        }

        mapView.gestures.addOnMoveListener(onMoveListener)
        locationPlugin.addOnIndicatorPositionChangedListener(positionChangedListener)
        mapboxMap.setCamera(CameraOptions.Builder().bearing(0.0).build())

        mapView.compass.fadeWhenFacingNorth = false
        mapView.compass.marginTop = 2000F
        mapView.scalebar.enabled = false
    }

    private fun onCameraTrackingDismissed() {
        locationPlugin.removeOnIndicatorPositionChangedListener(positionChangedListener)
        locationPlugin.removeOnIndicatorBearingChangedListener(bearingChangedListener)
    }

    companion object {
        const val ZOOM_LEVEL = 13.0
    }
}