package com.aetherx.mausamiq.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.aetherx.mausamiq.data.local.entity.WeatherCacheEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WeatherCacheDao {
    @Query("SELECT * FROM weather_cache WHERE locationKey = :key LIMIT 1")
    fun getWeatherCache(key: String): Flow<WeatherCacheEntity?>

    @Query("SELECT * FROM weather_cache ORDER BY lastUpdatedMillis DESC LIMIT 1")
    fun getLatestWeatherCache(): Flow<WeatherCacheEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCache(cache: WeatherCacheEntity)

    @Query("DELETE FROM weather_cache WHERE locationKey = :key")
    suspend fun deleteCache(key: String)
}
