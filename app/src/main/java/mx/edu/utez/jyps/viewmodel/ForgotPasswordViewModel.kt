package mx.edu.utez.jyps.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import mx.edu.utez.jyps.data.model.PasswordSetupRequest
import mx.edu.utez.jyps.data.network.RetrofitInstance
import mx.edu.utez.jyps.data.repository.AuthRepository
import mx.edu.utez.jyps.data.repository.PreferencesManager

/**
 * Represents the different states of the Forgot Password flow.
 * Synchronized with the actual backend flow.
 */
enum class ForgotPasswordStep {
    EMAIL_INPUT,
    CODE_VERIFICATION,
    PASSWORD_SETUP,
    SUCCESS
}

/**
 * UI State for the Forgot Password screen.
 * 
 * @property email The institutional email address being recovered.
 * @property verificationCode Current token input during the validation step.
 * @property newPassword The primary new password input.
 * @property confirmPassword The secondary confirmation password input.
 * @property currentStep The active stage in the multi-step recovery flow.
 * @property isLoading Global loading indicator for network operations.
 * @property resendCountdown Remaining seconds before the "Resend Code" action is allowed.
 * @property emailErrorMessage Validation or server error related to email input.
 * @property codeErrorMessage Validation or server error related to token input.
 * @property passwordErrorMessage Validation or server error related to password complexity/mismatch.
 */
data class ForgotPasswordUiState(
    val email: String = "",
    val verificationCode: String = "",
    val newPassword: String = "",
    val confirmPassword: String = "",
    val currentStep: ForgotPasswordStep = ForgotPasswordStep.EMAIL_INPUT,
    val isLoading: Boolean = false,
    val resendCountdown: Int = 0,
    val emailErrorMessage: String? = null,
    val codeErrorMessage: String? = null,
    val passwordErrorMessage: String? = null
)

/**
 * ForgotPasswordViewModel manages the state and business logic for password recovery.
 * Implements security-focused flows including token validation and resend rate-limiting.
 * 
 * @property application Android application context for repository initialization.
 */
class ForgotPasswordViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = AuthRepository(
        RetrofitInstance.api, 
        PreferencesManager(application)
    )

    private val _uiState = MutableStateFlow(ForgotPasswordUiState())
    val uiState: StateFlow<ForgotPasswordUiState> = _uiState.asStateFlow()

    /**
     * Updates the target email address in the state.
     * 
     * @param newEmail The updated string from the UI.
     */
    fun onEmailChange(newEmail: String) {
        _uiState.update { it.copy(email = newEmail, emailErrorMessage = null) }
    }

    /**
     * Updates the verification token input. Filters for UUID-compatible characters.
     * 
     * @param newCode The updated string from the UI.
     */
    fun onCodeChange(newCode: String) {
        val filteredCode = newCode.filter { it.isLetterOrDigit() || it == '-' }.take(255)
        _uiState.update { it.copy(verificationCode = filteredCode, codeErrorMessage = null) }
    }

    /**
     * Updates the primary new password input.
     * 
     * @param pass The updated password string.
     */
    fun onPasswordChange(pass: String) {
        _uiState.update { it.copy(newPassword = pass, passwordErrorMessage = null) }
    }

    /**
     * Updates the confirmation password input.
     * 
     * @param pass The updated confirmation string.
     */
    fun onConfirmPasswordChange(pass: String) {
        _uiState.update { it.copy(confirmPassword = pass, passwordErrorMessage = null) }
    }

    /**
     * Executes the token request to the backend service.
     * Triggers the initial countdown timer for the resend functionality.
     */
    fun submitEmail() {
        val email = _uiState.value.email

        if (email.isBlank()) {
            _uiState.update { it.copy(emailErrorMessage = "Por favor, ingresa tu correo electrónico") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, emailErrorMessage = null) }
            
            val result = repository.requestPasswordToken(email)
            
            result.onSuccess {
                _uiState.update { 
                    it.copy(
                        isLoading = false, 
                        currentStep = ForgotPasswordStep.CODE_VERIFICATION,
                        resendCountdown = 120 // 2 minute countdown started
                    ) 
                }
                startCountdown()
            }.onFailure { error ->
                _uiState.update { it.copy(isLoading = false, emailErrorMessage = error.message) }
            }
        }
    }

    /**
     * Re-triggers the token generation request for the current email.
     * Implements a 2-minute cooldown between requests as a security best practice.
     */
    fun resendCode() {
        val email = _uiState.value.email
        if (_uiState.value.resendCountdown > 0 || _uiState.value.isLoading) return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, codeErrorMessage = null) }
            
            val result = repository.requestPasswordToken(email)
            
            result.onSuccess {
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        resendCountdown = 120 // Reset cooldown
                    ) 
                }
                startCountdown()
            }.onFailure { error ->
                _uiState.update { it.copy(isLoading = false, codeErrorMessage = "Error al reconfigurar: ${error.message}") }
            }
        }
    }

    /**
     * Internal countdown logic that updates the [ForgotPasswordUiState.resendCountdown] every second.
     */
    private fun startCountdown() {
        viewModelScope.launch {
            while (_uiState.value.resendCountdown > 0) {
                kotlinx.coroutines.delay(1000)
                _uiState.update { it.copy(resendCountdown = it.resendCountdown - 1) }
            }
        }
    }

    /**
     * Validates the security token using the backend service.
     * Transitions to password entry step upon success.
     */
    fun verifyCode() {
        val token = _uiState.value.verificationCode

        if (token.isBlank()) {
            _uiState.update { it.copy(codeErrorMessage = "Debe ingresar el token enviado a su correo") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, codeErrorMessage = null) }
            
            val result = repository.verifySetupToken(token)
            
            result.onSuccess {
                _uiState.update { it.copy(isLoading = false, currentStep = ForgotPasswordStep.PASSWORD_SETUP) }
            }.onFailure { error ->
                _uiState.update { it.copy(isLoading = false, codeErrorMessage = error.message) }
            }
        }
    }

    /**
     * Finalizes the recovery flow by establishing the new password after token validation.
     */
    fun confirmPasswordReset() {
        val state = _uiState.value
        
        if (state.newPassword.length < 12) {
            _uiState.update { it.copy(passwordErrorMessage = "La contraseña debe tener al menos 12 caracteres") }
            return
        }
        
        if (state.newPassword != state.confirmPassword) {
            _uiState.update { it.copy(passwordErrorMessage = "Las contraseñas no coinciden") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.updateLoading(true) }
            
            val result = repository.setupPassword(
                PasswordSetupRequest(
                    token = state.verificationCode,
                    password = state.newPassword
                )
            )
            
            result.onSuccess {
                _uiState.update { it.copy(isLoading = false, currentStep = ForgotPasswordStep.SUCCESS) }
            }.onFailure { error ->
                _uiState.update { it.copy(isLoading = false, passwordErrorMessage = error.message) }
            }
        }
    }

    /**
     * Optimizes the UI state for loading indicators by clearing previous errors.
     */
    private fun ForgotPasswordUiState.updateLoading(loading: Boolean) = copy(
        isLoading = loading,
        passwordErrorMessage = null,
        codeErrorMessage = null,
        emailErrorMessage = null
    )

    /**
     * Resets the entire flow to the initial target stage.
     */
    fun resetState() {
        _uiState.value = ForgotPasswordUiState()
    }
}
