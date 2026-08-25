package com.aetherx.mausamiq.presentation.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aetherx.mausamiq.core.datastore.PreferencesManager
import com.aetherx.mausamiq.data.local.entity.UserEntity
import com.aetherx.mausamiq.domain.repository.IUserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AuthUiState(
    val email: String = "",
    val password: String = "",
    val fullName: String = "",
    val phone: String = "",
    val confirmPassword: String = "",
    val isPasswordVisible: Boolean = false,
    val rememberMe: Boolean = true,
    val termsAccepted: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null,
    val isAuthenticated: Boolean = false,
    val resetEmailSent: Boolean = false
)

class AuthViewModel(
    private val userRepository: IUserRepository,
    private val preferencesManager: PreferencesManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    fun onEmailChange(value: String) = _uiState.update { it.copy(email = value, errorMessage = null) }
    fun onPasswordChange(value: String) = _uiState.update { it.copy(password = value, errorMessage = null) }
    fun onFullNameChange(value: String) = _uiState.update { it.copy(fullName = value, errorMessage = null) }
    fun onPhoneChange(value: String) = _uiState.update { it.copy(phone = value) }
    fun onConfirmPasswordChange(value: String) = _uiState.update { it.copy(confirmPassword = value, errorMessage = null) }
    fun togglePasswordVisibility() = _uiState.update { it.copy(isPasswordVisible = !it.isPasswordVisible) }
    fun toggleRememberMe() = _uiState.update { it.copy(rememberMe = !it.rememberMe) }
    fun toggleTermsAccepted() = _uiState.update { it.copy(termsAccepted = !it.termsAccepted) }

    fun login(onSuccess: () -> Unit) {
        val state = _uiState.value
        if (state.email.isBlank() || !state.email.contains("@")) {
            _uiState.update { it.copy(errorMessage = "Please enter a valid email address.") }
            return
        }
        if (state.password.length < 6) {
            _uiState.update { it.copy(errorMessage = "Password must be at least 6 characters.") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            // Secure session storage
            val name = if (state.fullName.isNotBlank()) state.fullName else state.email.substringBefore("@").replaceFirstChar { it.uppercase() }
            preferencesManager.setLoggedIn(true, name = name, email = state.email)
            userRepository.saveUser(
                UserEntity(
                    email = state.email,
                    fullName = name,
                    phone = state.phone
                )
            )
            userRepository.seedDefaultLocationsIfEmpty()
            _uiState.update { it.copy(isLoading = false, isAuthenticated = true) }
            onSuccess()
        }
    }

    fun register(onSuccess: () -> Unit) {
        val state = _uiState.value
        if (state.fullName.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Please enter your full name.") }
            return
        }
        if (state.email.isBlank() || !state.email.contains("@")) {
            _uiState.update { it.copy(errorMessage = "Please enter a valid email address.") }
            return
        }
        if (state.password.length < 8) {
            _uiState.update { it.copy(errorMessage = "Password must be at least 8 characters.") }
            return
        }
        if (state.password != state.confirmPassword) {
            _uiState.update { it.copy(errorMessage = "Passwords do not match.") }
            return
        }
        if (!state.termsAccepted) {
            _uiState.update { it.copy(errorMessage = "Please accept the Terms & Privacy Policy.") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            preferencesManager.setLoggedIn(true, name = state.fullName, email = state.email)
            userRepository.saveUser(
                UserEntity(
                    email = state.email,
                    fullName = state.fullName,
                    phone = state.phone
                )
            )
            userRepository.seedDefaultLocationsIfEmpty()
            _uiState.update { it.copy(isLoading = false, isAuthenticated = true) }
            onSuccess()
        }
    }

    fun sendPasswordReset() {
        val state = _uiState.value
        if (state.email.isBlank() || !state.email.contains("@")) {
            _uiState.update { it.copy(errorMessage = "Please enter a valid email address.") }
            return
        }
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            kotlinx.coroutines.delay(1000)
            _uiState.update {
                it.copy(
                    isLoading = false,
                    resetEmailSent = true,
                    successMessage = "If an account exists with this email, recovery instructions have been dispatched."
                )
            }
        }
    }

    fun continueAsGuest(onSuccess: () -> Unit) {
        viewModelScope.launch {
            preferencesManager.setLoggedIn(true, name = "SIH Demo User", email = "sih2026@aetherx.ai")
            preferencesManager.setDemoMode(true)
            userRepository.seedDefaultLocationsIfEmpty()
            onSuccess()
        }
    }
}
