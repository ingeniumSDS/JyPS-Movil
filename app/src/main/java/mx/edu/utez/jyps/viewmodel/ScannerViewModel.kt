package mx.edu.utez.jyps.viewmodel

import androidx.lifecycle.ViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Holds all identifying and timing information extracted from a scanned pass.
 *
 * @param name Full name of the pass holder.
 * @param email Institutional email of the holder.
 * @param code The alphanumeric QR code.
 * @param date Human-readable date of the pass.
 * @param exitTime Timestamp recorded when the holder exited.
 * @param returnDeadline Latest time the holder must return (empty if no-return pass).
 * @param actualReturnTime Timestamp recorded on the second scan (return). Empty on first scan.
 */
data class ScannedPassInfo(
    val name: String,
    val email: String,
    val code: String,
    val date: String,
    val exitTime: String,
    val returnDeadline: String,
    val actualReturnTime: String = ""
)

/**
 * Represents every possible outcome of scanning a QR code.
 * Each subclass maps to one of the 5 Material 3 Dialogs shown in [ScanResultDialog].
 */
sealed class ScannerStatus {
    /** No scan in progress — default idle state. */
    object Idle : ScannerStatus()

    /**
     * First scan: exit granted with a 3-hour return deadline.
     * @param info Pass data to display.
     */
    data class ExitGranted(val info: ScannedPassInfo) : ScannerStatus()

    /**
     * First scan: exit granted with no return required (end-of-shift pass).
     * @param info Pass data to display.
     */
    data class ExitNoReturn(val info: ScannedPassInfo) : ScannerStatus()

    /**
     * Second scan: holder returned within the 3-hour window.
     * @param info Pass data including the recorded return time.
     */
    data class ReturnOnTime(val info: ScannedPassInfo) : ScannerStatus()

    /**
     * Second scan: holder returned after the 3-hour deadline.
     * @param info Pass data including the recorded late return time.
     */
    data class ReturnLate(val info: ScannedPassInfo) : ScannerStatus()

    /**
     * The scanned code is not registered in the system.
     * @param reason Human-readable explanation.
     */
    data class InvalidCode(val reason: String) : ScannerStatus()

    /**
     * The pass existed but was never used before its expiry date/end-of-shift.
     * Triggered by scanning a code listed in the expired dummy data set.
     * @param info Partial pass data to identify the holder.
     */
    data class ExpiredPass(val info: ScannedPassInfo) : ScannerStatus()

    /**
     * The pass has already completed its full usage cycle and cannot be reused.
     * Covers both: exit+return passes (used twice) and no-return passes (used once).
     * @param info Pass data to display context to the guard.
     */
    data class AlreadyUsed(val info: ScannedPassInfo) : ScannerStatus()
}

/**
 * Full reactive UI state for the Security Scanner screen.
 *
 * @param status Current result state of the scanner.
 * @param manualCode Text currently typed in the manual code field.
 * @param errorToast Short-lived error message for the toast overlay.
 * @param isQrInFrame True when the camera analyzer has a QR code in its current frame.
 */
data class ScannerUiState(
    val status: ScannerStatus = ScannerStatus.Idle,
    val manualCode: String = "",
    val errorToast: String? = null,
    val isQrInFrame: Boolean = false
)

/**
 * ViewModel managing the UI state for the Security Scanner.
 *
 * Business rules:
 * - First scan of a known code → records exit timestamp in [exitRegistry].
 * - Second scan of the same code → checks elapsed time against the 3-hour limit.
 * - "No-return" passes (end-of-shift) always show [ScannerStatus.ExitNoReturn] on every scan.
 * - All state persists only in memory for the duration of the session.
 */
class ScannerViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(ScannerUiState())
    val uiState: StateFlow<ScannerUiState> = _uiState.asStateFlow()

    /**
     * In-memory registry mapping a pass code to the Unix timestamp (ms) of first scan (exit).
     * Cleared automatically when the ViewModel is destroyed (session ends / logout).
     */
    private val exitRegistry = HashMap<String, Long>()

    /**
     * Tracks pass codes that have completed their full usage cycle (exit+return or single exit).
     * Once a code is here, any further scan results in [ScannerStatus.AlreadyUsed].
     * Pre-seeded with "USED" for manual testing purposes.
     */
    private val usedRegistry = mutableSetOf("USED")

    /**
     * Dummy set of codes that represent passes approved but never used before expiry.
     * In production this will be determined by a backend timestamp comparison.
     */
    private val expiredCodes = setOf("EXPIRED", "OLD001", "EXP002")

    /**
     * Dummy data set of recognized pass codes.
     * Key: QR code string. Value: true = has 3h return limit, false = no-return (end-of-shift).
     */
    private val validCodes = mapOf(
        "PASE004" to true,  // Exit pass with 3-hour return window
        "JUST001" to false  // Justification, no return required
    )

    private val locale = Locale("es", "MX")
    private val dateFormatter = SimpleDateFormat("EEEE, d 'de' MMMM 'de' yyyy", locale)
    private val timeFormatter = SimpleDateFormat("hh:mm a", locale)

    // ─── Camera & Manual Input ────────────────────────────────────────────────

    /**
     * Called by [QRCodeAnalyzer] when a QR code string is decoded from a camera frame.
     * Delegates to [processCode] with the raw decoded value.
     *
     * @param rawValue Extracted string constraint mapped natively variables validation target explicit string value bounds text logic.
     */
    fun onQrCodeDetected(rawValue: String) {
        processCode(rawValue.trim().uppercase())
    }

    /**
     * Updates the manual code text field. Clears any active error toast.
     *
     * @param newValue Mapped limits bounds constraints target logic string boundary value explicit property explicitly strings.
     */
    fun onManualCodeChange(newValue: String) {
        _uiState.value = _uiState.value.copy(manualCode = newValue, errorToast = null)
    }

    /**
     * Triggers verification of the manual code input field.
     */
    fun verifyManualCode() {
        processCode(_uiState.value.manualCode.trim().uppercase())
    }

    /**
     * Updates the real-time flag indicating whether a QR is visible in the camera frame.
     * Used by the [QRCodeAnalyzer] overlay signal.
     *
     * @param isInFrame Targets mapped value limits explicitly metadata properties text parameter bound arrays sequences logic limit variables string mapping map mappings constraint bool explicit mapping explicit limits.
     */
    fun setQrInFrame(isInFrame: Boolean) {
        if (_uiState.value.isQrInFrame != isInFrame) {
            _uiState.value = _uiState.value.copy(isQrInFrame = isInFrame)
        }
    }

    // ─── Core Business Logic ─────────────────────────────────────────────────

    /**
     * Central state machine: validates a code and determines which Dialog to display.
     *
     * Decision tree:
     * 1. Is the code in [expiredCodes]?  → [ScannerStatus.ExpiredPass]
     * 2. Is the code in [usedRegistry]?  → [ScannerStatus.AlreadyUsed]
     * 3. Is the code in [validCodes]?    → No → [ScannerStatus.InvalidCode]
     * 4. Is it in [exitRegistry]?        → No → first scan → [ExitGranted] or [ExitNoReturn]
     * 5. Is it in [exitRegistry]?        → Yes → second scan → [ReturnOnTime] or [ReturnLate]
     *
     * @param code The decoded alphanumeric sequence to validate against records.
     */
    private fun processCode(code: String) {
        // Check expired passes first
        if (expiredCodes.contains(code)) {
            _uiState.value = _uiState.value.copy(
                status = ScannerStatus.ExpiredPass(
                    buildPassInfo(code, hasReturnLimit = true)
                )
            )
            return
        }

        // Check fully consumed passes
        if (usedRegistry.contains(code)) {
            _uiState.value = _uiState.value.copy(
                status = ScannerStatus.AlreadyUsed(
                    buildPassInfo(code, hasReturnLimit = validCodes[code] ?: true)
                )
            )
            return
        }

        val hasReturnLimit = validCodes[code]
            ?: run {
                _uiState.value = _uiState.value.copy(
                    status = ScannerStatus.InvalidCode("Código \"$code\" no está registrado en el sistema.")
                )
                return
            }

        val now = System.currentTimeMillis()
        val exitTimestamp = exitRegistry[code]

        if (exitTimestamp == null) {
            // First scan → register exit
            exitRegistry[code] = now
            val info = buildPassInfo(code, hasReturnLimit)

            if (!hasReturnLimit) {
                // No-return pass: single use — mark as used immediately after exit
                usedRegistry.add(code)
                exitRegistry.remove(code)
            }

            _uiState.value = _uiState.value.copy(
                status = if (hasReturnLimit) ScannerStatus.ExitGranted(info)
                         else ScannerStatus.ExitNoReturn(info)
            )
        } else {
            // Second scan → verify return
            val elapsedMs = now - exitTimestamp
            val limitMs = 3 * 60 * 60 * 1000L
            val actualReturnTime = timeFormatter.format(Date(now))
            val info = buildPassInfo(code, hasReturnLimit, actualReturnTime)

            // Pass with return limit is now fully consumed after the return scan
            usedRegistry.add(code)
            exitRegistry.remove(code)

            _uiState.value = _uiState.value.copy(
                status = if (elapsedMs <= limitMs) ScannerStatus.ReturnOnTime(info)
                         else ScannerStatus.ReturnLate(info)
            )
        }
    }

    /**
     * Builds a [ScannedPassInfo] using the current time and known dummy holder data.
     *
     * @param code The validated pass code.
     * @param hasReturnLimit Whether to calculate and include a return deadline.
     * @param actualReturnTime If this is a return scan, the formatted return time. Empty otherwise.
     */
    private fun buildPassInfo(
        code: String,
        hasReturnLimit: Boolean,
        actualReturnTime: String = ""
    ): ScannedPassInfo {
        val now = Date()
        val exitTime = timeFormatter.format(now)
        val dateStr = dateFormatter.format(now)

        val returnDeadline = if (hasReturnLimit) {
            val calendar = Calendar.getInstance()
            calendar.add(Calendar.HOUR_OF_DAY, 3)
            timeFormatter.format(calendar.time)
        } else {
            "No requiere regreso (fin de jornada)"
        }

        return ScannedPassInfo(
            name = "Juan Pérez García",
            email = "juan.perez@utez.edu.mx",
            code = code,
            date = dateStr,
            exitTime = exitTime,
            returnDeadline = returnDeadline,
            actualReturnTime = actualReturnTime
        )
    }

    // ─── Debug Mock Helpers ───────────────────────────────────────────────────

    /** [DEBUG] Simulates scanning the standard exit pass. */
    fun mockValidQR() = processCode("PASE004")

    /** [DEBUG] Simulates scanning the no-return justification. */
    fun mockNoReturnQR() = processCode("JUST001")

    /** [DEBUG] Simulates scanning an unknown code. */
    fun mockInvalidQR() {
        _uiState.value = _uiState.value.copy(
            status = ScannerStatus.InvalidCode("Código \"INVALID\" no está registrado en el sistema.")
        )
    }

    // ─── State Management ─────────────────────────────────────────────────────

    /** Clears the transient error toast. */
    fun clearErrorToast() {
        _uiState.value = _uiState.value.copy(errorToast = null)
    }

    /** Resets the scanner to [ScannerStatus.Idle] and clears the QR frame flag. */
    fun resetScanner() {
        _uiState.value = _uiState.value.copy(
            status = ScannerStatus.Idle,
            isQrInFrame = false
        )
    }
}
