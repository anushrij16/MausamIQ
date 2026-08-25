package com.aetherx.mausamiq.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "weather_alerts")
data class AlertEntity(
    @PrimaryKey
    val id: String,
    val title: String,
    val severity: String, // "CRITICAL", "HIGH", "MODERATE", "INFO"
    val category: String, // "RAIN", "HEAT", "WIND", "STORM", "VISIBILITY"
    val locationName: String,
    val timeWindow: String,
    val description: String,
    val recommendedAction: String,
    val whyItMatters: String,
    val isRead: Boolean = false,
    val timestampMillis: Long = System.currentTimeMillis()
)
