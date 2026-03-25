package mx.edu.utez.jyps.data.repository

import android.util.Log
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import mx.edu.utez.jyps.data.model.CuentaResponse
import mx.edu.utez.jyps.data.model.Departamento
import mx.edu.utez.jyps.data.model.EstadoCuentaResponse
import mx.edu.utez.jyps.data.model.Usuario
import mx.edu.utez.jyps.data.model.UserRequest
import mx.edu.utez.jyps.data.network.ApiService

/**
 * Sealed class to distinguish between "no data" and "error" states.
 */
sealed class LoadResult<out T> {
    data class Success<T>(val data: T) : LoadResult<T>()
    data class Error(val message: String) : LoadResult<Nothing>()
    data object Loading : LoadResult<Nothing>()
}

class UsuarioRepository(
    private val apiService: ApiService
) {
    private val _allUsers = MutableStateFlow<List<Usuario>>(emptyList())
    val allUsers: Flow<List<Usuario>> = _allUsers.asStateFlow()

    private val _selectedUser = MutableStateFlow<Usuario?>(null)
    val selectedUser: Flow<Usuario?> = _selectedUser.asStateFlow()

    private val _loadState = MutableStateFlow<LoadResult<Unit>>(LoadResult.Loading)
    val loadState: Flow<LoadResult<Unit>> = _loadState.asStateFlow()

    // Account status cache: userId -> CuentaResponse
    private val _accountStatuses = MutableStateFlow<Map<Long, CuentaResponse>>(emptyMap())
    val accountStatuses: Flow<Map<Long, CuentaResponse>> = _accountStatuses.asStateFlow()

    private val _departamentos = MutableStateFlow<List<Departamento>>(emptyList())
    val departamentos: Flow<List<Departamento>> = _departamentos.asStateFlow()

    suspend fun getUsuarios(): List<Usuario> {
        _loadState.value = LoadResult.Loading
        return try {
            Log.d("UsuarioRepo", "GET /api/v1/usuarios")
            val response = apiService.getUsuarios()
            Log.d("UsuarioRepo", "OK: ${response.size} usuarios")
            _allUsers.value = response
            _loadState.value = LoadResult.Success(Unit)
            response
        } catch (e: Exception) {
            Log.e("UsuarioRepo", "Error al obtener usuarios", e)
            _loadState.value = LoadResult.Error(e.localizedMessage ?: "Error desconocido")
            emptyList()
        }
    }

    suspend fun getUsuarioPorId(id: Long): Usuario? {
        return try {
            val response = apiService.getUsuarioPorId(id)
            _selectedUser.value = response
            response
        } catch (e: Exception) {
            Log.e("UsuarioRepo", "Error getUsuarioPorId($id)", e)
            _allUsers.value.find { it.id == id }?.also { _selectedUser.value = it }
        }
    }

    suspend fun getCuentaUsuario(id: Long): CuentaResponse? {
        return try {
            val cuenta = apiService.getCuentaUsuario(id)
            val updated = _accountStatuses.value.toMutableMap()
            updated[id] = cuenta
            _accountStatuses.value = updated
            cuenta
        } catch (e: Exception) {
            Log.e("UsuarioRepo", "Error getCuenta($id)", e)
            null
        }
    }

    suspend fun fetchAllAccountStatuses() {
        val users = _allUsers.value
        val statusMap = mutableMapOf<Long, CuentaResponse>()
        for (user in users) {
            try {
                statusMap[user.id] = apiService.getCuentaUsuario(user.id)
            } catch (e: Exception) {
                Log.e("UsuarioRepo", "Error getCuenta(${user.id})", e)
            }
        }
        _accountStatuses.value = statusMap
    }

    suspend fun registrarUsuario(request: UserRequest): Result<Usuario> {
        return try {
            Log.d("UsuarioRepo", "POST /api/v1/usuarios")
            val response = apiService.registrarUsuario(request)
            if (response.isSuccessful && response.body() != null) {
                Log.d("UsuarioRepo", "Usuario creado exitosamente")
                getUsuarios() // refresh list
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Error ${response.code()}: ${response.message()}"))
            }
        } catch (e: Exception) {
            Log.e("UsuarioRepo", "Excepción al registrar", e)
            Result.failure(e)
        }
    }

    suspend fun actualizarUsuario(id: Long, request: UserRequest): Result<Usuario> {
        return try {
            Log.d("UsuarioRepo", "PUT /api/v1/usuarios/$id")
            val response = apiService.actualizarUsuario(id, request)
            if (response.isSuccessful && response.body() != null) {
                Log.d("UsuarioRepo", "Usuario actualizado exitosamente")
                getUsuarios() // refresh list
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Error ${response.code()}: ${response.message()}"))
            }
        } catch (e: Exception) {
            Log.e("UsuarioRepo", "Excepción al actualizar", e)
            Result.failure(e)
        }
    }

    suspend fun toggleEstadoUsuario(id: Long): Result<EstadoCuentaResponse> {
        return try {
            Log.d("UsuarioRepo", "PATCH /api/v1/usuarios/$id/estado")
            val response = apiService.toggleEstadoUsuario(id)
            if (response.isSuccessful && response.body() != null) {
                Log.d("UsuarioRepo", "Estado cambiado: ${response.body()?.message}")
                // Refresh account statuses after toggle
                getCuentaUsuario(id)
                getUsuarios()
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Error ${response.code()}: ${response.message()}"))
            }
        } catch (e: Exception) {
            Log.e("UsuarioRepo", "Excepción al toggle estado", e)
            Result.failure(e)
        }
    }

    suspend fun getDepartamentos(): List<Departamento> {
        return try {
            Log.d("UsuarioRepo", "GET /api/v1/departamentos")
            val response = apiService.getDepartamentos()
            _departamentos.value = response
            response
        } catch (e: Exception) {
            Log.e("UsuarioRepo", "Error getDepartamentos", e)
            emptyList()
        }
    }

    fun clearSelectedUser() {
        _selectedUser.value = null
    }
}
