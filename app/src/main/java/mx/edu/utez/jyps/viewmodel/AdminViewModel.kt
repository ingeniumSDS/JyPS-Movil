package mx.edu.utez.jyps.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import mx.edu.utez.jyps.data.model.CuentaResponse
import mx.edu.utez.jyps.data.model.Departamento
import mx.edu.utez.jyps.data.model.LocalTimeInfo
import mx.edu.utez.jyps.data.model.Usuario
import mx.edu.utez.jyps.data.model.UserRequest
import mx.edu.utez.jyps.data.network.RetrofitInstance
import mx.edu.utez.jyps.data.repository.LoadResult
import mx.edu.utez.jyps.data.repository.UsuarioRepository

class AdminViewModel(
    private val repository: UsuarioRepository = UsuarioRepository(RetrofitInstance.api)
) : ViewModel() {

    // ── User list ────────────────────────────────────
    val users: StateFlow<List<Usuario>> = repository.allUsers
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val selectedUser: StateFlow<Usuario?> = repository.selectedUser
        .stateIn(viewModelScope, SharingStarted.Lazily, null)

    val loadState: StateFlow<LoadResult<Unit>> = repository.loadState
        .stateIn(viewModelScope, SharingStarted.Lazily, LoadResult.Loading)

    val accountStatuses: StateFlow<Map<Long, CuentaResponse>> = repository.accountStatuses
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyMap())

    val departamentos: StateFlow<List<Departamento>> = repository.departamentos
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    // ── Navigation ───────────────────────────────────
    private val _selectedDrawerItem = MutableStateFlow("admin_users")
    val selectedDrawerItem: StateFlow<String> = _selectedDrawerItem

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    private val _selectedFilter = MutableStateFlow("Todos")
    val selectedFilter: StateFlow<String> = _selectedFilter

    private val _isLoadingUsers = MutableStateFlow(false)
    val isLoadingUsers: StateFlow<Boolean> = _isLoadingUsers

    // ── User Detail Dialog ───────────────────────────
    private val _isUserDetailVisible = MutableStateFlow(false)
    val isUserDetailVisible: StateFlow<Boolean> = _isUserDetailVisible

    // ── Feedback / Toast ─────────────────────────────
    private val _showToast = MutableStateFlow(false)
    val showToast: StateFlow<Boolean> = _showToast

    private val _toastMessage = MutableStateFlow("")
    val toastMessage: StateFlow<String> = _toastMessage

    private val _isToastSuccess = MutableStateFlow(true)
    val isToastSuccess: StateFlow<Boolean> = _isToastSuccess

    private val _isProcessing = MutableStateFlow(false)
    val isProcessing: StateFlow<Boolean> = _isProcessing

    // ── CREATE USER FORM ─────────────────────────────
    private val _isCreateUserVisible = MutableStateFlow(false)
    val isCreateUserVisible: StateFlow<Boolean> = _isCreateUserVisible

    private val _newUserName = MutableStateFlow("")
    val newUserName: StateFlow<String> = _newUserName

    private val _newUserPaterno = MutableStateFlow("")
    val newUserPaterno: StateFlow<String> = _newUserPaterno

    private val _newUserMaterno = MutableStateFlow("")
    val newUserMaterno: StateFlow<String> = _newUserMaterno

    private val _newUserPhone = MutableStateFlow("")
    val newUserPhone: StateFlow<String> = _newUserPhone

    private val _newUserEmail = MutableStateFlow("")
    val newUserEmail: StateFlow<String> = _newUserEmail

    private val _newUserRoles = MutableStateFlow<Set<Int>>(emptySet())
    val newUserRoles: StateFlow<Set<Int>> = _newUserRoles

    private val _newUserDepartmentId = MutableStateFlow(1)
    val newUserDepartmentId: StateFlow<Int> = _newUserDepartmentId

    private val _newUserStartHour = MutableStateFlow(8)
    val newUserStartHour: StateFlow<Int> = _newUserStartHour

    private val _newUserStartMinute = MutableStateFlow(0)
    val newUserStartMinute: StateFlow<Int> = _newUserStartMinute

    private val _newUserEndHour = MutableStateFlow(16)
    val newUserEndHour: StateFlow<Int> = _newUserEndHour

    private val _newUserEndMinute = MutableStateFlow(0)
    val newUserEndMinute: StateFlow<Int> = _newUserEndMinute

    // Create form feedback (shown inside dialog)
    private val _createFormErrors = MutableStateFlow<Map<String, String>>(emptyMap())
    val createFormErrors: StateFlow<Map<String, String>> = _createFormErrors

    private val _createServerResponseError = MutableStateFlow<String?>(null)
    val createServerResponseError: StateFlow<String?> = _createServerResponseError

    private val _scrollToTopTrigger = MutableStateFlow(0)
    val scrollToTopTrigger: StateFlow<Int> = _scrollToTopTrigger

    // ── EDIT USER FORM ───────────────────────────────
    private val _isEditUserVisible = MutableStateFlow(false)
    val isEditUserVisible: StateFlow<Boolean> = _isEditUserVisible

    private val _editingUser = MutableStateFlow<Usuario?>(null)
    val editingUser: StateFlow<Usuario?> = _editingUser

    private val _editName = MutableStateFlow("")
    val editName: StateFlow<String> = _editName

    private val _editPaterno = MutableStateFlow("")
    val editPaterno: StateFlow<String> = _editPaterno

    private val _editMaterno = MutableStateFlow("")
    val editMaterno: StateFlow<String> = _editMaterno

    private val _editPhone = MutableStateFlow("")
    val editPhone: StateFlow<String> = _editPhone

    private val _editEmail = MutableStateFlow("")
    val editEmail: StateFlow<String> = _editEmail

    private val _editRoles = MutableStateFlow<Set<Int>>(emptySet())
    val editRoles: StateFlow<Set<Int>> = _editRoles

    private val _editDepartmentId = MutableStateFlow(1)
    val editDepartmentId: StateFlow<Int> = _editDepartmentId

    private val _editStartHour = MutableStateFlow(8)
    val editStartHour: StateFlow<Int> = _editStartHour

    private val _editStartMinute = MutableStateFlow(0)
    val editStartMinute: StateFlow<Int> = _editStartMinute

    private val _editEndHour = MutableStateFlow(16)
    val editEndHour: StateFlow<Int> = _editEndHour

    private val _editEndMinute = MutableStateFlow(0)
    val editEndMinute: StateFlow<Int> = _editEndMinute

    private val _editFormErrors = MutableStateFlow<Map<String, String>>(emptyMap())
    val editFormErrors: StateFlow<Map<String, String>> = _editFormErrors

    private val _editServerResponseError = MutableStateFlow<String?>(null)
    val editServerResponseError: StateFlow<String?> = _editServerResponseError

    // ── Role Mapping ─────────────────────────────────
    private val roleMapping = mapOf(
        1 to "EMPLEADO",
        2 to "GUARDIA",
        3 to "JEFE_DE_DEPARTAMENTO",
        4 to "ADMINISTRADOR",
        5 to "AUDITOR"
    )
    private val reverseRoleMapping = roleMapping.entries.associate { (k, v) -> v to k }

    // ── Init ─────────────────────────────────────────
    init {
        loadUsuarios()
        loadDepartamentos()
    }

    fun loadUsuarios() {
        viewModelScope.launch {
            _isLoadingUsers.value = true
            repository.getUsuarios()
            repository.fetchAllAccountStatuses()
            _isLoadingUsers.value = false
        }
    }

    fun loadDepartamentos() {
        viewModelScope.launch { repository.getDepartamentos() }
    }

    // ── Navigation / Filters ─────────────────────────
    fun selectDrawerItem(item: String) { _selectedDrawerItem.value = item }
    fun onSearchQueryChange(query: String) { _searchQuery.value = query }
    fun setFilter(filter: String) { _selectedFilter.value = filter }

    // ── User Detail ──────────────────────────────────
    fun viewUserDetail(userId: Long) {
        viewModelScope.launch {
            repository.getUsuarioPorId(userId)
            repository.getCuentaUsuario(userId)
            _isUserDetailVisible.value = true
        }
    }

    fun closeUserDetail() {
        _isUserDetailVisible.value = false
        repository.clearSelectedUser()
    }

    // ── Toast ────────────────────────────────────────
    fun dismissToast() { _showToast.value = false }

    private fun showFeedback(message: String, success: Boolean) {
        _toastMessage.value = message
        _isToastSuccess.value = success
        _showToast.value = true
    }

    // ── CREATE USER ──────────────────────────────────
    fun setCreateUserVisible(visible: Boolean) {
        if (!visible) resetCreateForm()
        _isCreateUserVisible.value = visible
    }

    fun onNameChange(name: String) { _newUserName.value = name }
    fun onPaternoChange(paterno: String) { _newUserPaterno.value = paterno }
    fun onMaternoChange(materno: String) { _newUserMaterno.value = materno }
    fun onPhoneChange(phone: String) { _newUserPhone.value = phone }
    fun onEmailChange(email: String) { _newUserEmail.value = email }
    fun onDepartmentChange(id: Int) { _newUserDepartmentId.value = id }
    fun onStartTimeChange(hour: Int, minute: Int) {
        _newUserStartHour.value = hour
        _newUserStartMinute.value = minute
    }
    fun onEndTimeChange(hour: Int, minute: Int) {
        _newUserEndHour.value = hour
        _newUserEndMinute.value = minute
    }

    fun toggleRole(roleId: Int) {
        val current = _newUserRoles.value.toMutableSet()
        if (current.contains(roleId)) current.remove(roleId) else current.add(roleId)
        _newUserRoles.value = current
    }

    private fun resetCreateForm() {
        _newUserName.value = ""
        _newUserPaterno.value = ""
        _newUserMaterno.value = ""
        _newUserPhone.value = ""
        _newUserEmail.value = ""
        _newUserRoles.value = emptySet()
        _newUserDepartmentId.value = 1
        _newUserStartHour.value = 8
        _newUserStartMinute.value = 0
        _newUserEndHour.value = 16
        _newUserEndMinute.value = 0
        _createFormErrors.value = emptyMap()
        _createServerResponseError.value = null
    }

    fun saveNewUser() {
        val validationErrors = validateForm(
            _newUserName.value, _newUserPaterno.value, _newUserMaterno.value,
            _newUserPhone.value, _newUserEmail.value, _newUserRoles.value
        )
        _createFormErrors.value = validationErrors
        _createServerResponseError.value = null

        if (validationErrors.isNotEmpty()) {
            _scrollToTopTrigger.value += 1
            return
        }

        val selectedRoleNames = _newUserRoles.value.mapNotNull { roleMapping[it] }
        val request = UserRequest(
            nombre = _newUserName.value.trim(),
            apellidoPaterno = _newUserPaterno.value.trim(),
            apellidoMaterno = _newUserMaterno.value.trim(),
            correo = _newUserEmail.value.trim(),
            telefono = _newUserPhone.value.trim(),
            horaEntrada = "%02d:%02d:00".format(_newUserStartHour.value, _newUserStartMinute.value),
            horaSalida = "%02d:%02d:00".format(_newUserEndHour.value, _newUserEndMinute.value),
            roles = selectedRoleNames,
            departamentoId = _newUserDepartmentId.value.toLong()
        )

        viewModelScope.launch {
            _isProcessing.value = true
            val result = repository.registrarUsuario(request)
            result.onSuccess {
                setCreateUserVisible(false)
                showFeedback("Usuario creado exitosamente", true)
                repository.fetchAllAccountStatuses()
            }.onFailure { e ->
                _createServerResponseError.value = "Error: ${e.message}"
                _scrollToTopTrigger.value += 1
            }
            _isProcessing.value = false
        }
    }

    // ── EDIT USER ────────────────────────────────────
    fun openEditUser(usuario: Usuario) {
        _editingUser.value = usuario

        // Split nombreCompleto into parts
        val parts = usuario.nombreCompleto.split(" ")
        _editName.value = parts.getOrElse(0) { "" }
        _editPaterno.value = parts.getOrElse(1) { "" }
        _editMaterno.value = parts.drop(2).joinToString(" ")

        _editPhone.value = usuario.telefono
        _editEmail.value = usuario.correo
        _editRoles.value = usuario.roles.mapNotNull { reverseRoleMapping[it] }.toSet()
        _editDepartmentId.value = usuario.departamentoId.toInt()
        _editStartHour.value = usuario.entradaHour
        _editStartMinute.value = usuario.entradaMinute
        _editEndHour.value = usuario.salidaHour
        _editEndMinute.value = usuario.salidaMinute
        _editFormErrors.value = emptyMap()
        _editServerResponseError.value = null
        _isEditUserVisible.value = true
    }

    fun closeEditUser() {
        _isEditUserVisible.value = false
        _editingUser.value = null
        _editFormErrors.value = emptyMap()
        _editServerResponseError.value = null
    }

    fun onEditNameChange(v: String) { _editName.value = v }
    fun onEditPaternoChange(v: String) { _editPaterno.value = v }
    fun onEditMaternoChange(v: String) { _editMaterno.value = v }
    fun onEditPhoneChange(v: String) { _editPhone.value = v }
    fun onEditEmailChange(v: String) { _editEmail.value = v }
    fun onEditDepartmentChange(id: Int) { _editDepartmentId.value = id }
    fun onEditStartTimeChange(hour: Int, minute: Int) {
        _editStartHour.value = hour
        _editStartMinute.value = minute
    }
    fun onEditEndTimeChange(hour: Int, minute: Int) {
        _editEndHour.value = hour
        _editEndMinute.value = minute
    }
    fun toggleEditRole(roleId: Int) {
        val current = _editRoles.value.toMutableSet()
        if (current.contains(roleId)) current.remove(roleId) else current.add(roleId)
        _editRoles.value = current
    }

    fun saveEditUser() {
        val userId = _editingUser.value?.id ?: return
        val validationErrors = validateForm(
            _editName.value, _editPaterno.value, _editMaterno.value,
            _editPhone.value, _editEmail.value, _editRoles.value
        )
        _editFormErrors.value = validationErrors
        _editServerResponseError.value = null

        if (validationErrors.isNotEmpty()) {
            _scrollToTopTrigger.value += 1
            return
        }

        val selectedRoleNames = _editRoles.value.mapNotNull { roleMapping[it] }
        val request = UserRequest(
            nombre = _editName.value.trim(),
            apellidoPaterno = _editPaterno.value.trim(),
            apellidoMaterno = _editMaterno.value.trim(),
            correo = _editEmail.value.trim(),
            telefono = _editPhone.value.trim(),
            horaEntrada = "%02d:%02d:00".format(_editStartHour.value, _editStartMinute.value),
            horaSalida = "%02d:%02d:00".format(_editEndHour.value, _editEndMinute.value),
            roles = selectedRoleNames,
            departamentoId = _editDepartmentId.value.toLong()
        )

        viewModelScope.launch {
            _isProcessing.value = true
            val result = repository.actualizarUsuario(userId, request)
            result.onSuccess {
                closeEditUser()
                showFeedback("Usuario actualizado exitosamente", true)
                repository.fetchAllAccountStatuses()
            }.onFailure { e ->
                _editServerResponseError.value = "Error: ${e.message}"
                _scrollToTopTrigger.value += 1
            }
            _isProcessing.value = false
        }
    }

    // ── TOGGLE STATUS ────────────────────────────────
    fun toggleUserStatus(usuario: Usuario) {
        viewModelScope.launch {
            _isProcessing.value = true
            val result = repository.toggleEstadoUsuario(usuario.id)
            result.onSuccess { response ->
                val action = if (response.activa) "activado" else "desactivado"
                showFeedback("Usuario $action exitosamente", true)
            }.onFailure { e ->
                showFeedback("Error al cambiar estado: ${e.message}", false)
            }
            _isProcessing.value = false
        }
    }

    // ── Validation ───────────────────────────────────
    private fun validateForm(
        name: String, paterno: String, materno: String,
        phone: String, email: String, roles: Set<Int>
    ): Map<String, String> {
        val errors = mutableMapOf<String, String>()
        if (name.isBlank()) errors["nombre"] = "El nombre es obligatorio"
        if (paterno.isBlank()) errors["paterno"] = "El apellido paterno es obligatorio"
        if (materno.isBlank()) errors["materno"] = "El apellido materno es obligatorio"
        if (phone.isBlank()) {
            errors["telefono"] = "El teléfono es obligatorio"
        } else if (!phone.replace(" ", "").matches(Regex("^\\d{10}$"))) {
            errors["telefono"] = "El teléfono debe tener 10 dígitos"
        }
        if (email.isBlank()) {
            errors["email"] = "El correo es obligatorio"
        } else if (!email.matches(Regex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}\$"))) {
            errors["email"] = "Formato de correo inválido"
        }
        if (roles.isEmpty()) errors["roles"] = "Debe seleccionar al menos un rol"
        return errors
    }

    fun onLogout() { 
        // Simply clear local cached data so another user doesn't see it
        viewModelScope.launch {
            repository.clearSelectedUser()
        }
    }
}
