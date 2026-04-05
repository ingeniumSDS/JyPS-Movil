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

    fun onTimeClick() {
        _uiState.update { it.copy(showTimePicker = true) }
    }

    fun onTimeDismiss() {
        _uiState.update { it.copy(showTimePicker = false) }
    }

    fun onTimeSelected(time: LocalTime) {
        _uiState.update { 
            it.copy(
                selectedTime = time,
                showTimePicker = false
            )
        }
    }

    fun onDetailsChanged(newDetails: String) {
        if (newDetails.length <= _uiState.value.detailsLimit) {
            _uiState.update { it.copy(details = newDetails) }
        }
    }

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

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }

    fun clearActivePassError() {
        _uiState.update { it.copy(hasActivePassError = false) }
    }

    fun resetSuccess() {
        _uiState.update { it.copy(isSuccess = false) }
    }
}
