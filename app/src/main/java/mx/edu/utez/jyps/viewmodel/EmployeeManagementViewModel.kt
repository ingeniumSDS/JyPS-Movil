package mx.edu.utez.jyps.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import mx.edu.utez.jyps.data.model.DepartamentoResponse
import mx.edu.utez.jyps.data.model.EmployeeItem
import mx.edu.utez.jyps.data.model.UserRequest
import mx.edu.utez.jyps.data.model.Usuario
import mx.edu.utez.jyps.data.network.RetrofitInstance
import mx.edu.utez.jyps.data.repository.DepartmentRepository
import mx.edu.utez.jyps.data.repository.PreferencesManager
import mx.edu.utez.jyps.data.repository.UsuarioRepository
import timber.log.Timber

/**
 * State representing the Employee Management UI.
 * 
 * @property employees Complete list of current employees.
 * @property departments List of available departments for transfers.
 * @property managerDeptId The department ID of the logged manager.
 * @property searchQuery Input text for filtering the list.
 * @property isLoading Indicates background operations.
 * @property showCreateDialog Toggles visibility of the addition modal.
 * @property selectedEmployee The employee being edited, or null for none.
 * @property error Error message for UI feedback.
 */
data class EmployeeManagementState(
    val employees: List<EmployeeItem> = emptyList(),
    val departments: List<DepartamentoResponse> = emptyList(),
    val managerDeptId: Long = 0,
    val searchQuery: String = "",
    val isLoading: Boolean = false,
    val showCreateDialog: Boolean = false,
    val selectedEmployee: EmployeeItem? = null,
    val error: String? = null
) {
    val filteredEmployees: List<EmployeeItem>
        get() = if (searchQuery.isBlank()) employees
        else employees.filter { 
            it.fullName.contains(searchQuery, ignoreCase = true) || 
            it.employeeId.contains(searchQuery, ignoreCase = true) 
        }
}

/**
 * ViewModel responsible for Business Logic and state coordination in Employee Management.
 * Implements CRUD operations using real backend APIs.
 */
class EmployeeManagementViewModel(application: Application) : AndroidViewModel(application) {
    private val preferencesManager = PreferencesManager(application)
    private val usuarioRepository = UsuarioRepository(RetrofitInstance.api)
    private val departmentRepository = DepartmentRepository(RetrofitInstance.api)

    private val _uiState = MutableStateFlow(EmployeeManagementState())
    val uiState: StateFlow<EmployeeManagementState> = _uiState.asStateFlow()

    init {
        loadSessionAndData()
    }

    private fun loadSessionAndData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            
            // 1. Get Manager's Dept ID
            preferencesManager.deptIdFlow.collect { deptId ->
                _uiState.update { it.copy(managerDeptId = deptId) }
                if (deptId > 0) {
                    refreshEmployees(deptId)
                    loadDepartments()
                }
            }
        }
    }

    private suspend fun refreshEmployees(deptId: Long) {
        Timber.d("Refreshing employees for department $deptId")
        val users = usuarioRepository.getUsuariosByDepartamento(deptId)
        val items = users.map { it.toEmployeeItem() }
        _uiState.update { it.copy(employees = items, isLoading = false) }
    }

    private suspend fun loadDepartments() {
        val depts = departmentRepository.getDepartamentos()
        _uiState.update { it.copy(departments = depts) }
    }

    private fun Usuario.toEmployeeItem(): EmployeeItem = EmployeeItem(
        id = this.id,
        fullName = this.nombreCompleto,
        email = this.correo,
        phone = this.telefono,
        employeeId = "EMP-${this.id}",
        position = this.roles.firstOrNull() ?: "Empleado",
        department = this.nombreDepartamento ?: "Sin Depto",
        isActive = this.activo
    )

    fun onSearchQueryChanged(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }

    fun onCreateClick() {
        _uiState.update { it.copy(showCreateDialog = true) }
    }

    fun onEditClick(employee: EmployeeItem) {
        _uiState.update { it.copy(selectedEmployee = employee) }
    }

    fun onDismissDialogs() {
        _uiState.update { it.copy(showCreateDialog = false, selectedEmployee = null, error = null) }
    }

    /**
     * Registers a new employee using the real API.
     */
    fun addEmployee(
        name: String, 
        email: String, 
        phone: String, 
        entryTime: String, 
        exitTime: String
    ) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            
            val names = name.split(" ")
            val firstName = names.getOrNull(0) ?: ""
            val paternal = names.getOrNull(1) ?: ""
            val maternal = names.getOrNull(2) ?: ""

            val request = UserRequest(
                nombre = firstName,
                apellidoPaterno = paternal,
                apellidoMaterno = maternal,
                correo = email,
                telefono = phone,
                horaEntrada = entryTime,
                horaSalida = exitTime,
                roles = listOf("EMPLEADO"),
                departamentoId = _uiState.value.managerDeptId
            )

            val result = usuarioRepository.registrarUsuario(request)
            result.onSuccess {
                refreshEmployees(_uiState.value.managerDeptId)
                _uiState.update { it.copy(showCreateDialog = false) }
            }.onFailure { error ->
                _uiState.update { it.copy(error = error.message, isLoading = false) }
            }
        }
    }

    /**
     * Updates an existing employee using the real API.
     */
    fun updateEmployee(
        employee: EmployeeItem,
        entryTime: String = "08:00:00",
        exitTime: String = "16:00:00",
        targetDeptId: Long = 0
    ) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            
            val names = employee.fullName.split(" ")
            val firstName = names.getOrNull(0) ?: ""
            val paternal = names.getOrNull(1) ?: ""
            val maternal = names.getOrNull(2) ?: ""

            val request = UserRequest(
                nombre = firstName,
                apellidoPaterno = paternal,
                apellidoMaterno = maternal,
                correo = employee.email,
                telefono = employee.phone,
                horaEntrada = entryTime,
                horaSalida = exitTime,
                roles = listOf("EMPLEADO"),
                departamentoId = if (targetDeptId > 0) targetDeptId else _uiState.value.managerDeptId
            )

            val result = usuarioRepository.actualizarUsuario(employee.id, request)
            result.onSuccess {
                refreshEmployees(_uiState.value.managerDeptId)
                _uiState.update { it.copy(selectedEmployee = null) }
            }.onFailure { error ->
                _uiState.update { it.copy(error = error.message, isLoading = false) }
            }
        }
    }

    fun toggleEmployeeStatus(employeeId: Long) {
        viewModelScope.launch {
            val result = usuarioRepository.toggleEstadoUsuario(employeeId)
            result.onSuccess {
                refreshEmployees(_uiState.value.managerDeptId)
            }
        }
    }
}
