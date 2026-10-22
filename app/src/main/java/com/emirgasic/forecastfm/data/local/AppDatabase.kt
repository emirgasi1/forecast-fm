package com.emirgasic.forecastfm.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.emirgasic.forecastfm.data.local.daos.LocationDao
import com.emirgasic.forecastfm.data.local.entities.LocationEntity

@Database(
    entities = [
        LocationEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    // We will add more DAOs here later
    abstract fun locationDao(): LocationDao
}