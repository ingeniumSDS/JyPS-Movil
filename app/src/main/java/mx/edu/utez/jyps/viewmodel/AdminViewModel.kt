package mx.edu.utez.jyps.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import mx.edu.utez.jyps.data.model.Cuenta
import mx.edu.utez.jyps.data.model.Departamento
import mx.edu.utez.jyps.data.model.Roles
import mx.edu.utez.jyps.data.model.Usuario
import mx.edu.utez.jyps.data.model.UserWithDetails

class AdminViewModel : ViewModel() {

    private val _users = MutableStateFlow<List<UserWithDetails>>(emptyList())
    val users: StateFlow<List<UserWithDetails>> = _users

    // Menu Item Activo (Route string)
    private val _selectedDrawerItem = MutableStateFlow("admin_users")
    val selectedDrawerItem: StateFlow<String> = _selectedDrawerItem

    // Búsqueda
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    // Filtros: "Todos", "Activos", "Inactivos"
    private val _selectedFilter = MutableStateFlow("Todos")
    val selectedFilter: StateFlow<String> = _selectedFilter

    init {
        loadMockData()
    }

    private fun loadMockData() {
        // Roles hardcodeados
        val rolTrabajador = Roles(1, "Trabajador")
        val rolSeguridad = Roles(2, "Seguridad")
        val rolJefeArea = Roles(3, "Jefe de Área")
        val rolAdmin = Roles(4, "Administrador")
        val rolRH = Roles(5, "Recursos Humanos")

        // Departamentos
        val deptoTI = Departamento(1, "Tecnologías de la Información", null, "", true)
        val deptoSoftware = Departamento(2, "Desarrollo de Software", null, "", true)

        // Cuentas Base
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
                departamento = deptoSoftware, // Guessing from Figma consistency
                roles = listOf(rolRH)
            )
        )

        _users.value = mockUsers
    }

    fun selectDrawerItem(item: String) {
        _selectedDrawerItem.value = item
    }

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }

    fun setFilter(filter: String) {
        _selectedFilter.value = filter
    }

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

    private val _newUserDepartmentId = MutableStateFlow(1) // Default to TI for now
    val newUserDepartmentId: StateFlow<Int> = _newUserDepartmentId

    fun setCreateUserVisible(visible: Boolean) {
        if (!visible) resetForm()
        _isCreateUserVisible.value = visible
    }

    fun onNameChange(name: String) { _newUserName.value = name }
    fun onPaternoChange(paterno: String) { _newUserPaterno.value = paterno }
    fun onMaternoChange(materno: String) { _newUserMaterno.value = materno }
    fun onPhoneChange(phone: String) { _newUserPhone.value = phone }
    fun onEmailChange(email: String) { _newUserEmail.value = email }
    
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
    }

    fun saveNewUser() {
        val roleMap = mapOf(
            1 to Roles(1, "Trabajador"),
            2 to Roles(2, "Seguridad"),
            3 to Roles(3, "Jefe de Área"),
            4 to Roles(4, "Administrador"),
            5 to Roles(5, "Recursos Humanos")
        )

        val selectedRoles = _newUserRoles.value.mapNotNull { roleMap[it] }
        val depto = if (_newUserDepartmentId.value == 1) {
            Departamento(1, "Tecnologías de la Información", null, "", true)
        } else {
            Departamento(2, "Desarrollo de Software", null, "", true)
        }

        val newUser = UserWithDetails(
            usuario = Usuario(
                id = (_users.value.maxOfOrNull { it.usuario.id } ?: 0) + 1,
                nombre = _newUserName.value,
                apellidoPaterno = _newUserPaterno.value,
                apellidoMaterno = _newUserMaterno.value,
                correo = _newUserEmail.value,
                telefono = _newUserPhone.value,
                idDepartamento = depto.id,
                inicioJornada = "08:00",
                finJornada = "16:00"
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
                passwordHash = "temp"
            ),
            departamento = depto,
            roles = selectedRoles
        )

        _users.value = listOf(newUser) + _users.value
        setCreateUserVisible(false)
    }

    fun onLogout() {
        // Handle logic for logout
    }
}
