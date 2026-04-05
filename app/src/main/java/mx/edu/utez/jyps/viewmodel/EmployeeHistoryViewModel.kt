package mx.edu.utez.jyps.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import mx.edu.utez.jyps.ui.components.cards.HistoryItem
import mx.edu.utez.jyps.ui.components.status.HistoryStatus

/**
 * State representing the global employee history list and active modal overlays.
 */
data class EmployeeHistoryState(
    val pases: List<HistoryItem> = emptyList(),
    val justifications: List<HistoryItem> = emptyList(),
    val requestToDelete: String? = null,
    val requestToEditPass: HistoryItem? = null,
    val requestToEditJustification: HistoryItem? = null,
    val isSuccessOp: Boolean = false,
    val opMessage: String? = null
)

/**
 * ViewModel responsible for tracking active history requests and allowing 
 * mutations (Edit/Delete) on items stuck in state PENDIENTE.
 */
class EmployeeHistoryViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(EmployeeHistoryState())
    val uiState: StateFlow<EmployeeHistoryState> = _uiState.asStateFlow()

    init {
        loadDummyData()
    }

    private fun loadDummyData() {
        val pasesItems = listOf(
            HistoryItem("4", "Pase de Salida", HistoryStatus.APROBADO, "Salida a reunión externa", "27/3/2026", "11:00", "PASE004"),
            HistoryItem("5", "Pase de Salida", HistoryStatus.PENDIENTE, "Cita con dentista - Limpieza dental programada", "28/2/2026", "10:00", "PASE005", fileName = "comprobante_cita_dental.pdf"),
            HistoryItem("1", "Pase de Salida", HistoryStatus.USADO, "Trámite bancario - Gestión de crédito hipotecario", "25/2/2026", "14:30", "PASE001", internalInfo = "Este pase ya fue utilizado y no se puede usar más."),
            HistoryItem("2", "Pase de Salida", HistoryStatus.RECHAZADO, "Urgencia personal", "22/2/2026", "15:00", "PASE002", rejectionReason = "Motivo insuficiente. Se requiere documentación y más detalle sobre la urgencia."),
            HistoryItem("3", "Pase de Salida", HistoryStatus.CADUCADO, "Salida a comer", "20/2/2026", "13:00", "PASE003", rejectionReason = "Pase no utilizado durante la jornada.")
        )
        val justificantesItems = listOf(
            HistoryItem("10", "Justificante", HistoryStatus.APROBADO, "Consulta médica general - Revisión periódica", "24/2/2026", "10:00", "JUST001", fileName = "receta_medica.pdf"),
            HistoryItem("11", "Justificante", HistoryStatus.PENDIENTE, "Cita con dentista - Limpieza dental programada", "28/2/2026", "10:00", "JUST002", fileName = "comprobante_cita_dental.pdf"),
            HistoryItem("12", "Justificante", HistoryStatus.RECHAZADO, "Asunto personal sin documentación", "23/2/2026", "09:15", "JUST003", rejectionReason = "No se proporcionó evidencia médica o motivo válido.")
        )
        _uiState.update { it.copy(pases = pasesItems, justifications = justificantesItems) }
    }

    // ACTIONS: Delete
    fun promptDelete(id: String) {
        _uiState.update { it.copy(requestToDelete = id) }
    }

    fun dismissDelete() {
        _uiState.update { it.copy(requestToDelete = null) }
    }

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
    fun promptEditPass(item: HistoryItem) {
        _uiState.update { it.copy(requestToEditPass = item) }
    }

    fun dismissEditPass() {
        _uiState.update { it.copy(requestToEditPass = null) }
    }

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

    // ACTIONS: Edit Justification
    fun promptEditJustification(item: HistoryItem) {
        _uiState.update { it.copy(requestToEditJustification = item) }
    }

    fun dismissEditJustification() {
        _uiState.update { it.copy(requestToEditJustification = null) }
    }

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

    fun clearOpMessage() {
        _uiState.update { it.copy(isSuccessOp = false, opMessage = null) }
    }
}
