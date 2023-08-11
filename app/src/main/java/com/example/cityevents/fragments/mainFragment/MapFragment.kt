package com.example.cityevents.fragments.mainFragment

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.example.cityevents.Firebase
import com.example.cityevents.R
import com.example.cityevents.databinding.FragmentMapBinding
import com.example.cityevents.mapbox.MapManager
import com.example.cityevents.mapbox.Marker
import com.mapbox.maps.MapView
import com.mapbox.maps.plugin.gestures.addOnMapClickListener
import com.mapbox.maps.plugin.gestures.removeOnMapClickListener

class MapFragment : Fragment() {
    private lateinit var binding: FragmentMapBinding
    private lateinit var mapManager: MapManager
    private lateinit var mapView: MapView
    private lateinit var firebase: Firebase
    private lateinit var marker: Marker

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentMapBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        mapView = binding.mapView
        marker = Marker(requireContext(), mapView)
        firebase = Firebase()
        getEventsFromFirebase()
        mapManager = MapManager(this, mapView)
        mapManager.initMap()
    }

    private fun getEventsFromFirebase() {
        firebase.getEventsFromFirebase { events ->
            marker.addMarkersToMap(events.map { it.location!! })
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() = MapFragment()
    }
}