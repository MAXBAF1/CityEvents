package com.example.cityevents.mapbox

import android.content.Context
import androidx.appcompat.content.res.AppCompatResources
import com.example.cityevents.R
import com.example.cityevents.data.Event
import com.example.cityevents.data.LocationSerializable
import com.example.cityevents.utils.toBitmap
import com.mapbox.geojson.Point
import com.mapbox.maps.MapView
import com.mapbox.maps.plugin.annotation.annotations
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationOptions
import com.mapbox.maps.plugin.annotation.generated.createPointAnnotationManager

class Marker(private val context: Context, private val mapView: MapView) {
    fun addMarkerToMap(event: Event) {
        //val drawable = AppCompatResources.getDrawable(context, R.drawable.ic_marker)

        if (event.images != null) {
            val annotationApi = mapView.annotations
            val pointAnnotationManager = annotationApi.createPointAnnotationManager()
            val pointAnnotationOptions: PointAnnotationOptions = PointAnnotationOptions()
                .withPoint(Point.fromLngLat(event.location!!.longitude, event.location!!.latitude))
                .withIconImage(event.images!!.first())
            pointAnnotationManager.create(pointAnnotationOptions)
        }

    }

    fun addEventsToMap(events: Iterable<Event?>) {
        events.forEach {
            if (it != null) {
                addMarkerToMap(it)
            }
        }
    }
}