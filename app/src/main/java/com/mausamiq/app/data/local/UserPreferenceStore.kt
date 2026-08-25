package com.mausamiq.app.data.local

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.mausamPreferences by preferencesDataStore(name = "mausamiq_preferences")

class UserPreferenceStore(private val context: Context) {
    private object Keys {
        val adaptive = booleanPreferencesKey("adaptive_personalization")
        val notifications = booleanPreferencesKey("notifications_enabled")
        val quietHours = booleanPreferencesKey("quiet_hours")
        val demoMode = booleanPreferencesKey("demo_mode")
    }

    val adaptive: Flow<Boolean> = context.mausamPreferences.data.map { it[Keys.adaptive] ?: true }
    val notifications: Flow<Boolean> = context.mausamPreferences.data.map { it[Keys.notifications] ?: true }
    val quietHours: Flow<Boolean> = context.mausamPreferences.data.map { it[Keys.quietHours] ?: false }
    val demoMode: Flow<Boolean> = context.mausamPreferences.data.map { it[Keys.demoMode] ?: true }
}
