package com.aetherx.mausamiq.domain.model

data class LocationItem(
    val id: Long = 0,
    val name: String,
    val type: LocationType,
    val latitude: Double,
    val longitude: Double,
    val isPrimary: Boolean = false
)

enum class LocationType(val label: String, val iconEmoji: String) {
    HOME("Home", "🏠"),
    COLLEGE("College", "🎓"),
    WORK("Work", "💼"),
    FARM("Farm", "🌾"),
    OTHER("Other", "📍");

    companion object {
        fun fromString(str: String): LocationType {
            return entries.find { it.name.equals(str, ignoreCase = true) } ?: OTHER
        }
    }
}
