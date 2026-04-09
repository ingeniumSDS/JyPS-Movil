package mx.edu.utez.jyps.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

/**
 * State class for the Pass Request screen.
 *
 * @property fullName Automatically injected from user session.
 * @property email Automatically injected from user session.
 * @property selectedDate The designated exit day (default: today).
 * @property selectedTime Transformed instance tracking hour/minute choice.
 * @property details Textual details describing the reasoning.
 * @property detailsMinLimit Hard validation limit for UI bounds check.
 * @property detailsLimit Hard absolute limit.
 * @property showTimePicker Flags the display sequence of the material TimePicker.
 * @property isLoading Indicates if network submission is blocking UI.
 * @property isSuccess Lifecycle state evaluating to correct completion.
 * @property hasActivePassError True if user violates DFR limit per day.
 * @property error Active error message string if request crashed.
 */
data class PassRequestState(
    val fullName: String = "Juan Pérez García", // Injected from session in real app
    val email: String = "juan.perez@utez.edu.mx", // Injected from session in real app
    val selectedDate: LocalDate = LocalDate.now(),
    val selectedTime: LocalTime? = null,
    val details: String = "",
    val detailsMinLimit: Int = 25,
    val detailsLimit: Int = 255,
    val showTimePicker: Boolean = false,
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val hasActivePassError: Boolean = false,
    val error: String? = null
) {
    val dateDisplay: String
        get() = selectedDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))

    val timeDisplay: String
        get() = selectedTime?.format(DateTimeFormatter.ofPattern("hh:mm a")) ?: ""

    val isFormValid: Boolean
        get() = !isLoading && selectedTime != null && details.length in detailsMinLimit..detailsLimit
}

/**
 * ViewModel responsible for the logic of requesting a Pass.
 */
class PassRequestViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(PassRequestState())
    val uiState: StateFlow<PassRequestState> = _uiState.asStateFlow()

    init {
        checkActivePasses()
    }

    /**
     * Bootstraps session info into the request template.
     *
     * @param name Name representing the auth.
     * @param email Verified email.
     */
    fun setUserInfo(name: String, email: String) {
        _uiState.update { it.copy(fullName = name, email = email) }
    }

    private fun checkActivePasses() {
        // Dummy check for DFR rule: "el sistema impide la creación de un nuevo pase únicamente si ya existe una solicitud..."
        val hasActivePass = false // Toggle this to test the blockade
        if (hasActivePass) {
            _uiState.update { 
                it.copy(
                    hasActivePassError = true, 
                    error = "Aún cuenta con una Solicitud de Pase de salida pendiente para el día de hoy"
                )
            }
        }
    }

    /** Engages the material OS time selection widget. */
    fun onTimeClick() {
        _uiState.update { it.copy(showTimePicker = true) }
    }

    /** Hides the time selection widget natively. */
    fun onTimeDismiss() {
        _uiState.update { it.copy(showTimePicker = false) }
    }

    /**
     * Locks in the time choice picked by the user format.
     *
     * @param time Explicit Java time construct.
     */
    fun onTimeSelected(time: LocalTime) {
        _uiState.update { 
            it.copy(
                selectedTime = time,
                showTimePicker = false
            )
        }
    }

    /**
     * Appends reason text evaluating bounds checking continuously.
     *
     * @param newDetails Raw input string snippet.
     */
    fun onDetailsChanged(newDetails: String) {
        if (newDetails.length <= _uiState.value.detailsLimit) {
            _uiState.update { it.copy(details = newDetails) }
        }
    }

    /**
     * Assembles payload assuming all state transitions map clearly 
     * out of the scope and performs IO task mapping.
     */
    fun onSubmit() {
        if (!_uiState.value.isFormValid) return
        
        _uiState.update { it.copy(isLoading = true, error = null) }
        
        // Simulating API call (Dummy Data rule)
        _uiState.update { 
            it.copy(
                isLoading = false,
                isSuccess = true
            ) 
        }
    }

    /** Dips error state back to clean canvas constraints. */
    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }

    /** Resets blockade constraints. */
    fun clearActivePassError() {
        _uiState.update { it.copy(hasActivePassError = false) }
    }

    /** Destroys active completion signals. */
    fun resetSuccess() {
        _uiState.update { it.copy(isSuccess = false) }
    }
}
