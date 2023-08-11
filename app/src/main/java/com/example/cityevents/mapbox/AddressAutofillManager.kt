package com.example.cityevents.mapbox

import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.cityevents.R
import com.example.cityevents.data.LocationSerializable
import com.example.cityevents.utils.lastKnownLocation
import com.example.cityevents.utils.showToast
import com.mapbox.common.location.compat.LocationEngineProvider
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.MapboxMap
import com.mapbox.search.autofill.*
import com.mapbox.search.ui.adapter.autofill.AddressAutofillUiAdapter
import com.mapbox.search.ui.view.CommonSearchViewConfiguration
import com.mapbox.search.ui.view.DistanceUnitType
import com.mapbox.search.ui.view.SearchResultsView
import kotlinx.coroutines.launch

class AddressAutofillManager(
    private val fragment: Fragment,
    private val searchResultsView: SearchResultsView,
    private val queryEditText: EditText,
    private val mapboxMap: MapboxMap
) {
    private lateinit var addressAutofill: AddressAutofill
    private lateinit var addressAutofillUiAdapter: AddressAutofillUiAdapter

    private var ignoreNextMapIdleEvent: Boolean = false
    private var ignoreNextQueryTextUpdate: Boolean = false

    private val requireContext = fragment.requireContext()

    lateinit var queryLocation: LocationSerializable

    fun create() {
        initAddressAutofill()
        setupMapIdleListener()
        initSearchResultsView()
        initLocationEngine()
        setupAddressAutofillUiAdapter()
        setupQueryEditTextListener()
    }

    private fun initAddressAutofill() {
        addressAutofill =
            AddressAutofill.create(requireContext.getString(R.string.mapbox_access_token))
    }

    private fun setupMapIdleListener() {
        mapboxMap.addOnMapIdleListener {
            if (ignoreNextMapIdleEvent) {
                ignoreNextMapIdleEvent = false
                return@addOnMapIdleListener
            }

            val mapCenter = mapboxMap.cameraState.center
            findAddress(mapCenter)
        }
    }

    private fun initSearchResultsView() {
        searchResultsView.initialize(
            SearchResultsView.Configuration(CommonSearchViewConfiguration(DistanceUnitType.METRIC))
        )
    }

    private fun initLocationEngine() {
        LocationEngineProvider.getBestLocationEngine(requireContext.applicationContext)
            .lastKnownLocation(requireContext) { point ->
                point?.let {
                    mapboxMap.setCamera(
                        CameraOptions.Builder().center(point).bearing(0.0).zoom(13.0).build()
                    )
                    ignoreNextMapIdleEvent = true
                }
            }
    }

    private fun setupAddressAutofillUiAdapter() {
        addressAutofillUiAdapter = AddressAutofillUiAdapter(searchResultsView, addressAutofill)

        addressAutofillUiAdapter.addSearchListener(object :
            AddressAutofillUiAdapter.SearchListener {
            override fun onSuggestionSelected(suggestion: AddressAutofillSuggestion) {
                selectSuggestion(suggestion, false)
            }

            override fun onSuggestionsShown(suggestions: List<AddressAutofillSuggestion>) {
                // Nothing to do
            }

            override fun onError(e: Exception) {
                // Nothing to do
            }
        })
    }

    private fun setupQueryEditTextListener() {
        queryEditText.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(text: CharSequence, start: Int, before: Int, count: Int) {
                if (ignoreNextQueryTextUpdate) {
                    ignoreNextQueryTextUpdate = false
                    return
                }

                val query = Query.create(text.toString())
                if (query != null) {
                    launchWithRepeat {
                        addressAutofillUiAdapter.search(query)
                    }
                }

                searchResultsView.isVisible = query != null
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                // Nothing to do
            }

            override fun afterTextChanged(s: Editable) {
                // Nothing to do
            }
        })
    }

    private fun findAddress(point: Point) {
        launchWithRepeat {
            val response = addressAutofill.suggestions(point, AddressAutofillOptions())
            response.onValue { suggestions ->
                if (suggestions.isEmpty()) {
                    fragment.showToast(R.string.address_autofill_error_pin_correction)
                } else {
                    selectSuggestion(suggestions.first(), true)
                }
            }.onError {
                fragment.showToast(R.string.address_autofill_error_pin_correction)
            }
        }
    }

    private fun selectSuggestion(
        suggestion: AddressAutofillSuggestion, fromReverseGeocoding: Boolean
    ) {
        launchWithRepeat {
            val response = addressAutofill.select(suggestion)
            response.onValue { result ->
                showAddressAutofillResult(result, fromReverseGeocoding)
            }.onError {
                fragment.showToast(R.string.address_autofill_error_select)
            }
        }
    }

    private fun launchWithRepeat(action: suspend () -> Unit) {
        fragment.lifecycleScope.launch {
            fragment.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                action()
            }
        }
    }

    private fun showAddressAutofillResult(
        result: AddressAutofillResult, fromReverseGeocoding: Boolean
    ) {
        val address = result.address
        queryLocation = LocationSerializable(
            result.suggestion.coordinate.latitude(),
            result.suggestion.coordinate.longitude()
        )

        if (!fromReverseGeocoding) {
            mapboxMap.setCamera(
                CameraOptions.Builder().center(result.suggestion.coordinate).bearing(0.0).zoom(16.0)
                    .build()
            )
            ignoreNextMapIdleEvent = true
        }

        ignoreNextQueryTextUpdate = false
        queryEditText.setText(
            listOfNotNull(
                address.houseNumber, address.street
            ).joinToString()
        )
        searchResultsView.isVisible = false
    }
}
