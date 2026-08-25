package com.aetherx.mausamiq.presentation.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aetherx.mausamiq.core.datastore.PreferencesManager
import com.aetherx.mausamiq.data.local.entity.SavedLocationEntity
import com.aetherx.mausamiq.data.local.entity.UserEntity
import com.aetherx.mausamiq.domain.model.UserPersona
import com.aetherx.mausamiq.domain.repository.IUserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ProfileUiState(
    val user: UserEntity? = null,
    val savedLocations: List<SavedLocationEntity> = emptyList(),
    val persona: UserPersona = UserPersona.STUDENT,
    val isLoading: Boolean = true
)

class ProfileViewModel(
    private val userRepository: IUserRepository,
    private val preferencesManager: PreferencesManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        loadProfile()
    }

    private fun loadProfile() {
        viewModelScope.launch {
            val personaStr = preferencesManager.userPersona.firstOrNull() ?: "STUDENT"
            val persona = UserPersona.fromString(personaStr)
            _uiState.update { it.copy(persona = persona) }

            userRepository.getActiveUser().collect { u ->
                _uiState.update { it.copy(user = u, isLoading = false) }
            }
        }
        viewModelScope.launch {
            userRepository.getSavedLocations().collect { locs ->
                _uiState.update { it.copy(savedLocations = locs) }
            }
        }
    }

    fun updatePersona(newPersona: UserPersona) {
        viewModelScope.launch {
            preferencesManager.setPersona(newPersona.name)
            _uiState.update { it.copy(persona = newPersona) }
        }
    }
}
