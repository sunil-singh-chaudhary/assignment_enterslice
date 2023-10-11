package com.example.assignment_enterslice

import android.content.Intent
import android.os.Bundle
import com.example.assignment_enterslice.LocationStreamHandler.LocationStreamHandlers
import com.example.assignment_enterslice.dbHelper.DbHandler
import com.example.assignment_enterslice.locationModel.LocationEvent
import com.example.assignment_enterslice.services.LocationService
import io.flutter.embedding.android.FlutterActivity
import io.flutter.plugin.common.EventChannel
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe


class MainActivity : FlutterActivity() {
    companion object {
        private val METHOD_CHANNEL = "location_service" // Define your method channel name
        private val EVENT_CHANNEL = "EVENT_CHANNEL_SUB" // Define your method channel name
    }
    val locationStreamHandler = LocationStreamHandlers()
    val dbhandler=DbHandler()
    private var service: Intent? = null
    private var lastKnownLocation: LocationEvent? = null
    private var methodChannel: MethodChannel? = null
    private var eventChannel: EventChannel? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        service = Intent(this, LocationService::class.java)
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this)
        }
        methodChannel = MethodChannel(flutterEngine?.dartExecutor!!.binaryMessenger, METHOD_CHANNEL)
        // Register the MethodChannelHandler
        methodChannel!!.setMethodCallHandler { call: MethodCall, result: MethodChannel.Result ->
            when (call.method) {

                "startLocationService" -> {
                    // Start your location service here
                    startService(service)
                    result.success(null)
                }
                "stopLocationService" -> {
                    // Stop your location service here
                    print("Service stopped")
                    stopService(service)
                    result.success(null)
                }

                "fetchDataFromNative" -> {
                    try {
                        // Fetch data from the database
                        val LastKnownLocation = dbhandler.fetchDataFromDatabase(this)
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

        eventChannel= EventChannel(flutterEngine!!.dartExecutor.binaryMessenger, EVENT_CHANNEL)
        eventChannel!!.setStreamHandler(locationStreamHandler) //inside oncreate

    }

    override fun onDestroy() {
        super.onDestroy()
        stopService(service)
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this)
        }
    }

    @Subscribe
    fun receiveLocationEvent(locationEvent: LocationEvent) {  //getting update from service
        lastKnownLocation = locationEvent
        //pass location to event channels
        locationStreamHandler.sendLocationToFLutter(locationEvent)
    }

}


