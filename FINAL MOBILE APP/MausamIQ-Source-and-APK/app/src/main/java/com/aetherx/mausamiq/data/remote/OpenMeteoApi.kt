package com.aetherx.mausamiq.data.remote

import com.aetherx.mausamiq.data.remote.dto.GeocodingResponseDto
import com.aetherx.mausamiq.data.remote.dto.OpenMeteoResponseDto
import retrofit2.http.GET
import retrofit2.http.Query

interface OpenMeteoApi {

    @GET("v1/forecast")
    suspend fun getForecast(
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double,
        @Query("current") current: String = "temperature_2m,relative_humidity_2m,apparent_temperature,is_day,precipitation,weather_code,surface_pressure,wind_speed_10m,wind_direction_10m",
        @Query("hourly") hourly: String = "temperature_2m,relative_humidity_2m,precipitation_probability,precipitation,weather_code,wind_speed_10m,visibility,uv_index",
        @Query("daily") daily: String = "weather_code,temperature_2m_max,temperature_2m_min,sunrise,sunset,precipitation_sum,precipitation_probability_max,uv_index_max",
        @Query("timezone") timezone: String = "auto",
        @Query("forecast_days") forecastDays: Int = 7
    ): OpenMeteoResponseDto

    @GET("https://geocoding-api.open-meteo.com/v1/search")
    suspend fun searchLocations(
        @Query("name") query: String,
        @Query("count") count: Int = 10,
        @Query("language") language: String = "en",
        @Query("format") format: String = "json"
    ): GeocodingResponseDto
}
