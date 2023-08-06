package com.example.cityevents.fragments.mainFragment

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.cityevents.R
import com.example.cityevents.databinding.FragmentMapBinding
import com.example.cityevents.mapbox.MapManager
import com.example.cityevents.utils.hideKeyboard
import com.example.cityevents.utils.showToast
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.MapView
import com.mapbox.maps.plugin.gestures.addOnMapClickListener
import com.mapbox.maps.plugin.gestures.removeOnMapClickListener
import com.mapbox.search.autofill.*
import com.mapbox.search.ui.adapter.autofill.AddressAutofillUiAdapter
import com.mapbox.search.ui.view.CommonSearchViewConfiguration
import com.mapbox.search.ui.view.DistanceUnitType
import com.mapbox.search.ui.view.SearchResultsView

class MapFragment : Fragment() {
    private lateinit var binding: FragmentMapBinding
    private lateinit var mapManager: MapManager
    private lateinit var mapView: MapView
    private lateinit var events: Events
    private lateinit var addressAutofill: AddressAutofill
    private lateinit var addressAutofillUiAdapter: AddressAutofillUiAdapter
    private var ignoreNextMapIdleEvent: Boolean = false
    private var ignoreNextQueryTextUpdate: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentMapBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        mapView = binding.mapView
        addressAutofill = AddressAutofill.create(getString(R.string.mapbox_access_token))
        mapView.getMapboxMap().addOnMapIdleListener {
            if (ignoreNextMapIdleEvent) {
                ignoreNextMapIdleEvent = false
                return@addOnMapIdleListener
            }

            val mapCenter = mapView.getMapboxMap().cameraState.center
            findAddress(mapCenter)
        }
        binding.searchResultsView.initialize(
            SearchResultsView.Configuration(CommonSearchViewConfiguration(DistanceUnitType.METRIC))
        )
        addressAutofillUiAdapter = AddressAutofillUiAdapter(
            binding.searchResultsView, addressAutofill
        )

        addressAutofillUiAdapter.addSearchListener(object :
            AddressAutofillUiAdapter.SearchListener {
            override fun onSuggestionSelected(suggestion: AddressAutofillSuggestion) {
                selectSuggestion(suggestion, false)
            }

            override fun onSuggestionsShown(suggestions: List<AddressAutofillSuggestion>) {}
            override fun onError(e: Exception) {}
        })
        binding.queryEditText.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(text: CharSequence, start: Int, before: Int, count: Int) {
                if (ignoreNextQueryTextUpdate) {
                    ignoreNextQueryTextUpdate = false
                    return
                }
                val query = Query.create(text.toString())
                if (query != null) {
                    lifecycleScope.launchWhenStarted {
                        addressAutofillUiAdapter.search(query)
                    }
                }
                binding.searchResultsView.isVisible = query != null
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun afterTextChanged(s: Editable) {}
        })

        mapManager = MapManager(this, mapView)
        mapManager.initMap()
        eventsInit()
    }

    private fun findAddress(point: Point) {
        lifecycleScope.launchWhenStarted {
            val response = addressAutofill.suggestions(point, AddressAutofillOptions())
            response.onValue { suggestions ->
                if (suggestions.isEmpty()) {
                    showToast(R.string.address_autofill_error_pin_correction)
                } else {
                    selectSuggestion(suggestions.first(), true)
                }
            }.onError {
                showToast(R.string.address_autofill_error_pin_correction)
            }
        }
    }

    private fun selectSuggestion(
        suggestion: AddressAutofillSuggestion, fromReverseGeocoding: Boolean
    ) {
        lifecycleScope.launchWhenStarted {
            val response = addressAutofill.select(suggestion)
            response.onValue { result ->
                showAddressAutofillResult(result, fromReverseGeocoding)
            }.onError {
                showToast(getString(R.string.address_autofill_error_select))
            }
        }
    }

    private fun showAddressAutofillResult(
        result: AddressAutofillResult, fromReverseGeocoding: Boolean
    ) {
        val address = result.address

        if (!fromReverseGeocoding) {
            mapView.getMapboxMap().setCamera(
                CameraOptions.Builder().center(result.suggestion.coordinate).zoom(16.0).build()
            )
            ignoreNextMapIdleEvent = true
            binding.crossImg.isVisible = true
        }

        //ignoreNextMapIdleEvent = true
        binding.queryEditText.setText(
            listOfNotNull(address.houseNumber, address.street).joinToString()
        )
        binding.queryEditText.clearFocus()

        binding.searchResultsView.isVisible = false
        binding.searchResultsView.hideKeyboard()
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

    companion object {
        @JvmStatic
        fun newInstance() = MapFragment()
    }
}