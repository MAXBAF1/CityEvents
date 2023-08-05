package com.example.cityevents.fragments.mainFragment

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.example.cityevents.R
import com.example.cityevents.databinding.FragmentMainBinding
import com.example.cityevents.mapbox.MapManager
import com.mapbox.maps.MapView
import com.mapbox.maps.plugin.gestures.addOnMapClickListener
import com.mapbox.maps.plugin.gestures.removeOnMapClickListener

class MainFragment : Fragment() {
    private lateinit var binding: FragmentMainBinding
    private lateinit var mapManager: MapManager
    private lateinit var mapView: MapView
    private lateinit var events: Events

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentMainBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        mapView = binding.mapView
        mapManager = MapManager(this, mapView)
        mapManager.initMap()
        eventsInit()
    }

    private fun eventsInit() {
        val dialogView = layoutInflater.inflate(R.layout.event_dialog, null)
        val resultLauncher =
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
        events = Events(requireContext(), mapView, dialogView, resultLauncher, ::closeEventDialog)
        binding.addEventBtn.setOnClickListener {
            it.visibility = View.GONE
            binding.crossImg.visibility = View.VISIBLE
            binding.closeBtn.visibility = View.VISIBLE
            mapView.getMapboxMap().addOnMapClickListener(events)
        }

        binding.closeBtn.setOnClickListener {
            closeEventDialog()
        }
    }

    private fun closeEventDialog() {
        binding.addEventBtn.visibility = View.VISIBLE
        binding.crossImg.visibility = View.GONE
        binding.closeBtn.visibility = View.GONE
        mapView.getMapboxMap().removeOnMapClickListener(events)
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