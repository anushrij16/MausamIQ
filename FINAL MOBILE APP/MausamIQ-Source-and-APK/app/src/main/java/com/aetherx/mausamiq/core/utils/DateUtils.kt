package com.aetherx.mausamiq.core.utils

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

object DateUtils {

    fun formatHour(isoTime: String): String {
        return try {
            val parser = SimpleDateFormat("yyyy-MM-dd'T'HH:mm", Locale.getDefault())
            val date = parser.parse(isoTime) ?: return isoTime
            val formatter = SimpleDateFormat("h a", Locale.getDefault())
            formatter.format(date)
        } catch (e: Exception) {
            isoTime.takeLast(5)
        }
    }

    fun formatDayName(isoDate: String): String {
        return try {
            val parser = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val date = parser.parse(isoDate) ?: return isoDate
            val formatter = SimpleDateFormat("EEE, MMM d", Locale.getDefault())
            formatter.format(date)
        } catch (e: Exception) {
            isoDate
        }
    }

    fun getDayOfWeekShort(isoDate: String): String {
        return try {
            val parser = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val date = parser.parse(isoDate) ?: return isoDate
            val formatter = SimpleDateFormat("EEE", Locale.getDefault())
            formatter.format(date)
        } catch (e: Exception) {
            isoDate
        }
    }

    fun getCurrentGreeting(): String {
        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        return when (hour) {
            in 5..11 -> "Good Morning"
            in 12..16 -> "Good Afternoon"
            in 17..21 -> "Good Evening"
            else -> "Good Night"
        }
    }

    fun getCurrentTimeFormatted(): String {
        val formatter = SimpleDateFormat("hh:mm a", Locale.getDefault())
        return formatter.format(Date())
    }
}
