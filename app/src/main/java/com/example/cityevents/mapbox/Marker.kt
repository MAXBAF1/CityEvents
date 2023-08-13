package com.example.cityevents.mapbox

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.net.Uri
import android.view.animation.Transformation
import androidx.appcompat.app.AppCompatActivity
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
import com.example.cityevents.adapters.FirebaseImageAdapter
import com.example.cityevents.adapters.ViewPagerAdapter
import com.example.cityevents.data.Event
import com.example.cityevents.data.LocationSerializable
import com.example.cityevents.fragments.addEventFragments.FinalEventFragment
import com.example.cityevents.utils.toBitmap
import com.google.gson.Gson
import com.mapbox.geojson.Point
import com.mapbox.maps.MapView
import com.mapbox.maps.plugin.annotation.annotations
import com.mapbox.maps.plugin.annotation.generated.OnPointAnnotationClickListener
import com.mapbox.maps.plugin.annotation.generated.PointAnnotation
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationOptions
import com.mapbox.maps.plugin.annotation.generated.createPointAnnotationManager
import java.io.IOException

class Marker(private val context: Context, private val mapView: MapView) {
    private val gson = Gson()

    fun addMarkerToMap(event: Event) {
        val drawable = AppCompatResources.getDrawable(context, R.drawable.ic_marker_orange)
        val imageUrl = event.images?.values?.first() // Получаем ссылку на изображение

        val transformations = MultiTransformation(
            CenterCrop(), RoundedCorners(10)
        )

        if (imageUrl == null) {
            drawable?.toBitmap()?.let { showMarker(event, it) }
        }

        Glide.with(context).asBitmap().load(imageUrl).override(100, 100).transform(transformations)
            .into(object : CustomTarget<Bitmap>() {
                override fun onResourceReady(
                    resource: Bitmap,
                    transition: com.bumptech.glide.request.transition.Transition<in Bitmap>?
                ) {
                    showMarker(event, resource)
                }

                override fun onLoadFailed(errorDrawable: Drawable?) {}

                override fun onLoadCleared(placeholder: Drawable?) {}
            })
    }

    private fun showMarker(event: Event, iconImage: Bitmap) {
        if (event.location == null) return
        val annotationApi = mapView.annotations
        val pointAnnotationManager = annotationApi.createPointAnnotationManager()
        val pointAnnotationOptions: PointAnnotationOptions =
            PointAnnotationOptions().withPoint(
                Point.fromLngLat(
                    event.location!!.longitude, event.location!!.latitude
                )
            ).withData(gson.toJsonTree(event)).withIconImage(iconImage)
        pointAnnotationManager.create(pointAnnotationOptions)
        pointAnnotationManager.addClickListener(OnPointAnnotationClickListener { annotation ->
            onMarkerItemClick(annotation)
            true
        })
    }

    fun addEventsToMap(events: Iterable<Event?>) {
        events.forEach {
            if (it != null) {
                addMarkerToMap(it)
            }
        }
    }

    private fun onMarkerItemClick(marker: PointAnnotation) {
        val fragmentManager = (context as AppCompatActivity).supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        val event = gson.fromJson(marker.getData(), Event::class.java)

        val fragment = FinalEventFragment(false, event)

        fragmentTransaction.replace(R.id.placeHolder, fragment)
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()
    }
}