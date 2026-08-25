package com.mausamiq.app.ui

import androidx.lifecycle.ViewModel
import com.mausamiq.app.domain.AppUiState
import com.mausamiq.app.domain.IntelligenceEngine
import com.mausamiq.app.domain.Persona
import com.mausamiq.app.domain.UserProfile
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class AppViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(AppUiState())
    val uiState: StateFlow<AppUiState> = _uiState.asStateFlow()

    fun selectTab(index: Int) = _uiState.update { it.copy(selectedTab = index) }

    fun togglePersona(persona: Persona) = _uiState.update { state ->
        val existing = state.profile.personas
        val next = if (existing.contains(persona) && existing.size > 1) existing - persona else existing + persona
        val nextProfile = state.profile.copy(personas = next)
        state.copy(
            profile = nextProfile,
            recommendation = IntelligenceEngine.recommendation(nextProfile, state.weather, state.collegeWeather),
            risks = IntelligenceEngine.risks(nextProfile, state.weather, state.collegeWeather),
            lastFeedback = null
        )
    }

    fun setOffline(offline: Boolean) = _uiState.update { it.copy(isOffline = offline) }

    fun showExplanation(show: Boolean) = _uiState.update { it.copy(showExplanation = show) }
    fun showTrace(show: Boolean) = _uiState.update { it.copy(showTrace = show) }

    fun submitFeedback(feedback: String) = _uiState.update { state ->
        state.copy(
            lastFeedback = feedback,
            profile = state.profile.copy(feedbackCount = state.profile.feedbackCount + 1)
        )
    }

    fun toggleNotifications(enabled: Boolean) = _uiState.update { it.copy(profile = it.profile.copy(notificationsEnabled = enabled)) }
    fun toggleAdaptive(enabled: Boolean) = _uiState.update { it.copy(profile = it.profile.copy(adaptivePersonalization = enabled)) }
    fun toggleQuietHours(enabled: Boolean) = _uiState.update { it.copy(profile = it.profile.copy(quietHours = enabled)) }

    fun resetProfile() = _uiState.update { state ->
        val reset = UserProfile(name = state.profile.name)
        state.copy(
            profile = reset,
            recommendation = IntelligenceEngine.recommendation(reset, state.weather, state.collegeWeather),
            risks = IntelligenceEngine.risks(reset, state.weather, state.collegeWeather),
            lastFeedback = "Weather profile reset"
        )
    }

    fun refreshDemo() = _uiState.update { state ->
        val nextWeather = state.weather.copy(updatedMinutesAgo = 1)
        val nextCollege = state.collegeWeather.copy(updatedMinutesAgo = 1)
        state.copy(
            weather = nextWeather,
            collegeWeather = nextCollege,
            recommendation = IntelligenceEngine.recommendation(state.profile, nextWeather, nextCollege),
            risks = IntelligenceEngine.risks(state.profile, nextWeather, nextCollege),
            lastFeedback = "Forecast refreshed"
        )
    }
}
