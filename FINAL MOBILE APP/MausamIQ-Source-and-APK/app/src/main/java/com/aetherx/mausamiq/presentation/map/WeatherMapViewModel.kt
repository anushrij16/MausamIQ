package com.aetherx.mausamiq.presentation.map

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aetherx.mausamiq.core.datastore.PreferencesManager
import com.aetherx.mausamiq.domain.model.LocationItem
import com.aetherx.mausamiq.domain.model.LocationType
import com.aetherx.mausamiq.domain.repository.IUserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class MapLayerType(val label: String, val iconEmoji: String) {
    RAIN_RADAR("Precipitation", "🌧️"),
    TEMPERATURE("Temperature", "🌡️"),
    WIND_STREAM("Wind Stream", "💨"),
    SEVERE_ALERTS("Alerts", "⚠️")
}

data class WeatherStationMarker(
    val id: String,
    val name: String,
    val temp: Double,
    val rainProb: Int,
    val windKmH: Double,
    val xRatio: Float,
    val yRatio: Float
)

data class WeatherMapUiState(
    val selectedLayer: MapLayerType = MapLayerType.RAIN_RADAR,
    val currentLocationName: String = "New Delhi",
    val latitude: Double = 28.6139,
    val longitude: Double = 77.2090,
    val stations: List<WeatherStationMarker> = emptyList(),
    val selectedStation: WeatherStationMarker? = null,
    val isPlayingRadar: Boolean = true
)

class WeatherMapViewModel(
    private val preferencesManager: PreferencesManager,
    private val userRepository: IUserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(WeatherMapUiState())
    val uiState: StateFlow<WeatherMapUiState> = _uiState.asStateFlow()

    init {
        loadMapData()
    }

    private fun loadMapData() {
        viewModelScope.launch {
            val (name, lat, lng) = preferencesManager.primaryLocation.firstOrNull() ?: Triple("New Delhi", 28.6139, 77.2090)
            val markers = listOf(
                WeatherStationMarker("st_1", name, 29.4, 75, 14.5, 0.5f, 0.45f),
                WeatherStationMarker("st_2", "North Transit Hub", 28.1, 80, 22.0, 0.42f, 0.28f),
                WeatherStationMarker("st_3", "East Agro Station", 31.2, 45, 12.0, 0.72f, 0.52f),
                WeatherStationMarker("st_4", "South Campus Gate", 29.0, 65, 16.0, 0.48f, 0.68f),
                WeatherStationMarker("st_5", "West Expressway Toll", 27.8, 90, 32.0, 0.25f, 0.58f)
            )
            _uiState.update {
                it.copy(
                    currentLocationName = name,
                    latitude = lat,
                    longitude = lng,
                    stations = markers,
                    selectedStation = markers.firstOrNull()
                )
            }
        }
    }

    fun selectLayer(layer: MapLayerType) {
        _uiState.update { it.copy(selectedLayer = layer) }
    }

    fun selectStation(station: WeatherStationMarker) {
        _uiState.update { it.copy(selectedStation = station) }
    }

    fun toggleRadarPlayback() {
        _uiState.update { it.copy(isPlayingRadar = !it.isPlayingRadar) }
    }
}
