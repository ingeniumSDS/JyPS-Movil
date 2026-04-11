package mx.edu.utez.jyps.viewmodel

import android.app.Application
import android.util.Log
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
 * UI State for the Authentication Entry Point.
 * 
 * @param email Curated identity predicate for login.
 * @param password Secure credential input for authentication.
 * @param isLoading Transactional state for network handshakes.
 * @param errorMessage Diagnostic text for credential or security rejections.
 * @param isLoginSuccessful Success flag for session propagation.
 * @param isAccountBlocked Specific state for server-side restricted identities.
 * @param loginAttempts Incremental counter for local anti-bruteforce measures.
 * @param isLockedOut Transient state for local-side security timeouts.
 */
data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isLoginSuccessful: Boolean = false,
    val isAccountBlocked: Boolean = false,
    val loginAttempts: Int = 3,
    val isLockedOut: Boolean = false
)

/**
 * High-level orchestrator for the Authentication lifecycle.
 * 
 * Manages the security handshake between the identity provider and the local session. 
 * Enforces resilient security layers, including server-side block detection and 
 * local anti-bruteforce logic, ensuring atomic state transitions via [LoginUiState].
 *
 * @param application Global application context for secure preference management.
 */
class LoginViewModel(application: Application) : AndroidViewModel(application) {

    private val preferencesManager = PreferencesManager(application)
    private val repository = AuthRepository(RetrofitInstance.api, preferencesManager)

    /**
     * Provides an uncoupled authentication state boundary for the Navigation Host.
     */
    val sessionToken: StateFlow<String?> = repository.tokenFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    /**
     * Updates the email in the UI state.
     */
    fun onEmailChange(newEmail: String) {
        _uiState.update { it.copy(email = newEmail, errorMessage = null, isAccountBlocked = false) }
    }

    /**
     * Updates the password in the UI state.
     */
    fun onPasswordChange(newPassword: String) {
        _uiState.update { it.copy(password = newPassword, errorMessage = null) }
    }

    /**
     * Executes the main authentication flow against the real backend service.
     * 
     * Handles specific security exceptions like account lockouts (IllegalStateException)
     * and invalid credentials. Upon success, the session state is persisted out-of-bounds.
     */
    fun login() {
        if (_uiState.value.isLockedOut || _uiState.value.isAccountBlocked) return

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
                Log.d("LoginVM", "Auth success for $email. Resetting state.")
                _uiState.update { 
                    it.copy(
                        isLoading = false, 
                        isLoginSuccessful = true,
                        loginAttempts = 3,
                        errorMessage = null,
                        isAccountBlocked = false
                    ) 
                }
            }.onFailure { error ->
                val message = error.message ?: "Error desconocido"
                Log.d("LoginVM", "Auth failure for $email: $message")

                // Handle server-side account blocking
                if (message.contains("bloqueada", ignoreCase = true)) {
                    _uiState.update { 
                        it.copy(
                            isLoading = false,
                            isAccountBlocked = true,
                            errorMessage = null
                        ) 
                    }
                } else {
                    val remainingAttempts = _uiState.value.loginAttempts - 1
                    if (remainingAttempts <= 0) {
                        _uiState.update { it.copy(isLoading = false, isLockedOut = true, errorMessage = null) }
                        startAntiBruteforceTimer()
                    } else {
                        _uiState.update { 
                            it.copy(
                                isLoading = false, 
                                loginAttempts = remainingAttempts,
                                errorMessage = "$message ($remainingAttempts intento${if (remainingAttempts == 1) "" else "s"} restante${if (remainingAttempts == 1) "" else "s"})"
                            ) 
                        }
                    }
                }
            }
        }
    }

    /**
     * Resets the blocked state to allow the user to try a different account.
     */
    fun resetBlockedState() {
        _uiState.update { 
            it.copy(
                isAccountBlocked = false, 
                isLockedOut = false,
                loginAttempts = 3,
                email = "", 
                password = "", 
                errorMessage = null
            ) 
        }
    }

    /**
     * Handles the anti-bruteforce lockout duration (Local-side).
     */
    private fun startAntiBruteforceTimer() {
        viewModelScope.launch {
            kotlinx.coroutines.delay(60000) // 1 minute local lock
            _uiState.update { 
                it.copy(isLockedOut = false, loginAttempts = 3, errorMessage = null) 
            }
        }
    }

    /**
     * Resets the error message.
     */
    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    /**
     * Orchestrates a safe session termination.
     */
    fun logout() {
        viewModelScope.launch {
            repository.logout()
            _uiState.update { LoginUiState() } // Hard reset
        }
    }
}
