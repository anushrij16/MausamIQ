package com.aetherx.mausamiq.domain.repository

import com.aetherx.mausamiq.core.utils.Resource
import com.aetherx.mausamiq.domain.model.LocationItem
import com.aetherx.mausamiq.domain.model.WeatherInfo
import kotlinx.coroutines.flow.Flow

interface IWeatherRepository {
    fun getWeather(
        latitude: Double,
        longitude: Double,
        locationName: String,
        forceRefresh: Boolean = false,
        isDemoMode: Boolean = false
    ): Flow<Resource<WeatherInfo>>

    suspend fun searchLocations(query: String): List<LocationItem>
}
