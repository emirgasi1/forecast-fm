package com.emirgasic.forecastfm.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.emirgasic.forecastfm.data.local.daos.feed.FeedDao
import com.emirgasic.forecastfm.data.local.daos.location.LocationDao
import com.emirgasic.forecastfm.data.local.daos.music.MusicHistoryDao
import com.emirgasic.forecastfm.data.local.daos.music.PlaylistDao
import com.emirgasic.forecastfm.data.local.daos.place.PlaceDao
import com.emirgasic.forecastfm.data.local.daos.saved.SavedOutfitDao
import com.emirgasic.forecastfm.data.local.daos.saved.SavedPlaceDao
import com.emirgasic.forecastfm.data.local.daos.saved.SavedPlaylistDao
import com.emirgasic.forecastfm.data.local.daos.saved.SavedPostDao
import com.emirgasic.forecastfm.data.local.entities.feed.PostEntity
import com.emirgasic.forecastfm.data.local.entities.location.LocationEntity
import com.emirgasic.forecastfm.data.local.entities.music.MusicHistoryEntity
import com.emirgasic.forecastfm.data.local.entities.music.SongEntity
import com.emirgasic.forecastfm.data.local.entities.place.PlaceEntity
import com.emirgasic.forecastfm.data.local.entities.playlist.PlaylistEntity
import com.emirgasic.forecastfm.data.local.entities.saved.SavedOutfitEntity
import com.emirgasic.forecastfm.data.local.entities.saved.SavedPlaceEntity
import com.emirgasic.forecastfm.data.local.entities.saved.SavedPlaylistEntity
import com.emirgasic.forecastfm.data.local.entities.saved.SavedPostEntity

@Database(
    entities = [
        LocationEntity::class,
        PlaceEntity::class,
        SavedPlaceEntity::class,
        SavedPostEntity::class,
        SavedPlaylistEntity::class, // 👈 Make sure this is here
        SavedOutfitEntity::class,
        PlaylistEntity::class,      // 👈 ADD THIS
        SongEntity::class,
        MusicHistoryEntity::class,
        PostEntity::class
    ],
    version = 6,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun locationDao(): LocationDao
    abstract fun placeDao(): PlaceDao
    abstract fun savedPlaceDao(): SavedPlaceDao // 👈 ADD THIS LINE
    abstract fun savedPostDao(): SavedPostDao
    abstract fun savedPlaylistDao(): SavedPlaylistDao
    abstract fun savedOutfitDao(): SavedOutfitDao
    abstract fun playlistDao(): PlaylistDao
    abstract fun musicHistoryDao(): MusicHistoryDao
    abstract fun feedDao(): FeedDao

}