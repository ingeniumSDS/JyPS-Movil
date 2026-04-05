package mx.edu.utez.jyps.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

/**
 * General status of the scanner.
 */
sealed class ScannerStatus {
    object Idle : ScannerStatus()
    object Scanning : ScannerStatus()
    data class ValidQR(val code: String) : ScannerStatus()
    data class InvalidQR(val error: String) : ScannerStatus()
    data class ValidPass(
        val name: String,
        val email: String,
        val date: String,
        val exitTime: String,
        val returnTime: String,
        val code: String,
        val type: String = "Permitir Salida"
    ) : ScannerStatus()
}

/**
 * Full state of the scanner UI.
 */
data class ScannerUiState(
    val status: ScannerStatus = ScannerStatus.Idle,
    val manualCode: String = "",
    val errorToast: String? = null
)

/**
 * ViewModel managing the UI state for the Security Scanner.
 * Implements a StateFlow to be reactively observed by Compose,
 * ensuring MVVM architecture and separation of concerns.
 */
class ScannerViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(ScannerUiState())
    val uiState: StateFlow<ScannerUiState> = _uiState.asStateFlow()

    // Mock storage for tracking single-use QR codes
    private val usedCodes = mutableSetOf<String>()

    /**
     * Updates the manual code input text.
     */
    fun onManualCodeChange(newValue: String) {
        _uiState.value = _uiState.value.copy(manualCode = newValue, errorToast = null)
    }

    /**
     * Verifies the manual code against valid test scenarios.
     */
    fun verifyManualCode() {
        val code = _uiState.value.manualCode.uppercase()
        when (code) {
            "GDKF64NC" -> processValidCode(code)
            "LATE" -> processValidCode(code, isLate = true)
            "EXPIRED" -> _uiState.value = _uiState.value.copy(errorToast = "Caducado")
            "INVALID" -> _uiState.value = _uiState.value.copy(errorToast = "Código inválido")
            else -> {
                if (usedCodes.contains(code)) {
                    _uiState.value = _uiState.value.copy(errorToast = "Código ya utilizado")
                } else {
                    _uiState.value = _uiState.value.copy(errorToast = "Código inválido")
                }
            }
        }
    }

    /**
     * Internal logic processing the core business rules:
     * 1. Check for single use (blocks exit if used).
     * 2. Log exact exit time.
     * 3. Calculates dynamic limits (3 hours) or marks as shift end.
     */
    private fun processValidCode(code: String, isLate: Boolean = false) {
        if (usedCodes.contains(code)) {
            _uiState.value = _uiState.value.copy(errorToast = "Código ya utilizado")
            return
        }
        
        // Single-use enforcement
        usedCodes.add(code)

        val locale = Locale("es", "MX")
        val dateStr = SimpleDateFormat("EEEE, d 'de' MMMM 'de' yyyy", locale).format(Date())
        val timeStr = SimpleDateFormat("hh:mm a", locale).format(Date())
        
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.HOUR_OF_DAY, 3)
        val limitTimeStr = SimpleDateFormat("hh:mm a", locale).format(calendar.time)
        
        val returnTimeStr = if (isLate) "No requiere regreso (Fin jornada)" else limitTimeStr

        _uiState.value = _uiState.value.copy(
            status = ScannerStatus.ValidPass(
                name = "Juan Pérez García",
                email = "juan.perez@utez.edu.mx",
                date = dateStr,
                exitTime = timeStr,
                returnTime = returnTimeStr,
                code = code,
                type = "Permitir Salida"
            )
        )
    }

    /**
     * Clears the error toast.
     */
    fun clearErrorToast() {
        _uiState.value = _uiState.value.copy(errorToast = null)
    }

    /**
     * Starts the active scanning state (mock).
     */
    fun startScanning() {
        _uiState.value = _uiState.value.copy(status = ScannerStatus.Scanning)
    }

    /**
     * Simulates a valid QR scan result returning raw code.
     */
    fun mockValidQR(code: String = "GDKF64NC") {
        if (usedCodes.contains(code)) {
            _uiState.value = _uiState.value.copy(status = ScannerStatus.InvalidQR("Código ya utilizado"))
        } else {
            _uiState.value = _uiState.value.copy(status = ScannerStatus.ValidQR(code))
        }
    }

    /**
     * Simulates an invalid QR scan result.
     */
    fun mockInvalidQR() {
        _uiState.value = _uiState.value.copy(status = ScannerStatus.InvalidQR("INVALIDO"))
    }

    /**
     * Simulates validating a QR and obtaining the full success pass card.
     */
    fun mockValidPass() {
        processValidCode("GDKF64NC")
    }

    /**
     * Resets the scanner to the initial idle state.
     */
    fun resetScanner() {
        _uiState.value = _uiState.value.copy(status = ScannerStatus.Idle)
    }
}
