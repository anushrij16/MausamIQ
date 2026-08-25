package com.aetherx.mausamiq.presentation.travel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aetherx.mausamiq.domain.model.TravelPlan
import com.aetherx.mausamiq.domain.repository.IWeatherRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class SmartTravelUiState(
    val origin: String = "Delhi NCR",
    val destination: String = "Jaipur Highway",
    val travelDate: String = "Today",
    val departureTime: String = "02:00 PM",
    val isAnalyzing: Boolean = false,
    val travelPlan: TravelPlan? = null
)

class SmartTravelViewModel(
    private val weatherRepository: IWeatherRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SmartTravelUiState())
    val uiState: StateFlow<SmartTravelUiState> = _uiState.asStateFlow()

    init {
        analyzeRoute()
    }

    fun onOriginChange(v: String) = _uiState.update { it.copy(origin = v) }
    fun onDestinationChange(v: String) = _uiState.update { it.copy(destination = v) }
    fun onTimeChange(v: String) = _uiState.update { it.copy(departureTime = v) }

    fun analyzeRoute() {
        val state = _uiState.value
        viewModelScope.launch {
            _uiState.update { it.copy(isAnalyzing = true) }
            kotlinx.coroutines.delay(600) // smooth calculation transition

            val plan = TravelPlan(
                origin = state.origin,
                destination = state.destination,
                travelDate = state.travelDate,
                departureTime = state.departureTime,
                destinationTemp = 31.5,
                destinationRainProbability = 65,
                destinationWindSpeed = 26.0,
                visibilityKm = 4.2,
                weatherRisk = "CAUTION",
                travelAdvisory = "Isolated heavy showers and reduced visibility (< 5 km) anticipated around Jaipur bypass near 4:30 PM.",
                recommendedTravelWindow = "10:00 AM – 01:00 PM (Clear & Dry Highway)"
            )
            _uiState.update { it.copy(isAnalyzing = false, travelPlan = plan) }
        }
    }
}
