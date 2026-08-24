package com.mausamiq.app.data

import com.mausamiq.app.domain.WeatherSnapshot
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect

/** Remote provider responses should be normalized before the UI sees them. */
interface WeatherRepository {
    fun observeWeather(city: String): Flow<WeatherSnapshot>
    suspend fun refresh(city: String): Result<WeatherSnapshot>
}

/** Demo implementation keeps the application useful without network access. */
class DemoWeatherRepository : WeatherRepository {
    override fun observeWeather(city: String): Flow<WeatherSnapshot> = kotlinx.coroutines.flow.flow {
        emit(
            WeatherSnapshot(
                city = city,
                temperature = if (city == "Tambaram") 27 else 29,
                feelsLike = if (city == "Tambaram") 29 else 31,
                condition = if (city == "Tambaram") "Heavy rain likely" else "Partly cloudy",
                rainChance = if (city == "Tambaram") 75 else 20,
                humidity = if (city == "Tambaram") 84 else 78,
                windKph = if (city == "Tambaram") 22 else 18,
                visibilityKm = if (city == "Tambaram") 5.0 else 7.0,
                updatedMinutesAgo = 8
            )
        )
    }

    override suspend fun refresh(city: String): Result<WeatherSnapshot> = runCatching {
        observeWeather(city).let { flow ->
            var snapshot: WeatherSnapshot? = null
            flow.collect { snapshot = it.copy(updatedMinutesAgo = 1) }
            snapshot ?: error("No weather data available")
        }
    }
}
