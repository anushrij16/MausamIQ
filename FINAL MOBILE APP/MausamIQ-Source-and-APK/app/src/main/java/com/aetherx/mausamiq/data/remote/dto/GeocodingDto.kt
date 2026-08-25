package com.aetherx.mausamiq.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GeocodingResponseDto(
    val results: List<GeocodingResultDto> = emptyList()
)

@Serializable
data class GeocodingResultDto(
    val id: Long = 0,
    val name: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val country: String = "",
    @SerialName("admin1") val state: String? = null
)
