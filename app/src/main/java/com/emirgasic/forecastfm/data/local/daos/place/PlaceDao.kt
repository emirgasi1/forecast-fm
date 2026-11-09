package com.emirgasic.forecastfm.data.local.daos.place

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.emirgasic.forecastfm.data.local.entities.place.PlaceEntity
import com.emirgasic.forecastfm.data.local.entities.saved.SavedPlaceEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PlaceDao {
    // --- Venues / Places ---
    @Query("SELECT * FROM places")
    fun getAllPlaces(): Flow<List<PlaceEntity>>

    @Query("SELECT * FROM places WHERE venueId = :locationId")
    fun getPlacesByLocation(locationId: String): Flow<List<PlaceEntity>>

    @Query("SELECT * FROM places WHERE name LIKE '%' || :query || '%' OR category LIKE '%' || :query || '%'")
    fun searchPlaces(query: String): Flow<List<PlaceEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlaces(places: List<PlaceEntity>)

    @Query("DELETE FROM places")
    suspend fun clearPlaces()

    // --- Saved Places ---
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun savePlace(savedPlace: SavedPlaceEntity)

    @Query("DELETE FROM saved_places WHERE placeId = :placeId")
    suspend fun unsavePlace(placeId: String)

    @Query("SELECT * FROM saved_places")
    fun getSavedPlaces(): Flow<List<SavedPlaceEntity>>

    @Query("SELECT EXISTS(SELECT 1 FROM saved_places WHERE placeId = :placeId)")
    fun isPlaceSaved(placeId: String): Flow<Boolean>
}