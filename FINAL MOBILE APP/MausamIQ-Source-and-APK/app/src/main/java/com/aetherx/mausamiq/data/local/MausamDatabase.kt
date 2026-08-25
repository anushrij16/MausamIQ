package com.aetherx.mausamiq.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.aetherx.mausamiq.data.local.dao.AlertDao
import com.aetherx.mausamiq.data.local.dao.SavedLocationDao
import com.aetherx.mausamiq.data.local.dao.UserDao
import com.aetherx.mausamiq.data.local.dao.WeatherCacheDao
import com.aetherx.mausamiq.data.local.entity.AlertEntity
import com.aetherx.mausamiq.data.local.entity.SavedLocationEntity
import com.aetherx.mausamiq.data.local.entity.UserEntity
import com.aetherx.mausamiq.data.local.entity.WeatherCacheEntity

@Database(
    entities = [
        UserEntity::class,
        SavedLocationEntity::class,
        WeatherCacheEntity::class,
        AlertEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class MausamDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun savedLocationDao(): SavedLocationDao
    abstract fun weatherCacheDao(): WeatherCacheDao
    abstract fun alertDao(): AlertDao

    companion object {
        @Volatile
        private var INSTANCE: MausamDatabase? = null

        fun getInstance(context: Context): MausamDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    MausamDatabase::class.java,
                    "mausamiq_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
