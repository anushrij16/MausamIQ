package com.aetherx.mausamiq.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.aetherx.mausamiq.data.local.entity.AlertEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AlertDao {
    @Query("SELECT * FROM weather_alerts ORDER BY timestampMillis DESC")
    fun getAllAlerts(): Flow<List<AlertEntity>>

    @Query("SELECT COUNT(*) FROM weather_alerts WHERE isRead = 0")
    fun getUnreadAlertCount(): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAlerts(alerts: List<AlertEntity>)

    @Query("UPDATE weather_alerts SET isRead = 1 WHERE id = :alertId")
    suspend fun markAsRead(alertId: String)

    @Query("DELETE FROM weather_alerts")
    suspend fun clearAlerts()
}
