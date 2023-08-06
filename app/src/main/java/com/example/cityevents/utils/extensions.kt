package com.example.cityevents.utils

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.PorterDuff
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Build
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.fragment.app.Fragment
import com.example.cityevents.R
import com.example.cityevents.fragments.mainFragment.MapFragment
import com.mapbox.common.location.compat.LocationEngine
import com.mapbox.common.location.compat.LocationEngineCallback
import com.mapbox.common.location.compat.LocationEngineResult
import com.mapbox.common.location.compat.permissions.PermissionsManager
import com.mapbox.geojson.Point
import java.io.Serializable

fun Fragment.openFragment(f: Fragment) {
    (activity as AppCompatActivity).supportFragmentManager.beginTransaction()
        .setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
        .replace(R.id.nav_host_fragment, f).commit()
}

fun AppCompatActivity.openFragment(nextFragment: Fragment) {
    val fragmentTransaction = supportFragmentManager.beginTransaction()
        .setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right)

    val currentFragment = supportFragmentManager.fragments.firstOrNull()

    if (currentFragment == null || currentFragment::class != nextFragment::class) {
        if (nextFragment is MapFragment) {
            supportFragmentManager.popBackStack()
        } else {
            fragmentTransaction.addToBackStack(null)
        }
        fragmentTransaction.replace(R.id.nav_host_fragment, nextFragment).commit()
    }
}

fun Fragment.showToast(string: String) {
    Toast.makeText(activity, string, Toast.LENGTH_SHORT).show()
}

fun Fragment.showToast(resId: Int) {
    Toast.makeText(activity, getString(resId), Toast.LENGTH_SHORT).show()
}


@SuppressLint("MissingPermission")
fun LocationEngine.lastKnownLocation(context: Context, callback: (Point?) -> Unit) {
    if (!PermissionsManager.areLocationPermissionsGranted(context)) {
        callback(null)
    }

    getLastLocation(object : LocationEngineCallback<LocationEngineResult> {
        override fun onSuccess(result: LocationEngineResult) {
            val location = (result.locations?.lastOrNull() ?: result.lastLocation)?.let { location ->
                Point.fromLngLat(location.longitude, location.latitude)
            }
            callback(location)
        }

        override fun onFailure(exception: Exception) {
            callback(null)
        }
    })
}

fun Fragment.checkPermission(p: String): Boolean {
    return when (PackageManager.PERMISSION_GRANTED) {
        ContextCompat.checkSelfPermission(activity as AppCompatActivity, p) -> true
        else -> false
    }
}

fun Drawable.setColor(context: Context, colorResId: Int) {
    val color = ContextCompat.getColor(context, colorResId)
    val wrappedDrawable = DrawableCompat.wrap(this.mutate())
    DrawableCompat.setTint(wrappedDrawable, color)
    DrawableCompat.setTintMode(wrappedDrawable, PorterDuff.Mode.SRC_IN)
}

fun View.hideKeyboard() {
    context.inputMethodManager.hideSoftInputFromWindow(windowToken, 0)
}

val Context.inputMethodManager: InputMethodManager
    get() = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

fun Drawable.toBitmap(): Bitmap? {
    if (this is BitmapDrawable) {
        return this.bitmap
    } else {
        val constantState = this.constantState ?: return null
        val drawable = constantState.newDrawable().mutate()
        val bitmap: Bitmap = Bitmap.createBitmap(
            drawable.intrinsicWidth, drawable.intrinsicHeight, Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)
        return bitmap
    }
}

inline fun <reified T : Serializable> Intent.serializable(key: String): T? = when {
    Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> getSerializableExtra(
        key, T::class.java
    )
    else -> @Suppress("DEPRECATION") getSerializableExtra(key) as? T
}
