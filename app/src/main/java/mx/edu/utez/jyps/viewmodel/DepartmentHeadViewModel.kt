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
import mx.edu.utez.jyps.data.model.RequestItem
import mx.edu.utez.jyps.data.model.RequestStatus
import mx.edu.utez.jyps.data.model.RequestType
import mx.edu.utez.jyps.data.network.RetrofitInstance
import mx.edu.utez.jyps.data.repository.JustificationRepository
import mx.edu.utez.jyps.data.repository.PassRepository
import mx.edu.utez.jyps.data.repository.PreferencesManager
import timber.log.Timber

/**
 * Filter options available in the dashboard's chip row.
 */
enum class RequestFilter { ALL, PENDING, APPROVED, REJECTED }

/**
 * Reactive state exposed to the Department Head dashboard composable.
 *
 * @property requests Full, unfiltered list of requests.
 * @property activeFilter Currently selected chip filter.
 * @property filteredRequests Subset of [requests] matching [activeFilter].
 * @property totalCount Total request count.
 * @property pendingCount Requests awaiting decision.
 * @property approvedCount Requests approved.
 * @property rejectedCount Requests rejected.
 * @property showPendingAlert Whether to display the warning banner.
 * @property selectedItem Item currently being viewed in a detail dialog.
 * @property showRejectDialog Whether the reject reason dialog is visible.
 */
data class DepartmentHeadUiState(
    val requests: List<RequestItem> = emptyList(),
    val activeFilter: RequestFilter = RequestFilter.ALL,
    val filteredRequests: List<RequestItem> = emptyList(),
    val totalCount: Int = 0,
    val pendingCount: Int = 0,
    val approvedCount: Int = 0,
    val rejectedCount: Int = 0,
    val showPendingAlert: Boolean = false,
    val selectedItem: RequestItem? = null,
    val showRejectDialog: Boolean = false,
    val isLoading: Boolean = false
)

/**
 * ViewModel serving the Department Head dashboard.
 * Manages real request data fetching, filtering, and state transitions for approve/reject actions.
 * Follows manual dependency instantiation to comply with project standards.
 */
class DepartmentHeadViewModel(application: Application) : AndroidViewModel(application) {

    private val preferencesManager = PreferencesManager(application)
    private val justificationRepository = JustificationRepository(RetrofitInstance.api, application)
    private val passRepository = PassRepository(RetrofitInstance.api)

    private val _uiState = MutableStateFlow(DepartmentHeadUiState())
    val uiState: StateFlow<DepartmentHeadUiState> = _uiState.asStateFlow()

    /** Threshold above which the pending-alert banner becomes visible. */
    private val pendingAlertThreshold = 5

    init {
        refreshHistory()
    }

    /**
     * Synchronizes the dashboard data with the backend.
     * Fetches both justifications and passes associated with the current manager.
     */
    fun refreshHistory() {
        if (_uiState.value.isLoading) return
        
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            
            val email = preferencesManager.userEmailFlow.first() ?: ""
            val userId = preferencesManager.userIdFlow.first()
            
            // Business Rule: Mocked user does not make real API calls
            if (email == "juan.perez@utez.edu.mx") {
                loadMockData()
                _uiState.update { it.copy(isLoading = false) }
                return@launch
            }

            if (userId > 0) {
                fetchRealData(userId)
            }
            
            _uiState.update { it.copy(isLoading = false) }
        }
    }

    /**
     * Fetches justifications and passes from repositories and maps them to [RequestItem].
     * 
     * @param jefeId The manager ID to filter by.
     */
    private suspend fun fetchRealData(jefeId: Long) {
        val passesResult = passRepository.getPasesPorJefe(jefeId)
        val justificationsResult = justificationRepository.getJustificantesPorJefe(jefeId)

        val passItems = passesResult.getOrNull()?.map { res ->
            RequestItem(
                id = "P-${res.id}",
                numericId = res.id,
                employeeName = res.nombreCompleto ?: "Empleado ${res.empleadoId}",
                employeeEmail = "N/A", // Email not present in specific list responses
                requestType = RequestType.PASS,
                reason = res.descripcion ?: "Sin descripción",
                date = res.fechaSolicitud,
                time = res.horaSolicitada.substringBeforeLast(":"),
                exitTime = res.horaEsperada.substringAfter("T").substringBeforeLast(":").takeIf { it.isNotEmpty() },
                status = mapBackendStatus(res.estado)
            )
        } ?: emptyList()

        val justificationItems = justificationsResult.getOrNull()?.map { res ->
            RequestItem(
                id = "J-${res.id}",
                numericId = res.id,
                employeeName = res.nombreCompleto ?: "Empleado ${res.employeeId}",
                employeeEmail = "N/A",
                requestType = RequestType.JUSTIFICATION,
                reason = res.description,
                date = res.requestedDate,
                time = "N/A",
                status = mapBackendStatus(res.status),
                attachmentName = res.attachments.firstOrNull()?.originalName
            )
        } ?: emptyList()

        val allRequests = passItems + justificationItems
        _uiState.update { state -> 
            rebuildState(allRequests.sortedByDescending { it.date }, state.activeFilter)
        }
    }

    /**
     * Helper to map backend status strings to UI [RequestStatus].
     */
    private fun mapBackendStatus(status: String): RequestStatus = try {
        when (status.uppercase()) {
            "PENDIENTE" -> RequestStatus.PENDING
            "APROBADO" -> RequestStatus.APPROVED
            "RECHAZADO" -> RequestStatus.REJECTED
            "USADO", "A_TIEMPO", "RETARDO", "FUERA" -> RequestStatus.USED
            else -> RequestStatus.PENDING
        }
    } catch (e: Exception) {
        RequestStatus.PENDING
    }

    // region Public actions

    /**
     * Applies a new filter and recomputes the visible request list.
     *
     * @param filter The filter criterion describing which statuses should be visible.
     */
    fun onFilterChange(filter: RequestFilter) {
        _uiState.update { state ->
            state.copy(
                activeFilter = filter,
                filteredRequests = applyFilter(state.requests, filter)
            )
        }
    }

    /**
     * Opens the detail dialog for the given request.
     *
     * @param item The request item to be viewed in detail.
     */
    fun onRequestClick(item: RequestItem) {
        _uiState.update { it.copy(selectedItem = item) }
    }

    /**
     * Closes the currently open detail dialog.
     */
    fun dismissDetailDialog() {
        _uiState.update { it.copy(selectedItem = null, showRejectDialog = false) }
    }

    /**
     * Opens the reject-reason dialog while keeping the detail dialog underneath.
     */
    fun openRejectDialog() {
        _uiState.update { it.copy(showRejectDialog = true) }
    }

    /**
     * Closes only the reject-reason dialog; returns to the detail dialog.
     */
    fun dismissRejectDialog() {
        _uiState.update { it.copy(showRejectDialog = false) }
    }

    /**
     * Approves a pending request — updates status to APPROVED in backend.
     *
     * @param id Tracking unique identifier.
     */
    fun approveRequest(id: String) {
        val item = _uiState.value.requests.find { it.id == id } ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, selectedItem = null, showRejectDialog = false) }
            val userId = preferencesManager.userIdFlow.first()
            
            val result = if (item.requestType == RequestType.PASS) {
                passRepository.revisarPase(item.numericId, "APROBADO", null)
            } else {
                justificationRepository.revisarJustificante(item.numericId, "APROBADO", null)
            }

            result.onSuccess {
                refreshHistory()
            }.onFailure { e ->
                Timber.e(e, "Error approving request $id")
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    /**
     * Rejects a pending request — updates status to REJECTED in backend.
     * @param id Request identifier.
     * @param reason Rejection reason (preset or custom).
     */
    fun rejectRequest(id: String, reason: String) {
        val item = _uiState.value.requests.find { it.id == id } ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, selectedItem = null, showRejectDialog = false) }
            
            val result = if (item.requestType == RequestType.PASS) {
                passRepository.revisarPase(item.numericId, "RECHAZADO", reason)
            } else {
                justificationRepository.revisarJustificante(item.numericId, "RECHAZADO", reason)
            }

            result.onSuccess {
                refreshHistory()
            }.onFailure { e ->
                Timber.e(e, "Error rejecting request $id")
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    // endregion

    // region Internal helpers

    /**
     * Updates an arbitrary item.
     *
     * @param id The id payload target limit bounds mapping explicit mapping.
     * @param newStatus Target payload value constraints metadata explicit status map.
     */
    private fun updateRequestStatus(id: String, newStatus: RequestStatus) {
        _uiState.update { state ->
            val updated = state.requests.map { req ->
                if (req.id == id) req.copy(status = newStatus) else req
            }
            rebuildState(updated, state.activeFilter)
        }
    }

    /**
     * Recreates execution array metadata state instance explicitly binding natively limits limits maps.
     *
     * @param requests Metadata string.
     * @param filter Instance bound mapping limits values properties limits definition text mappings arrays.
     */
    private fun rebuildState(
        requests: List<RequestItem>,
        filter: RequestFilter
    ): DepartmentHeadUiState {
        val pendingCount = requests.count { it.status == RequestStatus.PENDING }
        return DepartmentHeadUiState(
            requests = requests,
            activeFilter = filter,
            filteredRequests = applyFilter(requests, filter),
            totalCount = requests.size,
            pendingCount = pendingCount,
            approvedCount = requests.count { it.status == RequestStatus.APPROVED },
            rejectedCount = requests.count { it.status == RequestStatus.REJECTED },
            showPendingAlert = pendingCount > pendingAlertThreshold
        )
    }

    /**
     * Execution constraint filters mappings validation strings arrays mappings mapped explicitly.
     *
     * @param requests Map array boundaries execution native strings.
     * @param filter Mapped variables targets properties logic execution payload limits mapping arrays definitions strings parameters bindings strings maps constraints map parameters bounds explicit array targets payload parameters limits property bounds constraints boolean maps limit strings boundaries array map.
     */
    private fun applyFilter(
        requests: List<RequestItem>,
        filter: RequestFilter
    ): List<RequestItem> = when (filter) {
        RequestFilter.ALL -> requests
        RequestFilter.PENDING -> requests.filter { it.status == RequestStatus.PENDING }
        RequestFilter.APPROVED -> requests.filter { it.status == RequestStatus.APPROVED }
        RequestFilter.REJECTED -> requests.filter { it.status == RequestStatus.REJECTED }
    }

    // endregion

    // region Mock data from Figma design

    private fun loadMockData() {
        val mock = listOf(
            req("1", "Juan Pérez García", "juan.perez@utez.edu.mx", RequestType.PASS,
                "Cita con dentista - Limpieza dental programada", "27/2/2026", "08:30 a.m.",
                "10:00", RequestStatus.PENDING),
            req("2", "María López Hernández", "maria.lopez@utez.edu.mx", RequestType.JUSTIFICATION,
                "Problemas de transporte - Falla mecánica en vehículo", "26/2/2026", "07:00 a.m.",
                null, RequestStatus.PENDING, "comprobante_grua.pdf"),
            req("3", "Carlos Mendoza Ríos", "carlos.mendoza@utez.edu.mx", RequestType.PASS,
                "Consulta médica - Revisión oftalmológica", "25/2/2026", "09:00 a.m.",
                "11:30", RequestStatus.PENDING),
            req("4", "Ana Torres Vega", "ana.torres@utez.edu.mx", RequestType.JUSTIFICATION,
                "Cita en IMSS - Control prenatal", "24/2/2026", "08:00 a.m.",
                null, RequestStatus.PENDING, "receta_medica.pdf"),
            req("5", "Roberto García Mendoza", "roberto.garcia@utez.edu.mx", RequestType.PASS,
                "Trámite bancario - Apertura de cuenta", "23/2/2026", "10:30 a.m.",
                "12:00", RequestStatus.PENDING),
            req("6", "Laura Díaz Flores", "laura.diaz@utez.edu.mx", RequestType.JUSTIFICATION,
                "Incapacidad médica - Gripe severa", "22/2/2026", "07:30 a.m.",
                null, RequestStatus.PENDING, "incapacidad_imss.pdf"),
            req("7", "Miguel Ángel Ruiz", "miguel.ruiz@utez.edu.mx", RequestType.PASS,
                "Servicio de vehículo - Mantenimiento preventivo", "21/2/2026", "08:00 a.m.",
                "10:00", RequestStatus.PENDING),
            req("8", "Diana García Mendoza", "diana.garcia@utez.edu.mx", RequestType.JUSTIFICATION,
                "Pago en institución gubernamental - SAT", "20/2/2026", "09:30 a.m.",
                null, RequestStatus.PENDING),
            req("9", "Fernando Castillo Pérez", "fernando.castillo@utez.edu.mx", RequestType.PASS,
                "Análisis clínicos - Laboratorio médico", "19/2/2026", "07:00 a.m.",
                "09:00", RequestStatus.PENDING),
            req("10", "Patricia Morales Ruiz", "patricia.morales@utez.edu.mx", RequestType.JUSTIFICATION,
                "Consulta médica hijo menor - Pediatría", "18/2/2026", "08:45 a.m.",
                null, RequestStatus.PENDING, "receta_pediatria.pdf"),
            req("11", "Sofía Ramírez Díaz", "sofia.ramirez@utez.edu.mx", RequestType.PASS,
                "Trámite de licencia - Módulo de licencias", "17/2/2026", "11:00 a.m.",
                "13:00", RequestStatus.PENDING),
            req("12", "Luis Fernando Vega", "luis.vega@utez.edu.mx", RequestType.JUSTIFICATION,
                "Trámite ante autoridad judicial - Asunto legal", "16/2/2026", "10:00 a.m.",
                null, RequestStatus.PENDING),
            req("13", "Andrea Morales Salazar", "andrea.morales@utez.edu.mx", RequestType.PASS,
                "Firma de documentos notariales - Notaría", "15/2/2026", "09:00 a.m.",
                "11:00", RequestStatus.PENDING),

            // Approved (22)
            req("14", "Juan Pérez García", "juan.perez@utez.edu.mx", RequestType.PASS,
                "Pago de servicios - CFE y agua", "15/2/2026", "10:00 a.m.",
                "11:30", RequestStatus.APPROVED),
            req("15", "María López Hernández", "maria.lopez@utez.edu.mx", RequestType.JUSTIFICATION,
                "Consulta dental - Endodoncia", "15/2/2026", "08:00 a.m.",
                null, RequestStatus.APPROVED, "recibo_dentista.pdf"),
            req("16", "Carlos Mendoza Ríos", "carlos.mendoza@utez.edu.mx", RequestType.PASS,
                "Trámite vehicular - Verificación", "14/2/2026", "09:00 a.m.",
                "11:00", RequestStatus.APPROVED),
            req("17", "Sofía Ramírez Díaz", "sofia.ramirez@utez.edu.mx", RequestType.JUSTIFICATION,
                "Incapacidad médica - Gripe severa", "15/2/2026", "11:30 a.m.",
                null, RequestStatus.APPROVED),
            req("18", "Ricardo Flores Soto", "ricardo.flores@utez.edu.mx", RequestType.PASS,
                "Servicio de vehículo - Mantenimiento", "14/2/2026", "08:00 a.m.",
                "10:00", RequestStatus.APPROVED),
            req("19", "Luis Fernando Vega", "luis.vega@utez.edu.mx", RequestType.JUSTIFICATION,
                "Trámite ante autoridad judicial - Audiencia", "13/2/2026", "10:00 a.m.",
                null, RequestStatus.APPROVED),
            req("20", "Diana García Mendoza", "diana.garcia@utez.edu.mx", RequestType.PASS,
                "Pago en institución gubernamental - SAT", "12/2/2026", "01:00 p.m.",
                "15:00", RequestStatus.APPROVED),
            req("21", "Patricia Morales Ruiz", "patricia.morales@utez.edu.mx", RequestType.JUSTIFICATION,
                "Consulta médica hijo menor - Pediatría", "11/2/2026", "08:45 a.m.",
                null, RequestStatus.APPROVED),
            req("22", "Fernando Castillo Pérez", "fernando.castillo@utez.edu.mx", RequestType.PASS,
                "Análisis clínicos - Laboratorio médico", "10/2/2026", "03:00 p.m.",
                "16:30", RequestStatus.APPROVED),
            req("23", "Ricardo Flores Soto", "ricardo.flores@utez.edu.mx", RequestType.JUSTIFICATION,
                "Accidente de tránsito menor - Atención", "10/2/2026", "09:30 a.m.",
                null, RequestStatus.APPROVED, "parte_accidente.pdf"),
            req("24", "Claudia Hernández Ríos", "claudia.hernandez@utez.edu.mx", RequestType.PASS,
                "Pago de servicios - CFE y agua", "8/2/2026", "02:30 p.m.",
                "15:30", RequestStatus.APPROVED),
            req("25", "Diana García Mendoza", "diana.garcia@utez.edu.mx", RequestType.JUSTIFICATION,
                "Cita con especialista - Oftalmología", "7/2/2026", "11:00 a.m.",
                null, RequestStatus.APPROVED),
            req("26", "María Elena Sánchez", "maria.sanchez@utez.edu.mx", RequestType.PASS,
                "Trámite de pasaporte - Secretaría", "6/2/2026", "10:00 a.m.",
                "12:00", RequestStatus.APPROVED),
            req("27", "Carlos Alberto Ruiz", "carlos.ruiz@utez.edu.mx", RequestType.PASS,
                "Consulta médica - Medicina general", "5/2/2026", "04:00 p.m.",
                "17:00", RequestStatus.APPROVED),
            req("28", "Fernando Castillo Pérez", "fernando.castillo@utez.edu.mx", RequestType.JUSTIFICATION,
                "Exámenes médicos periódicos - Chequeo", "4/2/2026", "10:15 a.m.",
                null, RequestStatus.APPROVED),
            req("29", "Fernanda López Ríos", "fernanda.lopez@utez.edu.mx", RequestType.PASS,
                "Recoger medicamento - Farmacia especializada", "3/2/2026", "11:00 a.m.",
                "12:00", RequestStatus.APPROVED),
            req("30", "Ana Torres Vega", "ana.torres@utez.edu.mx", RequestType.JUSTIFICATION,
                "Control prenatal - Revisión mensual", "3/2/2026", "08:00 a.m.",
                null, RequestStatus.APPROVED, "ultrasonido.pdf"),
            req("31", "Roberto García Mendoza", "roberto.garcia@utez.edu.mx", RequestType.PASS,
                "Trámite INE - Renovación credencial", "2/2/2026", "09:00 a.m.",
                "11:00", RequestStatus.APPROVED),
            req("32", "Laura Díaz Flores", "laura.diaz@utez.edu.mx", RequestType.JUSTIFICATION,
                "Cita médica de seguimiento - Rehabilitación", "1/2/2026", "07:30 a.m.",
                null, RequestStatus.APPROVED),
            req("33", "Miguel Ángel Ruiz", "miguel.ruiz@utez.edu.mx", RequestType.PASS,
                "Pago de tenencia - Tesorería municipal", "1/2/2026", "10:00 a.m.",
                "11:30", RequestStatus.APPROVED),
            req("34", "Andrea Morales Salazar", "andrea.morales@utez.edu.mx", RequestType.JUSTIFICATION,
                "Junta escolar hijo - Reunión de padres", "31/1/2026", "08:00 a.m.",
                null, RequestStatus.APPROVED),
            req("35", "Sofía Ramírez Díaz", "sofia.ramirez@utez.edu.mx", RequestType.PASS,
                "Cita embajada - Trámite de visa", "30/1/2026", "07:00 a.m.",
                "12:00", RequestStatus.APPROVED),

            // Rejected (2)
            req("36", "Juan Pérez García", "juan.perez@utez.edu.mx", RequestType.PASS,
                "Día personal - Asuntos familiares", "20/2/2026", "07:00 a.m.",
                "07:00", RequestStatus.REJECTED),
            req("37", "Carlos Mendoza Ríos", "carlos.mendoza@utez.edu.mx", RequestType.JUSTIFICATION,
                "Falta injustificada - Sin documentos", "19/2/2026", "07:00 a.m.",
                null, RequestStatus.REJECTED),

            // Used (1)
            req("38", "Ana Torres Vega", "ana.torres@utez.edu.mx", RequestType.PASS,
                "Consulta médica - Laboratorio de sangre", "10/2/2026", "08:00 a.m.",
                "09:30", RequestStatus.USED)
        )

        _uiState.value = rebuildState(mock, RequestFilter.ALL)
    }

    /** Convenience factory to reduce verbosity in mock data construction. */
    private fun req(
        id: String,
        name: String,
        email: String,
        type: RequestType,
        reason: String,
        date: String,
        time: String,
        exitTime: String?,
        status: RequestStatus,
        attachment: String? = null
    ) = RequestItem(
        id = id,
        numericId = id.toLong(),
        employeeName = name,
        employeeEmail = email,
        requestType = type,
        reason = reason,
        date = date,
        time = time,
        exitTime = exitTime,
        status = status,
        attachmentName = attachment
    )

    // endregion
}
