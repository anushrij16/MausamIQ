package com.aetherx.mausamiq.presentation.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aetherx.mausamiq.core.datastore.PreferencesManager
import com.aetherx.mausamiq.domain.repository.IUserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class SettingsUiState(
    val language: String = "en",
    val themeMode: String = "DARK",
    val isDemoMode: Boolean = false,
    val showAboutDialog: Boolean = false
)

class SettingsViewModel(
    private val preferencesManager: PreferencesManager,
    private val userRepository: IUserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
        loadSettings()
    }

    private fun loadSettings() {
        viewModelScope.launch {
            val lang = preferencesManager.appLanguage.firstOrNull() ?: "en"
            val theme = preferencesManager.themeMode.firstOrNull() ?: "DARK"
            val demo = preferencesManager.isDemoMode.firstOrNull() ?: false
            _uiState.update {
                it.copy(language = lang, themeMode = theme, isDemoMode = demo)
            }
        }
    }

    fun setLanguage(lang: String) {
        viewModelScope.launch {
            preferencesManager.setLanguage(lang)
            _uiState.update { it.copy(language = lang) }
        }
    }

    fun setThemeMode(theme: String) {
        viewModelScope.launch {
            preferencesManager.setThemeMode(theme)
            _uiState.update { it.copy(themeMode = theme) }
        }
    }

    fun toggleDemoMode(enabled: Boolean) {
        viewModelScope.launch {
            preferencesManager.setDemoMode(enabled)
            _uiState.update { it.copy(isDemoMode = enabled) }
        }
    }

    fun toggleAboutDialog(show: Boolean) {
        _uiState.update { it.copy(showAboutDialog = show) }
    }

    fun logout(onLogoutComplete: () -> Unit) {
        viewModelScope.launch {
            preferencesManager.clearSession()
            userRepository.clearUser()
            onLogoutComplete()
        }
    }
}
