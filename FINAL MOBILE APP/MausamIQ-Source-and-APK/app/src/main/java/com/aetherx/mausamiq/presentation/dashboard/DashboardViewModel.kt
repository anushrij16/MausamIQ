package com.aetherx.mausamiq.presentation.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aetherx.mausamiq.ai.CommuteIntelligence
import com.aetherx.mausamiq.ai.PersonalizationEngine
import com.aetherx.mausamiq.core.datastore.PreferencesManager
import com.aetherx.mausamiq.core.utils.Resource
import com.aetherx.mausamiq.domain.model.CommutePlan
import com.aetherx.mausamiq.domain.model.ExplainableInsight
import com.aetherx.mausamiq.domain.model.LocationItem
import com.aetherx.mausamiq.domain.model.UserPersona
import com.aetherx.mausamiq.domain.model.WeatherInfo
import com.aetherx.mausamiq.domain.repository.IAlertRepository
import com.aetherx.mausamiq.domain.repository.IUserRepository
import com.aetherx.mausamiq.domain.repository.IWeatherRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class DashboardUiState(
    val isLoading: Boolean = true,
    val weather: WeatherInfo? = null,
    val insight: ExplainableInsight? = null,
    val commutePlan: CommutePlan? = null,
    val userName: String = "Explorer",
    val persona: UserPersona = UserPersona.STUDENT,
    val isDemoMode: Boolean = false,
    val unreadAlertCount: Int = 0,
    val errorMessage: String? = null,
    val showExplainabilityModal: Boolean = false,
    val showLocationPicker: Boolean = false,
    val savedLocations: List<LocationItem> = emptyList(),
    val isRefreshing: Boolean = false
)

class DashboardViewModel(
    private val weatherRepository: IWeatherRepository,
    private val userRepository: IUserRepository,
    private val alertRepository: IAlertRepository,
    private val preferencesManager: PreferencesManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    init {
        loadDashboardData()
        observeAlerts()
    }

    fun loadDashboardData(forceRefresh: Boolean = false) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = !forceRefresh, isRefreshing = forceRefresh) }

            val (locName, lat, lng) = preferencesManager.primaryLocation.firstOrNull() ?: Triple("New Delhi", 28.6139, 77.2090)
            val personaStr = preferencesManager.userPersona.firstOrNull() ?: "STUDENT"
            val persona = UserPersona.fromString(personaStr)
            val userName = preferencesManager.userName.firstOrNull() ?: "Explorer"
            val isDemo = preferencesManager.isDemoMode.firstOrNull() ?: false

            _uiState.update { it.copy(userName = userName, persona = persona, isDemoMode = isDemo) }

            weatherRepository.getWeather(
                latitude = lat,
                longitude = lng,
                locationName = locName,
                forceRefresh = forceRefresh,
                isDemoMode = isDemo
            ).collect { res ->
                when (res) {
                    is Resource.Success -> {
                        val w = res.data
                        if (w != null) {
                            val insight = PersonalizationEngine.generateInsight(
                                weather = w,
                                persona = persona,
                                userName = userName
                            )
                            val commute = CommuteIntelligence.evaluateCommute(
                                weather = w,
                                originName = "Home Residence",
                                destinationName = if (persona == UserPersona.STUDENT) "College Campus" else "Work Office"
                            )
                            _uiState.update {
                                it.copy(
                                    isLoading = false,
                                    isRefreshing = false,
                                    weather = w,
                                    insight = insight,
                                    commutePlan = commute,
                                    errorMessage = null
                                )
                            }
                        }
                    }
                    is Resource.Error -> {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                isRefreshing = false,
                                errorMessage = res.message
                            )
                        }
                    }
                    is Resource.Loading -> {
                        // Keep loading state
                    }
                }
            }
        }
    }

    private fun observeAlerts() {
        viewModelScope.launch {
            alertRepository.getUnreadCount().collect { count ->
                _uiState.update { it.copy(unreadAlertCount = count) }
            }
        }
    }

    fun toggleExplainabilityModal(show: Boolean) {
        _uiState.update { it.copy(showExplainabilityModal = show) }
    }

    fun toggleLocationPicker(show: Boolean) {
        _uiState.update { it.copy(showLocationPicker = show) }
        if (show) {
            viewModelScope.launch {
                userRepository.getSavedLocations().collect { entities ->
                    val items = entities.map {
                        LocationItem(it.id, it.name, com.aetherx.mausamiq.domain.model.LocationType.fromString(it.type), it.latitude, it.longitude, it.isPrimary)
                    }
                    _uiState.update { it.copy(savedLocations = items) }
                }
            }
        }
    }

    fun selectLocation(loc: LocationItem) {
        viewModelScope.launch {
            preferencesManager.setPrimaryLocation(loc.name, loc.latitude, loc.longitude)
            toggleLocationPicker(false)
            loadDashboardData(forceRefresh = true)
        }
    }
}
