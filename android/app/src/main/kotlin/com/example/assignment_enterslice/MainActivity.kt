package com.example.assignment_enterslice

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.annotation.NonNull
import com.example.assignment_enterslice.dbHelper.LocationDatabaseHelper
import com.example.assignment_enterslice.locationModel.LocationEvent
import com.example.assignment_enterslice.services.LocationService
import io.flutter.embedding.android.FlutterActivity
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import java.util.Timer
import java.util.TimerTask


class MainActivity : FlutterActivity() {
    private val CHANNEL = "location_service" // Define your method channel name
    private var service: Intent? = null
    private var lastKnownLocation: LocationEvent? = null
    private var timer: Timer? = null
    private var methodChannel: MethodChannel? = null
    private var UpdatedLocationEveryTime: LocationEvent? = null

    override fun configureFlutterEngine(@NonNull flutterEngine: FlutterEngine) {
        super.configureFlutterEngine(flutterEngine)
        methodChannel = MethodChannel(flutterEngine.dartExecutor.binaryMessenger, CHANNEL)
        // Register the MethodChannelHandler
        methodChannel!!.setMethodCallHandler { call: MethodCall, result: MethodChannel.Result ->
                when (call.method) {

                    "startLocationService" -> {
                        // Start your location service here
                        print("Permission check and service started")
                        startService(service)

                        result.success(null)
                    }
                    "updateLocation" -> {
                        val ndata = UpdatedLocationEveryTime
                        val data = ndata?.let {
                            mapOf(
                                "latitude" to it.latitude,
                                "longitude" to it.longitude
                            )
                        } ?: emptyMap()
                        result.success(data)
                    }
                    "stopLocationService" -> {
                        // Stop your location service here
                        print("Service stopped")
                        stopService(service)
                        timer?.cancel()
                        result.success(null)
                    }

                    "fetchDataFromNative" -> {
                        try {
                            // Fetch data from the database
                            val LastKnownLocation = fetchDataFromDatabase()
                            // Send the data to Flutter
                            result.success(LastKnownLocation)
                        } catch (e: Exception) {
                            // Handle exceptions if needed
                            result.error("FETCH_ERROR", e.localizedMessage, null)
                        }
                    }
                    else -> {
                        result.notImplemented()
                    }
                }
            }
    }

    private fun fetchDataFromDatabase(): List<Map<String, Any?>> {
        val dbHelper = LocationDatabaseHelper(this)

        // Read data from the database
        val data = dbHelper.readAllData() // Implement this function in your dbHelper

        // Convert data to a format that can be sent to Flutter
        val dataToSend = data.map { location ->
            mapOf(
                "latitude" to location[LocationDatabaseHelper.COLUMN_LATITUDE],
                "longitude" to location[LocationDatabaseHelper.COLUMN_LONGITUDE],
                "timestamp" to location[LocationDatabaseHelper.COLUMN_TIMESTAMP]
            )
        }
        return dataToSend
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        service = Intent(this, LocationService::class.java)
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        stopService(service)
        Log.e("call", "onDestroy: ")
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this)
        }
    }

    @Subscribe
    fun receiveLocationEvent(locationEvent: LocationEvent) {
        Log.e("Latitude", "${locationEvent.latitude}")
        Log.e("Longitude", "${locationEvent.longitude}")

        // If the timer is not running, start it
        if (timer == null) {
            timer = Timer()
            timer?.scheduleAtFixedRate(object : TimerTask() {
                override fun run() {
                    // Send location updates periodically
                    Log.e("TiMER", "run: " )
                    sendLocationUpdates(locationEvent)
                }
            }, 0, 2000)
            lastKnownLocation = locationEvent
            UpdatedLocationEveryTime = locationEvent

        } else {
            // Update the last known location with each new event
            lastKnownLocation = locationEvent
            UpdatedLocationEveryTime = locationEvent

        }
    }
    private fun sendLocationUpdates(locationEvent: LocationEvent) {
        val latitude = locationEvent.latitude
        val longitude = locationEvent.longitude

        UpdatedLocationEveryTime = locationEvent

        // Check if methodChannel is initialized
        if (methodChannel != null) {
            // Run the method invocation on the main (UI) thread
            runOnUiThread {
                try {
                    val data = mapOf(
                        "latitude" to UpdatedLocationEveryTime!!.latitude,
                        "longitude" to UpdatedLocationEveryTime!!.longitude
                    )
                    methodChannel!!.invokeMethod("updateLocation", data)
                } catch (e: Exception) {
                    Log.e("TAG", "Error invoking method: ${e.message}")
                }
            }
        } else {
            Log.e("TAG", "MethodChannel is not initialized")
        }
    }


}


