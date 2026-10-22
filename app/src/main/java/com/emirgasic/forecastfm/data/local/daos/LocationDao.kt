package com.emirgasic.forecastfm.data.local.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.emirgasic.forecastfm.data.local.entities.LocationEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface LocationDao {
    @Query("SELECT * FROM locations")
    fun getAllLocations(): Flow<List<LocationEntity>>

    // Filter to only get cafes, restaurants, etc. (Fixes your map lag!)
    @Query("SELECT * FROM locations WHERE category NOT IN ('bus_stop', 'parking', 'crossing')")
    fun getVenuesOnly(): Flow<List<LocationEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLocations(locations: List<LocationEntity>)

    @Query("DELETE FROM locations")
    suspend fun clearLocations()
}