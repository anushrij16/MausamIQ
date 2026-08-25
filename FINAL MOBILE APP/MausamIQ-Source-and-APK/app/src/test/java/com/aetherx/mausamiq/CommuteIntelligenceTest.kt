package com.aetherx.mausamiq

import com.aetherx.mausamiq.ai.CommuteIntelligence
import com.aetherx.mausamiq.data.repository.DemoWeatherRepository
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class CommuteIntelligenceTest {

    private val demoRepo = DemoWeatherRepository()

    @Test
    fun testEvaluateCommute_atPeakRainHour_returnsHighRisk() {
        val weather = demoRepo.createDemoScenario()
        val commute = CommuteIntelligence.evaluateCommute(
            weather = weather,
            originName = "Home",
            destinationName = "College",
            departureTime = "08:30",
            returnTime = "17:00" // 5 PM is peak rain (85%) in demo scenario
        )

        assertNotNull(commute)
        assertEquals("HIGH", commute.riskLevel)
        assertTrue(commute.rainProbability >= 70)
        assertTrue(commute.advice.contains("rain protection", ignoreCase = true) || commute.advice.contains("transit buffer", ignoreCase = true))
    }

    @Test
    fun testEvaluateCommute_preservesRouteEndpoints() {
        val weather = demoRepo.createDemoScenario()
        val commute = CommuteIntelligence.evaluateCommute(
            weather = weather,
            originName = "Hostel A",
            destinationName = "Main Library",
            returnTime = "12:00"
        )

        assertEquals("Hostel A", commute.originName)
        assertEquals("Main Library", commute.destinationName)
    }
}
