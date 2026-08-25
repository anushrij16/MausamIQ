package com.aetherx.mausamiq.domain.model

enum class AlertSeverity(val displayName: String, val level: Int) {
    CRITICAL("CRITICAL", 4),
    HIGH("HIGH", 3),
    MODERATE("MODERATE", 2),
    INFO("INFO", 1)
}

enum class AlertCategory {
    HEAVY_RAIN,
    EXTREME_HEAT,
    HIGH_WIND,
    THUNDERSTORM,
    LOW_VISIBILITY,
    FROST
}

data class WeatherAlert(
    val id: String,
    val title: String,
    val severity: AlertSeverity,
    val category: AlertCategory,
    val locationName: String,
    val timeWindow: String,
    val description: String,
    val recommendedAction: String,
    val whyItMatters: String,
    val isRead: Boolean = false,
    val timestampMillis: Long = System.currentTimeMillis()
)
