package com.example.cityevents.fragments

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.cityevents.data.DateTime
import com.example.cityevents.data.Event
import com.example.cityevents.databinding.FragmentMapBinding
import com.example.cityevents.firebase.Firebase
import com.example.cityevents.mapbox.MapManager
import com.example.cityevents.mapbox.Marker
import com.example.cityevents.parsing.Parsing
import com.example.cityevents.parsing.TrustAllCertificates
import com.mapbox.maps.MapView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter

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

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        mapView = binding.mapView
        marker = Marker(requireContext(), mapView)
        firebase = Firebase()
        getEventsFromFirebase()
        mapManager = MapManager(this, mapView)
        mapManager.initMap()

       /* firebase.getInternetEvents {
            marker.addEventsToMap(it)
        }*/
        Parsing(requireContext()).getEventsFromWeb {
            //firebase.sendInternetEvents(it)
            marker.addEventsToMap(it)
        }
    }



    private fun getEventsFromFirebase() {
        firebase.getEventsFromFirebase { events ->
            marker.addEventsToMap(events)
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() = MapFragment()
    }
}