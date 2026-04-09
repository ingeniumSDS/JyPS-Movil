package mx.edu.utez.jyps.viewmodel

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.time.LocalDate
import java.time.format.DateTimeFormatter

/**
 * State class for the Justification Request screen.
 *
 * @property fullName Session populated name.
 * @property email Session populated email limit.
 * @property selectedDate Validated target parameter constraints day.
 * @property details Explanation parameter.
 * @property detailsMinLimit UI Limit minimum range limit validation limit.
 * @property detailsLimit Max limits per data.
 * @property attachedUris Selected attachments from gallery.
 * @property maxFiles Upper DFR bounds for items.
 * @property maxFileSizeMb Hard cap metric checks.
 * @property showDatePicker Trigger Boolean for UI prompt UI.
 * @property isLoading Network lock.
 * @property isSuccess Transversal UI state indicating request success.
 * @property error Active block error mapping layout text message display constraints.
 */
data class JustificationRequestState(
    val fullName: String = "Juan Pérez García", // Dummy data
    val email: String = "juan.perez@utez.edu.mx", // Dummy data
    val selectedDate: LocalDate? = null,
    val details: String = "",
    val detailsMinLimit: Int = 25,
    val detailsLimit: Int = 255,
    val attachedUris: List<Uri> = emptyList(),
    val maxFiles: Int = 3,
    val maxFileSizeMb: Int = 3,
    val showDatePicker: Boolean = false,
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val error: String? = null
) {
    val dateDisplay: String
        get() = selectedDate?.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) ?: ""

    val isFormValid: Boolean
        get() = !isLoading && selectedDate != null && details.length in detailsMinLimit..detailsLimit
}

/**
 * ViewModel managing the Justificante request flow.
 */
class JustificationRequestViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(JustificationRequestState())
    val uiState: StateFlow<JustificationRequestState> = _uiState.asStateFlow()

    /**
     * Maps global settings natively.
     *
     * @param name Mapped global Name.
     * @param email Mapped identity.
     */
    fun setUserInfo(name: String, email: String) {
        _uiState.update { it.copy(fullName = name, email = email) }
    }

    /** Prompts Material widget logic execution sequence. */
    fun onDateClick() {
        _uiState.update { it.copy(showDatePicker = true) }
    }

    /** Hides the Material Date Picker widget. */
    fun onDateDismiss() {
        _uiState.update { it.copy(showDatePicker = false) }
    }

    /**
     * Links output instance.
     *
     * @param date User selected sequence selection mapping.
     */
    fun onDateSelected(date: LocalDate) {
        _uiState.update { 
            it.copy(
                selectedDate = date,
                showDatePicker = false
            )
        }
    }

    /**
     * Binds real-time description limits matching sequences strings explicitly mapping.
     *
     * @param newDetails Raw input updates constraints limits execution parameters mapped logic definitions mapping explicitly strings checking parameters parameters mapping natively data constraint text field text.
     */
    fun onDetailsChanged(newDetails: String) {
        if (newDetails.length <= _uiState.value.detailsLimit) {
            _uiState.update { it.copy(details = newDetails) }
        }
    }

    /**
     * Upload parameter bindings.
     *
     * @param context Host OS activity natively.
     * @param uri Scoped storage pointer format.
     */
    fun onFileAttached(context: Context, uri: Uri?) {
        if (uri == null) return
        val state = _uiState.value
        
        if (state.attachedUris.size >= state.maxFiles) {
            showError("No puedes adjuntar más de ${state.maxFiles} archivos.")
            return
        }

        // Validate MIME type
        val mimeType = context.contentResolver.getType(uri)
        val validMimeTypes = listOf("image/jpeg", "image/png", "application/pdf")
        if (mimeType == null || !validMimeTypes.contains(mimeType)) {
            showError("Formato no permitido. Solo se acepta PDF, JPG o PNG.")
            return
        }

        // Validate File Size (Max 3MB = 3 * 1024 * 1024 bytes)
        var fileSize = 0L
        context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
            val sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE)
            if (sizeIndex != -1 && cursor.moveToFirst()) {
                fileSize = cursor.getLong(sizeIndex)
            }
        }
        
        val maxBytes = state.maxFileSizeMb * 1024 * 1024
        if (fileSize > maxBytes) {
            showError("El archivo excede el límite de ${state.maxFileSizeMb}MB.")
            return
        }

        // File is valid
        _uiState.update { it.copy(attachedUris = it.attachedUris + uri) }
    }

    /**
     * Purges pointer sequence array metadata.
     *
     * @param uri URI targeted string map execution item pointer map sequence metadata arrays explicitly memory sequence logic boundaries validation target map mappings constraints explicitly parameters targets target memory strings targets.
     */
    fun removeFile(uri: Uri) {
        _uiState.update { it.copy(attachedUris = it.attachedUris - uri) }
    }

    /**
     * Constructs and executes submit API boundary natively sequence map.
     */
    fun onSubmit() {
        if (!_uiState.value.isFormValid) return
        
        _uiState.update { it.copy(isLoading = true, error = null) }
        
        // Simulating API call & DFR requirements (Register -> Notify Boss -> set status Pending)
        // Dummy backend logic simulated by simple success flag
        _uiState.update { 
            it.copy(
                isLoading = false,
                isSuccess = true
            ) 
        }
    }

    /**
     * Injects overlay UI alert sequence mappings parameters text natively parameters message parameters execution variables map constraint mappings constraint natively explicitly target message sequences bounds text array mapping.
     *
     * @param message Text array mapped parameters mapping explicitly bounds target array boundaries explicit limits target limits strings boundaries explicitly properties validation.
     */
    fun showError(message: String) {
        _uiState.update { it.copy(error = message) }
    }

    /** Unbinds error flag string natively limit target sequences arrays definitions map explicitly. */
    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }

    /** Reverts mapping flags parameters constraint boundaries variables validation mapped states map states explicit boundaries boolean boundaries explicitly limit mapping target boundaries constraints. */
    fun resetSuccess() {
        _uiState.update { it.copy(isSuccess = false) }
    }
}
