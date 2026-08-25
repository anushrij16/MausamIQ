package com.aetherx.mausamiq.core.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "mausamiq_preferences")

class PreferencesManager(private val context: Context) {

    companion object {
        private val KEY_IS_LOGGED_IN = booleanPreferencesKey("is_logged_in")
        private val KEY_IS_ONBOARDING_COMPLETED = booleanPreferencesKey("is_onboarding_completed")
        private val KEY_USER_NAME = stringPreferencesKey("user_name")
        private val KEY_USER_EMAIL = stringPreferencesKey("user_email")
        private val KEY_PERSONA = stringPreferencesKey("user_persona")
        private val KEY_LANGUAGE = stringPreferencesKey("app_language")
        private val KEY_THEME_MODE = stringPreferencesKey("theme_mode")
        private val KEY_IS_DEMO_MODE = booleanPreferencesKey("is_demo_mode")
        private val KEY_PRIMARY_LOCATION_NAME = stringPreferencesKey("primary_location_name")
        private val KEY_PRIMARY_LAT = doublePreferencesKey("primary_latitude")
        private val KEY_PRIMARY_LNG = doublePreferencesKey("primary_longitude")
    }

    val isLoggedIn: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[KEY_IS_LOGGED_IN] ?: false
    }

    val isOnboardingCompleted: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[KEY_IS_ONBOARDING_COMPLETED] ?: false
    }

    val userName: Flow<String> = context.dataStore.data.map { prefs ->
        prefs[KEY_USER_NAME] ?: "Guest Explorer"
    }

    val userEmail: Flow<String> = context.dataStore.data.map { prefs ->
        prefs[KEY_USER_EMAIL] ?: "demo@mausamiq.ai"
    }

    val userPersona: Flow<String> = context.dataStore.data.map { prefs ->
        prefs[KEY_PERSONA] ?: "STUDENT"
    }

    val appLanguage: Flow<String> = context.dataStore.data.map { prefs ->
        prefs[KEY_LANGUAGE] ?: "en"
    }

    val themeMode: Flow<String> = context.dataStore.data.map { prefs ->
        prefs[KEY_THEME_MODE] ?: "DARK"
    }

    val isDemoMode: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[KEY_IS_DEMO_MODE] ?: false
    }

    val primaryLocation: Flow<Triple<String, Double, Double>> = context.dataStore.data.map { prefs ->
        val name = prefs[KEY_PRIMARY_LOCATION_NAME] ?: "New Delhi"
        val lat = prefs[KEY_PRIMARY_LAT] ?: 28.6139
        val lng = prefs[KEY_PRIMARY_LNG] ?: 77.2090
        Triple(name, lat, lng)
    }

    suspend fun setLoggedIn(loggedIn: Boolean, name: String = "", email: String = "") {
        context.dataStore.edit { prefs ->
            prefs[KEY_IS_LOGGED_IN] = loggedIn
            if (name.isNotEmpty()) prefs[KEY_USER_NAME] = name
            if (email.isNotEmpty()) prefs[KEY_USER_EMAIL] = email
        }
    }

    suspend fun setOnboardingCompleted(completed: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[KEY_IS_ONBOARDING_COMPLETED] = completed
        }
    }

    suspend fun setPersona(persona: String) {
        context.dataStore.edit { prefs ->
            prefs[KEY_PERSONA] = persona
        }
    }

    suspend fun setLanguage(language: String) {
        context.dataStore.edit { prefs ->
            prefs[KEY_LANGUAGE] = language
        }
    }

    suspend fun setThemeMode(mode: String) {
        context.dataStore.edit { prefs ->
            prefs[KEY_THEME_MODE] = mode
        }
    }

    suspend fun setDemoMode(enabled: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[KEY_IS_DEMO_MODE] = enabled
        }
    }

    suspend fun setPrimaryLocation(name: String, lat: Double, lng: Double) {
        context.dataStore.edit { prefs ->
            prefs[KEY_PRIMARY_LOCATION_NAME] = name
            prefs[KEY_PRIMARY_LAT] = lat
            prefs[KEY_PRIMARY_LNG] = lng
        }
    }

    suspend fun clearSession() {
        context.dataStore.edit { prefs ->
            prefs[KEY_IS_LOGGED_IN] = false
            prefs[KEY_IS_ONBOARDING_COMPLETED] = false
        }
    }
}
