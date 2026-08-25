package com.aetherx.mausamiq.core.utils

object WeatherProbabilityFormatter {

    fun formatRainChance(probability: Int): String {
        return "$probability%"
    }

    fun getProbabilityInterpretation(probability: Int): String {
        return when {
            probability >= 80 -> "Extremely likely precipitation during this window"
            probability >= 60 -> "High likelihood of rain showers"
            probability >= 40 -> "Moderate chance of scattered precipitation"
            probability >= 20 -> "Low probability of isolated drops"
            else -> "Dry weather expected"
        }
    }

    fun getPrecipitationDescription(amountMm: Double): String {
        return when {
            amountMm > 25.0 -> "Torrential downpour ($amountMm mm)"
            amountMm > 10.0 -> "Heavy rainfall ($amountMm mm)"
            amountMm > 2.5 -> "Moderate steady rain ($amountMm mm)"
            amountMm > 0.5 -> "Light drizzle ($amountMm mm)"
            else -> "Negligible accumulation"
        }
    }
}
