package com.hbouzidi.compassqibla

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.core.app.ActivityCompat

object LocationUtils {

    fun Context.checkLocationPermission(onMissing: () -> Unit) {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            onMissing()
            return
        }
    }

    @SuppressLint("NewApi")
    fun handlePermission(
        permissions: Map<String, @JvmSuppressWildcards Boolean>,
        onPermissionGranted: (permission: String) -> Unit,
        onPermissionDenied: () -> Unit,
    ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            when {
                permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false) ->{
                    Log.d("TAG", "requestLocationPermission: Precise location access granted")
                    onPermissionGranted(Manifest.permission.ACCESS_FINE_LOCATION)
                }
                permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false) ->{
                    Log.d("TAG", "requestLocationPermission: Only approximate location access granted.")
                    onPermissionGranted(Manifest.permission.ACCESS_COARSE_LOCATION)
                }
                else -> {
                    Log.d("TAG", "requestLocationPermission: No location access granted.")
                    onPermissionDenied()
                }
            }
        }
    }

    fun launchPermission(locationPermissionRequest: ActivityResultLauncher<Array<String>>) {
        locationPermissionRequest.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )
    }
}