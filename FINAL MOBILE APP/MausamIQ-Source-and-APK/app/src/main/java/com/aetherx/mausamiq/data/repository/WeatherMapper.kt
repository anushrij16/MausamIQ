package com.aetherx.mausamiq.data.repository

import com.aetherx.mausamiq.core.designsystem.components.getWeatherConditionName
import com.aetherx.mausamiq.core.utils.DateUtils
import com.aetherx.mausamiq.data.remote.dto.OpenMeteoResponseDto
import com.aetherx.mausamiq.domain.model.CurrentWeather
import com.aetherx.mausamiq.domain.model.DailyWeather
import com.aetherx.mausamiq.domain.model.HourlyWeather
import com.aetherx.mausamiq.domain.model.WeatherInfo

object WeatherMapper {

    fun mapDtoToDomain(
        dto: OpenMeteoResponseDto,
        locationName: String,
        isOffline: Boolean = false,
        isDemoMode: Boolean = false,
        lastUpdatedText: String = DateUtils.getCurrentTimeFormatted()
    ): WeatherInfo {
        val currentDto = dto.current
        val hourlyDto = dto.hourly
        val dailyDto = dto.daily

        val currentWeather = CurrentWeather(
            time = currentDto?.time ?: "",
            temperature = currentDto?.temperature ?: 24.0,
            feelsLike = currentDto?.apparentTemperature ?: 25.0,
            weatherCode = currentDto?.weatherCode ?: 0,
            conditionName = getWeatherConditionName(currentDto?.weatherCode ?: 0),
            humidity = currentDto?.relativeHumidity ?: 50,
            windSpeedKmH = currentDto?.windSpeed ?: 12.0,
            windDirectionDeg = currentDto?.windDirection ?: 180,
            precipitationMm = currentDto?.precipitation ?: 0.0,
            surfacePressureHPa = currentDto?.surfacePressure ?: 1013.0,
            isDay = currentDto?.isDay == 1,
            uvIndex = hourlyDto?.uvIndices?.firstOrNull() ?: 3.5,
            visibilityKm = (hourlyDto?.visibilities?.firstOrNull() ?: 10000.0) / 1000.0,
            sunrise = dailyDto?.sunrises?.firstOrNull()?.let { DateUtils.formatHour(it) } ?: "06:15 AM",
            sunset = dailyDto?.sunsets?.firstOrNull()?.let { DateUtils.formatHour(it) } ?: "06:45 PM"
        )

        val hourlyList = mutableListOf<HourlyWeather>()
        if (hourlyDto != null) {
            val size = minOf(hourlyDto.time.size, 24)
            for (i in 0 until size) {
                val iso = hourlyDto.time.getOrNull(i) ?: ""
                hourlyList.add(
                    HourlyWeather(
                        isoTime = iso,
                        formattedHour = DateUtils.formatHour(iso),
                        temperature = hourlyDto.temperatures.getOrNull(i) ?: 24.0,
                        precipitationProbability = hourlyDto.precipitationProbabilities.getOrNull(i) ?: 0,
                        precipitationMm = hourlyDto.precipitations.getOrNull(i) ?: 0.0,
                        weatherCode = hourlyDto.weatherCodes.getOrNull(i) ?: 0,
                        windSpeedKmH = hourlyDto.windSpeeds.getOrNull(i) ?: 10.0,
                        humidity = hourlyDto.humidities.getOrNull(i) ?: 50,
                        visibilityKm = (hourlyDto.visibilities.getOrNull(i) ?: 10000.0) / 1000.0,
                        uvIndex = hourlyDto.uvIndices.getOrNull(i) ?: 0.0
                    )
                )
            }
        }

        val dailyList = mutableListOf<DailyWeather>()
        if (dailyDto != null) {
            val size = minOf(dailyDto.time.size, 7)
            for (i in 0 until size) {
                val iso = dailyDto.time.getOrNull(i) ?: ""
                dailyList.add(
                    DailyWeather(
                        isoDate = iso,
                        dayOfWeek = DateUtils.getDayOfWeekShort(iso),
                        weatherCode = dailyDto.weatherCodes.getOrNull(i) ?: 0,
                        tempMax = dailyDto.maxTemperatures.getOrNull(i) ?: 28.0,
                        tempMin = dailyDto.minTemperatures.getOrNull(i) ?: 18.0,
                        precipitationProbabilityMax = dailyDto.precipitationProbabilitiesMax.getOrNull(i) ?: 10,
                        precipitationSumMm = dailyDto.precipitationSums.getOrNull(i) ?: 0.0,
                        sunrise = dailyDto.sunrises.getOrNull(i)?.let { DateUtils.formatHour(it) } ?: "06:15 AM",
                        sunset = dailyDto.sunsets.getOrNull(i)?.let { DateUtils.formatHour(it) } ?: "06:45 PM",
                        uvIndexMax = dailyDto.uvIndicesMax.getOrNull(i) ?: 5.0
                    )
                )
            }
        }

        return WeatherInfo(
            locationName = locationName,
            latitude = dto.latitude,
            longitude = dto.longitude,
            current = currentWeather,
            hourly = hourlyList,
            daily = dailyList,
            isOffline = isOffline,
            isDemoMode = isDemoMode,
            lastUpdatedText = lastUpdatedText
        )
    }
}
