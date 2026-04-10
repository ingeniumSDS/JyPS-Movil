package mx.edu.utez.jyps.viewmodel

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
import kotlinx.coroutines.launch

/**
 * UI State for the Department Management screen.
 *
 * @property departments List of departments to display.
 * @property searchQuery Current text in the search bar.
 * @property selectedFilter Active filter (Todos, Activos, Inactivos).
 * @property totalCount Overall number of departments.
 * @property activeCount Number of active departments.
 * @property inactiveCount Number of inactive departments.
 */
data class DepartmentUiState(
    val departments: List<DepartamentoResponse> = emptyList(),
    val searchQuery: String = "",
    val selectedFilter: String = "Todos",
    val totalCount: Int = 0,
    val activeCount: Int = 0,
    val inactiveCount: Int = 0,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

/**
 * ViewModel managing the state and logic for Department Management.
 * Uses dummy data as requested for initial implementation.
 */
class DepartmentManagementViewModel(
    private val repository: DepartmentRepository = DepartmentRepository(RetrofitInstance.api)
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    private val _selectedFilter = MutableStateFlow("Todos")
    val selectedFilter: StateFlow<String> = _selectedFilter

    private val _rawDepartments = MutableStateFlow<List<DepartamentoResponse>>(emptyList())
    private val _isLoading = MutableStateFlow(false)
    private val _errorMessage = MutableStateFlow<String?>(null)

    val uiState: StateFlow<DepartmentUiState> = combine(
        _rawDepartments,
        _searchQuery,
        _selectedFilter,
        _isLoading,
        _errorMessage
    ) { departments, query, filter, loading, error ->
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
            errorMessage = error
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

    // Potenciales jefes elegibles
    private val _availableHeads = MutableStateFlow<List<Usuario>>(emptyList())
    val availableHeads: StateFlow<List<Usuario>> = _availableHeads

    // Usuarios vinculados actuales (para warning de desactivación)
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
        loadDepartments()
        loadPotentialHeads()
    }

    fun loadDepartments() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                _rawDepartments.value = repository.getDepartamentos()
            } catch (e: Exception) {
                _errorMessage.value = "Error al conectar con el servidor"
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun loadPotentialHeads(currentDeptId: Long? = null) {
        viewModelScope.launch {
            val allJefes = repository.getJefesDisponibles()
            // Filtramos: usuarios que no tienen depto (0L) 
            // O que pertenecen al departamento que estamos editando actualmente
            _availableHeads.value = allJefes.filter { user ->
                user.departamentoId == 0L || (currentDeptId != null && user.departamentoId == currentDeptId)
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
        val dept = _rawDepartments.value.find { it.id == id } ?: return
        _selectedDept.value = dept
        _formName.value = dept.nombre
        _formDescription.value = dept.descripcion
        _formJefeId.value = dept.jefeId
        _formErrors.value = emptyMap()
        
        // Recargamos jefes filtrando para este departamento específico
        loadPotentialHeads(dept.id)
        
        _isEditVisible.value = true
    }

    fun closeEdit() { 
        _isEditVisible.value = false 
        _selectedDept.value = null
    }

    fun saveDepartment() {
        if (!validateForm()) return

        viewModelScope.launch {
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
                loadDepartments()
                loadPotentialHeads() // Refresh generic eligible list after assigning
                closeCreate()
                closeEdit()
            }.onFailure { e ->
                _formErrors.value = mapOf("api" to (e.localizedMessage ?: "Error al guardar"))
            }
            _isProcessing.value = false
        }
    }

    fun requestToggleStatus(id: Long) {
        val dept = _rawDepartments.value.find { it.id == id } ?: return
        _selectedDept.value = dept
        
        viewModelScope.launch {
            _isProcessing.value = true
            val users = repository.getUsuariosByDepartamento(dept.id)
            _linkedUsers.value = users
            
            if (dept.activo && users.isNotEmpty()) {
                _isWarningVisible.value = true
            } else {
                _isStatusToggleVisible.value = true
            }
            _isProcessing.value = false
        }
    }

    fun confirmToggleStatus() {
        val dept = _selectedDept.value ?: return
        viewModelScope.launch {
            _isProcessing.value = true
            repository.toggleEstado(dept.id).onSuccess {
                loadDepartments()
                closeStatusDialogs()
            }.onFailure {
                // Handle error
            }
            _isProcessing.value = false
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
