package mx.edu.utez.jyps.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

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
 */
class LoginViewModel : ViewModel() {

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
     * Handles the login action. 
     * Uses dummy logic for now.
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
            _uiState.update { it.copy(isLoading = true) }
            
            // Simulating API call
            kotlinx.coroutines.delay(1500)

            if (email == "carlos.rodriguez@utez.edu.mx" && password == "") {
                _uiState.update { 
                    it.copy(
                        isLoading = false, 
                        isLoginSuccessful = true,
                        loginAttempts = 3, // Reset on success
                        errorMessage = null
                    ) 
                }
            } else {
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
                            errorMessage = "Credenciales inválidas ($remainingAttempts intento${if (remainingAttempts == 1) "" else "s"} restante${if (remainingAttempts == 1) "" else "s"})"
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
}
