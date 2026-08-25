package com.mausamiq.app

import com.mausamiq.app.domain.IntelligenceEngine
import com.mausamiq.app.domain.Persona
import com.mausamiq.app.domain.UserProfile
import com.mausamiq.app.domain.WeatherSnapshot
import org.junit.Assert.assertTrue
import org.junit.Test

class IntelligenceEngineTest {
    private val weather = WeatherSnapshot("Chennai", 29, 31, "Partly cloudy", 62, 78, 18, 7.0, 8)
    private val college = WeatherSnapshot("Tambaram", 27, 29, "Heavy rain likely", 75, 84, 22, 5.0, 8)

    @Test
    fun farmerContextPrioritizesFarmWeather() {
        val profile = UserProfile(personas = setOf(Persona.FARMER))
        val recommendation = IntelligenceEngine.recommendation(profile, weather, college)
        assertTrue(recommendation.title.contains("farm", ignoreCase = true))
        assertTrue(IntelligenceEngine.prioritizedSections(profile).first().contains("Rainfall"))
    }

    @Test
    fun travellerContextDoesNotClaimRouteData() {
        val profile = UserProfile(personas = setOf(Persona.TRAVELLER))
        val recommendation = IntelligenceEngine.recommendation(profile, weather, college)
        assertTrue(recommendation.whereText.contains("unavailable", ignoreCase = true))
    }

    @Test
    fun studentContextElevatesTravelRisk() {
        val profile = UserProfile(personas = setOf(Persona.STUDENT))
        val risks = IntelligenceEngine.risks(profile, weather, college)
        assertTrue(risks.travel == "HIGH")
    }
}
