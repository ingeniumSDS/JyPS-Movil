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
    val detailsLimit: Int = 500,
    val showTimePicker: Boolean = false,
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val error: String? = null
) {
    val dateDisplay: String
        get() = selectedDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))

    val timeDisplay: String
        get() = selectedTime?.format(DateTimeFormatter.ofPattern("hh:mm a")) ?: ""

    val isFormValid: Boolean
        get() = selectedTime != null && details.isNotBlank() && details.length <= detailsLimit
}

/**
 * ViewModel responsible for the logic of requesting a Pass.
 */
class PassRequestViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(PassRequestState())
    val uiState: StateFlow<PassRequestState> = _uiState.asStateFlow()

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
        
        _uiState.update { it.copy(isLoading = true) }
        
        // Simulating API call (Dummy Data rule)
        _uiState.update { 
            it.copy(
                isLoading = false,
                isSuccess = true
            ) 
        }
    }

    fun resetSuccess() {
        _uiState.update { it.copy(isSuccess = false) }
    }
}
