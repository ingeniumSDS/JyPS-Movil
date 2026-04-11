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
 * Sincronizado con el flujo del backend real.
 */
enum class ForgotPasswordStep {
    EMAIL_INPUT,
    CODE_VERIFICATION,
    PASSWORD_SETUP,
    SUCCESS
}

/**
 * UI State for the Forgot Password screen.
 */
data class ForgotPasswordUiState(
    val email: String = "",
    val verificationCode: String = "",
    val newPassword: String = "",
    val confirmPassword: String = "",
    val currentStep: ForgotPasswordStep = ForgotPasswordStep.EMAIL_INPUT,
    val isLoading: Boolean = false,
    val emailErrorMessage: String? = null,
    val codeErrorMessage: String? = null,
    val passwordErrorMessage: String? = null
)

/**
 * ForgotPasswordViewModel manages the state and business logic for password recovery.
 * 
 * @property application Android application context.
 */
class ForgotPasswordViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = AuthRepository(
        RetrofitInstance.api, 
        PreferencesManager(application)
    )

    private val _uiState = MutableStateFlow(ForgotPasswordUiState())
    val uiState: StateFlow<ForgotPasswordUiState> = _uiState.asStateFlow()

    fun onEmailChange(newEmail: String) {
        _uiState.update { it.copy(email = newEmail, emailErrorMessage = null) }
    }

    fun onCodeChange(newCode: String) {
        // Soporta tokens UUID (guiones permitidos)
        val filteredCode = newCode.filter { it.isLetterOrDigit() || it == '-' }.take(255)
        _uiState.update { it.copy(verificationCode = filteredCode, codeErrorMessage = null) }
    }

    fun onPasswordChange(pass: String) {
        _uiState.update { it.copy(newPassword = pass, passwordErrorMessage = null) }
    }

    fun onConfirmPasswordChange(pass: String) {
        _uiState.update { it.copy(confirmPassword = pass, passwordErrorMessage = null) }
    }

    /**
     * Executes the token request to the real backend service.
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
                _uiState.update { it.copy(isLoading = false, currentStep = ForgotPasswordStep.CODE_VERIFICATION) }
            }.onFailure { error ->
                _uiState.update { it.copy(isLoading = false, emailErrorMessage = error.message) }
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
        
        if (state.newPassword.length < 8) {
            _uiState.update { it.copy(passwordErrorMessage = "La contraseña debe tener al menos 8 caracteres") }
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

    private fun ForgotPasswordUiState.updateLoading(loading: Boolean) = copy(
        isLoading = loading,
        passwordErrorMessage = null,
        codeErrorMessage = null,
        emailErrorMessage = null
    )

    fun resetState() {
        _uiState.value = ForgotPasswordUiState()
    }
}
