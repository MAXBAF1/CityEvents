package com.example.cityevents.fragments.addEventFragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.cityevents.R
import com.example.cityevents.databinding.FragmentChoosePlaceBinding
import com.example.cityevents.mapbox.AddressAutofillManager
import com.mapbox.maps.MapboxMap
import com.mapbox.maps.plugin.scalebar.scalebar

class ChoosePlaceFragment : Fragment() {
    private lateinit var mapboxMap: MapboxMap

    private lateinit var binding: FragmentChoosePlaceBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentChoosePlaceBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mapboxMap = binding.map.getMapboxMap()
        mapboxMap.loadStyleUri(getString(R.string.globe3dKey))
        binding.map.scalebar.enabled = false

        AddressAutofillManager(
            this, binding.searchResultsView, binding.queryText, mapboxMap
        ).create()
    }

    companion object {
        @JvmStatic
        fun newInstance() = ChoosePlaceFragment()
    }
}