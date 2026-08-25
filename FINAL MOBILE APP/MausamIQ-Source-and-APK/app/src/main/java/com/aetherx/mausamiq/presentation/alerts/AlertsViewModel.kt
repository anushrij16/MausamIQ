package com.aetherx.mausamiq.presentation.alerts

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aetherx.mausamiq.core.datastore.PreferencesManager
import com.aetherx.mausamiq.domain.model.AlertSeverity
import com.aetherx.mausamiq.domain.model.WeatherAlert
import com.aetherx.mausamiq.domain.repository.IAlertRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AlertsUiState(
    val alerts: List<WeatherAlert> = emptyList(),
    val filteredAlerts: List<WeatherAlert> = emptyList(),
    val selectedFilter: String = "ALL", // "ALL", "CRITICAL", "HIGH", "MODERATE"
    val isLoading: Boolean = true
)

class AlertsViewModel(
    private val alertRepository: IAlertRepository,
    private val preferencesManager: PreferencesManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(AlertsUiState())
    val uiState: StateFlow<AlertsUiState> = _uiState.asStateFlow()

    init {
        loadAlerts()
    }

    fun loadAlerts() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val isDemo = preferencesManager.isDemoMode.firstOrNull() ?: false
            alertRepository.refreshAlerts(isDemo)

            alertRepository.getAlerts().collect { list ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        alerts = list,
                        filteredAlerts = filterAlerts(list, it.selectedFilter)
                    )
                }
            }
        }
    }

    fun filterBySeverity(severity: String) {
        _uiState.update {
            it.copy(
                selectedFilter = severity,
                filteredAlerts = filterAlerts(it.alerts, severity)
            )
        }
    }

    fun markAsRead(alertId: String) {
        viewModelScope.launch {
            alertRepository.markAlertAsRead(alertId)
        }
    }

    private fun filterAlerts(list: List<WeatherAlert>, filter: String): List<WeatherAlert> {
        return if (filter == "ALL") {
            list
        } else {
            list.filter { it.severity.name.equals(filter, ignoreCase = true) }
        }
    }
}
