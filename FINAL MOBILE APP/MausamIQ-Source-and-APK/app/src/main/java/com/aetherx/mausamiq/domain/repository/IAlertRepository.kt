package com.aetherx.mausamiq.domain.repository

import com.aetherx.mausamiq.domain.model.WeatherAlert
import kotlinx.coroutines.flow.Flow

interface IAlertRepository {
    fun getAlerts(): Flow<List<WeatherAlert>>
    fun getUnreadCount(): Flow<Int>
    suspend fun markAlertAsRead(alertId: String)
    suspend fun refreshAlerts(isDemoMode: Boolean = false)
}
