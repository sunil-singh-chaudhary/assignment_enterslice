package com.example.assignment_enterslice.LocationStreamHandler

import androidx.core.app.ActivityCompat
import com.example.assignment_enterslice.MainActivity
import android.os.Build
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel

class LocationMethodChannelHandler(private val activity: MainActivity) {
    private val locationPermissionUtils = activity.locationPermissionUtils

    fun handleMethodCall(call: MethodCall, result: MethodChannel.Result) {
        when (call.method) {
            "startLocationService" -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (locationPermissionUtils.checkLocationPermission()) {
                        activity.startService(activity.service)
                    } else {
                        requestLocationPermissions()
                    }
                } else {
                    activity.startService(activity.service)
                }

                result.success(null)
            }

            "stopLocationService" -> {
                activity.stopService(activity.service)
                result.success(null)
            }

            "fetchDataFromNative" -> {
                try {
                    val lastKnownLocation = activity.dbhandler.fetchDataFromDatabase(activity)
                    result.success(lastKnownLocation)
                } catch (e: Exception) {
                    result.error("FETCH_ERROR", e.localizedMessage, null)
                }
            }

            else -> {
                result.notImplemented()
            }
        }
    }

    private fun requestLocationPermissions() {
        ActivityCompat.requestPermissions(
            activity,
            arrayOf(
                android.Manifest.permission.ACCESS_COARSE_LOCATION,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ),
            10
        )
    }
}
