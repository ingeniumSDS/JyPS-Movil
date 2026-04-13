package mx.edu.utez.jyps.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import mx.edu.utez.jyps.data.model.HistoryItem
import mx.edu.utez.jyps.data.model.EstadosIncidencia
import mx.edu.utez.jyps.data.local.FileMetadataStore
import android.net.Uri
import mx.edu.utez.jyps.data.network.RetrofitInstance
import mx.edu.utez.jyps.data.repository.JustificationRepository
import mx.edu.utez.jyps.data.repository.PreferencesManager
import timber.log.Timber

/**
 * State representing the global employee history list and active modal overlays.
 *
 * @property pases List of registered exit passes for the employee.
 * @property justifications List of registered justifications for the employee.
 * @property requestToDelete ID of the item currently pending deletion confirmation.
 * @property requestToEditPass Item staged for edition in the pass dialog.
 * @property requestToEditJustification Item staged for edition in the justification dialog.
 * @property requestToShowQr Item requesting its QR to be displayed.
 * @property isSuccessOp Indicates if the last CRUD operation was successful.
 * @property opMessage Status message detailing the outcome of the last operation.
 */
data class EmployeeHistoryState(
    val pases: List<HistoryItem> = emptyList(),
    val justifications: List<HistoryItem> = emptyList(),
    val requestToDelete: String? = null,
    val requestToEditPass: HistoryItem? = null,
    val requestToEditJustification: HistoryItem? = null,
    val requestToShowQr: HistoryItem? = null,
    val selectedItemForDetail: HistoryItem? = null,
    val isLoading: Boolean = false,
    val downloadingFileName: String? = null,
    val downloadedFiles: Map<String, android.net.Uri> = emptyMap(),
    val isSuccessOp: Boolean = false,
    val opMessage: String? = null
)

/**
 * ViewModel responsible for tracking active history requests and allowing 
 * mutations (Edit/Delete) on items stuck in state PENDIENTE.
 */
class EmployeeHistoryViewModel(application: Application) : AndroidViewModel(application) {
    
    private val preferencesManager = PreferencesManager(application)
    private val repository = JustificationRepository(RetrofitInstance.api, application)
    private val fileMetadataStore = FileMetadataStore(application)
    private val evidenceDir = java.io.File(application.filesDir, "evidences").apply { if (!exists()) mkdirs() }
    
    private val _uiState = MutableStateFlow(EmployeeHistoryState())
    val uiState: StateFlow<EmployeeHistoryState> = _uiState.asStateFlow()
 
    init {
        viewModelScope.launch {
            loadPersistedFiles()
            refreshHistory()
        }
    }

    private suspend fun loadPersistedFiles() {
        kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) {
            val mappings = fileMetadataStore.getAllMappings()
            val downloaded = mutableMapOf<String, Uri>()
            
            mappings.forEach { (techName, path) ->
                val file = java.io.File(path)
                if (file.exists()) {
                    try {
                        val uri = androidx.core.content.FileProvider.getUriForFile(
                            getApplication(),
                            "${getApplication<Application>().packageName}.provider",
                            file
                        )
                        synchronized(downloaded) { downloaded[techName] = uri }
                    } catch (e: Exception) {
                        Timber.e(e, "Error al generar URI para archivo persistido: $techName")
                    }
                }
            }
            
            _uiState.update { it.copy(downloadedFiles = downloaded) }
        }
    }
 
    /**
     * Refresca la información del historial consumiendo los servicios del backend.
     */
    fun refreshHistory() {
        if (_uiState.value.isLoading) return
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val email = preferencesManager.userEmailFlow.first() ?: ""
            val userId = preferencesManager.userIdFlow.first()
            
            // Cargar datos de prueba para Juan Perez pero también intentar reales
            if (email == "juan.perez@utez.edu.mx") {
                loadDummyData()
            }
            
            // Siempre intentar cargar reales si el ID es válido
            if (userId > 0) {
                fetchRealHistory(userId)
            }
            _uiState.update { it.copy(isLoading = false) }
        }
    }

    /**
     * Performs a background synchronization of real justifications from the repository.
     *
     * @param userId The ID of the employee to filter.
     */
    private suspend fun fetchRealHistory(userId: Long) {
        if (userId <= 0) return
        
        val result = repository.getJustificantesPorEmpleado(userId)
        
        result.onSuccess { responses ->
            val items = responses.map { res ->
                HistoryItem(
                    id = res.id.toString(),
                    type = "Justificante",
                    status = try { 
                        EstadosIncidencia.valueOf(res.status.uppercase()) 
                    } catch (e: Exception) { 
                        Timber.w("Estado desconocido: ${res.status}")
                        EstadosIncidencia.PENDIENTE 
                    },
                    description = res.description,
                    date = res.requestedDate,
                    time = "N/A",
                    code = "JUST-${res.id}",
                    attachments = res.attachments.map { att ->
                        mx.edu.utez.jyps.data.model.AttachmentItem(
                            technicalName = att.downloadUrl.substringAfterLast("/"),
                            displayName = att.originalName
                        )
                    },
                    rejectionReason = res.managerComment
                )
            }
            _uiState.update { it.copy(justifications = items) }
        }.onFailure { e ->
            Timber.e(e, "Error al cargar el historial real para el usuario $userId")
            val errorMessage = when {
                e is retrofit2.HttpException && e.code() == 401 -> {
                    Timber.w("Sesión caducada (401). Iniciando limpieza de datos sensibles.")
                    clearSecureCache()
                    "Sesión caducada. Por favor, vuelve a ingresar."
                }
                e is retrofit2.HttpException && (e.code() == 403 || e.code() == 500) -> {
                    // Mantenemos los archivos intactos, solo informamos la falta de permisos
                    "Acceso denegado. Contacta a soporte si crees que es un error."
                }
                else -> "Error al sincronizar con el servidor."
            }

            _uiState.update { it.copy(isLoading = false, isSuccessOp = false, opMessage = errorMessage) }
        }
    }

    private fun loadDummyData() {
        val pasesItems = listOf(
            HistoryItem("4", "Pase de Salida", EstadosIncidencia.APROBADO, "Salida a reunión externa", "27/3/2026", "11:00", "PASE004"),
            HistoryItem("5", "Pase de Salida", EstadosIncidencia.PENDIENTE, "Cita con dentista - Limpieza dental programada", "28/2/2026", "10:00", "PASE005", attachments = listOf(mx.edu.utez.jyps.data.model.AttachmentItem("tech.pdf", "comprobante_cita_dental.pdf"))),
            HistoryItem("1", "Pase de Salida", EstadosIncidencia.USADO, "Trámite bancario - Gestión de crédito hipotecario", "25/2/2026", "14:30", "PASE001", internalInfo = "Este pase ya fue utilizado y no se puede usar más."),
            HistoryItem("2", "Pase de Salida", EstadosIncidencia.RECHAZADO, "Urgencia personal", "22/2/2026", "15:00", "PASE002", rejectionReason = "Motivo insuficiente. Se requiere documentación y más detalle sobre la urgencia."),
            HistoryItem("3", "Pase de Salida", EstadosIncidencia.CADUCADO, "Salida a comer", "20/2/2026", "13:00", "PASE003", rejectionReason = "Pase no utilizado durante la jornada.")
        )
        val justificantesItems = listOf(
            HistoryItem("10", "Justificante", EstadosIncidencia.APROBADO, "Consulta médica general - Revisión periódica", "24/2/2026", "10:00", "JUST001", attachments = listOf(mx.edu.utez.jyps.data.model.AttachmentItem("tech.pdf", "receta_medica.pdf"))),
            HistoryItem("11", "Justificante", EstadosIncidencia.PENDIENTE, "Cita con dentista - Limpieza dental programada", "28/2/2026", "10:00", "JUST002", attachments = listOf(mx.edu.utez.jyps.data.model.AttachmentItem("dental.pdf", "comprobante_cita_dental.pdf"))),
            HistoryItem("12", "Justificante", EstadosIncidencia.RECHAZADO, "Asunto personal sin documentación", "23/2/2026", "09:15", "JUST003", rejectionReason = "No se proporcionó evidencia médica o motivo válido.")
        )
        _uiState.update { it.copy(pases = pasesItems, justifications = justificantesItems) }
    }

    // Delete process management
    /**
     * Prompts the deletion confirmation dialog for a specific item.
     *
     * @param id The unique identifier of the request to be deleted.
     */
    fun promptDelete(id: String) {
        _uiState.update { it.copy(requestToDelete = id) }
    }

    /** Hides the deletion confirmation dialog. */
    fun dismissDelete() {
        _uiState.update { it.copy(requestToDelete = null) }
    }

    /** Permanently removes the selected request from the UI state. */
    fun confirmDelete() {
        val targetId = _uiState.value.requestToDelete ?: return
        
        _uiState.update { state ->
            val updatedPases = state.pases.filterNot { it.id == targetId }
            val updatedJustifications = state.justifications.filterNot { it.id == targetId }
            state.copy(
                pases = updatedPases,
                justifications = updatedJustifications,
                requestToDelete = null,
                isSuccessOp = true,
                opMessage = "Solicitud eliminada con éxito."
            )
        }
    }

    // ACTIONS: Edit Pass
    /**
     * Prepares an exit pass for edition and opens the dialog.
     *
     * @param item The target [HistoryItem] to be edited.
     */
    fun promptEditPass(item: HistoryItem) {
        _uiState.update { it.copy(requestToEditPass = item) }
    }

    /** Closes the pass edit dialog. */
    fun dismissEditPass() {
        _uiState.update { it.copy(requestToEditPass = null) }
    }

    /**
     * Saves the modified exit pass details.
     *
     * @param id The unique identifier of the pass.
     * @param newDetails The updated textual justification details.
     * @param newTime The updated scheduled exit time.
     */
    fun saveEditPass(id: String, newDetails: String, newTime: String) {
        _uiState.update { state ->
            val updatedPases = state.pases.map { 
                if (it.id == id) it.copy(description = newDetails, time = newTime) else it 
            }
            state.copy(
                pases = updatedPases,
                requestToEditPass = null,
                isSuccessOp = true,
                opMessage = "Pase actualizado con éxito."
            )
        }
    }

    /**
     * Prepares a justification for edition and opens the dialog.
     *
     * @param item The target [HistoryItem] to be edited.
     */
    fun promptEditJustification(item: HistoryItem) {
        _uiState.update { it.copy(requestToEditJustification = item) }
    }

    /** Closes the justification edit dialog. */
    fun dismissEditJustification() {
        _uiState.update { it.copy(requestToEditJustification = null) }
    }

    /**
     * Saves the modified justification details.
     *
     * @param id The unique identifier of the justification.
     * @param newDetails The updated textual explanation.
     */
    fun saveEditJustification(id: String, newDetails: String) {
        _uiState.update { state ->
            val updated = state.justifications.map { 
                if (it.id == id) it.copy(description = newDetails) else it 
            }
            state.copy(
                justifications = updated,
                requestToEditJustification = null,
                isSuccessOp = true,
                opMessage = "Justificante actualizado con éxito."
            )
        }
    }

    /** Clears any active operation status message from the screen. */
    fun clearOpMessage() {
        _uiState.update { it.copy(isSuccessOp = false, opMessage = null) }
    }

    /**
     * Requests the visualization of the QR code for a specific pass.
     *
     * @param item The pass whose QR will be displayed.
     */
    fun promptShowQr(item: HistoryItem) {
        /* Work In Progress - Future Features - No Delete */
        _uiState.update { it.copy(requestToShowQr = item) }
        Timber.d("Visualización de QR solicitada para el código ${item.code} [WIP]")
    }

    /** Closes the QR code dialog. */
    fun dismissShowQr() {
        _uiState.update { it.copy(requestToShowQr = null) }
    }

    /**
     * Handles the callback when a user tries to download the QR code to their gallery.
     *
     * @param isSuccess True if the IO operation saved the image successfully.
     */
    fun dispatchDownloadQrResult(isSuccess: Boolean) {
        if (isSuccess) {
            _uiState.update { it.copy(
                requestToShowQr = null,
                isSuccessOp = true,
                opMessage = "QR descargado en la galería exitosamente."
            ) }
        } else {
            _uiState.update { it.copy(
                isSuccessOp = true, // Force toast
                opMessage = "Error al intentar guardar el QR en la galería."
            ) }
        }
    }

    /**
     * Stashes a history item in state to trigger its detail view and fetches 
     * fresh data from the server for justifications.
     *
     * @param item The target log record to display.
     */
    fun onItemClickDetails(item: HistoryItem) {
        if (item.type == "Justificante") {
            viewModelScope.launch {
                repository.getJustificanteDetalles(item.id.toLong()).onSuccess { res ->
                    val updatedItem = item.copy(
                        description = res.description,
                        rejectionReason = res.managerComment,
                        attachments = res.attachments.map { att ->
                            mx.edu.utez.jyps.data.model.AttachmentItem(
                                technicalName = att.downloadUrl.substringAfterLast("/"),
                                displayName = att.originalName
                            )
                        },
                        internalInfo = if (res.attachments.isNotEmpty()) "ID Empleado: ${res.employeeId}" else null
                    )
                    _uiState.update { it.copy(selectedItemForDetail = updatedItem) }
                }.onFailure {
                    // Fallback to current item if network fails
                    _uiState.update { it.copy(selectedItemForDetail = item) }
                }
            }
        } else {
            _uiState.update { it.copy(selectedItemForDetail = item) }
        }
    }

    /**
     * Triggers the file download process for a justification evidence.
     * 
     * @param empleadoId The owner of the file.
     * @param fileName The server-side filename.
     */
    fun downloadJustificationFile(empleadoId: Long, fileName: String) {
        // 1. If already downloading something, block
        if (_uiState.value.downloadingFileName != null) return
        
        // 2. If already downloaded, just open it (with a brief loader for feedback)
        _uiState.value.downloadedFiles[fileName]?.let { uri ->
            viewModelScope.launch {
                _uiState.update { it.copy(downloadingFileName = fileName) }
                kotlinx.coroutines.delay(400) // Brief delay to show loader as requested
                openFileIntent(uri, fileName)
                _uiState.update { it.copy(downloadingFileName = null) }
            }
            return
        }
        
        viewModelScope.launch {
            _uiState.update { it.copy(downloadingFileName = fileName) }
            Timber.d("Validando descarga real de: $fileName...")
            
            repository.downloadJustificanteFile(empleadoId, fileName).onSuccess { body ->
                // Solo informamos cuando tenemos respuesta positiva del servidor
                _uiState.update { it.copy(
                    isSuccessOp = true,
                    opMessage = "Descarga iniciada con éxito."
                ) }
                saveAndOpenProviderFile(body, fileName)
            }.onFailure { e ->
                Timber.e(e, "Error al bajar el archivo del servidor.")
                _uiState.update { it.copy(
                    downloadingFileName = null,
                    isSuccessOp = true,
                    opMessage = "No se pudo conectar con el servidor de archivos."
                ) }
            }
        }
    }

    /**
     * Persists the byte stream to local cache and notifies the UI.
     */
    private fun saveAndOpenProviderFile(body: okhttp3.ResponseBody, fileName: String) {
        viewModelScope.launch(kotlinx.coroutines.Dispatchers.IO) {
            try {
                val file = java.io.File(evidenceDir, fileName)
                body.byteStream().use { input ->
                    file.outputStream().use { output ->
                        input.copyTo(output)
                    }
                }
                
                // Persist mapping securely
                fileMetadataStore.saveFileMapping(fileName, file.absolutePath)
                
                val uri = androidx.core.content.FileProvider.getUriForFile(
                    getApplication(),
                    "${getApplication<Application>().packageName}.provider",
                    file
                )
                
                kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.Main) {
                    openFileIntent(uri, fileName)
                }
                
                _uiState.update { state ->
                    state.copy(
                        downloadedFiles = state.downloadedFiles + (fileName to uri),
                        downloadingFileName = null,
                        opMessage = "Archivo listo para visualizar."
                    )
                }
                
                // Intent logic to open the file automatically
                openFileIntent(uri, fileName)
                
            } catch (e: Exception) {
                Timber.e(e, "Error al guardar el archivo en caché")
            }
        }
    }

    private fun openFileIntent(uri: android.net.Uri, fileName: String) {
        val intent = android.content.Intent(android.content.Intent.ACTION_VIEW).apply {
            setDataAndType(uri, getMimeType(fileName))
            addFlags(android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION)
            addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        getApplication<Application>().startActivity(intent)
    }

    private fun getMimeType(fileName: String): String {
        return if (fileName.endsWith(".pdf", true)) "application/pdf" 
               else "image/*"
    }

    /** Clears the selected item to hide the detail overlay. */
    fun dismissDetails() {
        _uiState.update { it.copy(selectedItemForDetail = null) }
    }

    /**
     * Purges all local evidence files and secure metadata.
     * Call this on logout or session expiration.
     */
    fun clearSecureCache() {
        viewModelScope.launch(kotlinx.coroutines.Dispatchers.IO) {
            try {
                // 1. Clear Encrypted Metadata
                fileMetadataStore.clearAll()
                
                // 2. Delete Physical Files
                if (evidenceDir.exists()) {
                    evidenceDir.listFiles()?.forEach { it.delete() }
                    evidenceDir.delete()
                }
                
                // 3. Clear UI state
                _uiState.update { it.copy(downloadedFiles = emptyMap()) }
                
                Timber.i("Limpieza de caché segura completada con éxito.")
            } catch (e: Exception) {
                Timber.e(e, "Error durante la limpieza de caché segura")
            }
        }
    }
}
