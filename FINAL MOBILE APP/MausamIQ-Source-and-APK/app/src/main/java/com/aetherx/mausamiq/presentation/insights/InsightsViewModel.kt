package com.aetherx.mausamiq.presentation.insights

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aetherx.mausamiq.ai.PersonalizationEngine
import com.aetherx.mausamiq.core.datastore.PreferencesManager
import com.aetherx.mausamiq.core.utils.Resource
import com.aetherx.mausamiq.domain.model.ExplainableInsight
import com.aetherx.mausamiq.domain.model.UserPersona
import com.aetherx.mausamiq.domain.model.WeatherInfo
import com.aetherx.mausamiq.domain.repository.IWeatherRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class InsightsUiState(
    val isLoading: Boolean = true,
    val weather: WeatherInfo? = null,
    val insight: ExplainableInsight? = null,
    val persona: UserPersona = UserPersona.STUDENT,
    val selectedPersonaFilter: UserPersona = UserPersona.STUDENT,
    val showExplainabilitySheet: Boolean = false,
    val errorMessage: String? = null
)

class InsightsViewModel(
    private val weatherRepository: IWeatherRepository,
    private val preferencesManager: PreferencesManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(InsightsUiState())
    val uiState: StateFlow<InsightsUiState> = _uiState.asStateFlow()

    init {
        loadInsights()
    }

    fun loadInsights() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val (locName, lat, lng) = preferencesManager.primaryLocation.firstOrNull() ?: Triple("New Delhi", 28.6139, 77.2090)
            val personaStr = preferencesManager.userPersona.firstOrNull() ?: "STUDENT"
            val persona = UserPersona.fromString(personaStr)
            val isDemo = preferencesManager.isDemoMode.firstOrNull() ?: false

            _uiState.update { it.copy(persona = persona, selectedPersonaFilter = persona) }

            weatherRepository.getWeather(
                latitude = lat,
                longitude = lng,
                locationName = locName,
                forceRefresh = false,
                isDemoMode = isDemo
            ).collect { res ->
                when (res) {
                    is Resource.Success -> {
                        val w = res.data
                        if (w != null) {
                            val insight = PersonalizationEngine.generateInsight(
                                weather = w,
                                persona = _uiState.value.selectedPersonaFilter
                            )
                            _uiState.update {
                                it.copy(isLoading = false, weather = w, insight = insight, errorMessage = null)
                            }
                        }
                    }
                    is Resource.Error -> {
                        _uiState.update { it.copy(isLoading = false, errorMessage = res.message) }
                    }
                    is Resource.Loading -> {}
                }
            }
        }
    }

    fun switchPersonaFilter(persona: UserPersona) {
        _uiState.update { it.copy(selectedPersonaFilter = persona) }
        val w = _uiState.value.weather
        if (w != null) {
            val updatedInsight = PersonalizationEngine.generateInsight(weather = w, persona = persona)
            _uiState.update { it.copy(insight = updatedInsight) }
        }
    }

    fun toggleExplainabilitySheet(show: Boolean) {
        _uiState.update { it.copy(showExplainabilitySheet = show) }
    }
}
