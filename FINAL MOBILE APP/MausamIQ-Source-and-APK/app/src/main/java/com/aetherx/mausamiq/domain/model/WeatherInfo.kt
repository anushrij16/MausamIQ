package com.aetherx.mausamiq.domain.model

data class WeatherInfo(
    val locationName: String,
    val latitude: Double,
    val longitude: Double,
    val current: CurrentWeather,
    val hourly: List<HourlyWeather>,
    val daily: List<DailyWeather>,
    val isOffline: Boolean = false,
    val isDemoMode: Boolean = false,
    val lastUpdatedText: String = ""
)

data class CurrentWeather(
    val time: String,
    val temperature: Double,
    val feelsLike: Double,
    val weatherCode: Int,
    val conditionName: String,
    val humidity: Int,
    val windSpeedKmH: Double,
    val windDirectionDeg: Int,
    val precipitationMm: Double,
    val surfacePressureHPa: Double,
    val isDay: Boolean,
    val uvIndex: Double = 0.0,
    val visibilityKm: Double = 10.0,
    val sunrise: String = "06:00",
    val sunset: String = "18:30"
)

data class HourlyWeather(
    val isoTime: String,
    val formattedHour: String,
    val temperature: Double,
    val precipitationProbability: Int,
    val precipitationMm: Double,
    val weatherCode: Int,
    val windSpeedKmH: Double,
    val humidity: Int,
    val visibilityKm: Double,
    val uvIndex: Double
)

data class DailyWeather(
    val isoDate: String,
    val dayOfWeek: String,
    val weatherCode: Int,
    val tempMax: Double,
    val tempMin: Double,
    val precipitationProbabilityMax: Int,
    val precipitationSumMm: Double,
    val sunrise: String,
    val sunset: String,
    val uvIndexMax: Double
)
