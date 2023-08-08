package com.example.cityevents.mapbox.location

import android.Manifest
import android.content.pm.PackageManager
import android.os.Handler
import android.os.Looper
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.cityevents.R
import com.example.cityevents.utils.showToast

class LocationPermissionManager(
    private val fragment: Fragment, private val grantedFun: () -> Unit
) {
    private lateinit var pLauncher: ActivityResultLauncher<String>

    fun registerPermissionLauncher() {
        pLauncher =
            fragment.registerForActivityResult(ActivityResultContracts.RequestPermission()) {
                if (it) grantedFun()
                else if (ContextCompat.checkSelfPermission(
                    fragment.requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED) grantedFun()
                else {
                    fragment.showToast(fragment.getString(R.string.location_permission))
                }
            }
    }

    fun checkLocationPermission() {
        when (PackageManager.PERMISSION_GRANTED) {
            ContextCompat.checkSelfPermission(
                fragment.requireContext(), Manifest.permission.ACCESS_FINE_LOCATION
            ) -> grantedFun()
            ContextCompat.checkSelfPermission(
                fragment.requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION
            ) -> grantedFun()

            else -> pLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }
}