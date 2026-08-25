package com.aetherx.mausamiq.domain.model

data class DecisionFactor(
    val label: String,
    val value: String,
    val importance: String, // "HIGH", "MEDIUM", "LOW"
    val iconEmoji: String
)

data class ExplainableInsight(
    val headline: String,
    val recommendation: String,
    val reason: String,
    val actionText: String,
    val factors: List<DecisionFactor>,
    val confidencePercentage: Int,
    val personaContext: String,
    val formulaExplanation: String
)

data class CommutePlan(
    val originName: String,
    val destinationName: String,
    val departureTime: String,
    val estimatedArrival: String,
    val rainProbability: Int,
    val expectedCondition: String,
    val temperature: Double,
    val riskLevel: String, // "LOW", "MODERATE", "HIGH"
    val advice: String,
    val departureLocationLat: Double,
    val departureLocationLng: Double
)

data class TravelPlan(
    val origin: String,
    val destination: String,
    val travelDate: String,
    val departureTime: String,
    val destinationTemp: Double,
    val destinationRainProbability: Int,
    val destinationWindSpeed: Double,
    val visibilityKm: Double,
    val weatherRisk: String, // "SAFE", "CAUTION", "SEVERE"
    val travelAdvisory: String,
    val recommendedTravelWindow: String
)
