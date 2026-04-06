package mx.edu.utez.jyps.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import mx.edu.utez.jyps.data.model.EmployeeItem

/**
 * State representing the Employee Management UI.
 * 
 * @property employees Complete list of current employees.
 * @property searchQuery Input text for filtering the list.
 * @property isLoading Indicates background operations.
 * @property showCreateDialog Toggles visibility of the addition modal.
 * @property selectedEmployee The employee being edited, or null for none.
 * @property error Error message for UI feedback.
 */
data class EmployeeManagementState(
    val employees: List<EmployeeItem> = emptyList(),
    val searchQuery: String = "",
    val isLoading: Boolean = false,
    val showCreateDialog: Boolean = false,
    val selectedEmployee: EmployeeItem? = null,
    val error: String? = null
) {
    /**
     * Filters the full list based on names or employee IDs.
     */
    val filteredEmployees: List<EmployeeItem>
        get() = if (searchQuery.isBlank()) employees
        else employees.filter { 
            it.fullName.contains(searchQuery, ignoreCase = true) || 
            it.employeeId.contains(searchQuery, ignoreCase = true) 
        }
}

/**
 * ViewModel responsible for Business Logic and state coordination in Employee Management.
 * Implements CRUD operations using dummy data for mock mode.
 */
class EmployeeManagementViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(EmployeeManagementState())
    val uiState: StateFlow<EmployeeManagementState> = _uiState.asStateFlow()

    init {
        loadMockEmployees()
    }

    private fun loadMockEmployees() {
        val mocks = listOf(
            EmployeeItem(1, "Ana Martinez Lopez", "ana.martinez@utez.edu.mx", "7771234567", "EMP001", "Docente", "DACEA"),
            EmployeeItem(2, "Luis Garcia Ruiz", "luis.garcia@utez.edu.mx", "7772345678", "EMP002", "Investigador", "DACEA"),
            EmployeeItem(3, "Elena Soto Perez", "elena.soto@utez.edu.mx", "7773456789", "EMP003", "Asistente", "DACEA"),
            EmployeeItem(4, "Diego Hernadez", "diego.h@utez.edu.mx", "7774567890", "EMP004", "Técnico", "DACEA")
        )
        _uiState.update { it.copy(employees = mocks) }
    }

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
        _uiState.update { it.copy(showCreateDialog = false, selectedEmployee = null) }
    }

    fun addEmployee(name: String, email: String, phone: String, empId: String, pos: String, dept: String) {
        val newEmployee = EmployeeItem(
            id = (_uiState.value.employees.maxOfOrNull { it.id } ?: 0) + 1,
            fullName = name,
            email = email,
            phone = phone,
            employeeId = empId,
            position = pos,
            department = dept
        )
        _uiState.update { state -> 
            state.copy(
                employees = state.employees + newEmployee,
                showCreateDialog = false 
            )
        }
    }

    fun updateEmployee(employee: EmployeeItem) {
        _uiState.update { state ->
            state.copy(
                employees = state.employees.map { if (it.id == employee.id) employee else it },
                selectedEmployee = null
            )
        }
    }

    fun toggleEmployeeStatus(employeeId: Int) {
        _uiState.update { state ->
            state.copy(
                employees = state.employees.map { 
                    if (it.id == employeeId) it.copy(isActive = !it.isActive) else it 
                }
            )
        }
    }

    fun deleteEmployee(employeeId: Int) {
        _uiState.update { state ->
            state.copy(employees = state.employees.filter { it.id != employeeId })
        }
    }
}
