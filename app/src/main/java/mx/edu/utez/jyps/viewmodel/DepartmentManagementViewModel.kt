package mx.edu.utez.jyps.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.stateIn
import mx.edu.utez.jyps.data.model.Departamento
import mx.edu.utez.jyps.data.model.LinkedUser

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
    val departments: List<Departamento> = emptyList(),
    val searchQuery: String = "",
    val selectedFilter: String = "Todos",
    val totalCount: Int = 0,
    val activeCount: Int = 0,
    val inactiveCount: Int = 0
)

/**
 * ViewModel managing the state and logic for Department Management.
 * Uses dummy data as requested for initial implementation.
 */
class DepartmentManagementViewModel : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    private val _selectedFilter = MutableStateFlow("Todos")
    val selectedFilter: StateFlow<String> = _selectedFilter

    // Dummy data source
    private val _rawDepartments = MutableStateFlow(
        listOf(
            Departamento(1, "Tecnologías de la Información", "Departamento encargado de la infraestructura tecnológica y sistemas de información.", 101, true),
            Departamento(2, "Desarrollo de Software", "Área especializada en el desarrollo y mantenimiento de aplicaciones institucionales.", 102, true),
            Departamento(3, "Recursos Humanos", "Gestión del capital humano, nómina, reclutamiento y desarrollo del personal.", 103, true),
            Departamento(4, "Administración", "Coordinación de procesos administrativos y operativos de la institución.", 104, true),
            Departamento(5, "Finanzas", "Control financiero, presupuestos y gestión de recursos económicos.", 105, true),
            Departamento(6, "Contabilidad", "Registro contable, reportes financieros y cumplimiento fiscal.", 106, true),
            Departamento(7, "Marketing", "Promoción institucional, comunicación y estrategias de difusión.", 107, true),
            Departamento(8, "Biblioteca", "Servicios bibliotecarios y gestión de acervo bibliográfico.", 108, false)
        )
    )

    val uiState: StateFlow<DepartmentUiState> = combine(
        _rawDepartments,
        _searchQuery,
        _selectedFilter
    ) { departments, query, filter ->
        val filtered = departments.filter { dept ->
            val matchesQuery = dept.nombre.contains(query, ignoreCase = true) || 
                               dept.descripcion.contains(query, ignoreCase = true)
            val matchesFilter = when (filter) {
                "Activos" -> dept.estaActivo
                "Inactivos" -> !dept.estaActivo
                else -> true
            }
            matchesQuery && matchesFilter
        }

        DepartmentUiState(
            departments = filtered,
            searchQuery = query,
            selectedFilter = filter,
            totalCount = departments.size,
            activeCount = departments.count { it.estaActivo },
            inactiveCount = departments.count { !it.estaActivo }
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

    private val _selectedDept = MutableStateFlow<Departamento?>(null)
    val selectedDept: StateFlow<Departamento?> = _selectedDept

    private val _linkedUsers = MutableStateFlow<List<LinkedUser>>(emptyList())
    val linkedUsers: StateFlow<List<LinkedUser>> = _linkedUsers

    // Dummy data for linked users per department
    private val departmentUsersMap = mapOf(
        1L to listOf(
            LinkedUser(1, "Juan Pérez García", "Trabajador"),
            LinkedUser(2, "Roberto Sánchez López", "Jefe De Área")
        ),
        3L to listOf(
            LinkedUser(3, "Maria Elena Soto", "Auxiliar RRHH")
        )
    )

    // ── Form States ──────────────────────────────────
    private val _formName = MutableStateFlow("")
    val formName: StateFlow<String> = _formName

    private val _formDescription = MutableStateFlow("")
    val formDescription: StateFlow<String> = _formDescription

    private val _formErrors = MutableStateFlow<Map<String, String>>(emptyMap())
    val formErrors: StateFlow<Map<String, String>> = _formErrors

    // ── Handlers ─────────────────────────────────────
    fun onSearchQueryChange(query: String) { _searchQuery.value = query }
    fun onFilterChange(filter: String) { _selectedFilter.value = filter }

    fun onFormNameChange(v: String) { _formName.value = v }
    fun onFormDescriptionChange(v: String) { _formDescription.value = v }

    fun openCreate() {
        resetForm()
        _isCreateVisible.value = true
    }

    fun closeCreate() { _isCreateVisible.value = false }

    fun openEdit(id: Long) {
        val dept = _rawDepartments.value.find { it.id == id } ?: return
        _selectedDept.value = dept
        _formName.value = dept.nombre
        _formDescription.value = dept.descripcion
        _formErrors.value = emptyMap()
        _isEditVisible.value = true
    }

    fun closeEdit() { 
        _isEditVisible.value = false 
        _selectedDept.value = null
    }

    fun saveDepartment() {
        if (!validateForm()) return
        
        val newList = if (_isCreateVisible.value) {
            val nextId = (_rawDepartments.value.maxOfOrNull { it.id } ?: 0) + 1
            _rawDepartments.value + Departamento(nextId, _formName.value, _formDescription.value, 0, true)
        } else {
            _rawDepartments.value.map {
                if (it.id == _selectedDept.value?.id) {
                    it.copy(nombre = _formName.value, descripcion = _formDescription.value)
                } else it
            }
        }
        
        _rawDepartments.value = newList
        closeCreate()
        closeEdit()
    }

    fun requestToggleStatus(id: Long) {
        val dept = _rawDepartments.value.find { it.id == id } ?: return
        _selectedDept.value = dept
        
        val users = departmentUsersMap[id] ?: emptyList()
        _linkedUsers.value = users
        
        if (dept.estaActivo && users.isNotEmpty()) {
            _isWarningVisible.value = true
        } else {
            _isStatusToggleVisible.value = true
        }
    }

    fun confirmToggleStatus() {
        val dept = _selectedDept.value ?: return
        _rawDepartments.value = _rawDepartments.value.map {
            if (it.id == dept.id) it.copy(estaActivo = !it.estaActivo) else it
        }
        closeStatusDialogs()
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
        _formErrors.value = emptyMap()
        _selectedDept.value = null
    }
}
