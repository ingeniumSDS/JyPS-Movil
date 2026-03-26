package mx.edu.utez.jyps.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Represents the three tabs or visual options for the screen.
 */
enum class ScannerTab {
    QR, MANUAL
}

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
        val code: String,
        val type: String = "Allow exit"
    ) : ScannerStatus()
}

/**
 * Full state of the scanner UI.
 */
data class ScannerUiState(
    val currentTab: ScannerTab = ScannerTab.QR,
    val status: ScannerStatus = ScannerStatus.Idle
)

/**
 * ViewModel managing the UI state for the Security Scanner.
 * Implements a StateFlow to be reactively observed by Compose,
 * ensuring MVVM architecture and separation of concerns.
 */
class ScannerViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(ScannerUiState())
    val uiState: StateFlow<ScannerUiState> = _uiState.asStateFlow()

    /**
     * Changes the currently active tab in the scanner.
     */
    fun setTab(tab: ScannerTab) {
        _uiState.value = _uiState.value.copy(currentTab = tab, status = ScannerStatus.Idle)
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
        _uiState.value = _uiState.value.copy(status = ScannerStatus.ValidQR(code))
    }

    /**
     * Simulates an invalid QR scan result.
     */
    fun mockInvalidQR() {
        _uiState.value = _uiState.value.copy(status = ScannerStatus.InvalidQR("INVALID"))
    }

    /**
     * Simulates validating a QR and obtaining the full success pass card.
     */
    fun mockValidPass() {
        _uiState.value = _uiState.value.copy(
            status = ScannerStatus.ValidPass(
                name = "Juan Pérez García",
                email = "juan.perez@utez.edu.mx",
                date = "Tuesday, February 24, 2026",
                code = "JUST001"
            )
        )
    }

    /**
     * Resets the scanner to the initial idle state.
     */
    fun resetScanner() {
        _uiState.value = _uiState.value.copy(status = ScannerStatus.Idle)
    }
}
