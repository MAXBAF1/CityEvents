package com.example.cityevents.mapbox

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.view.animation.Transformation
import androidx.appcompat.content.res.AppCompatResources
import androidx.transition.Transition
import com.bumptech.glide.Glide
import com.bumptech.glide.load.MultiTransformation
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.FutureTarget
import com.bumptech.glide.request.target.CustomTarget
import com.example.cityevents.R
import com.example.cityevents.data.Event
import com.example.cityevents.data.LocationSerializable
import com.example.cityevents.utils.toBitmap
import com.mapbox.geojson.Point
import com.mapbox.maps.MapView
import com.mapbox.maps.plugin.annotation.annotations
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationOptions
import com.mapbox.maps.plugin.annotation.generated.createPointAnnotationManager
import java.io.IOException

class Marker(private val context: Context, private val mapView: MapView) {
    fun addMarkerToMap(event: Event) {
        //val drawable = AppCompatResources.getDrawable(context, R.drawable.ic_marker)
        val imageUrl = event.images?.values?.first() // Получаем ссылку на изображение

        val transformations = MultiTransformation(
            CenterCrop(),
            RoundedCorners(10)
        )

        Glide.with(context)
            .asBitmap()
            .load(imageUrl)
            .override(100, 100)
            .transform(transformations)
            .into(object : CustomTarget<Bitmap>() {
                override fun onResourceReady(
                    resource: Bitmap,
                    transition: com.bumptech.glide.request.transition.Transition<in Bitmap>?
                ) {
                    val annotationApi = mapView.annotations
                    val pointAnnotationManager = annotationApi.createPointAnnotationManager()
                    val pointAnnotationOptions: PointAnnotationOptions =
                        PointAnnotationOptions().withPoint(
                            Point.fromLngLat(
                                event.location!!.longitude, event.location!!.latitude
                            )
                        ).withIconImage(resource)
                    pointAnnotationManager.create(pointAnnotationOptions)
                }

                override fun onLoadFailed(errorDrawable: Drawable?) {}

                override fun onLoadCleared(placeholder: Drawable?) {}
            })
    }

    fun addEventsToMap(events: Iterable<Event?>) {
        events.forEach {
            if (it != null) {
                addMarkerToMap(it)
            }
        }
    }
}