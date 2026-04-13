package mx.edu.utez.jyps.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import mx.edu.utez.jyps.data.model.EstadosIncidencia
import mx.edu.utez.jyps.data.model.PassResponse
import mx.edu.utez.jyps.data.network.RetrofitInstance
import mx.edu.utez.jyps.data.repository.PreferencesManager
import mx.edu.utez.jyps.data.repository.ScannerRepository
import timber.log.Timber
import java.util.Calendar
import java.util.Date

/**
 * Holds all identifying and timing information extracted from a scanned pass.
 *
 * @param name Full name of the pass holder.
 * @param code The alphanumeric QR code.
 * @param date Human-readable date of the pass.
 * @param exitTime Timestamp recorded when the holder exited.
 * @param returnDeadline Latest time the holder must return (empty if no-return pass).
 * @param actualReturnTime Timestamp recorded on the second scan (return). Empty on first scan.
 */
data class ScannedPassInfo(
    val name: String,
    val code: String,
    val date: String,
    val exitTime: String,
    val returnDeadline: String,
    val actualReturnTime: String = ""
)

/**
 * Represents every possible outcome of scanning a QR code.
 * Each subclass maps to one of the UI States in ScanResultDialog.
 */
sealed class ScannerStatus {
    object Idle : ScannerStatus()
    data class ExitGranted(val info: ScannedPassInfo) : ScannerStatus()
    data class ExitNoReturn(val info: ScannedPassInfo) : ScannerStatus()
    data class ReturnOnTime(val info: ScannedPassInfo) : ScannerStatus()
    data class ReturnLate(val info: ScannedPassInfo) : ScannerStatus()
    data class InvalidCode(val reason: String) : ScannerStatus()
    data class ExpiredPass(val info: ScannedPassInfo) : ScannerStatus()
    data class AlreadyUsed(val info: ScannedPassInfo) : ScannerStatus()
}

/**
 * Full reactive UI state for the Security Scanner screen.
 *
 * @param status Current result state of the scanner.
 * @param manualCode Text currently typed in the manual code field.
 * @param errorToast Short-lived error message for the toast overlay.
 * @param isQrInFrame True when the camera analyzer has a QR code in its current frame.
 * @param currentUserEmail The email of the logged-in user, used for conditional UI.
 */
data class ScannerUiState(
    val status: ScannerStatus = ScannerStatus.Idle,
    val manualCode: String = "",
    val errorToast: String? = null,
    val isQrInFrame: Boolean = false,
    val currentUserEmail: String = "",
    val currentUserName: String = ""
)

/**
 * ViewModel managing the UI state for the Security Scanner.
 * Refactored to integrate real backend via ScannerRepository while maintaining
 * mocked behavior for testing users.
 */
class ScannerViewModel(application: Application) : AndroidViewModel(application) {

    private val preferencesManager = PreferencesManager(application)
    private val repository = ScannerRepository(RetrofitInstance.api)

    private val _uiState = MutableStateFlow(ScannerUiState())
    val uiState: StateFlow<ScannerUiState> = _uiState.asStateFlow()

    // ─── Mock Registries ─────────────────────────────────────────────────────
    private val exitRegistry = HashMap<String, Long>()
    private val usedRegistry = mutableSetOf("USED")
    private val validCodes = mapOf(
        "PASE004" to true,  // Exit pass with 3-hour return window
        "JUST001" to false  // Justification, no return required
    )

    private val locale = Locale("es", "MX")
    private val dateFormatter = DateTimeFormatter.ofPattern("EEEE, d 'de' MMMM 'de' yyyy", locale)
    private val timeFormatter = DateTimeFormatter.ofPattern("hh:mm a", locale)

    init {
        viewModelScope.launch {
            preferencesManager.userEmailFlow.collect { email ->
                _uiState.value = _uiState.value.copy(currentUserEmail = email ?: "")
            }
        }
        viewModelScope.launch {
            preferencesManager.userNameFlow.collect { name ->
                _uiState.value = _uiState.value.copy(currentUserName = name ?: "Usuario")
            }
        }
    }

    // ─── Camera & Manual Input ────────────────────────────────────────────────

    fun onQrCodeDetected(rawValue: String) {
        processCode(rawValue.trim().uppercase())
    }

    fun onManualCodeChange(newValue: String) {
        _uiState.value = _uiState.value.copy(manualCode = newValue, errorToast = null)
    }

    fun verifyManualCode() {
        processCode(_uiState.value.manualCode.trim().uppercase())
    }

    fun setQrInFrame(isInFrame: Boolean) {
        if (_uiState.value.isQrInFrame != isInFrame) {
            _uiState.value = _uiState.value.copy(isQrInFrame = isInFrame)
        }
    }

    // ─── Core Business Logic ─────────────────────────────────────────────────

    private fun processCode(code: String) {
        if (code.isBlank()) return

        // Check if we should use Mock logic based on the user identity
        if (_uiState.value.currentUserEmail == "maria.gonzalez@utez.edu.mx") {
            processMockCode(code)
        } else {
            processRealCode(code)
        }
    }

    /**
     * Executes the scan using real backend endpoints.
     */
    private fun processRealCode(code: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(status = ScannerStatus.Idle)
            
            repository.processPassCheckout(code).onSuccess { response ->
                val status = mapResponseToStatus(response)
                _uiState.value = _uiState.value.copy(status = status)
            }.onFailure { error ->
                _uiState.value = _uiState.value.copy(
                    status = ScannerStatus.InvalidCode(error.message ?: "Código no válido")
                )
            }
        }
    }

    /**
     * Executes the scan using in-memory mocked logic for testing.
     */
    private fun processMockCode(code: String) {
        Timber.d("[MOCK] Processing code: %s", code)
        
        if (usedRegistry.contains(code)) {
            _uiState.value = _uiState.value.copy(
                status = ScannerStatus.AlreadyUsed(buildMockPassInfo(code, true))
            )
            return
        }

        val hasReturnLimit = validCodes[code] ?: run {
            _uiState.value = _uiState.value.copy(
                status = ScannerStatus.InvalidCode("Código \"$code\" no registrado (MOCK).")
            )
            return
        }

        val now = System.currentTimeMillis()
        val exitTimestamp = exitRegistry[code]

        if (exitTimestamp == null) {
            exitRegistry[code] = now
            val info = buildMockPassInfo(code, hasReturnLimit)
            if (!hasReturnLimit) {
                usedRegistry.add(code)
                exitRegistry.remove(code)
            }
            _uiState.value = _uiState.value.copy(
                status = if (hasReturnLimit) ScannerStatus.ExitGranted(info) else ScannerStatus.ExitNoReturn(info)
            )
        } else {
            val actualReturnTime = ZonedDateTime.now().format(timeFormatter)
            val info = buildMockPassInfo(code, hasReturnLimit, actualReturnTime)
            usedRegistry.add(code)
            exitRegistry.remove(code)
            _uiState.value = _uiState.value.copy(status = ScannerStatus.ReturnOnTime(info))
        }
    }

    private fun mapResponseToStatus(response: PassResponse): ScannerStatus {
        val info = ScannedPassInfo(
            name = response.nombreCompleto ?: "Empleado",
            code = response.QR ?: "N/A",
            date = formatIsoDate(response.fechaSolicitud),
            exitTime = formatIsoTime(response.horaSalidaReal),
            returnDeadline = formatIsoTime(response.horaEsperada),
            actualReturnTime = if (response.horaRetornoReal != null) formatIsoTime(response.horaRetornoReal) else ""
        )

        val hasHoraRetornoReal = response.horaRetornoReal != null
        val upperState = response.estado.uppercase()

        return when {
            upperState == EstadosIncidencia.A_TIEMPO.name && !hasHoraRetornoReal -> ScannerStatus.ExitNoReturn(info)
            upperState == EstadosIncidencia.FUERA.name -> ScannerStatus.ExitGranted(info)
            upperState == EstadosIncidencia.A_TIEMPO.name && hasHoraRetornoReal -> ScannerStatus.ReturnOnTime(info)
            upperState == EstadosIncidencia.RETARDO.name && hasHoraRetornoReal -> ScannerStatus.ReturnLate(info)
            upperState == EstadosIncidencia.CADUCADO.name -> ScannerStatus.ExpiredPass(info)
            upperState == EstadosIncidencia.USADO.name -> ScannerStatus.AlreadyUsed(info)
            upperState == EstadosIncidencia.APROBADO.name -> ScannerStatus.ExitGranted(info) // Fallback just in case
            else -> ScannerStatus.InvalidCode("Estado de pase desconocido: ${response.estado}")
        }
    }

    private fun formatIsoDate(isoString: String?): String {
        if (isoString.isNullOrBlank()) return "N/A"
        return try {
            val dateTime = if (isoString.contains("T")) ZonedDateTime.parse(isoString)
                           else ZonedDateTime.parse("${isoString}T00:00:00Z")
            dateTime.format(dateFormatter)
        } catch (e: Exception) {
            isoString
        }
    }

    private fun formatIsoTime(isoString: String?): String {
        if (isoString.isNullOrBlank() || isoString == "0") return "N/A"
        return try {
            val cleaned = if (isoString.contains("T")) isoString else "1970-01-01T$isoString"
            val normalized = if (cleaned.contains("+") || cleaned.endsWith("Z")) cleaned else "${cleaned}Z"
            val dateTime = ZonedDateTime.parse(normalized)
            dateTime.format(timeFormatter)
        } catch (e: Exception) {
             try {
                 val localTime = java.time.LocalDateTime.parse(isoString)
                 localTime.format(timeFormatter)
             } catch (e2: Exception) {
                 isoString
             }
        }
    }

    private fun buildMockPassInfo(code: String, hasReturnLimit: Boolean, actualReturnTime: String = ""): ScannedPassInfo {
        val now = ZonedDateTime.now()
        val deadline = if (hasReturnLimit) now.plusHours(3) else null
        
        return ScannedPassInfo(
            name = "Kioga Lee Medrano (TEST)",
            code = code,
            date = now.format(dateFormatter),
            exitTime = now.format(timeFormatter),
            returnDeadline = deadline?.format(timeFormatter) ?: "Sin regreso",
            actualReturnTime = actualReturnTime
        )
    }

    // ─── Debug Mock Helpers ───────────────────────────────────────────────────

    fun mockValidQR() = processCode("PASE004")
    fun mockNoReturnQR() = processCode("JUST001")
    fun mockInvalidQR() {
        _uiState.value = _uiState.value.copy(
            status = ScannerStatus.InvalidCode("Código \"INVALID\" no registrado (MOCK).")
        )
    }

    // ─── State Management ─────────────────────────────────────────────────────

    fun clearErrorToast() {
        _uiState.value = _uiState.value.copy(errorToast = null)
    }

    fun resetScanner() {
        _uiState.value = _uiState.value.copy(
            status = ScannerStatus.Idle,
            isQrInFrame = false
        )
    }
}
