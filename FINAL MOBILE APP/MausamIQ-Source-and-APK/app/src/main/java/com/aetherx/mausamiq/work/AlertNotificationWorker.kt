package com.aetherx.mausamiq.work

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.aetherx.mausamiq.MausamApplication
import kotlinx.coroutines.flow.firstOrNull

class AlertNotificationWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    companion object {
        const val CHANNEL_ID = "mausamiq_severe_alerts"
        const val CHANNEL_NAME = "MausamIQ Weather Alerts"
    }

    override suspend fun doWork(): Result {
        return try {
            val app = applicationContext as MausamApplication
            val unreadCount = app.alertRepository.getUnreadCount().firstOrNull() ?: 0

            if (unreadCount > 0) {
                createNotificationChannel()
                val notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                val notification = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
                    .setSmallIcon(android.R.drawable.ic_dialog_alert)
                    .setContentTitle("MausamIQ Weather Advisory")
                    .setContentText("You have $unreadCount active severe weather recommendations for your commute.")
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setAutoCancel(true)
                    .build()

                notificationManager.notify(101, notification)
            }
            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Severe weather warnings and commute advisories"
            }
            val notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}
