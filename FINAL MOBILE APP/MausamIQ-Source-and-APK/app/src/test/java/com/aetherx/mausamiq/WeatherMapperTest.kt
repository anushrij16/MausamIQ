package com.aetherx.mausamiq

import com.aetherx.mausamiq.data.remote.dto.CurrentWeatherDto
import com.aetherx.mausamiq.data.remote.dto.DailyDto
import com.aetherx.mausamiq.data.remote.dto.HourlyDto
import com.aetherx.mausamiq.data.remote.dto.OpenMeteoResponseDto
import com.aetherx.mausamiq.data.repository.WeatherMapper
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test

class WeatherMapperTest {

    @Test
    fun testMapDtoToDomain_correctlyExtractsCurrentAndHourly() {
        val dto = OpenMeteoResponseDto(
            latitude = 28.61,
            longitude = 77.20,
            current = CurrentWeatherDto(
                time = "2026-08-25T10:00",
                temperature = 28.5,
                apparentTemperature = 30.2,
                relativeHumidity = 65,
                weatherCode = 0,
                windSpeed = 15.0
            ),
            hourly = HourlyDto(
                time = listOf("2026-08-25T10:00", "2026-08-25T11:00"),
                temperatures = listOf(28.5, 29.5),
                humidities = listOf(65, 60),
                precipitationProbabilities = listOf(10, 15),
                precipitations = listOf(0.0, 0.0),
                weatherCodes = listOf(0, 1),
                windSpeeds = listOf(15.0, 16.0),
                visibilities = listOf(10000.0, 10000.0),
                uvIndices = listOf(6.0, 7.5)
            ),
            daily = DailyDto(
                time = listOf("2026-08-25"),
                weatherCodes = listOf(0),
                maxTemperatures = listOf(32.0),
                minTemperatures = listOf(22.0),
                sunrises = listOf("2026-08-25T05:55"),
                sunsets = listOf("2026-08-25T18:50")
            )
        )

        val domain = WeatherMapper.mapDtoToDomain(dto, "Delhi")
        assertNotNull(domain)
        assertEquals("Delhi", domain.locationName)
        assertEquals(28.5, domain.current.temperature, 0.01)
        assertEquals(2, domain.hourly.size)
        assertEquals(1, domain.daily.size)
        assertEquals("Clear Sky", domain.current.conditionName)
    }
}
