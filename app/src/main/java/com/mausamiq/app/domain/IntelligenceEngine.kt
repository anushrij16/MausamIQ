package com.mausamiq.app.domain

object IntelligenceEngine {
    fun recommendation(profile: UserProfile, weather: WeatherSnapshot, college: WeatherSnapshot): Recommendation {
        val persona = profile.primaryPersona()
        return when (persona) {
            Persona.FARMER -> Recommendation(
                "Afternoon farm window needs attention",
                "Rain is expected later today; outdoor farm conditions may become less favorable.",
                "Today, 14:00–17:00",
                "Your saved FARM location",
                "Rain timing and wind are prioritized for your Farmer context.",
                "Consider reviewing outdoor work timing; this is advisory, not a guaranteed agronomic instruction.",
                "Very high",
                listOf("Selected persona: Farmer", "Rain probability: 75%", "Wind: 22 km/h", "Rainfall timing affects the activity window")
            )
            Persona.TRAVELLER -> Recommendation(
                "Trip weather needs a timing check",
                "The destination has a higher rain risk than your current location.",
                "Before your planned departure",
                "Chennai → Coimbatore · Route data unavailable",
                "Your Traveller context prioritizes destination conditions and timing.",
                "Review destination weather before leaving; route-level weather is unavailable in demo mode.",
                "High",
                listOf("Selected persona: Traveller", "Destination weather selected", "Current rain risk: 62%", "Route data unavailable")
            )
            Persona.FISHERMAN -> Recommendation(
                "Marine conditions unavailable",
                "The current data source does not provide wave or marine-warning data.",
                "For today’s planning window",
                "Saved marine location not configured",
                "Your Fisherman context requires marine-specific data before making a useful recommendation.",
                "Use an official marine source for navigation decisions; MausamIQ is not providing a safety guarantee.",
                "High",
                listOf("Selected persona: Fisherman", "Wind data available: 22 km/h", "Wave data: unavailable", "Marine warning: unavailable")
            )
            Persona.FITNESS_USER -> Recommendation(
                "Best outdoor activity window",
                "Conditions look more favorable for an early activity window.",
                "Today, 06:00–07:30",
                "Near your current location",
                "Lower expected rain and moderate temperature are prioritized for Fitness context.",
                "Consider an early walk or run if it fits your routine.",
                "High",
                listOf("Selected persona: Fitness user", "Moderate temperature", "Lower wind in the morning", "Rain risk rises later")
            )
            else -> Recommendation(
                "College commute rain alert",
                "Heavy rain is likely during your usual college commute.",
                "Today, 08:00–09:00",
                "Near your saved college location",
                "This overlaps with your student persona, college location and usual travel time.",
                "Consider leaving earlier and carrying rain protection.",
                "Very high",
                listOf("Selected persona: ${persona.label}", "College location: Tambaram", "Usual travel time: 08:00", "Rain probability: ${college.rainChance}%", "Forecast overlaps with your commute")
            )
        }
    }

    fun risks(profile: UserProfile, weather: WeatherSnapshot, college: WeatherSnapshot): RiskSummary {
        val travel = if (profile.personas.contains(Persona.TRAVELLER) || profile.personas.contains(Persona.STUDENT) || college.rainChance >= 70) "HIGH" else "MODERATE"
        val heat = if (weather.feelsLike >= 35) "HIGH" else "MODERATE"
        val wind = if (college.windKph >= 35) "HIGH" else if (college.windKph >= 20) "MODERATE" else "LOW"
        return RiskSummary(if (college.rainChance >= 70) "HIGH" else "MODERATE", heat, wind, travel)
    }

    fun prioritizedSections(profile: UserProfile): List<String> = when {
        profile.personas.contains(Persona.FARMER) -> listOf("Rainfall outlook", "Farm weather window", "Wind", "Temperature", "7-day forecast")
        profile.personas.contains(Persona.TRAVELLER) -> listOf("Trip status", "Destination weather", "Route risk", "Travel window", "Forecast")
        profile.personas.contains(Persona.FITNESS_USER) -> listOf("Activity window", "Temperature & humidity", "Rain timeline", "Wind", "Forecast")
        else -> listOf("College commute alert", "Rain timeline", "Departure recommendation", "Home vs college", "7-day forecast")
    }
}
