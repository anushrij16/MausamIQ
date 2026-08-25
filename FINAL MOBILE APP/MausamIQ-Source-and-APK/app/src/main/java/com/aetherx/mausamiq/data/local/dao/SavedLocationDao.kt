package com.aetherx.mausamiq.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.aetherx.mausamiq.data.local.entity.SavedLocationEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SavedLocationDao {
    @Query("SELECT * FROM saved_locations ORDER BY isPrimary DESC, id ASC")
    fun getAllLocations(): Flow<List<SavedLocationEntity>>

    @Query("SELECT * FROM saved_locations WHERE type = :type LIMIT 1")
    suspend fun getLocationByType(type: String): SavedLocationEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLocation(location: SavedLocationEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLocations(locations: List<SavedLocationEntity>)

    @Delete
    suspend fun deleteLocation(location: SavedLocationEntity)

    @Query("DELETE FROM saved_locations")
    suspend fun clearLocations()
}
