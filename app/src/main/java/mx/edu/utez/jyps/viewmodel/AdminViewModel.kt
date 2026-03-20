package mx.edu.utez.jyps.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import mx.edu.utez.jyps.data.model.Cuenta
import mx.edu.utez.jyps.data.model.Departamento
import mx.edu.utez.jyps.data.model.Roles
import mx.edu.utez.jyps.data.model.UserWithDetails
import mx.edu.utez.jyps.data.model.Usuario
import mx.edu.utez.jyps.data.model.UserRequest
import mx.edu.utez.jyps.data.network.RetrofitInstance
import mx.edu.utez.jyps.data.repository.UsuarioRepository

class AdminViewModel(
    private val repository: UsuarioRepository = UsuarioRepository(RetrofitInstance.api)
) : ViewModel() {

    // Observamos la lista desde el repositorio
    val users: StateFlow<List<UserWithDetails>> = repository.allUsers
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    private val _selectedDrawerItem = MutableStateFlow("admin_users")
    val selectedDrawerItem: StateFlow<String> = _selectedDrawerItem

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    private val _selectedFilter = MutableStateFlow("Todos")
    val selectedFilter: StateFlow<String> = _selectedFilter

    init {
        loadInitialData()
    }

    private fun loadInitialData() {
        val rolTrabajador = Roles(1, "Trabajador")
        val rolSeguridad = Roles(2, "Seguridad")
        val rolJefeArea = Roles(3, "Jefe de Área")
        val rolAdmin = Roles(4, "Administrador")
        val rolRH = Roles(5, "Recursos Humanos")

        val deptoTI = Departamento(1, "Tecnologías de la Información", null, "", true)
        val deptoSoftware = Departamento(2, "Desarrollo de Software", null, "", true)

        val cuentaActiva = Cuenta(0, 0, null, null, false, false, null, true, "****")
        val cuentaInactiva = Cuenta(0, 0, null, null, false, false, null, false, "****")

        val mockUsers = listOf(
            UserWithDetails(
                usuario = Usuario(1, "Juan", "Pérez", "García", "juan.perez@utez.edu.mx", "777 123 4567", 1, "08:00", "16:00"),
                cuenta = cuentaActiva.copy(idUsuario = 1),
                departamento = deptoTI,
                roles = listOf(rolTrabajador)
            ),
            UserWithDetails(
                usuario = Usuario(2, "María", "González", "Hernández", "maria.gonzalez@utez.edu.mx", "777 234 5678", 1, "08:00", "16:00"),
                cuenta = cuentaActiva.copy(idUsuario = 2),
                departamento = deptoTI,
                roles = listOf(rolSeguridad)
            ),
            UserWithDetails(
                usuario = Usuario(3, "Roberto", "Sánchez", "López", "roberto.sanchez@utez.edu.mx", "777 345 6789", 1, "08:00", "16:00"),
                cuenta = cuentaActiva.copy(idUsuario = 3),
                departamento = deptoTI,
                roles = listOf(rolJefeArea)
            ),
            UserWithDetails(
                usuario = Usuario(4, "Carlos", "Rodríguez", "Torres", "carlos.rodriguez@utez.edu.mx", "777 567 8901", 1, "08:00", "16:00"),
                cuenta = cuentaActiva.copy(idUsuario = 4),
                departamento = deptoTI,
                roles = listOf(rolAdmin)
            ),
            UserWithDetails(
                usuario = Usuario(5, "Pedro", "Ramírez", "Gómez", "pedro.ramirez@utez.edu.mx", "777 678 9012", 2, "08:00", "16:00"),
                cuenta = cuentaInactiva.copy(idUsuario = 5),
                departamento = deptoSoftware,
                roles = listOf(rolTrabajador)
            ),
            UserWithDetails(
                usuario = Usuario(6, "Ana", "Torres", "Méndez", "ana.torres@utez.edu.mx", "777 789 0123", 2, "08:00", "16:00"),
                cuenta = cuentaActiva.copy(idUsuario = 6),
                departamento = deptoSoftware,
                roles = listOf(rolJefeArea)
            ),
            UserWithDetails(
                usuario = Usuario(7, "Laura", "Martínez", "Ramírez", "laura.martinez@utez.edu.mx", "777 456 7890", 2, "08:00", "16:00"),
                cuenta = cuentaActiva.copy(idUsuario = 7),
                departamento = deptoSoftware,
                roles = listOf(rolRH)
            )
        )
        repository.setInitialMockData(mockUsers)
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

    // Feedback State
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
        if (current.contains(roleId)) current.remove(roleId)
        else current.add(roleId)
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
                    
                    // Actualizar lista local en el repositorio
                    updateLocalListAfterSuccess()
                    
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

    private fun updateLocalListAfterSuccess() {
        val roleMap = mapOf(
            1 to Roles(1, "Trabajador"),
            2 to Roles(2, "Seguridad"),
            3 to Roles(3, "Jefe de Área"),
            4 to Roles(4, "Administrador"),
            5 to Roles(5, "Recursos Humanos")
        )

        val selectedRoles = _newUserRoles.value.mapNotNull { roleMap[it] }
        val depto = when(_newUserDepartmentId.value) {
            1 -> Departamento(1, "Tecnologías de la Información", null, "", true)
            2 -> Departamento(2, "Desarrollo de Software", null, "", true)
            else -> Departamento(_newUserDepartmentId.value, "Departamento ${_newUserDepartmentId.value}", null, "", true)
        }

        val newUser = UserWithDetails(
            usuario = Usuario(
                id = (users.value.maxOfOrNull { it.usuario.id } ?: 0) + 1,
                nombre = _newUserName.value,
                apellidoPaterno = _newUserPaterno.value,
                apellidoMaterno = _newUserMaterno.value,
                correo = _newUserEmail.value,
                telefono = _newUserPhone.value,
                idDepartamento = depto.id,
                inicioJornada = _newUserStartTime.value.take(5),
                finJornada = _newUserEndTime.value.take(5)
            ),
            cuenta = Cuenta(
                idUsuario = 0,
                intentosFallidos = 0,
                tokenRecuperacion = null,
                tokenExpiresAt = null,
                tokenUsado = false,
                bloqueada = false,
                blockedAt = null,
                activa = true,
                passwordHash = "****"
            ),
            departamento = depto,
            roles = selectedRoles
        )

        repository.updateLocalList(newUser)
    }

    fun onLogout() { /* Logic for logout */ }
}
