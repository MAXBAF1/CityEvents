package com.example.cityevents.mapbox

import android.content.Context
import androidx.appcompat.content.res.AppCompatResources
import androidx.room.PrimaryKey
import com.example.cityevents.R
import com.example.cityevents.data.LocationSerializable
import com.example.cityevents.utils.toBitmap
import com.mapbox.geojson.Point
import com.mapbox.maps.MapView
import com.mapbox.maps.plugin.annotation.annotations
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationOptions
import com.mapbox.maps.plugin.annotation.generated.createPointAnnotationManager

class Marker(private val context: Context, private val mapView: MapView) {
    fun addMarkerToMap(point: Point) {
        val drawable = AppCompatResources.getDrawable(context, R.drawable.ic_marker)

        drawable?.toBitmap()?.let {
            val annotationApi = mapView.annotations
            val pointAnnotationManager = annotationApi.createPointAnnotationManager()
            val pointAnnotationOptions: PointAnnotationOptions = PointAnnotationOptions()
                .withPoint(point)
                .withIconImage(it)
            pointAnnotationManager.create(pointAnnotationOptions)
        }
    }

    fun addMarkersToMap(locations: Iterable<LocationSerializable>) {
        locations.forEach {
            addMarkerToMap(Point.fromLngLat(it.longitude, it.latitude))
        }
    }
}