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

    fun setUserInfo(name: String, email: String) {
        _uiState.update { it.copy(fullName = name, email = email) }
    }

    fun onDateClick() {
        _uiState.update { it.copy(showDatePicker = true) }
    }

    fun onDateDismiss() {
        _uiState.update { it.copy(showDatePicker = false) }
    }

    fun onDateSelected(date: LocalDate) {
        _uiState.update { 
            it.copy(
                selectedDate = date,
                showDatePicker = false
            )
        }
    }

    fun onDetailsChanged(newDetails: String) {
        if (newDetails.length <= _uiState.value.detailsLimit) {
            _uiState.update { it.copy(details = newDetails) }
        }
    }

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

    fun removeFile(uri: Uri) {
        _uiState.update { it.copy(attachedUris = it.attachedUris - uri) }
    }

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

    fun showError(message: String) {
        _uiState.update { it.copy(error = message) }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }

    fun resetSuccess() {
        _uiState.update { it.copy(isSuccess = false) }
    }
}
