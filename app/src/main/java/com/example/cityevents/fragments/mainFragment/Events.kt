package com.example.cityevents.fragments.mainFragment

import android.content.Context
import android.content.Intent
import android.graphics.Point
import android.provider.MediaStore
import android.view.View
import android.widget.ImageButton
import androidx.activity.result.ActivityResultLauncher
import androidx.appcompat.app.AlertDialog
import com.example.cityevents.R
import com.example.cityevents.mapbox.Marker
import com.mapbox.maps.MapView
import com.mapbox.maps.ScreenCoordinate
import com.mapbox.maps.plugin.gestures.OnMapClickListener

class Events(
    private val context: Context,
    private val mapView: MapView,
    private val dialogView: View,
    private val resultLauncher: ActivityResultLauncher<Intent>,
    private val closeEventFun: () -> Unit
) : OnMapClickListener {
    private var currentDialog: AlertDialog? = null
    private val marker = Marker(context, mapView)

    override fun onMapClick(point: com.mapbox.geojson.Point): Boolean {
        val screenSize = Point(mapView.width, mapView.height)
        val screenCenter = ScreenCoordinate(screenSize.x / 2.0, screenSize.y / 2.0)
        val centerLatLng = mapView.getMapboxMap().coordinateForPixel(screenCenter)
        showEventDialog(centerLatLng)
        return true
    }

    private fun showEventDialog(point: com.mapbox.geojson.Point) {
        val imageBtn = dialogView.findViewById<ImageButton>(R.id.eventImageBtn)

        imageBtn.setOnClickListener {
            openGallery()
        }

        val dialogBuilder = AlertDialog.Builder(context).setView(dialogView)
            .setTitle(context.getString(R.string.add_event)).setPositiveButton("OK") { dialog, _ ->
                marker.addMarkerToMap(point)
                closeEventFun()
                dialog.dismiss()
            }

        val dialog = dialogBuilder.create()
        currentDialog?.dismiss() // Закрываем предыдущий диалог, если есть
        currentDialog = dialog
        dialog.show()
    }

    private fun openGallery() {
        val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        resultLauncher.launch(galleryIntent)
    }
}