package com.emirgasic.forecastfm.di

import android.content.Context
import com.emirgasic.forecastfm.data.local.AppDatabase
import com.emirgasic.forecastfm.data.repository.LocationRepository
import com.emirgasic.forecastfm.network.location.LocationApi

object ServiceLocator {
    @Volatile
    private var database: AppDatabase? = null

    fun init(context: Context) {
        if (database == null) {
            synchronized(this) {
                if (database == null) {
                    database = androidx.room.Room.databaseBuilder(
                        context.applicationContext,
                        AppDatabase::class.java,
                        "forecastfm_db"
                    ).build()
                }
            }
        }
    }

    fun getDatabase(): AppDatabase {
        return database ?: throw IllegalStateException("ServiceLocator not initialized")
    }

    // Provide a shared LocationRepository
    fun provideLocationRepository(): LocationRepository {
        return LocationRepository(
            api = LocationApi(),
            locationDao = getDatabase().locationDao()
        )
    }
}