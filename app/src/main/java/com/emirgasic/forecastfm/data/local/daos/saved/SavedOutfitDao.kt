package com.emirgasic.forecastfm.data.local.daos.saved

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.emirgasic.forecastfm.data.local.entities.saved.SavedOutfitEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SavedOutfitDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveOutfit(outfit: SavedOutfitEntity)

    @Query("DELETE FROM saved_outfits WHERE outfitId = :outfitId")
    suspend fun unsaveOutfit(outfitId: String)

    @Query("SELECT * FROM saved_outfits ORDER BY savedAt DESC")
    fun getSavedOutfits(): Flow<List<SavedOutfitEntity>>

    @Query("SELECT EXISTS(SELECT 1 FROM saved_outfits WHERE outfitId = :outfitId)")
    fun isOutfitSaved(outfitId: String): Flow<Boolean>
}