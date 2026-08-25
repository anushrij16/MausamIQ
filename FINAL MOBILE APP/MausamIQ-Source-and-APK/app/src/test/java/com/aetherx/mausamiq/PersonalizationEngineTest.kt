package com.aetherx.mausamiq

import com.aetherx.mausamiq.ai.PersonalizationEngine
import com.aetherx.mausamiq.data.repository.DemoWeatherRepository
import com.aetherx.mausamiq.domain.model.UserPersona
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class PersonalizationEngineTest {

    private val demoRepo = DemoWeatherRepository()

    @Test
    fun testStudentPersona_withRainCommute_generatesCommuteAlert() {
        val weather = demoRepo.createDemoScenario(persona = UserPersona.STUDENT)
        val insight = PersonalizationEngine.generateInsight(
            weather = weather,
            persona = UserPersona.STUDENT,
            returnTime = "17:00"
        )

        assertNotNull(insight)
        assertTrue(insight.headline.contains("Rain", ignoreCase = true) || insight.headline.contains("Commute", ignoreCase = true))
        assertTrue(insight.confidencePercentage >= 80)
        assertEquals(4, insight.factors.size)
    }

    @Test
    fun testFarmerPersona_evaluatesAgriculturalFactors() {
        val weather = demoRepo.createDemoScenario(persona = UserPersona.FARMER)
        val insight = PersonalizationEngine.generateInsight(
            weather = weather,
            persona = UserPersona.FARMER
        )

        assertNotNull(insight)
        assertTrue(insight.actionText.contains("Spraying", ignoreCase = true) || insight.actionText.contains("Harvesting", ignoreCase = true) || insight.actionText.contains("Drainage", ignoreCase = true))
    }

    @Test
    fun testFitnessPersona_providesOptimalWorkoutWindow() {
        val weather = demoRepo.createDemoScenario(persona = UserPersona.FITNESS)
        val insight = PersonalizationEngine.generateInsight(
            weather = weather,
            persona = UserPersona.FITNESS
        )

        assertNotNull(insight)
        assertTrue(insight.headline.contains("Workout", ignoreCase = true) || insight.headline.contains("Morning", ignoreCase = true))
    }
}
