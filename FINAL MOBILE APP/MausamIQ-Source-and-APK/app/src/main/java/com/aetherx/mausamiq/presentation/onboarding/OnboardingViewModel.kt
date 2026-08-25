package com.aetherx.mausamiq.presentation.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aetherx.mausamiq.core.datastore.PreferencesManager
import com.aetherx.mausamiq.data.local.entity.SavedLocationEntity
import com.aetherx.mausamiq.data.local.entity.UserEntity
import com.aetherx.mausamiq.domain.model.LocationItem
import com.aetherx.mausamiq.domain.model.LocationType
import com.aetherx.mausamiq.domain.model.UserPersona
import com.aetherx.mausamiq.domain.repository.IUserRepository
import com.aetherx.mausamiq.domain.repository.IWeatherRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class OnboardingUiState(
    val currentStep: Int = 1,
    val totalSteps: Int = 7,
    // Step 1: Primary Location
    val selectedLocationName: String = "New Delhi",
    val latitude: Double = 28.6139,
    val longitude: Double = 77.2090,
    val searchQuery: String = "",
    val searchResults: List<LocationItem> = emptyList(),
    val isSearching: Boolean = false,
    // Step 2: Persona
    val selectedPersona: UserPersona = UserPersona.STUDENT,
    // Step 3: Interests
    val selectedInterests: Set<String> = setOf("College", "Commute", "Events"),
    // Step 4: Activities
    val selectedActivities: Set<String> = setOf("College commute", "Walking"),
    val customActivity: String = "",
    // Step 5: Schedule
    val departureTime: String = "08:30",
    val returnTime: String = "17:00",
    val workoutTime: String = "06:30",
    // Step 6: Important Locations
    val savedLocations: List<LocationItem> = listOf(
        LocationItem(1, "Home Residence", LocationType.HOME, 28.6139, 77.2090, true),
        LocationItem(2, "Engineering Campus", LocationType.COLLEGE, 28.5450, 77.1926, false),
        LocationItem(3, "Work Tech Park", LocationType.WORK, 28.4595, 77.0266, false)
    ),
    // Step 7: Language
    val selectedLanguage: String = "en",
    val isCompleting: Boolean = false
)

class OnboardingViewModel(
    private val preferencesManager: PreferencesManager,
    private val userRepository: IUserRepository,
    private val weatherRepository: IWeatherRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(OnboardingUiState())
    val uiState: StateFlow<OnboardingUiState> = _uiState.asStateFlow()

    fun nextStep() {
        if (_uiState.value.currentStep < _uiState.value.totalSteps) {
            _uiState.update { it.copy(currentStep = it.currentStep + 1) }
        }
    }

    fun previousStep() {
        if (_uiState.value.currentStep > 1) {
            _uiState.update { it.copy(currentStep = it.currentStep - 1) }
        }
    }

    fun searchLocation(query: String) {
        _uiState.update { it.copy(searchQuery = query, isSearching = true) }
        viewModelScope.launch {
            val results = weatherRepository.searchLocations(query)
            _uiState.update { it.copy(searchResults = results, isSearching = false) }
        }
    }

    fun selectPrimaryLocation(loc: LocationItem) {
        _uiState.update {
            it.copy(
                selectedLocationName = loc.name,
                latitude = loc.latitude,
                longitude = loc.longitude,
                searchQuery = loc.name,
                searchResults = emptyList()
            )
        }
    }

    fun selectPersona(persona: UserPersona) {
        _uiState.update { it.copy(selectedPersona = persona) }
    }

    fun toggleInterest(interest: String) {
        _uiState.update { state ->
            val set = state.selectedInterests.toMutableSet()
            if (set.contains(interest)) set.remove(interest) else set.add(interest)
            state.copy(selectedInterests = set)
        }
    }

    fun toggleActivity(activity: String) {
        _uiState.update { state ->
            val set = state.selectedActivities.toMutableSet()
            if (set.contains(activity)) set.remove(activity) else set.add(activity)
            state.copy(selectedActivities = set)
        }
    }

    fun addCustomActivity(activity: String) {
        if (activity.isNotBlank()) {
            _uiState.update { state ->
                val set = state.selectedActivities.toMutableSet()
                set.add(activity.trim())
                state.copy(selectedActivities = set, customActivity = "")
            }
        }
    }

    fun updateDepartureTime(time: String) = _uiState.update { it.copy(departureTime = time) }
    fun updateReturnTime(time: String) = _uiState.update { it.copy(returnTime = time) }
    fun updateWorkoutTime(time: String) = _uiState.update { it.copy(workoutTime = time) }

    fun addSavedLocation(name: String, type: LocationType, lat: Double, lng: Double) {
        val newLoc = LocationItem(
            id = System.currentTimeMillis(),
            name = name,
            type = type,
            latitude = lat,
            longitude = lng
        )
        _uiState.update { it.copy(savedLocations = it.savedLocations + newLoc) }
    }

    fun removeSavedLocation(loc: LocationItem) {
        _uiState.update { it.copy(savedLocations = it.savedLocations.filter { item -> item.id != loc.id }) }
    }

    fun selectLanguage(langCode: String) {
        _uiState.update { it.copy(selectedLanguage = langCode) }
    }

    fun completeOnboarding(onFinish: () -> Unit) {
        val state = _uiState.value
        viewModelScope.launch {
            _uiState.update { it.copy(isCompleting = true) }

            preferencesManager.setPrimaryLocation(state.selectedLocationName, state.latitude, state.longitude)
            preferencesManager.setPersona(state.selectedPersona.name)
            preferencesManager.setLanguage(state.selectedLanguage)
            preferencesManager.setOnboardingCompleted(true)

            // Save user profile
            userRepository.saveUser(
                UserEntity(
                    email = "demo@mausamiq.ai",
                    fullName = "Explorer",
                    persona = state.selectedPersona.name,
                    interestsCsv = state.selectedInterests.joinToString(","),
                    activitiesCsv = state.selectedActivities.joinToString(","),
                    departureTime = state.departureTime,
                    returnTime = state.returnTime,
                    preferredLanguage = state.selectedLanguage
                )
            )

            // Save locations to database
            state.savedLocations.forEach { loc ->
                userRepository.addSavedLocation(loc)
            }

            _uiState.update { it.copy(isCompleting = false) }
            onFinish()
        }
    }
}
