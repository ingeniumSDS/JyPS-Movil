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

    fun onLogout() {
        // Handle logic for logout, maybe resetting data or updating a specific flow State
    }
}
