package com.aetherx.mausamiq.work

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.aetherx.mausamiq.MausamApplication
import com.aetherx.mausamiq.core.utils.Resource
import kotlinx.coroutines.flow.firstOrNull

class WeatherSyncWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            val app = applicationContext as MausamApplication
            val (name, lat, lng) = app.preferencesManager.primaryLocation.firstOrNull() ?: Triple("New Delhi", 28.6139, 77.2090)
            val isDemo = app.preferencesManager.isDemoMode.firstOrNull() ?: false

            app.weatherRepository.getWeather(
                latitude = lat,
                longitude = lng,
                locationName = name,
                forceRefresh = true,
                isDemoMode = isDemo
            ).firstOrNull()

            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }
}
