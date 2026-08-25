package com.aetherx.mausamiq.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "weather_cache")
data class WeatherCacheEntity(
    @PrimaryKey
    val locationKey: String, // "lat_lng"
    val locationName: String,
    val temperature: Double,
    val feelsLike: Double,
    val weatherCode: Int,
    val humidity: Int,
    val windSpeed: Double,
    val rainProbability: Int,
    val uvIndex: Double,
    val surfacePressure: Double,
    val visibilityMeters: Double,
    val sunrise: String,
    val sunset: String,
    val hourlyJson: String,
    val dailyJson: String,
    val lastUpdatedMillis: Long
)
