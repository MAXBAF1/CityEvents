package com.example.cityevents.fragments.mainFragment

import android.app.Activity
import android.content.Intent
import android.graphics.Point
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.example.cityevents.R
import com.example.cityevents.databinding.FragmentMainBinding
import com.example.cityevents.mapbox.MapManager
import com.example.cityevents.mapbox.Marker
import com.mapbox.maps.MapView
import com.mapbox.maps.ScreenCoordinate
import com.mapbox.maps.plugin.gestures.OnMapClickListener
import com.mapbox.maps.plugin.gestures.addOnMapClickListener
import com.mapbox.maps.plugin.gestures.removeOnMapClickListener

class MainFragment : Fragment(), OnMapClickListener {
    private lateinit var binding: FragmentMainBinding
    private lateinit var mapManager: MapManager
    private lateinit var mapView: MapView
    private lateinit var marker: Marker
    private lateinit var dialogView: View
    private lateinit var pickGalleryImage: ActivityResultLauncher<Intent>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentMainBinding.inflate(inflater, container, false)
        dialogView = layoutInflater.inflate(R.layout.event_dialog, null)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        mapView = binding.mapView
        marker = Marker(requireContext(), mapView)
        pickGalleryImage =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                val selectedImageUri = data?.data
                if (selectedImageUri != null) {
                    val imageBtn = dialogView.findViewById<ImageButton>(R.id.eventImageBtn)
                    imageBtn.setImageURI(selectedImageUri)
                }
            }
        }
        mapManager = MapManager(this, mapView)
        mapManager.initMap()
        addEventsInit()
    }

    private fun addEventsInit() {
        binding.addEventBtn.setOnClickListener {
            it.visibility = View.GONE
            binding.crossImg.visibility = View.VISIBLE
            binding.closeBtn.visibility = View.VISIBLE
            mapView.getMapboxMap().addOnMapClickListener(this)
        }

        binding.closeBtn.setOnClickListener {
            closeEventDialog()
        }
    }

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

        val dialogBuilder = AlertDialog.Builder(requireContext()).setView(dialogView)
            .setTitle(getString(R.string.add_event)).setPositiveButton("OK") { dialog, _ ->
                marker.addMarkerToMap(point)
                closeEventDialog()
                dialog.dismiss()
            }

        val dialog = dialogBuilder.create()
        dialog.show()
    }

    private fun openGallery() {
        val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        pickGalleryImage.launch(galleryIntent)
    }

    private fun closeEventDialog() {
        binding.addEventBtn.visibility = View.VISIBLE
        binding.crossImg.visibility = View.GONE
        binding.closeBtn.visibility = View.GONE
        mapView.getMapboxMap().removeOnMapClickListener(this)
    }

    override fun onDetach() {
        super.onDetach()
        LocalBroadcastManager.getInstance(activity as AppCompatActivity)
            .unregisterReceiver(mapManager.broadcastReceiver)
    }

    companion object {
        @JvmStatic
        fun newInstance() = MainFragment()
    }
}