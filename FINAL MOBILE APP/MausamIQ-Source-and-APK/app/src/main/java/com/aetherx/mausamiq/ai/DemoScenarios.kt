package com.aetherx.mausamiq.ai

import com.aetherx.mausamiq.domain.model.UserPersona

data class SihDemoScenario(
    val id: String,
    val title: String,
    val persona: UserPersona,
    val targetCity: String,
    val scenarioDescription: String,
    val weatherHighlight: String,
    val keyAction: String
)

object DemoScenarios {

    val allScenarios = listOf(
        SihDemoScenario(
            id = "student_monsoon",
            title = "Student: Monsoon Commute Alert",
            persona = UserPersona.STUDENT,
            targetCity = "Chennai / Delhi",
            scenarioDescription = "Sudden high precipitation spike (85%) during 5:00 PM college return commute.",
            weatherHighlight = "85% Rain Probability at 5 PM • 9.8mm Downpour",
            keyAction = "Pack umbrella & waterproof laptop bag before leaving campus."
        ),
        SihDemoScenario(
            id = "farmer_harvest",
            title = "Farmer: 48h Rain Window",
            persona = UserPersona.FARMER,
            targetCity = "Punjab / Vidarbha",
            scenarioDescription = "Upcoming 3-day precipitation pattern determining fertilizer spraying & harvest window.",
            weatherHighlight = "Clear window for next 36h • Heavy rain arriving Thursday",
            keyAction = "Complete pesticide spraying today; clear farm drainage."
        ),
        SihDemoScenario(
            id = "fitness_runner",
            title = "Fitness: Optimal Morning Run",
            persona = UserPersona.FITNESS,
            targetCity = "Bengaluru / Pune",
            scenarioDescription = "Crisp 21°C morning transitioning into 34°C high UV midday.",
            weatherHighlight = "Best aerobic window: 6:00 AM - 7:30 AM (21°C, UV 1.2)",
            keyAction = "Schedule run for 6:30 AM before heat index climbs."
        ),
        SihDemoScenario(
            id = "traveller_highway",
            title = "Traveller: Expressway Fog & Wind",
            persona = UserPersona.TRAVELLER,
            targetCity = "Mumbai-Pune Expressway",
            scenarioDescription = "Localized crosswinds (45 km/h) & reduced visibility on ghat sections.",
            weatherHighlight = "Crosswinds 45 km/h • Visibility 3.5 km",
            keyAction = "Check route radar; allow 30 min buffer for expressway travel."
        )
    )
}
