package mx.edu.utez.jyps.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import mx.edu.utez.jyps.data.model.CreateDepartmentRequest
import mx.edu.utez.jyps.data.model.DepartamentoResponse
import mx.edu.utez.jyps.data.model.UpdateDepartmentRequest
import mx.edu.utez.jyps.data.model.Usuario
import mx.edu.utez.jyps.data.network.RetrofitInstance
import mx.edu.utez.jyps.data.repository.DepartmentRepository
import mx.edu.utez.jyps.data.repository.UsuarioRepository
import kotlinx.coroutines.launch

/**
 * UI State for the Department Management module.
 *
 * @param departments The filtered sequence of institutional departments.
 * @param searchQuery Current predicate for department attribute filtering.
 * @param selectedFilter Active operational status filter (Active/Inactive).
 * @param totalCount Cumulative number of registered departments.
 * @param activeCount Tally of departments in functional status.
 * @param inactiveCount Tally of departments in restricted status.
 * @param isLoading Operational indicator for asynchronous network requests.
 * @param errorMessage Descriptive error text for diagnostic feedback.
 */
data class DepartmentUiState(
    val departments: List<DepartamentoResponse> = emptyList(),
    val searchQuery: String = "",
    val selectedFilter: String = "Todos",
    val totalCount: Int = 0,
    val activeCount: Int = 0,
    val inactiveCount: Int = 0,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val processingMessage: String = ""
)

/**
 * ViewModel orchestrating the lifecycle and structural integrity of institutional departments.
 */
class DepartmentManagementViewModel(
    private val repository: DepartmentRepository = DepartmentRepository(RetrofitInstance.api),
    private val userRepository: UsuarioRepository = UsuarioRepository(RetrofitInstance.api)
) : ViewModel() {

    private val TAG = "DeptViewModel"

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    private val _selectedFilter = MutableStateFlow("Todos")
    val selectedFilter: StateFlow<String> = _selectedFilter

    private val _isLoading = MutableStateFlow(false)
    private val _errorMessage = MutableStateFlow<String?>(null)
    private val _processingMessage = MutableStateFlow("")

    val uiState: StateFlow<DepartmentUiState> = combine(
        repository.allDepartments,
        _searchQuery,
        _selectedFilter,
        _isLoading,
        _errorMessage,
        _processingMessage
    ) { flows ->
        val departments = flows[0] as List<DepartamentoResponse>
        val query = flows[1] as String
        val filter = flows[2] as String
        val loading = flows[3] as Boolean
        val error = flows[4] as String?
        val procMsg = flows[5] as String

        val filtered = departments.filter { dept ->
            val matchesQuery = dept.nombre.contains(query, ignoreCase = true) || 
                               dept.descripcion.contains(query, ignoreCase = true)
            val matchesFilter = when (filter) {
                "Activos" -> dept.activo
                "Inactivos" -> !dept.activo
                else -> true
            }
            matchesQuery && matchesFilter
        }

        DepartmentUiState(
            departments = filtered,
            searchQuery = query,
            selectedFilter = filter,
            totalCount = departments.size,
            activeCount = departments.count { it.activo },
            inactiveCount = departments.count { !it.activo },
            isLoading = loading,
            errorMessage = error,
            processingMessage = procMsg
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), DepartmentUiState())

    // ── Dialog States ────────────────────────────────
    private val _isCreateVisible = MutableStateFlow(false)
    val isCreateVisible: StateFlow<Boolean> = _isCreateVisible

    private val _isEditVisible = MutableStateFlow(false)
    val isEditVisible: StateFlow<Boolean> = _isEditVisible

    private val _isStatusToggleVisible = MutableStateFlow(false)
    val isStatusToggleVisible: StateFlow<Boolean> = _isStatusToggleVisible

    private val _isWarningVisible = MutableStateFlow(false)
    val isWarningVisible: StateFlow<Boolean> = _isWarningVisible

    private val _selectedDept = MutableStateFlow<DepartamentoResponse?>(null)
    val selectedDept: StateFlow<DepartamentoResponse?> = _selectedDept

    // Eligible potential heads
    private val _availableHeads = MutableStateFlow<List<Usuario>>(emptyList())
    val availableHeads: StateFlow<List<Usuario>> = _availableHeads

    // Current linked active users (for deactivation warning)
    private val _linkedUsers = MutableStateFlow<List<Usuario>>(emptyList())
    val linkedUsers: StateFlow<List<Usuario>> = _linkedUsers

    // ── Form States ──────────────────────────────────
    private val _formName = MutableStateFlow("")
    val formName: StateFlow<String> = _formName

    private val _formDescription = MutableStateFlow("")
    val formDescription: StateFlow<String> = _formDescription

    private val _formJefeId = MutableStateFlow<Long?>(null)
    val formJefeId: StateFlow<Long?> = _formJefeId

    private val _formErrors = MutableStateFlow<Map<String, String>>(emptyMap())
    val formErrors: StateFlow<Map<String, String>> = _formErrors

    private val _isProcessing = MutableStateFlow(false)
    val isProcessing: StateFlow<Boolean> = _isProcessing

    init {
        viewModelScope.launch {
            // Staggered slightly after users for overall smoothness
            kotlinx.coroutines.delay(200)
            loadDepartments()
            loadPotentialHeads()
        }
    }

    /**
     * Loads the master list of departments.
     */
    fun loadDepartments() {
        if (_isLoading.value) return
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                Log.d(TAG, "Cargando lista global de departamentos (Secuencial)")
                repository.getDepartamentos()
            } catch (e: Exception) {
                Log.e(TAG, "Error loadDepartments: ${e.message}")
                _errorMessage.value = "Error al conectar con el servidor"
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Orchestrates eligible heads filtering based on department context.
     */
    private fun loadPotentialHeads(currentDeptId: Long? = null) {
        viewModelScope.launch {
            try {
                Log.d(TAG, "Consultando jefes disponibles")
                val allJefes = repository.getJefesDisponibles()
                _availableHeads.value = allJefes.filter { user ->
                    user.departamentoId == 0L || (currentDeptId != null && user.departamentoId == currentDeptId)
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error loadPotentialHeads (Expected if 403): ${e.message}")
                // Fallback: Continue without available heads instead of breaking the VM
                _availableHeads.value = emptyList()
            }
        }
    }

    // ── Handlers ─────────────────────────────────────
    fun onSearchQueryChange(query: String) { _searchQuery.value = query }
    fun onFilterChange(filter: String) { _selectedFilter.value = filter }

    fun onFormNameChange(v: String) { _formName.value = v }
    fun onFormDescriptionChange(v: String) { _formDescription.value = v }
    fun onFormJefeSelected(id: Long?) { _formJefeId.value = id }

    fun openCreate() {
        resetForm()
        loadPotentialHeads()
        _isCreateVisible.value = true
    }

    fun closeCreate() { _isCreateVisible.value = false }

    fun openEdit(id: Long) {
        val dept = repository.allDepartments.value.find { it.id == id } ?: return
        _selectedDept.value = dept
        _formName.value = dept.nombre
        _formDescription.value = dept.descripcion
        _formJefeId.value = dept.jefeId
        _formErrors.value = emptyMap()
        loadPotentialHeads(dept.id)
        _isEditVisible.value = true
    }

    fun closeEdit() { 
        _isEditVisible.value = false 
        _selectedDept.value = null
    }

    /**
     * Pushes department changes to the server.
     */
    fun saveDepartment() {
        if (!validateForm()) return

        viewModelScope.launch {
            _processingMessage.value = if (_isEditVisible.value) "Actualizando departamento..." else "Creando departamento..."
            _isProcessing.value = true
            val isEdit = _isEditVisible.value
            val result = if (isEdit) {
                val request = UpdateDepartmentRequest(
                    id = _selectedDept.value?.id ?: 0L,
                    nombre = _formName.value.trim(),
                    descripcion = _formDescription.value.trim(),
                    jefeId = _formJefeId.value ?: 0L,
                    activo = _selectedDept.value?.activo ?: true
                )
                repository.actualizarDepartamento(request)
            } else {
                val request = CreateDepartmentRequest(
                    nombre = _formName.value.trim(),
                    descripcion = _formDescription.value.trim(),
                    jefeId = _formJefeId.value ?: 0L,
                    activo = true
                )
                repository.crearDepartamento(request)
            }

            result.onSuccess {
                Log.d(TAG, "Departamento guardado exitosamente")
                loadDepartments()
                closeCreate()
                closeEdit()
            }.onFailure { e ->
                _formErrors.value = mapOf("api" to (e.localizedMessage ?: "Error al guardar"))
            }
            _isProcessing.value = false
            _processingMessage.value = ""
        }
    }

    /**
     * Validates deactivation criteria using local data instead of network calls.
     */
    fun requestToggleStatus(id: Long) {
        val dept = repository.allDepartments.value.find { it.id == id } ?: return
        _selectedDept.value = dept
        
        viewModelScope.launch {
            _processingMessage.value = "Validando personal vinculado..."
            _isProcessing.value = true
            
            // USE Specialized endpoint as requested
            Log.d(TAG, "Consultando usuarios vinculados al depto $id desde el servidor")
            val allLinked = repository.getUsuariosByDepartamento(id)
            
            // FILTER: Only active users prevent deactivation
            val activeLinked = allLinked.filter { it.activo }
            _linkedUsers.value = activeLinked
            
            Log.d(TAG, "Hito: ${activeLinked.size} usuarios ACTIVOS bloqueando la inactivación")

            if (dept.activo && activeLinked.isNotEmpty()) {
                _isWarningVisible.value = true
            } else {
                _isStatusToggleVisible.value = true
            }
            _isProcessing.value = false
            _processingMessage.value = ""
        }
    }

    fun confirmToggleStatus() {
        val dept = _selectedDept.value ?: return
        viewModelScope.launch {
            _processingMessage.value = "Cambiando estado..."
            _isProcessing.value = true
            repository.toggleEstado(dept.id).onSuccess {
                Log.d(TAG, "Cambio de estado exitoso para depto ${dept.id}")
                loadDepartments()
                closeStatusDialogs()
            }.onFailure { e ->
                Log.e(TAG, "Error toggleEstado: ${e.message}")
            }
            _isProcessing.value = false
            _processingMessage.value = ""
        }
    }

    fun closeStatusDialogs() {
        _isStatusToggleVisible.value = false
        _isWarningVisible.value = false
        _selectedDept.value = null
    }

    private fun validateForm(): Boolean {
        val errors = mutableMapOf<String, String>()
        if (_formName.value.isBlank()) errors["nombre"] = "El nombre es obligatorio"
        if (_formDescription.value.isBlank()) errors["descripcion"] = "La descripción es obligatoria"
        _formErrors.value = errors
        return errors.isEmpty()
    }

    private fun resetForm() {
        _formName.value = ""
        _formDescription.value = ""
        _formJefeId.value = null
        _formErrors.value = emptyMap()
        _selectedDept.value = null
    }
}
