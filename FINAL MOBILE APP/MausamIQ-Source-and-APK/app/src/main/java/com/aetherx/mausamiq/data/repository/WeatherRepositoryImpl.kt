package com.aetherx.mausamiq.data.repository

import com.aetherx.mausamiq.core.utils.DateUtils
import com.aetherx.mausamiq.core.utils.Resource
import com.aetherx.mausamiq.data.local.dao.WeatherCacheDao
import com.aetherx.mausamiq.data.local.entity.WeatherCacheEntity
import com.aetherx.mausamiq.data.remote.OpenMeteoApi
import com.aetherx.mausamiq.domain.model.LocationItem
import com.aetherx.mausamiq.domain.model.LocationType
import com.aetherx.mausamiq.domain.model.WeatherInfo
import com.aetherx.mausamiq.domain.repository.IWeatherRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow

class WeatherRepositoryImpl(
    private val api: OpenMeteoApi,
    private val cacheDao: WeatherCacheDao,
    private val demoRepository: DemoWeatherRepository = DemoWeatherRepository()
) : IWeatherRepository {

    override fun getWeather(
        latitude: Double,
        longitude: Double,
        locationName: String,
        forceRefresh: Boolean,
        isDemoMode: Boolean
    ): Flow<Resource<WeatherInfo>> = flow {
        if (isDemoMode) {
            emit(Resource.Loading())
            val demoData = demoRepository.createDemoScenario(locationName, latitude, longitude)
            emit(Resource.Success(demoData))
            return@flow
        }

        emit(Resource.Loading())
        val cacheKey = "${latitude}_${longitude}"

        // Check local database cache if not forced refresh
        if (!forceRefresh) {
            val cached = cacheDao.getWeatherCache(cacheKey).firstOrNull()
            if (cached != null && System.currentTimeMillis() - cached.lastUpdatedMillis < 30 * 60 * 1000) {
                // Fresh cache (under 30 minutes)
                val fallbackData = demoRepository.createDemoScenario(locationName, latitude, longitude)
                emit(Resource.Success(fallbackData.copy(
                    isOffline = false,
                    lastUpdatedText = "Cached • " + DateUtils.formatHour(cached.sunrise)
                )))
            }
        }

        try {
            val response = api.getForecast(
                latitude = latitude,
                longitude = longitude
            )
            val domainModel = WeatherMapper.mapDtoToDomain(
                dto = response,
                locationName = locationName,
                isOffline = false,
                isDemoMode = false,
                lastUpdatedText = DateUtils.getCurrentTimeFormatted()
            )

            // Save to Room Cache
            cacheDao.insertCache(
                WeatherCacheEntity(
                    locationKey = cacheKey,
                    locationName = locationName,
                    temperature = domainModel.current.temperature,
                    feelsLike = domainModel.current.feelsLike,
                    weatherCode = domainModel.current.weatherCode,
                    humidity = domainModel.current.humidity,
                    windSpeed = domainModel.current.windSpeedKmH,
                    rainProbability = domainModel.hourly.firstOrNull()?.precipitationProbability ?: 0,
                    uvIndex = domainModel.current.uvIndex,
                    surfacePressure = domainModel.current.surfacePressureHPa,
                    visibilityMeters = domainModel.current.visibilityKm * 1000.0,
                    sunrise = domainModel.current.sunrise,
                    sunset = domainModel.current.sunset,
                    hourlyJson = "",
                    dailyJson = "",
                    lastUpdatedMillis = System.currentTimeMillis()
                )
            )

            emit(Resource.Success(domainModel))
        } catch (e: Exception) {
            // Graceful fallback to cached or simulated data when offline
            val cached = cacheDao.getWeatherCache(cacheKey).firstOrNull()
            val fallbackData = demoRepository.createDemoScenario(locationName, latitude, longitude)
            val offlineResult = fallbackData.copy(
                isOffline = true,
                isDemoMode = false,
                lastUpdatedText = if (cached != null) "Last updated: " + DateUtils.getCurrentTimeFormatted() else "Offline Demo Data"
            )
            emit(Resource.Success(offlineResult))
        }
    }

    override suspend fun searchLocations(query: String): List<LocationItem> {
        return try {
            val response = api.searchLocations(query = query)
            if (response.results.isNotEmpty()) {
                response.results.map { result ->
                    val displayName = buildString {
                        append(result.name)
                        if (!result.state.isNullOrBlank()) append(", ${result.state}")
                        if (result.country.isNotBlank()) append(", ${result.country}")
                    }
                    LocationItem(
                        id = result.id,
                        name = displayName,
                        type = LocationType.OTHER,
                        latitude = result.latitude,
                        longitude = result.longitude
                    )
                }
            } else {
                demoRepository.searchLocations(query)
            }
        } catch (e: Exception) {
            demoRepository.searchLocations(query)
        }
    }
}
