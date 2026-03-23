package mx.edu.utez.jyps.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import mx.edu.utez.jyps.data.model.Usuario
import mx.edu.utez.jyps.data.model.UserRequest
import mx.edu.utez.jyps.data.network.RetrofitInstance
import mx.edu.utez.jyps.data.repository.UsuarioRepository

class AdminViewModel(
    private val repository: UsuarioRepository = UsuarioRepository(RetrofitInstance.api)
) : ViewModel() {

    val users: StateFlow<List<Usuario>> = repository.allUsers
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val selectedUser: StateFlow<Usuario?> = repository.selectedUser
        .stateIn(viewModelScope, SharingStarted.Lazily, null)

    private val _selectedDrawerItem = MutableStateFlow("admin_users")
    val selectedDrawerItem: StateFlow<String> = _selectedDrawerItem

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    private val _selectedFilter = MutableStateFlow("Todos")
    val selectedFilter: StateFlow<String> = _selectedFilter

    private val _isLoadingUsers = MutableStateFlow(false)
    val isLoadingUsers: StateFlow<Boolean> = _isLoadingUsers

    private val _isUserDetailVisible = MutableStateFlow(false)
    val isUserDetailVisible: StateFlow<Boolean> = _isUserDetailVisible

    init {
        loadUsuarios()
    }

    fun loadUsuarios() {
        viewModelScope.launch {
            _isLoadingUsers.value = true
            repository.getUsuarios()
            _isLoadingUsers.value = false
        }
    }

    fun viewUserDetail(userId: Long) {
        viewModelScope.launch {
            repository.getUsuarioPorId(userId)
            _isUserDetailVisible.value = true
        }
    }

    fun closeUserDetail() {
        _isUserDetailVisible.value = false
        repository.clearSelectedUser()
    }

    fun selectDrawerItem(item: String) { _selectedDrawerItem.value = item }
    fun onSearchQueryChange(query: String) { _searchQuery.value = query }
    fun setFilter(filter: String) { _selectedFilter.value = filter }

    // --- CREATE USER FORM STATE ---
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

    private val _newUserStartTime = MutableStateFlow("08:00:00")
    val newUserStartTime: StateFlow<String> = _newUserStartTime

    private val _newUserEndTime = MutableStateFlow("16:00:00")
    val newUserEndTime: StateFlow<String> = _newUserEndTime

    private val _showToast = MutableStateFlow(false)
    val showToast: StateFlow<Boolean> = _showToast

    private val _toastMessage = MutableStateFlow("")
    val toastMessage: StateFlow<String> = _toastMessage

    private val _isProcessing = MutableStateFlow(false)
    val isProcessing: StateFlow<Boolean> = _isProcessing

    fun setCreateUserVisible(visible: Boolean) {
        if (!visible) resetForm()
        _isCreateUserVisible.value = visible
    }

    fun onNameChange(name: String) { _newUserName.value = name }
    fun onPaternoChange(paterno: String) { _newUserPaterno.value = paterno }
    fun onMaternoChange(materno: String) { _newUserMaterno.value = materno }
    fun onPhoneChange(phone: String) { _newUserPhone.value = phone }
    fun onEmailChange(email: String) { _newUserEmail.value = email }
    fun onDepartmentChange(id: Int) { _newUserDepartmentId.value = id }
    fun onStartTimeChange(time: String) { _newUserStartTime.value = time }
    fun onEndTimeChange(time: String) { _newUserEndTime.value = time }

    fun toggleRole(roleId: Int) {
        val current = _newUserRoles.value.toMutableSet()
        if (current.contains(roleId)) current.remove(roleId) else current.add(roleId)
        _newUserRoles.value = current
    }

    private fun resetForm() {
        _newUserName.value = ""
        _newUserPaterno.value = ""
        _newUserMaterno.value = ""
        _newUserPhone.value = ""
        _newUserEmail.value = ""
        _newUserRoles.value = emptySet()
        _newUserDepartmentId.value = 1
        _newUserStartTime.value = "08:00:00"
        _newUserEndTime.value = "16:00:00"
    }

    fun dismissToast() { _showToast.value = false }

    fun saveNewUser() {
        val roleMapping = mapOf(
            1 to "EMPLEADO",
            2 to "GUARDIA",
            3 to "JEFE_DE_DEPARTAMENTO",
            4 to "ADMINISTRADOR",
            5 to "AUDITOR"
        )
        val selectedRoleNames = _newUserRoles.value.mapNotNull { roleMapping[it] }

        val request = UserRequest(
            nombre = _newUserName.value,
            apellidoPaterno = _newUserPaterno.value,
            apellidoMaterno = _newUserMaterno.value,
            correo = _newUserEmail.value,
            telefono = _newUserPhone.value,
            horaEntrada = _newUserStartTime.value,
            horaSalida = _newUserEndTime.value,
            roles = selectedRoleNames,
            departamentoId = _newUserDepartmentId.value.toLong()
        )

        viewModelScope.launch {
            _isProcessing.value = true
            try {
                val response = repository.registrarUsuario(request)
                if (response.isSuccessful) {
                    _toastMessage.value = "Usuario creado exitosamente"
                    _showToast.value = true
                    setCreateUserVisible(false)
                } else {
                    _toastMessage.value = "Error al crear usuario: ${response.code()}"
                    _showToast.value = true
                }
            } catch (e: Exception) {
                _toastMessage.value = "Error de red: ${e.message}"
                _showToast.value = true
            } finally {
                _isProcessing.value = false
            }
        }
    }

    fun onLogout() { /* Logic for logout */ }
}
