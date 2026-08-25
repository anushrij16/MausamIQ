package com.aetherx.mausamiq.domain.model

enum class UserPersona(
    val title: String,
    val iconEmoji: String,
    val description: String,
    val primaryConcern: String
) {
    STUDENT(
        title = "Student",
        iconEmoji = "🎓",
        description = "Focuses on college commute windows, sudden rain showers, and outdoor campus activities.",
        primaryConcern = "Rain probability during commute hours (8-9 AM, 4-6 PM)"
    ),
    FARMER(
        title = "Farmer",
        iconEmoji = "🌾",
        description = "Focuses on rainfall quantity, wind speed for spraying, soil moisture, and storm alerts.",
        primaryConcern = "Precipitation windows, wind speed & temperature extremes"
    ),
    TRAVELLER(
        title = "Traveller",
        iconEmoji = "✈️",
        description = "Focuses on highway visibility, crosswinds, destination weather, and flight/transit delays.",
        primaryConcern = "Destination weather, visibility & storm severity"
    ),
    FITNESS(
        title = "Fitness Enthusiast",
        iconEmoji = "🏃",
        description = "Focuses on best morning/evening running windows, air quality index, heat index, and hydration.",
        primaryConcern = "Comfortable temperature windows & UV index"
    ),
    OUTDOOR_WORKER(
        title = "Outdoor Worker",
        iconEmoji = "🦺",
        description = "Focuses on extreme heat alerts, lightning safety, UV protection, and hydration intervals.",
        primaryConcern = "Heat index safety & severe lightning risk"
    ),
    GENERAL(
        title = "General Everyday",
        iconEmoji = "🌤",
        description = "Balanced everyday weather insights, umbrella reminders, and weekend planning.",
        primaryConcern = "Daily temperature range & rain likelihood"
    );

    companion object {
        fun fromString(name: String): UserPersona {
            return entries.find { it.name.equals(name, ignoreCase = true) } ?: STUDENT
        }
    }
}
