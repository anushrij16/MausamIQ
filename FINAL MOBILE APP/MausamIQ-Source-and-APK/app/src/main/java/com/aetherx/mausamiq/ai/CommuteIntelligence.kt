package com.aetherx.mausamiq.ai

import com.aetherx.mausamiq.core.utils.WeatherProbabilityFormatter
import com.aetherx.mausamiq.domain.model.CommutePlan
import com.aetherx.mausamiq.domain.model.HourlyWeather
import com.aetherx.mausamiq.domain.model.WeatherInfo

object CommuteIntelligence {

    fun evaluateCommute(
        weather: WeatherInfo,
        originName: String = "Home Residence",
        destinationName: String = "College Campus",
        departureTime: String = "08:30",
        returnTime: String = "17:00"
    ): CommutePlan {
        val hourly = weather.hourly

        // Parse return hour (e.g., "17:00" -> 17)
        val returnHourInt = try {
            returnTime.split(":").first().toInt()
        } catch (e: Exception) {
            17
        }

        // Find forecast around commute return time (e.g. 5 PM)
        val commuteHourly = hourly.find {
            val isoHour = it.isoTime.substringAfter("T").take(2).toIntOrNull()
            isoHour == returnHourInt
        } ?: hourly.firstOrNull()

        val rainProb = commuteHourly?.precipitationProbability ?: 25
        val rainMm = commuteHourly?.precipitationMm ?: 0.0
        val condition = commuteHourly?.weatherCode?.let {
            com.aetherx.mausamiq.core.designsystem.components.getWeatherConditionName(it)
        } ?: "Partly Cloudy"

        val riskLevel = when {
            rainProb >= 70 || rainMm >= 5.0 -> "HIGH"
            rainProb >= 40 -> "MODERATE"
            else -> "LOW"
        }

        val advice = when (riskLevel) {
            "HIGH" -> "Heavy rain expected during your $returnTime return journey ($rainProb% chance, $rainMm mm). Carry rain protection and allow 20 minutes extra transit buffer."
            "MODERATE" -> "Scattered showers possible near $returnTime (${WeatherProbabilityFormatter.formatRainChance(rainProb)}). Pack a folding umbrella in your backpack."
            else -> "Dry road conditions and smooth commute expected around $returnTime."
        }

        return CommutePlan(
            originName = originName,
            destinationName = destinationName,
            departureTime = departureTime,
            estimatedArrival = returnTime,
            rainProbability = rainProb,
            expectedCondition = condition,
            temperature = commuteHourly?.temperature ?: weather.current.temperature,
            riskLevel = riskLevel,
            advice = advice,
            departureLocationLat = weather.latitude,
            departureLocationLng = weather.longitude
        )
    }
}
