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
    private val fragment: Fragment,
    private val grantedFun: () -> Unit
) {
    private lateinit var pLauncher: ActivityResultLauncher<String>

    fun registerPermissionLauncher() {
        pLauncher =
            fragment.registerForActivityResult(ActivityResultContracts.RequestPermission()) {
                if (it) grantedFun()
                else {
                    fragment.showToast(fragment.getString(R.string.location_permission))

                    Handler(Looper.getMainLooper()).postDelayed({
                        fragment.activity?.finish()
                    }, 3000)
                }
            }
    }

    fun checkLocationPermission() {
        when {
            ContextCompat.checkSelfPermission(
                fragment.requireContext(), Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED -> grantedFun()

            fragment.shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION) -> {
                fragment.showToast(fragment.getString(R.string.location_permission))

                Handler(Looper.getMainLooper()).postDelayed({
                    fragment.activity?.finish()
                }, 3000)
            }

            else -> {
                pLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            }
        }
    }
}