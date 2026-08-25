package com.aetherx.mausamiq.data.repository

import com.aetherx.mausamiq.data.local.dao.AlertDao
import com.aetherx.mausamiq.data.local.entity.AlertEntity
import com.aetherx.mausamiq.domain.model.AlertCategory
import com.aetherx.mausamiq.domain.model.AlertSeverity
import com.aetherx.mausamiq.domain.model.WeatherAlert
import com.aetherx.mausamiq.domain.repository.IAlertRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class AlertRepositoryImpl(
    private val alertDao: AlertDao
) : IAlertRepository {

    override fun getAlerts(): Flow<List<WeatherAlert>> = alertDao.getAllAlerts().map { list ->
        list.map { entity ->
            WeatherAlert(
                id = entity.id,
                title = entity.title,
                severity = AlertSeverity.valueOf(entity.severity),
                category = AlertCategory.valueOf(entity.category),
                locationName = entity.locationName,
                timeWindow = entity.timeWindow,
                description = entity.description,
                recommendedAction = entity.recommendedAction,
                whyItMatters = entity.whyItMatters,
                isRead = entity.isRead,
                timestampMillis = entity.timestampMillis
            )
        }
    }

    override fun getUnreadCount(): Flow<Int> = alertDao.getUnreadAlertCount()

    override suspend fun markAlertAsRead(alertId: String) {
        alertDao.markAsRead(alertId)
    }

    override suspend fun refreshAlerts(isDemoMode: Boolean) {
        // Seed default high-impact alerts for demonstration
        val sampleAlerts = listOf(
            AlertEntity(
                id = "alert_rain_01",
                title = "Heavy Rain & Waterlogging Risk",
                severity = "HIGH",
                category = "HEAVY_RAIN",
                locationName = "Campus Transit Route",
                timeWindow = "04:30 PM - 06:30 PM",
                description = "Isolated torrential downpours exceeding 25 mm/hr expected along the western expressway during evening peak commute.",
                recommendedAction = "Depart 30 minutes early or equip rain gear before 4:00 PM.",
                whyItMatters = "Directly coincides with your scheduled 5:00 PM return commute."
            ),
            AlertEntity(
                id = "alert_wind_02",
                title = "Strong Crosswinds Warning",
                severity = "MODERATE",
                category = "HIGH_WIND",
                locationName = "Highway NH-44",
                timeWindow = "02:00 PM - 08:00 PM",
                description = "Gusty crosswinds up to 48 km/h. High-profile vehicles and two-wheelers advised caution on elevated flyovers.",
                recommendedAction = "Reduce speed and maintain larger braking distance.",
                whyItMatters = "High impact on motorcycle and cycling commutes."
            ),
            AlertEntity(
                id = "alert_uv_03",
                title = "High UV Index Alert (Level 8)",
                severity = "INFO",
                category = "EXTREME_HEAT",
                locationName = "City Center",
                timeWindow = "11:30 AM - 03:30 PM",
                description = "Very high UV radiation level reaching peak score of 8.2. Sun protection strongly recommended.",
                recommendedAction = "Apply SPF 30+ sunscreen and wear UV-protective eyewear.",
                whyItMatters = "Outdoor workouts or field activities risk sun glare and burns."
            )
        )
        alertDao.insertAlerts(sampleAlerts)
    }
}
