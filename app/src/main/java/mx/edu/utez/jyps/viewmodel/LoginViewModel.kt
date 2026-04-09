package mx.edu.utez.jyps.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import mx.edu.utez.jyps.data.network.RetrofitInstance
import mx.edu.utez.jyps.data.repository.AuthRepository
import mx.edu.utez.jyps.data.repository.PreferencesManager

/**
 * UI State for the Login screen.
 */
data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isLoginSuccessful: Boolean = false,
    val loginAttempts: Int = 3,
    val isLockedOut: Boolean = false
)

/**
 * LoginViewModel manages the state and business logic for the Login screen.
 *
 * @property application Android application context provided by the framework.
 */
class LoginViewModel(application: Application) : AndroidViewModel(application) {

    private val preferencesManager = PreferencesManager(application)
    private val repository = AuthRepository(RetrofitInstance.api, preferencesManager)

    /**
     * Provides an uncoupled authentication state boundary for the Navigation Host.
     * Downstream UI consumers extract the token to decide the initial route (e.g. Scanner vs Dashboard)
     */
    val sessionToken: StateFlow<String?> = repository.tokenFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    /**
     * Updates the email in the UI state.
     */
    fun onEmailChange(newEmail: String) {
        _uiState.update { it.copy(email = newEmail, errorMessage = null) }
    }

    /**
     * Updates the password in the UI state.
     */
    fun onPasswordChange(newPassword: String) {
        _uiState.update { it.copy(password = newPassword, errorMessage = null) }
    }

    /**
     * Executes the main authentication flow.
     * Delegates exclusively to [AuthRepository] to ensure the cryptographic material
     * is resolved entirely out-of-bounds from the UI Thread, preventing memory leaks 
     * of sensitive data. Handles internal anti-bruteforce locking directly.
     */
    fun login() {
        if (_uiState.value.isLockedOut) return

        val email = _uiState.value.email
        val password = _uiState.value.password

        if (email.isBlank() || password.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Por favor, completa todos los campos.") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            
            val result = repository.login(email, password)
            
            result.onSuccess {
                _uiState.update { 
                    it.copy(
                        isLoading = false, 
                        isLoginSuccessful = true,
                        loginAttempts = 3, // Reset on success
                        errorMessage = null
                    ) 
                }
            }.onFailure { error ->
                val remainingAttempts = _uiState.value.loginAttempts - 1
                if (remainingAttempts <= 0) {
                    _uiState.update { 
                        it.copy(
                            isLoading = false, 
                            isLockedOut = true, 
                            errorMessage = null 
                        ) 
                    }
                    startLockoutTimer()
                } else {
                    _uiState.update { 
                        it.copy(
                            isLoading = false, 
                            loginAttempts = remainingAttempts,
                            errorMessage = "${error.message} ($remainingAttempts intento${if (remainingAttempts == 1) "" else "s"} restante${if (remainingAttempts == 1) "" else "s"})"
                        ) 
                    }
                }
            }
        }
    }

    /**
     * Handles the lockout duration timer.
     */
    private fun startLockoutTimer() {
        viewModelScope.launch {
            // Lockout duration 1 minute (simulated by wait)
            kotlinx.coroutines.delay(60000)
            _uiState.update { 
                it.copy(
                    isLockedOut = false,
                    loginAttempts = 3,
                    errorMessage = null
                )
            }
        }
    }


    /**
     * Handles the forgot password action.
     * Uses dummy logic for now.
     */
    fun onForgotPassword() {
        val email = _uiState.value.email

        if (email.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Por favor, ingresa tu correo electrónico.") }
            return
        }
    }
    
    /**
     * Resets the error message.
     */
    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    /**
     * Orchestrates a safe session termination by dispatching to the secure repository.
     * This action immediately mutates [isLoggedIn] back to false, propagating the 
     * effect straight to the app's root navigation component.
     */
    fun logout() {
        viewModelScope.launch {
            repository.logout()
            _uiState.update { LoginUiState() } // Reset form
        }
    }
}
