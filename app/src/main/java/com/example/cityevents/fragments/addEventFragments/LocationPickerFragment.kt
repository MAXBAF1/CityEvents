package com.example.cityevents.fragments.addEventFragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.cityevents.R
import com.example.cityevents.databinding.FragmentLocationPickerBinding
import com.example.cityevents.mapbox.AddressAutofillManager
import com.example.cityevents.utils.openFragment
import com.mapbox.maps.MapboxMap
import com.mapbox.maps.plugin.scalebar.scalebar

class LocationPickerFragment : Fragment() {
    private lateinit var mapboxMap: MapboxMap

    private lateinit var binding: FragmentLocationPickerBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentLocationPickerBinding.inflate(inflater, container, false)
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

        binding.backBtn.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }

        binding.nextStepBtn.setOnClickListener {
            openFragment(DateTimePickerFragment.newInstance())
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() = LocationPickerFragment()
    }
}