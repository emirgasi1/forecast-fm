package com.emirgasic.forecastfm.data.local.daos.saved

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.emirgasic.forecastfm.data.local.entities.saved.SavedPlaceEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SavedPlaceDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun savePlace(savedPlace: SavedPlaceEntity)

    @Query("DELETE FROM saved_places WHERE placeId = :placeId")
    suspend fun unsavePlace(placeId: String)

    @Query("SELECT * FROM saved_places ORDER BY savedAt DESC")
    fun getSavedPlaces(): Flow<List<SavedPlaceEntity>>

    @Query("SELECT EXISTS(SELECT 1 FROM saved_places WHERE placeId = :placeId)")
    fun isPlaceSaved(placeId: String): Flow<Boolean>
}