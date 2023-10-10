package com.example.assignment_enterslice.dbHelper

import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class LocationDatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "location_data.db"
        private const val DATABASE_VERSION = 1
         const val TABLE_NAME = "location_table"
        private const val COLUMN_ID = "_id"
         const val COLUMN_LATITUDE = "latitude"
         const val COLUMN_LONGITUDE = "longitude"
         const val COLUMN_TIMESTAMP = "timestamp"
    }
    fun readAllData(): List<Map<String, Any>> {
        val data = mutableListOf<Map<String, Any>>()
        val db = this.readableDatabase

        val query = "SELECT * FROM $TABLE_NAME ORDER BY $COLUMN_TIMESTAMP DESC"
        val cursor: Cursor = db.rawQuery(query, null)

        if (cursor.moveToFirst()) {
            do {
                val locationData = HashMap<String, Any>()

                val columnIndexLatitude = cursor.getColumnIndex(COLUMN_LATITUDE)
                if (columnIndexLatitude != -1) {
                    locationData[COLUMN_LATITUDE] = cursor.getDouble(columnIndexLatitude)
                } else {
                    // Handle the case where the column does not exist in the cursor
                    // You can set a default value or take appropriate action
                    locationData[COLUMN_LATITUDE] = 0.0 // Set a default value, or handle the case accordingly
                }
                val columnIndexLongitude = cursor.getColumnIndex(COLUMN_LONGITUDE)
                if (columnIndexLongitude != -1) {
                    locationData[COLUMN_LONGITUDE] = cursor.getDouble(columnIndexLongitude)
                } else {
                    // Handle the case where the column does not exist in the cursor
                    // You can set a default value or take appropriate action
                    locationData[COLUMN_LONGITUDE] = 0.0 // Set a default value, or handle the case accordingly
                }
                val columnIndexTimestamp = cursor.getColumnIndex(COLUMN_TIMESTAMP)
                if (columnIndexTimestamp != -1) {
                    locationData[COLUMN_TIMESTAMP] = cursor.getLong(columnIndexTimestamp)
                } else {
                    // Handle the case where the column does not exist in the cursor
                    // You can set a default value or take appropriate action
                    locationData[COLUMN_TIMESTAMP] = 0.0 // Set a default value, or handle the case accordingly
                }
//                locationData[COLUMN_TIMESTAMP] = cursor.getLong(cursor.getColumnIndex(COLUMN_TIMESTAMP))
                data.add(locationData)
            } while (cursor.moveToNext())
        }

        cursor.close()
        db.close()

        return data
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createTableQuery = """
            CREATE TABLE $TABLE_NAME (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_LATITUDE REAL,
                $COLUMN_LONGITUDE REAL,
                $COLUMN_TIMESTAMP INTEGER
            )
        """.trimIndent()

        db.execSQL(createTableQuery)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // Implement database upgrade logic if needed
    }
}
