package com.example.assignment_enterslice.dbHelper

import android.content.Context

class DbHandler {

    fun fetchDataFromDatabase(context : Context): List<Map<String, Any?>> {
        val dbHelper = LocationDatabaseHelper(context)

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

}