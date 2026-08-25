package com.aetherx.mausamiq.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "saved_locations")
data class SavedLocationEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val type: String, // "HOME", "COLLEGE", "WORK", "FARM", "OTHER"
    val latitude: Double,
    val longitude: Double,
    val isPrimary: Boolean = false
)
