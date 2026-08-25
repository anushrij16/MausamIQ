package com.aetherx.mausamiq

import android.app.Application
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.aetherx.mausamiq.core.datastore.PreferencesManager
import com.aetherx.mausamiq.data.local.MausamDatabase
import com.aetherx.mausamiq.data.remote.RetrofitClient
import com.aetherx.mausamiq.data.repository.AlertRepositoryImpl
import com.aetherx.mausamiq.data.repository.DemoWeatherRepository
import com.aetherx.mausamiq.data.repository.UserRepositoryImpl
import com.aetherx.mausamiq.data.repository.WeatherRepositoryImpl
import com.aetherx.mausamiq.domain.repository.IAlertRepository
import com.aetherx.mausamiq.domain.repository.IUserRepository
import com.aetherx.mausamiq.domain.repository.IWeatherRepository
import com.aetherx.mausamiq.work.WeatherSyncWorker
import java.util.concurrent.TimeUnit

class MausamApplication : Application() {

    lateinit var database: MausamDatabase
        private set

    lateinit var preferencesManager: PreferencesManager
        private set

    lateinit var weatherRepository: IWeatherRepository
        private set

    lateinit var userRepository: IUserRepository
        private set

    lateinit var alertRepository: IAlertRepository
        private set

    override fun onCreate() {
        super.onCreate()

        database = MausamDatabase.getInstance(this)
        preferencesManager = PreferencesManager(this)

        val demoRepo = DemoWeatherRepository()
        weatherRepository = WeatherRepositoryImpl(
            api = RetrofitClient.openMeteoApi,
            cacheDao = database.weatherCacheDao(),
            demoRepository = demoRepo
        )

        userRepository = UserRepositoryImpl(
            userDao = database.userDao(),
            locationDao = database.savedLocationDao()
        )

        alertRepository = AlertRepositoryImpl(
            alertDao = database.alertDao()
        )

        schedulePeriodicSync()
    }

    private fun schedulePeriodicSync() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .setRequiresBatteryNotLow(true)
            .build()

        val syncRequest = PeriodicWorkRequestBuilder<WeatherSyncWorker>(
            repeatInterval = 3,
            repeatIntervalTimeUnit = TimeUnit.HOURS
        )
            .setConstraints(constraints)
            .build()

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "MausamWeatherSync",
            ExistingPeriodicWorkPolicy.KEEP,
            syncRequest
        )
    }
}
