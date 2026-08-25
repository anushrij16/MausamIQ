package com.aetherx.mausamiq.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class OpenMeteoResponseDto(
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val timezone: String = "UTC",
    @SerialName("current") val current: CurrentWeatherDto? = null,
    @SerialName("hourly") val hourly: HourlyDto? = null,
    @SerialName("daily") val daily: DailyDto? = null
)

@Serializable
data class CurrentWeatherDto(
    val time: String = "",
    @SerialName("temperature_2m") val temperature: Double = 0.0,
    @SerialName("apparent_temperature") val apparentTemperature: Double = 0.0,
    @SerialName("relative_humidity_2m") val relativeHumidity: Int = 0,
    @SerialName("is_day") val isDay: Int = 1,
    @SerialName("precipitation") val precipitation: Double = 0.0,
    @SerialName("weather_code") val weatherCode: Int = 0,
    @SerialName("surface_pressure") val surfacePressure: Double = 1013.25,
    @SerialName("wind_speed_10m") val windSpeed: Double = 0.0,
    @SerialName("wind_direction_10m") val windDirection: Int = 0
)

@Serializable
data class HourlyDto(
    val time: List<String> = emptyList(),
    @SerialName("temperature_2m") val temperatures: List<Double> = emptyList(),
    @SerialName("relative_humidity_2m") val humidities: List<Int> = emptyList(),
    @SerialName("precipitation_probability") val precipitationProbabilities: List<Int> = emptyList(),
    @SerialName("precipitation") val precipitations: List<Double> = emptyList(),
    @SerialName("weather_code") val weatherCodes: List<Int> = emptyList(),
    @SerialName("wind_speed_10m") val windSpeeds: List<Double> = emptyList(),
    @SerialName("visibility") val visibilities: List<Double> = emptyList(),
    @SerialName("uv_index") val uvIndices: List<Double> = emptyList()
)

@Serializable
data class DailyDto(
    val time: List<String> = emptyList(),
    @SerialName("weather_code") val weatherCodes: List<Int> = emptyList(),
    @SerialName("temperature_2m_max") val maxTemperatures: List<Double> = emptyList(),
    @SerialName("temperature_2m_min") val minTemperatures: List<Double> = emptyList(),
    @SerialName("sunrise") val sunrises: List<String> = emptyList(),
    @SerialName("sunset") val sunsets: List<String> = emptyList(),
    @SerialName("precipitation_sum") val precipitationSums: List<Double> = emptyList(),
    @SerialName("precipitation_probability_max") val precipitationProbabilitiesMax: List<Int> = emptyList(),
    @SerialName("uv_index_max") val uvIndicesMax: List<Double> = emptyList()
)
