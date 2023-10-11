package com.example.assignment_enterslice.LocationStreamHandler

import android.util.Log
import com.example.assignment_enterslice.locationModel.LocationEvent
import io.flutter.plugin.common.EventChannel
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class LocationStreamHandlers : EventChannel.StreamHandler { //for event use this method dont use direactly events
    private var eventSink: EventChannel.EventSink? = null

    override fun onListen(arguments: Any?, events: EventChannel.EventSink) {
        eventSink = events
    }
    override fun onCancel(arguments: Any?) {
        eventSink = null
    }
    fun sendLocationToFLutter(locationEvent: LocationEvent) {
        MainScope().launch {
            Log.e("LocationEvent-", "$locationEvent" )
            val locationData = HashMap<String, Double?>()
            locationData["latitude"] = locationEvent.latitude
            locationData["longitude"] = locationEvent.longitude

            eventSink?.success(locationData) //call on every 2 second to flutter pass value
        }
    }

}