package com.emirgasic.forecastfm.di

import android.content.Context
import androidx.room.Room
import com.emirgasic.forecastfm.data.local.AppDatabase
import com.emirgasic.forecastfm.data.repository.LocationRepository
import com.emirgasic.forecastfm.data.repository.PlaceRepository
import com.emirgasic.forecastfm.data.repository.SavedOutfitRepository
import com.emirgasic.forecastfm.data.repository.SavedPlaceRepository
import com.emirgasic.forecastfm.data.repository.SavedPlaylistRepository
import com.emirgasic.forecastfm.data.repository.SavedPostRepository
import com.emirgasic.forecastfm.data.local.daos.saved.SavedPlaceDao
import com.emirgasic.forecastfm.data.local.datastore.OutfitCacheManager
import com.emirgasic.forecastfm.data.local.datastore.WeatherCacheManager
import com.emirgasic.forecastfm.data.repository.MusicHistoryRepository
import com.emirgasic.forecastfm.data.repository.OutfitRepository
import com.emirgasic.forecastfm.data.repository.PlaylistRepository
import com.emirgasic.forecastfm.data.repository.ProfileRepository
import com.emirgasic.forecastfm.data.repository.WeatherRepository
import com.emirgasic.forecastfm.network.outfit.OutfitApi
import com.emirgasic.forecastfm.network.weather.WeatherApi

object ServiceLocator {
    private var database: AppDatabase? = null
    private var appContext: Context? = null
    private var locationRepository: LocationRepository? = null
    private var placeRepository: PlaceRepository? = null
    private var weatherRepository: WeatherRepository? = null

    fun init(context: Context) {
        appContext = context.applicationContext
        if (database == null) {
            database = Room.databaseBuilder(
                appContext!!,
                AppDatabase::class.java,
                "forecastfm_db"
            ).fallbackToDestructiveMigration().build()
        }
    }

    fun provideLocationRepository(): LocationRepository {
        return locationRepository ?: LocationRepository(
            locationDao = database!!.locationDao()
        ).also { locationRepository = it }
    }

    fun providePlaceRepository(): PlaceRepository {
        return placeRepository ?: PlaceRepository(
            placeDao = database!!.placeDao()
        ).also { placeRepository = it }
    }

    fun provideSavedPlaceRepository(): SavedPlaceRepository {
        return SavedPlaceRepository(savedPlaceDao = database!!.savedPlaceDao())
    }

    fun provideSavedPostRepository(): SavedPostRepository {
        return SavedPostRepository(savedPostDao = database!!.savedPostDao())
    }

    fun provideSavedOutfitRepository(): SavedOutfitRepository {
        return SavedOutfitRepository(savedOutfitDao = database!!.savedOutfitDao())
    }

    fun provideSavedPlaylistRepository(): SavedPlaylistRepository {
        return SavedPlaylistRepository(savedPlaylistDao = database!!.savedPlaylistDao())
    }
    fun providePlaylistRepository(): PlaylistRepository {
        return PlaylistRepository(
            playlistDao = database!!.playlistDao(),
            playlistApi = com.emirgasic.forecastfm.network.playlist.PlaylistApi()
        )
    }
    fun provideMusicHistoryRepository(): MusicHistoryRepository {
        return MusicHistoryRepository(database!!.musicHistoryDao())
    }

    fun provideWeatherRepository(): WeatherRepository {
        return weatherRepository ?: WeatherRepository(
            weatherApi = WeatherApi(),
            cacheManager = WeatherCacheManager(appContext!!) // 👈 Inject Cache
        ).also { weatherRepository = it }
    }

    fun provideProfileRepository(): ProfileRepository {
        return ProfileRepository(
            profileApi = com.emirgasic.forecastfm.network.profile.ProfileApi(),
            cacheManager = com.emirgasic.forecastfm.data.local.datastore.ProfileCacheManager(appContext!!)
        )
    }
    fun provideWeatherCacheManager(): WeatherCacheManager {
        return WeatherCacheManager(appContext!!)
    }

    fun provideOutfitRepository(): OutfitRepository {
        return OutfitRepository(
            outfitApi = OutfitApi(),
            cacheManager = OutfitCacheManager(appContext!!)
        )
    }
}