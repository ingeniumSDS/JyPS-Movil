package mx.edu.utez.jyps.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * Represents the different states of the Forgot Password flow.
 */
enum class ForgotPasswordStep {
    EMAIL_INPUT,
    CODE_VERIFICATION,
    SUCCESS
}

/**
 * UI State for the Forgot Password screen.
 *
 * @property email The email address inputted by the user.
 * @property verificationCode The 6-digit confirmation code.
 * @property currentStep The current phase of the recovery phase.
 * @property isLoading Indicates if a network request is executing.
 * @property emailErrorMessage Validation error message for the email field.
 * @property codeErrorMessage Validation error message for the code field.
 */
data class ForgotPasswordUiState(
    val email: String = "",
    val verificationCode: String = "",
    val currentStep: ForgotPasswordStep = ForgotPasswordStep.EMAIL_INPUT,
    val isLoading: Boolean = false,
    val emailErrorMessage: String? = null,
    val codeErrorMessage: String? = null
)

/**
 * ForgotPasswordViewModel manages the state and business logic for password recovery.
 */
class ForgotPasswordViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(ForgotPasswordUiState())
    val uiState: StateFlow<ForgotPasswordUiState> = _uiState.asStateFlow()

    /**
     * Updates the user's email input and clears any previous error.
     *
     * @param newEmail The new text in the email field.
     */
    fun onEmailChange(newEmail: String) {
        _uiState.update { it.copy(email = newEmail, emailErrorMessage = null) }
    }

    /**
     * Updates the verification code up to 6 numerical digits.
     *
     * @param newCode The new text in the code field.
     */
    fun onCodeChange(newCode: String) {
        // Ensure only numbers and max 6 digits
        val filteredCode = newCode.filter { it.isDigit() }.take(6)
        _uiState.update { it.copy(verificationCode = filteredCode, codeErrorMessage = null) }
    }

    /**
     * Validates the email and moves to code verification step if valid.
     */
    fun submitEmail() {
        val email = _uiState.value.email

        if (email.isBlank()) {
            _uiState.update { it.copy(emailErrorMessage = "Por favor, ingresa tu correo electrónico") }
            return
        }

        if (!email.endsWith("@utez.edu.mx")) {
            _uiState.update { it.copy(emailErrorMessage = "El correo debe pertenecer al dominio @utez.edu.mx") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            // Simulating API call to send code
            delay(1500)
            _uiState.update { it.copy(isLoading = false, currentStep = ForgotPasswordStep.CODE_VERIFICATION) }
        }
    }

    /**
     * Validates the entered verification code.
     */
    fun verifyCode() {
        val code = _uiState.value.verificationCode

        if (code.length < 6) {
            _uiState.update { it.copy(codeErrorMessage = "Código no válido. Debe ingresar los 6 dígitos") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            
            // Simulating verification API call
            delay(1500)

            // Dummy validation based on Figma mockups
            if (code == "218811" || code == "242637") {
                _uiState.update { it.copy(isLoading = false, currentStep = ForgotPasswordStep.SUCCESS) }
            } else {
                _uiState.update { it.copy(isLoading = false, codeErrorMessage = "Código no válido. Verifique el código enviado a su correo") }
            }
        }
    }

    /**
     * Resets the flow to the beginning.
     */
    fun resetState() {
        _uiState.value = ForgotPasswordUiState()
    }
}
