package com.example.assignment_enterslice.services

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.ContentValues
import android.content.Intent
import android.location.Location
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.assignment_enterslice.locationModel.LocationEvent
import com.example.assignment_enterslice.R
import com.example.assignment_enterslice.dbHelper.LocationDatabaseHelper
import com.google.android.gms.location.*
import org.greenrobot.eventbus.EventBus

class LocationService : Service() {
    companion object {
        const val CHANNEL_ID = "12345"
        const val NOTIFICATION_ID=123457
    }
    private var fusedLocationProviderClient: FusedLocationProviderClient? = null
    private var locationCallback: LocationCallback? = null
    private var locationRequest: LocationRequest? = null
    private var notificationManager: NotificationManager? = null
    private lateinit var databaseHelper: LocationDatabaseHelper

    private var location:Location?=null

    override fun onCreate() {
        super.onCreate()
        databaseHelper = LocationDatabaseHelper(this)
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        locationRequest =
            LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 1000).setIntervalMillis(500)
                .build()
        locationCallback = object : LocationCallback() {
            override fun onLocationAvailability(p0: LocationAvailability) {
                super.onLocationAvailability(p0)
            }
            override fun onLocationResult(locationResult: LocationResult) {
                super.onLocationResult(locationResult)
                onNewLocation(locationResult)
            }
        }
        notificationManager = this.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel =
                NotificationChannel(CHANNEL_ID, "locations", NotificationManager.IMPORTANCE_HIGH)
            notificationManager?.createNotificationChannel(notificationChannel)
        }
    }

    @Suppress("MissingPermission")
    fun createLocationRequest(){
        try {
            fusedLocationProviderClient?.requestLocationUpdates(
                locationRequest!!,locationCallback!!,null
            )
        }catch (e:Exception){
            e.printStackTrace()
        }

    }

    private fun removeLocationUpdates(){
        locationCallback?.let {
            fusedLocationProviderClient?.removeLocationUpdates(it)
        }
        stopForeground(true)
        stopSelf()
    }

    private fun onNewLocation(locationResult: LocationResult) {
        location = locationResult.lastLocation
        EventBus.getDefault().post(
            LocationEvent(
            latitude = location?.latitude,
            longitude = location?.longitude
        )
        )
        startForeground(NOTIFICATION_ID,getNotification())
    }

    fun getNotification():Notification{
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Location Updates")
            .setContentText(
                "Lat-> ${location?.latitude}\nLong -> ${location?.longitude}"
            )
            .setSmallIcon(R.mipmap.ic_launcher)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setOngoing(true)
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
            notification.setChannelId(CHANNEL_ID)
        }
        return notification.build()
    }


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        createLocationRequest()
        return START_STICKY
    }

    override fun onBind(intent: Intent): IBinder? = null

    override fun onDestroy() {
        super.onDestroy()
        removeLocationUpdates()
        Log.e("serive called", "Distroy " )

        if (location != null) {
            saveLocationToDatabase(location!!.latitude, location!!.longitude)
        }
    }
    private fun saveLocationToDatabase(latitude: Double, longitude: Double ) {
        val db = databaseHelper.writableDatabase

        Log.e("saving db", "lat- $latitude " )
        Log.e("saving db", "long- $longitude " )

        //delete old data and save new for last known location
        db.delete(LocationDatabaseHelper.TABLE_NAME, null, null)


        val values = ContentValues().apply {
            put("latitude", latitude)
            put("longitude", longitude)
            put("timestamp", System.currentTimeMillis()) // Add a timestamp
        }

        db.insert("location_table", null, values)
    }
}

