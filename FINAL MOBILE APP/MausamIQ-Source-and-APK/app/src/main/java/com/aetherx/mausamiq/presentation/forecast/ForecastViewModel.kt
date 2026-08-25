package com.aetherx.mausamiq.presentation.forecast

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aetherx.mausamiq.core.datastore.PreferencesManager
import com.aetherx.mausamiq.core.utils.Resource
import com.aetherx.mausamiq.domain.model.WeatherInfo
import com.aetherx.mausamiq.domain.repository.IWeatherRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ForecastUiState(
    val isLoading: Boolean = true,
    val selectedTab: Int = 0, // 0 = Hourly, 1 = Daily
    val weather: WeatherInfo? = null,
    val errorMessage: String? = null
)

class ForecastViewModel(
    private val weatherRepository: IWeatherRepository,
    private val preferencesManager: PreferencesManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(ForecastUiState())
    val uiState: StateFlow<ForecastUiState> = _uiState.asStateFlow()

    init {
        loadForecast()
    }

    fun selectTab(tabIndex: Int) {
        _uiState.update { it.copy(selectedTab = tabIndex) }
    }

    fun loadForecast() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val (locName, lat, lng) = preferencesManager.primaryLocation.firstOrNull() ?: Triple("New Delhi", 28.6139, 77.2090)
            val isDemo = preferencesManager.isDemoMode.firstOrNull() ?: false

            weatherRepository.getWeather(
                latitude = lat,
                longitude = lng,
                locationName = locName,
                forceRefresh = false,
                isDemoMode = isDemo
            ).collect { res ->
                when (res) {
                    is Resource.Success -> {
                        _uiState.update {
                            it.copy(isLoading = false, weather = res.data, errorMessage = null)
                        }
                    }
                    is Resource.Error -> {
                        _uiState.update {
                            it.copy(isLoading = false, errorMessage = res.message)
                        }
                    }
                    is Resource.Loading -> {}
                }
            }
        }
    }
}
