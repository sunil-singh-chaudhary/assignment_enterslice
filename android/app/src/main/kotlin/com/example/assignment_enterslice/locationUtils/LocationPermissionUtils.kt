package com.example.assignment_enterslice.locationUtils
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class LocationPermissionUtils(private val activity: Activity) {
    companion object {
        const val LOCATION_PERMISSION_REQUEST_CODE = 123
    }

    fun checkLocationPermission(): Boolean {
        val finePermission = PackageManager.PERMISSION_GRANTED ==
                ContextCompat.checkSelfPermission(activity, android.Manifest.permission.ACCESS_FINE_LOCATION)
        val coarsePermission = PackageManager.PERMISSION_GRANTED ==
                ContextCompat.checkSelfPermission(activity, android.Manifest.permission.ACCESS_COARSE_LOCATION)
        val backgroundPermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            PackageManager.PERMISSION_GRANTED ==
                    ContextCompat.checkSelfPermission(activity, android.Manifest.permission.ACCESS_BACKGROUND_LOCATION)
        } else {
            true
        }

        return finePermission && coarsePermission && backgroundPermission
    }

    fun requestLocationPermission() {
        val permissions = mutableListOf(
            android.Manifest.permission.ACCESS_FINE_LOCATION,
            android.Manifest.permission.ACCESS_COARSE_LOCATION
        )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            permissions.add(android.Manifest.permission.ACCESS_BACKGROUND_LOCATION)
        }

        ActivityCompat.requestPermissions(
            activity,
            permissions.toTypedArray(),
            LOCATION_PERMISSION_REQUEST_CODE
        )
    }

    fun showPermissionRationale() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    activity,
                    android.Manifest.permission.ACCESS_FINE_LOCATION
                )
            ) {
                // Show a rationale dialog explaining the need for permissions
                val dialog = AlertDialog.Builder(activity)
                dialog.setTitle("Permission Required")
                dialog.setMessage("You have to allow permission to access the user's location.")
                dialog.setPositiveButton("OK") { _, _ ->
                    requestLocationPermission()
                }
                dialog.setCancelable(false)
                dialog.show()
            }
        }
    }

    fun navigateToAppSettings() {
        if (!ActivityCompat.shouldShowRequestPermissionRationale(
                activity,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            )
        ) {
            // Navigate the user to the app settings
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
            intent.data = Uri.fromParts("package", activity.packageName, null)
            activity.startActivityForResult(intent, 1001)
        }
    }
}
