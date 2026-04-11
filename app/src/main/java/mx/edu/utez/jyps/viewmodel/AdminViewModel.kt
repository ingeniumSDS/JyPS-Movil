package mx.edu.utez.jyps.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import mx.edu.utez.jyps.data.model.CuentaResponse
import mx.edu.utez.jyps.data.model.DepartamentoResponse
import mx.edu.utez.jyps.data.model.Usuario
import mx.edu.utez.jyps.data.model.UserRequest
import mx.edu.utez.jyps.data.network.RetrofitInstance
import mx.edu.utez.jyps.data.repository.DepartmentRepository
import mx.edu.utez.jyps.data.repository.LoadResult
import mx.edu.utez.jyps.data.repository.UsuarioRepository

/**
 * ViewModel orchestrating the administrator dashboard UI state.
 * Manages user CRUD with real-time reactive validation.
 */
class AdminViewModel(
    private val repository: UsuarioRepository = UsuarioRepository(RetrofitInstance.api),
    private val deptRepository: DepartmentRepository = DepartmentRepository(RetrofitInstance.api)
) : ViewModel() {

    // ── User list ────────────────────────────────────
    val users: StateFlow<List<Usuario>> = repository.allUsers
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val selectedUser: StateFlow<Usuario?> = repository.selectedUser
        .stateIn(viewModelScope, SharingStarted.Lazily, null)

    val selectedUserAccount: StateFlow<CuentaResponse?> = repository.selectedUserAccount
        .stateIn(viewModelScope, SharingStarted.Lazily, null)

    val loadState: StateFlow<LoadResult<Unit>> = repository.loadState
        .stateIn(viewModelScope, SharingStarted.Lazily, LoadResult.Loading)

    val departamentos: StateFlow<List<DepartamentoResponse>> = deptRepository.allDepartments
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

    private val _newUserDepartmentId = MutableStateFlow(1L)
    val newUserDepartmentId: StateFlow<Long> = _newUserDepartmentId

    private val _newUserStartHour = MutableStateFlow(8)
    val newUserStartHour: StateFlow<Int> = _newUserStartHour

    private val _newUserStartMinute = MutableStateFlow(0)
    val newUserStartMinute: StateFlow<Int> = _newUserStartMinute

    private val _newUserEndHour = MutableStateFlow(16)
    val newUserEndHour: StateFlow<Int> = _newUserEndHour

    private val _newUserEndMinute = MutableStateFlow(0)
    val newUserEndMinute: StateFlow<Int> = _newUserEndMinute

    // Real-time validation states
    private val _createFormErrors = MutableStateFlow<Map<String, String>>(emptyMap())
    val createFormErrors: StateFlow<Map<String, String>> = _createFormErrors

    private val _managedDepartmentId = MutableStateFlow<Long?>(null)
    val managedDepartmentId: StateFlow<Long?> = _managedDepartmentId

    private val _createServerResponseError = MutableStateFlow<String?>(null)
    val createServerResponseError: StateFlow<String?> = _createServerResponseError

    private val _scrollToTopTrigger = MutableStateFlow(0)
    val scrollToTopTrigger: StateFlow<Int> = _scrollToTopTrigger

    // Validation flags to prevent showing "required" errors too early
    private var createSubmitAttempted = false
    private var editSubmitAttempted = false

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

    private val _editDepartmentId = MutableStateFlow(1L)
    val editDepartmentId: StateFlow<Long> = _editDepartmentId

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

    private val _editManagedDepartmentId = MutableStateFlow<Long?>(null)
    val editManagedDepartmentId: StateFlow<Long?> = _editManagedDepartmentId

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
        viewModelScope.launch {
            // Minimal buffer for UI stability, nearly instant
            kotlinx.coroutines.delay(100)
            loadAllData()
        }
    }

    private var isCurrentlyLoading = false

    /**
     * Proactively loads both users and departments to ensure all dropdowns
     * and lists are ready for the administrator.
     */
    fun loadAllData() {
        if (isCurrentlyLoading) return
        viewModelScope.launch {
            isCurrentlyLoading = true
            _isLoadingUsers.value = true
            
            // Run both requests in parallel for maximum speed
            val usersDeferred = launch { repository.getUsuarios() }
            val deptsDeferred = launch { deptRepository.getDepartamentos() }
            
            usersDeferred.join()
            deptsDeferred.join()
            
            _isLoadingUsers.value = false
            isCurrentlyLoading = false
        }
    }

    fun loadUsuarios() = loadAllData()

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

    // ── CREATE USER HANDLERS ──────────────────────────
    fun setCreateUserVisible(visible: Boolean) {
        if (!visible) resetCreateForm()
        _isCreateUserVisible.value = visible
    }

    fun onNameChange(v: String) { _newUserName.value = v; runCreateValidation() }
    fun onPaternoChange(v: String) { _newUserPaterno.value = v; runCreateValidation() }
    fun onMaternoChange(v: String) { _newUserMaterno.value = v; runCreateValidation() }
    fun onPhoneChange(v: String) { _newUserPhone.value = v; runCreateValidation() }
    fun onEmailChange(v: String) { _newUserEmail.value = v; runCreateValidation() }
    fun onDepartmentChange(v: Long) { _newUserDepartmentId.value = v; runCreateValidation() }
    fun onManagedDepartmentChange(v: Long?) { _managedDepartmentId.value = v; runCreateValidation() }

    fun onStartTimeChange(h: Int, m: Int) { _newUserStartHour.value = h; _newUserStartMinute.value = m }
    fun onEndTimeChange(h: Int, m: Int) { _newUserEndHour.value = h; _newUserEndMinute.value = m }

    fun toggleRole(roleId: Int) {
        val current = _newUserRoles.value.toMutableSet()
        if (roleId == 2) {
            current.clear(); current.add(2)
        } else {
            current.remove(2)
            if (current.contains(roleId)) {
                current.remove(roleId); if (roleId == 3) _managedDepartmentId.value = null
            } else {
                current.add(roleId); if (roleId == 3 || roleId == 4 || roleId == 5) current.add(1)
            }
        }
        _newUserRoles.value = current
        runCreateValidation()
    }

    private fun runCreateValidation() {
        _createFormErrors.value = validateForm(
            _newUserName.value, _newUserPaterno.value, _newUserMaterno.value,
            _newUserPhone.value, _newUserEmail.value, _newUserRoles.value,
            _managedDepartmentId.value, createSubmitAttempted
        )
    }

    private fun resetCreateForm() {
        createSubmitAttempted = false
        _newUserName.value = ""
        _newUserPaterno.value = ""
        _newUserMaterno.value = ""
        _newUserPhone.value = ""
        _newUserEmail.value = ""
        _newUserRoles.value = emptySet()
        _newUserDepartmentId.value = 1
        _newUserStartHour.value = 8; _newUserStartMinute.value = 0
        _newUserEndHour.value = 16; _newUserEndMinute.value = 0
        _createFormErrors.value = emptyMap()
        _managedDepartmentId.value = null
        _createServerResponseError.value = null
    }

    fun saveNewUser() {
        createSubmitAttempted = true
        runCreateValidation()
        if (_createFormErrors.value.isNotEmpty()) {
            _scrollToTopTrigger.value += 1
            return
        }

        val request = UserRequest(
            nombre = _newUserName.value.trim(),
            apellidoPaterno = _newUserPaterno.value.trim(),
            apellidoMaterno = _newUserMaterno.value.trim(),
            correo = _newUserEmail.value.trim(),
            telefono = _newUserPhone.value.trim(),
            horaEntrada = "%02d:%02d:00".format(_newUserStartHour.value, _newUserStartMinute.value),
            horaSalida = "%02d:%02d:00".format(_newUserEndHour.value, _newUserEndMinute.value),
            roles = _newUserRoles.value.mapNotNull { roleMapping[it] },
            departamentoId = _newUserDepartmentId.value
        )

        viewModelScope.launch {
            _isProcessing.value = true
            repository.registrarUsuario(request).onSuccess { newUser ->
                var assignmentError: String? = null
                
                if (_newUserRoles.value.contains(3) && _managedDepartmentId.value != null) {
                    val assignResult = deptRepository.asignarJefe(_managedDepartmentId.value!!, newUser.id)
                    if (!assignResult.isSuccess) {
                        val msg = assignResult.exceptionOrNull()?.message ?: ""
                        assignmentError = "Usuario creado, pero falló asignación de jefe: " + 
                            (if (msg.contains("403")) "Este usuario ya es jefe de otro departamento." else "Error de servidor.")
                    }
                }

                if (assignmentError == null) {
                    loadAllData() // Refrescamos todos los datos
                    setCreateUserVisible(false)
                    showFeedback("Usuario creado exitosamente", true)
                } else {
                    _createServerResponseError.value = assignmentError
                    _scrollToTopTrigger.value += 1
                }
            }.onFailure { e ->
                _createServerResponseError.value = "Error al crear usuario: ${e.message}"
                _scrollToTopTrigger.value += 1
            }
            _isProcessing.value = false
        }
    }

    // ── EDIT USER HANDLERS ───────────────────────────
    fun openEditUser(usuario: Usuario) {
        editSubmitAttempted = false
        _editingUser.value = usuario
        val parts = usuario.nombreCompleto.split(" ")
        _editName.value = parts.getOrElse(0) { "" }
        _editPaterno.value = parts.getOrElse(1) { "" }
        _editMaterno.value = parts.drop(2).joinToString(" ")
        _editPhone.value = usuario.telefono
        _editEmail.value = usuario.correo
        _editRoles.value = usuario.roles.mapNotNull { reverseRoleMapping[it] }.toSet()
        _editDepartmentId.value = usuario.departamentoId
        
        // Búsqueda ultra-resiliente (forzando conversión de tipos para evitar Int vs Long mismatch)
        val targetId = usuario.id.toLong()
        val managedDept = departamentos.value.find { dept ->
            val jId = dept.jefeId?.toLong()
            jId != null && jId == targetId
        }
        
        _editManagedDepartmentId.value = managedDept?.id
        
        Log.d("AdminVM", "Lookup Jefe - UsuarioID: $targetId | Encontrado Depto: ${managedDept?.nombre ?: "NINGUNO"} (ID: ${managedDept?.id ?: "N/A"})")
        
        _editStartHour.value = usuario.entradaHour
        _editStartMinute.value = usuario.entradaMinute
        _editEndHour.value = usuario.salidaHour
        _editEndMinute.value = usuario.salidaMinute
        _editFormErrors.value = emptyMap()
        _editServerResponseError.value = null
        _isEditUserVisible.value = true
    }

    fun closeEditUser() {
        _isEditUserVisible.value = false; _editingUser.value = null
        _editFormErrors.value = emptyMap(); _editServerResponseError.value = null
    }

    fun onEditNameChange(v: String) { _editName.value = v; runEditValidation() }
    fun onEditPaternoChange(v: String) { _editPaterno.value = v; runEditValidation() }
    fun onEditMaternoChange(v: String) { _editMaterno.value = v; runEditValidation() }
    fun onEditPhoneChange(v: String) { _editPhone.value = v; runEditValidation() }
    fun onEditEmailChange(v: String) { _editEmail.value = v; runEditValidation() }
    fun onEditDepartmentChange(id: Long) { _editDepartmentId.value = id; runEditValidation() }
    fun onEditManagedDepartmentChange(id: Long?) { _editManagedDepartmentId.value = id; runEditValidation() }

    fun onEditStartTimeChange(h: Int, m: Int) { _editStartHour.value = h; _editStartMinute.value = m }
    fun onEditEndTimeChange(h: Int, m: Int) { _editEndHour.value = h; _editEndMinute.value = m }

    fun toggleEditRole(roleId: Int) {
        val current = _editRoles.value.toMutableSet()
        if (roleId == 2) {
            current.clear(); current.add(2)
        } else {
            current.remove(2)
            if (current.contains(roleId)) {
                current.remove(roleId); if (roleId == 3) _editManagedDepartmentId.value = null
            } else {
                current.add(roleId); if (roleId == 3 || roleId == 4 || roleId == 5) current.add(1)
            }
        }
        _editRoles.value = current
        runEditValidation()
    }

    private fun runEditValidation() {
        _editFormErrors.value = validateForm(
            _editName.value, _editPaterno.value, _editMaterno.value,
            _editPhone.value, _editEmail.value, _editRoles.value,
            _editManagedDepartmentId.value, editSubmitAttempted
        )
    }

    fun saveEditUser() {
        val userId = _editingUser.value?.id ?: return
        editSubmitAttempted = true
        runEditValidation()
        if (_editFormErrors.value.isNotEmpty()) {
            _scrollToTopTrigger.value += 1; return
        }

        val startStr = "%02d:%02d:00".format(_editStartHour.value, _editStartMinute.value)
        val endStr = "%02d:%02d:00".format(_editEndHour.value, _editEndMinute.value)
        val selectedRoles = _editRoles.value.mapNotNull { roleMapping[it] }

        val request = UserRequest(
            nombre = _editName.value.trim(),
            apellidoPaterno = _editPaterno.value.trim(),
            apellidoMaterno = _editMaterno.value.trim(),
            correo = _editEmail.value.trim(),
            telefono = _editPhone.value.trim(),
            horaEntrada = startStr,
            horaSalida = endStr,
            roles = selectedRoles,
            departamentoId = _editDepartmentId.value
        )

        Log.d("AdminVM", "Intentando actualizar usuario $userId con roles: $selectedRoles")

        viewModelScope.launch {
            _isProcessing.value = true
            
            // Paso 1: Actualizar datos básicos y roles del usuario
            repository.actualizarUsuario(userId, request).onSuccess { updatedUser ->
                Log.d("AdminVM", "Paso 1 exitoso: Usuario actualizado")
                
                var assignmentError: String? = null
                
                // Paso 2: Si es jefe, intentar la asignación de departamento
                if (_editRoles.value.contains(3) && _editManagedDepartmentId.value != null) {
                    Log.d("AdminVM", "Paso 2: Asignando cargo de jefe en depto: ${_editManagedDepartmentId.value}")
                    val assignResult = deptRepository.asignarJefe(_editManagedDepartmentId.value!!, updatedUser.id)
                    
                    if (!assignResult.isSuccess) {
                        val msg = assignResult.exceptionOrNull()?.message ?: ""
                        assignmentError = "Usuario actualizado, pero falló asignación de jefe: " + 
                            (if (msg.contains("403")) "Este usuario ya es jefe de otro departamento." else "Error de servidor.")
                        Log.e("AdminVM", "Error en Paso 2: $assignmentError")
                    }
                }

                if (assignmentError == null) {
                    Log.d("AdminVM", "Flujo completo de guardado exitoso")
                    loadAllData() // Refrescamos todos los datos (Usuarios y Deptos)
                    closeEditUser()
                    showFeedback("Usuario actualizado exitosamente", true)
                } else {
                    // FALLÓ LA SEGUNDA PARTE
                    _editServerResponseError.value = assignmentError
                    _scrollToTopTrigger.value += 1
                }
            }.onFailure { e ->
                // FALLÓ LA PRIMERA PARTE
                Log.e("AdminVM", "Fallo en Paso 1: ${e.message}")
                _editServerResponseError.value = "Error al actualizar perfil: ${e.message}"
                _scrollToTopTrigger.value += 1
            }
            
            _isProcessing.value = false
        }
    }

    // ── Validation ───────────────────────────────────
    private fun validateForm(
        name: String, paterno: String, materno: String,
        phone: String, email: String, roles: Set<Int>,
        managedDeptId: Long?, showRequired: Boolean
    ): Map<String, String> {
        val errors = mutableMapOf<String, String>()
        
        // Helper to check if we should show "Required" error
        fun checkRequired(v: String, key: String, msg: String) {
            if (v.isBlank() && showRequired) errors[key] = msg
        }

        checkRequired(name, "nombre", "El nombre es obligatorio")
        checkRequired(paterno, "paterno", "El apellido paterno es obligatorio")
        checkRequired(materno, "materno", "El apellido materno es obligatorio")
        
        if (phone.isNotBlank()) {
            if (!phone.replace(" ", "").matches(Regex("^\\d{10}$"))) errors["telefono"] = "El teléfono debe tener 10 dígitos"
        } else if (showRequired) {
            errors["telefono"] = "El teléfono es obligatorio"
        }
        
        if (email.isNotBlank()) {
            if (!email.matches(Regex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}\$"))) errors["email"] = "Formato de correo inválido"
        } else if (showRequired) {
            errors["email"] = "El correo es obligatorio"
        }
        
        if (showRequired && roles.isEmpty()) errors["roles"] = "Debe seleccionar al menos un rol"
        if (roles.contains(3) && managedDeptId == null && showRequired) errors["managedDepto"] = "Asignación obligatoria"
        
        return errors
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

    fun onLogout() { viewModelScope.launch { repository.clearSelectedUser() } }
}
