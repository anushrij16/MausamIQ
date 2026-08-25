package com.aetherx.mausamiq.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey
    val email: String,
    val fullName: String,
    val phone: String = "",
    val persona: String = "STUDENT",
    val interestsCsv: String = "College,Commute,Events",
    val activitiesCsv: String = "College commute,Walking",
    val departureTime: String = "08:30",
    val returnTime: String = "17:00",
    val preferredLanguage: String = "en"
)
