package com.example.cityevents.utils

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.PorterDuff
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Build
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.fragment.app.Fragment
import com.example.cityevents.R
import java.io.Serializable

fun Fragment.openFragment(f: Fragment) {
    (activity as AppCompatActivity).supportFragmentManager.beginTransaction()
        .setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
        .replace(R.id.placeHolder, f).commit()
}

fun AppCompatActivity.openFragment(f: Fragment) {
    if (supportFragmentManager.fragments.isNotEmpty() && supportFragmentManager.fragments[0].javaClass == f.javaClass) return

    supportFragmentManager.beginTransaction()
        .setCustomAnimations(R.anim.slide_in_up, R.anim.slide_out_up).replace(R.id.placeHolder, f)
        .addToBackStack(null).commit()
}

fun Fragment.showToast(string: String) {
    Toast.makeText(activity, string, Toast.LENGTH_SHORT).show()
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
