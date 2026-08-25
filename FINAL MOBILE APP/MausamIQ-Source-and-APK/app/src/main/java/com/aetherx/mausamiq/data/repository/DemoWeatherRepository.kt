package com.aetherx.mausamiq.data.repository

import com.aetherx.mausamiq.core.utils.DateUtils
import com.aetherx.mausamiq.core.utils.Resource
import com.aetherx.mausamiq.domain.model.CurrentWeather
import com.aetherx.mausamiq.domain.model.DailyWeather
import com.aetherx.mausamiq.domain.model.HourlyWeather
import com.aetherx.mausamiq.domain.model.LocationItem
import com.aetherx.mausamiq.domain.model.LocationType
import com.aetherx.mausamiq.domain.model.UserPersona
import com.aetherx.mausamiq.domain.model.WeatherInfo
import com.aetherx.mausamiq.domain.repository.IWeatherRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class DemoWeatherRepository : IWeatherRepository {

    override fun getWeather(
        latitude: Double,
        longitude: Double,
        locationName: String,
        forceRefresh: Boolean,
        isDemoMode: Boolean
    ): Flow<Resource<WeatherInfo>> = flow {
        emit(Resource.Loading())
        val mockWeather = createDemoScenario(locationName, latitude, longitude)
        emit(Resource.Success(mockWeather))
    }

    override suspend fun searchLocations(query: String): List<LocationItem> {
        val sampleCities = listOf(
            LocationItem(1, "New Delhi", LocationType.OTHER, 28.6139, 77.2090),
            LocationItem(2, "Chennai", LocationType.OTHER, 13.0827, 80.2707),
            LocationItem(3, "Bengaluru", LocationType.OTHER, 12.9716, 77.5946),
            LocationItem(4, "Mumbai", LocationType.OTHER, 19.0760, 72.8777),
            LocationItem(5, "Hyderabad", LocationType.OTHER, 17.3850, 78.4867),
            LocationItem(6, "Kolkata", LocationType.OTHER, 22.5726, 88.3639),
            LocationItem(7, "Pune", LocationType.OTHER, 18.5204, 73.8567),
            LocationItem(8, "Ahmedabad", LocationType.OTHER, 23.0225, 72.5714),
            LocationItem(9, "Jaipur", LocationType.OTHER, 26.9124, 75.7873),
            LocationItem(10, "Coimbatore", LocationType.OTHER, 11.0168, 76.9558)
        )
        return if (query.isBlank()) {
            sampleCities
        } else {
            sampleCities.filter { it.name.contains(query, ignoreCase = true) }
        }
    }

    fun createDemoScenario(
        locationName: String = "Campus (Demo)",
        lat: Double = 28.6139,
        lng: Double = 77.2090,
        persona: UserPersona = UserPersona.STUDENT
    ): WeatherInfo {
        val hourly = (0..23).map { hour ->
            val hourFormatted = if (hour == 0) "12 AM" else if (hour < 12) "$hour AM" else if (hour == 12) "12 PM" else "${hour - 12} PM"
            // High precipitation chance around 4 PM - 6 PM for student commute scenario
            val (rainChance, rainMm, code) = when (hour) {
                16 -> Triple(65, 4.2, 61) // 4 PM
                17 -> Triple(85, 9.8, 65) // 5 PM Peak Rain
                18 -> Triple(70, 5.0, 61) // 6 PM
                19 -> Triple(35, 1.2, 51) // 7 PM
                in 11..15 -> Triple(15, 0.0, 2)
                else -> Triple(5, 0.0, 0)
            }

            val temp = when (hour) {
                in 0..5 -> 22.0 + (hour * 0.2)
                in 6..12 -> 24.0 + ((hour - 6) * 1.5)
                in 13..15 -> 32.5 - ((hour - 13) * 0.5)
                in 16..18 -> 26.0 // drops during rain
                else -> 25.0 - ((hour - 19) * 0.6)
            }

            HourlyWeather(
                isoTime = "2026-08-25T${hour.toString().padStart(2, '0')}:00",
                formattedHour = hourFormatted,
                temperature = (temp * 10).toInt() / 10.0,
                precipitationProbability = rainChance,
                precipitationMm = rainMm,
                weatherCode = code,
                windSpeedKmH = if (hour in 16..18) 28.5 else 12.0,
                humidity = if (hour in 16..18) 88 else 58,
                visibilityKm = if (hour in 16..18) 3.5 else 9.8,
                uvIndex = if (hour in 10..15) 7.8 else 1.2
            )
        }

        val days = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
        val daily = days.mapIndexed { idx, day ->
            DailyWeather(
                isoDate = "2026-08-${25 + idx}",
                dayOfWeek = day,
                weatherCode = if (idx == 0) 65 else if (idx == 1) 61 else if (idx == 2) 2 else 0,
                tempMax = 32.0 + (idx % 3),
                tempMin = 22.0 + (idx % 2),
                precipitationProbabilityMax = if (idx == 0) 85 else if (idx == 1) 50 else 10,
                precipitationSumMm = if (idx == 0) 20.2 else if (idx == 1) 4.5 else 0.0,
                sunrise = "05:58 AM",
                sunset = "06:48 PM",
                uvIndexMax = 8.5
            )
        }

        val current = CurrentWeather(
            time = "2026-08-25T10:00",
            temperature = 29.4,
            feelsLike = 32.0,
            weatherCode = 2,
            conditionName = "Partly Cloudy • Commute Alert",
            humidity = 64,
            windSpeedKmH = 14.5,
            windDirectionDeg = 210,
            precipitationMm = 0.0,
            surfacePressureHPa = 1011.8,
            isDay = true,
            uvIndex = 6.4,
            visibilityKm = 9.0,
            sunrise = "05:58 AM",
            sunset = "06:48 PM"
        )

        return WeatherInfo(
            locationName = locationName,
            latitude = lat,
            longitude = lng,
            current = current,
            hourly = hourly,
            daily = daily,
            isOffline = false,
            isDemoMode = true,
            lastUpdatedText = "Demo Mode (SIH Scenario)"
        )
    }
}
