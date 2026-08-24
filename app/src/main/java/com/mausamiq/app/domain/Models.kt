package com.mausamiq.app.domain

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/** The product supports additive contexts instead of a single fixed persona. */
enum class Persona(val label: String, val shortDescription: String) {
    STUDENT("Student", "Commute, college and rain timing"),
    FARMER("Farmer", "Rainfall, wind and farm windows"),
    FISHERMAN("Fisherman", "Wind, visibility and marine data"),
    TRAVELLER("Traveller", "Trip status and destination risk"),
    DELIVERY_WORKER("Delivery worker", "Heat, rain and route conditions"),
    OUTDOOR_WORKER("Outdoor worker", "Working-time weather risk"),
    FITNESS_USER("Fitness user", "Comfortable activity windows"),
    COMMUTER("Commuter", "Departure and arrival conditions"),
    SENIOR("Senior-friendly", "Simple, high-contrast summaries"),
    PARENT("Parent / family", "Family locations and important warnings"),
    GENERAL("General user", "A balanced weather summary")
}

data class SavedPlace(val label: String, val city: String, val temperature: Int, val rainChance: Int)
data class Routine(val label: String, val time: String, val placeLabel: String, val activity: String)
data class WeatherSnapshot(
    val city: String,
    val temperature: Int,
    val feelsLike: Int,
    val condition: String,
    val rainChance: Int,
    val humidity: Int,
    val windKph: Int,
    val visibilityKm: Double,
    val updatedMinutesAgo: Int,
    val source: String = "MausamIQ demo weather feed"
)

data class RiskSummary(val rain: String, val heat: String, val wind: String, val travel: String)
data class Recommendation(
    val title: String,
    val summary: String,
    val whenText: String,
    val whereText: String,
    val whyText: String,
    val actionText: String,
    val relevance: String,
    val factors: List<String>
)
data class WeatherAlert(
    val title: String,
    val detail: String,
    val priority: String,
    val location: String,
    val time: String,
    val updated: Boolean = false
)
data class UserProfile(
    val name: String = "Naren",
    val personas: Set<Persona> = setOf(Persona.STUDENT),
    val places: List<SavedPlace> = listOf(
        SavedPlace("HOME", "Chennai", 29, 20),
        SavedPlace("COLLEGE", "Tambaram", 27, 75)
    ),
    val routines: List<Routine> = listOf(Routine("College commute", "08:00", "COLLEGE", "Travel")),
    val adaptivePersonalization: Boolean = true,
    val notificationsEnabled: Boolean = true,
    val quietHours: Boolean = false,
    val feedbackCount: Int = 0
)

data class AppUiState(
    val profile: UserProfile = UserProfile(),
    val selectedTab: Int = 0,
    val selectedPlace: String = "HOME",
    val isDemoMode: Boolean = true,
    val isOffline: Boolean = false,
    val showExplanation: Boolean = false,
    val showTrace: Boolean = false,
    val lastFeedback: String? = null,
    val weather: WeatherSnapshot = WeatherSnapshot("Chennai", 29, 31, "Partly cloudy", 62, 78, 18, 7.0, 8),
    val collegeWeather: WeatherSnapshot = WeatherSnapshot("Tambaram", 27, 29, "Heavy rain likely", 75, 84, 22, 5.0, 8),
    val risks: RiskSummary = RiskSummary("HIGH", "MODERATE", "LOW", "HIGH"),
    val recommendation: Recommendation = Recommendation(
        "College commute rain alert",
        "Heavy rain is likely during your usual college commute.",
        "Today, 08:00–09:00",
        "Near your saved college location",
        "This overlaps with your student persona, college location and usual travel time.",
        "Consider leaving earlier and carrying rain protection.",
        "Very high",
        listOf("Selected persona: Student", "College location: Tambaram", "Usual travel time: 08:00", "Rain probability: 75%", "Forecast overlaps with your commute")
    ),
    val alerts: List<WeatherAlert> = listOf(
        WeatherAlert("Heavy rain — Tambaram", "Rain risk overlaps with your college commute.", "HIGH PRIORITY", "COLLEGE", "08:00–09:00"),
        WeatherAlert("Moderate heat — Chennai", "Temperatures may feel warmer during the afternoon.", "INFORMATIONAL", "HOME", "13:00–16:00")
    )
)

fun UserProfile.primaryPersona(): Persona = personas.firstOrNull() ?: Persona.GENERAL
fun String.toTitleCase(): String = lowercase().replaceFirstChar { it.titlecase() }
fun freshnessText(minutes: Int, offline: Boolean): String = if (offline) "Last updated $minutes minutes ago · Cached" else "Updated $minutes minutes ago"
fun nowLabel(): String = LocalDateTime.now().format(DateTimeFormatter.ofPattern("EEE, d MMM · h:mm a"))
