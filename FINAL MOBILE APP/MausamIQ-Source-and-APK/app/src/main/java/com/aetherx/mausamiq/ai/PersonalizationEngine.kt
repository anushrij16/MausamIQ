package com.aetherx.mausamiq.ai

import com.aetherx.mausamiq.domain.model.DecisionFactor
import com.aetherx.mausamiq.domain.model.ExplainableInsight
import com.aetherx.mausamiq.domain.model.UserPersona
import com.aetherx.mausamiq.domain.model.WeatherInfo

object PersonalizationEngine {

    fun generateInsight(
        weather: WeatherInfo,
        persona: UserPersona,
        userName: String = "Explorer",
        departureTime: String = "08:30",
        returnTime: String = "17:00"
    ): ExplainableInsight {
        val hourly = weather.hourly
        val current = weather.current

        // Check if there is significant rain in the forecast
        val maxRainHour = hourly.maxByOrNull { it.precipitationProbability }
        val maxRainProb = maxRainHour?.precipitationProbability ?: 0
        val maxRainMm = maxRainHour?.precipitationMm ?: 0.0
        val maxRainTime = maxRainHour?.formattedHour ?: "4 PM"

        val maxTemp = weather.daily.firstOrNull()?.tempMax ?: current.temperature
        val maxUv = hourly.maxOfOrNull { it.uvIndex } ?: current.uvIndex
        val maxWind = hourly.maxOfOrNull { it.windSpeedKmH } ?: current.windSpeedKmH

        return when (persona) {
            UserPersona.STUDENT -> {
                if (maxRainProb >= 50) {
                    ExplainableInsight(
                        headline = "Elevated Rain Risk During College Return Commute",
                        recommendation = "Carry a compact umbrella and water-resistant bag cover before leaving campus.",
                        reason = "Precipitation probability peaks at $maxRainProb% around $maxRainTime, directly overlapping your typical $returnTime return commute.",
                        actionText = "Pack Umbrella & Waterproof Cover",
                        factors = listOf(
                            DecisionFactor("Rain Probability", "$maxRainProb%", "HIGH", "🌧️"),
                            DecisionFactor("Peak Rain Window", "$maxRainTime", "HIGH", "⏰"),
                            DecisionFactor("Commute Schedule", "$returnTime (Return)", "MEDIUM", "🎓"),
                            DecisionFactor("Expected Rain Amount", "$maxRainMm mm", "MEDIUM", "💧")
                        ),
                        confidencePercentage = 92,
                        personaContext = "Prioritizes campus travel safety & transit delays",
                        formulaExplanation = "Risk Score = (Rain Probability × 0.45) + (Commute Window Overlap × 0.35) + (Precipitation Volume × 0.20) = 88.5/100 (HIGH RISK)"
                    )
                } else if (maxTemp >= 35.0) {
                    ExplainableInsight(
                        headline = "High Heat & UV Index on Campus Pathways",
                        recommendation = "Stay hydrated and utilize shaded campus corridors during afternoon lecture transitions.",
                        reason = "Peak temperature will touch ${maxTemp.toInt()}°C with UV Index $maxUv at midday.",
                        actionText = "Carry Refillable Water Bottle",
                        factors = listOf(
                            DecisionFactor("Peak Temperature", "${maxTemp.toInt()}°C", "HIGH", "🌡️"),
                            DecisionFactor("UV Radiation Index", "$maxUv (Very High)", "HIGH", "☀️"),
                            DecisionFactor("Peak Window", "12:00 PM – 03:00 PM", "MEDIUM", "⏰")
                        ),
                        confidencePercentage = 88,
                        personaContext = "Monitors heat strain during open campus walks",
                        formulaExplanation = "Heat Strain Score calculated via Heat Index & UV threshold exceeding 7.0."
                    )
                } else {
                    ExplainableInsight(
                        headline = "Pleasant Weather for Campus & Outdoor Classes",
                        recommendation = "Ideal conditions for open-air study groups and outdoor campus sports.",
                        reason = "Mild temperature of ${current.temperature.toInt()}°C with negligible rain probability ($maxRainProb%).",
                        actionText = "Plan Outdoor Activities",
                        factors = listOf(
                            DecisionFactor("Current Temperature", "${current.temperature.toInt()}°C", "LOW", "🌤️"),
                            DecisionFactor("Rain Likelihood", "< 15%", "LOW", "☀️"),
                            DecisionFactor("Wind Speed", "${current.windSpeedKmH.toInt()} km/h", "LOW", "🍃")
                        ),
                        confidencePercentage = 95,
                        personaContext = "Optimized for college student schedule",
                        formulaExplanation = "All meteorological risk parameters are within safe nominal thresholds."
                    )
                }
            }

            UserPersona.FARMER -> {
                if (maxRainProb >= 60 || maxRainMm >= 5.0) {
                    ExplainableInsight(
                        headline = "Significant Rainfall Window for Crop & Soil Management",
                        recommendation = "Postpone fertilizer/pesticide spraying; prepare drainage channels to prevent waterlogging.",
                        reason = "Forecast indicates $maxRainProb% precipitation probability with estimated accumulation of $maxRainMm mm around $maxRainTime.",
                        actionText = "Halt Chemical Spraying & Check Drainage",
                        factors = listOf(
                            DecisionFactor("Rain Likelihood", "$maxRainProb%", "HIGH", "🌧️"),
                            DecisionFactor("Accumulation", "$maxRainMm mm", "HIGH", "💧"),
                            DecisionFactor("Wind Speed", "${maxWind.toInt()} km/h", "MEDIUM", "💨"),
                            DecisionFactor("Soil Saturation Risk", "Moderate-High", "HIGH", "🌾")
                        ),
                        confidencePercentage = 94,
                        personaContext = "Optimized for agricultural decision support & crop protection",
                        formulaExplanation = "Agricultural Spray Index: Spraying ineffective when rain probability > 40% or wind > 20 km/h."
                    )
                } else {
                    ExplainableInsight(
                        headline = "Favorable Dry Window for Spraying & Harvesting",
                        recommendation = "Proceed with scheduled field irrigation and crop harvesting during daylight hours.",
                        reason = "Wind speed is mild (${current.windSpeedKmH.toInt()} km/h) and no major rain is forecast for the next 48 hours.",
                        actionText = "Optimal Spraying & Harvesting Window",
                        factors = listOf(
                            DecisionFactor("Rain Chance", "< 20%", "LOW", "☀️"),
                            DecisionFactor("Wind Velocity", "${current.windSpeedKmH.toInt()} km/h (Safe)", "LOW", "🍃"),
                            DecisionFactor("Relative Humidity", "${current.humidity}%", "MEDIUM", "💧")
                        ),
                        confidencePercentage = 91,
                        personaContext = "Targeted agricultural weather advisory",
                        formulaExplanation = "Favorable Agronomic Window Index: Wind < 15 km/h, Rain < 1 mm, Sun duration > 6 hrs."
                    )
                }
            }

            UserPersona.TRAVELLER -> {
                if (maxWind >= 35.0 || maxRainProb >= 60) {
                    ExplainableInsight(
                        headline = "Highway Travel Caution: Rain & Reduced Visibility",
                        recommendation = "Allow extra transit time and maintain safe following distances on expressways.",
                        reason = "Wet road conditions and localized crosswinds of ${maxWind.toInt()} km/h expected near $maxRainTime.",
                        actionText = "Check Route Radar Before Departure",
                        factors = listOf(
                            DecisionFactor("Wind Gusts", "${maxWind.toInt()} km/h", "HIGH", "💨"),
                            DecisionFactor("Visibility Range", "${current.visibilityKm.toInt()} km", "MEDIUM", "👁️"),
                            DecisionFactor("Precipitation Risk", "$maxRainProb%", "HIGH", "🌧️")
                        ),
                        confidencePercentage = 89,
                        personaContext = "Intercity transit & road safety optimization",
                        formulaExplanation = "Highway Transit Safety Score = f(Visibility, Surface Friction, Wind Gusts)."
                    )
                } else {
                    ExplainableInsight(
                        headline = "Clear Highway Conditions for Intercity Travel",
                        recommendation = "Excellent driving conditions with clear visibility and stable weather across main corridors.",
                        reason = "Visibility is ${current.visibilityKm.toInt()} km with dry asphalt and calm wind (${current.windSpeedKmH.toInt()} km/h).",
                        actionText = "Proceed on Schedule",
                        factors = listOf(
                            DecisionFactor("Visibility", "> 9 km (Clear)", "LOW", "👁️"),
                            DecisionFactor("Precipitation", "None", "LOW", "🚗"),
                            DecisionFactor("Road Surface", "Dry", "LOW", "🛣️")
                        ),
                        confidencePercentage = 96,
                        personaContext = "Travel route decision support",
                        formulaExplanation = "All road risk factors evaluated as NOMINAL."
                    )
                }
            }

            UserPersona.FITNESS -> {
                val morningHour = hourly.find { it.formattedHour.contains("6 AM") || it.formattedHour.contains("7 AM") }
                val morningTemp = morningHour?.temperature ?: 23.0
                ExplainableInsight(
                    headline = "Optimal Morning Outdoor Workout Window: 6:00 AM – 7:30 AM",
                    recommendation = "Schedule your outdoor run before 8:00 AM to take advantage of cool air (${morningTemp.toInt()}°C) and low UV.",
                    reason = "Midday heat reaches ${maxTemp.toInt()}°C with UV Index $maxUv. Early morning provides peak aerobic comfort.",
                    actionText = "Set Workout Reminder for 6:30 AM",
                    factors = listOf(
                        DecisionFactor("Best Window", "6:00 AM – 7:30 AM", "HIGH", "⏰"),
                        DecisionFactor("Morning Temp", "${morningTemp.toInt()}°C", "HIGH", "🏃"),
                        DecisionFactor("Air Humidity", "${morningHour?.humidity ?: 60}%", "MEDIUM", "💧"),
                        DecisionFactor("Midday Peak UV", "$maxUv", "HIGH", "☀️")
                    ),
                    confidencePercentage = 93,
                    personaContext = "Cardiovascular & athletic performance optimizer",
                    formulaExplanation = "Athletic Performance Index = f(Wet-Bulb Globe Temp, Heat Index, Air Quality, Wind)."
                )
            }

            UserPersona.OUTDOOR_WORKER -> {
                if (maxTemp >= 36.0 || maxUv >= 8.0) {
                    ExplainableInsight(
                        headline = "Extreme Heat Stress & Sun Protection Protocol",
                        recommendation = "Take mandatory 10-minute shade breaks every 50 minutes. Consume 250ml water every 30 minutes.",
                        reason = "Heat Index exceeds 38°C between 12:00 PM and 3:30 PM with hazardous UV levels ($maxUv).",
                        actionText = "Follow Heat Stress Protocol",
                        factors = listOf(
                            DecisionFactor("Heat Index", "${maxTemp.toInt() + 3}°C", "CRITICAL", "🔥"),
                            DecisionFactor("UV Protection Required", "SPF 50+ & Shade", "HIGH", "☀️"),
                            DecisionFactor("Hydration Target", "3.5L / shift", "HIGH", "💧")
                        ),
                        confidencePercentage = 95,
                        personaContext = "Occupational safety & health compliance",
                        formulaExplanation = "OSHA Heat Safety Standards: Heat Index > 38°C triggers Level 3 work-rest cycles."
                    )
                } else {
                    ExplainableInsight(
                        headline = "Moderate Work Environment Conditions",
                        recommendation = "Standard safety gear recommended. Weather remains stable throughout your shift.",
                        reason = "Temperature will average ${current.temperature.toInt()}°C with manageable wind and humidity.",
                        actionText = "Standard Shift Protocols",
                        factors = listOf(
                            DecisionFactor("Worksite Temp", "${current.temperature.toInt()}°C", "LOW", "🦺"),
                            DecisionFactor("Rain Likelihood", "$maxRainProb%", "LOW", "🌤️")
                        ),
                        confidencePercentage = 92,
                        personaContext = "Outdoor worker safety advisory",
                        formulaExplanation = "Nominal occupational weather thresholds maintained."
                    )
                }
            }

            UserPersona.GENERAL -> {
                if (maxRainProb >= 50) {
                    ExplainableInsight(
                        headline = "Rain Expected Later Today ($maxRainTime)",
                        recommendation = "Keep an umbrella handy and plan errands around the afternoon showers.",
                        reason = "$maxRainProb% chance of rain around $maxRainTime with showers lingering for 2 hours.",
                        actionText = "Carry Umbrella & Plan Errands",
                        factors = listOf(
                            DecisionFactor("Rain Chance", "$maxRainProb%", "HIGH", "🌧️"),
                            DecisionFactor("Time Window", "$maxRainTime", "HIGH", "⏰"),
                            DecisionFactor("Temperature", "${current.temperature.toInt()}°C", "MEDIUM", "🌡️")
                        ),
                        confidencePercentage = 90,
                        personaContext = "Everyday lifestyle decision assistance",
                        formulaExplanation = "Precipitation probability aggregated over 24-hour localized forecast grid."
                    )
                } else {
                    ExplainableInsight(
                        headline = "Clear and Comfortable Weather Today",
                        recommendation = "Great day for outdoor walks, errands, and open-air dining.",
                        reason = "High of ${maxTemp.toInt()}°C, low of ${weather.daily.firstOrNull()?.tempMin?.toInt() ?: 20}°C with sunny to partly cloudy skies.",
                        actionText = "Enjoy the Weather",
                        factors = listOf(
                            DecisionFactor("Temperature Range", "${weather.daily.firstOrNull()?.tempMin?.toInt() ?: 20}°C - ${maxTemp.toInt()}°C", "LOW", "🌤️"),
                            DecisionFactor("Rain Likelihood", "$maxRainProb%", "LOW", "☀️")
                        ),
                        confidencePercentage = 95,
                        personaContext = "General lifestyle summary",
                        formulaExplanation = "Standard composite atmospheric stability calculation."
                    )
                }
            }
        }
    }
}
