package mx.edu.utez.jyps.data.repository

import android.util.Log
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import mx.edu.utez.jyps.data.model.CuentaResponse
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

/**
 * Repository handling all network operations related to users and accounts.
 * Orchestrates data between the ApiService and local state flows.
 */
class UsuarioRepository(
    private val apiService: ApiService
) {
    private val TAG = "UsuarioRepo"
    
    private val _allUsers = MutableStateFlow<List<Usuario>>(emptyList())
    val allUsers: Flow<List<Usuario>> = _allUsers.asStateFlow()

    private val _selectedUser = MutableStateFlow<Usuario?>(null)
    val selectedUser: Flow<Usuario?> = _selectedUser.asStateFlow()

    private val _selectedUserAccount = MutableStateFlow<CuentaResponse?>(null)
    val selectedUserAccount: Flow<CuentaResponse?> = _selectedUserAccount.asStateFlow()

    private val _loadState = MutableStateFlow<LoadResult<Unit>>(LoadResult.Loading)
    val loadState: Flow<LoadResult<Unit>> = _loadState.asStateFlow()

    /**
     * Fetches all registered users from the backend endpoint.
     */
    suspend fun getUsuarios(): List<Usuario> {
        _loadState.value = LoadResult.Loading
        return try {
            Log.d(TAG, "GET /api/v1/usuarios")
            val response = apiService.getUsuarios()
            Log.d(TAG, "${response.size} usuarios recibidos del servidor")
            
            _allUsers.value = response
            _loadState.value = LoadResult.Success(Unit)
            response
        } catch (e: Exception) {
            Log.e(TAG, "Error al obtener usuarios: ${e.message}", e)
            _loadState.value = LoadResult.Error(e.localizedMessage ?: "Error desconocido")
            emptyList()
        }
    }

    /**
     * Fetches a specific user by their unique database identifier.
     */
    suspend fun getUsuarioPorId(id: Long): Usuario? {
        return try {
            Log.d(TAG, "GET /api/v1/usuarios/$id")
            val response = apiService.getUsuarioPorId(id)
            Log.d(TAG, "Usuario '${response.nombreCompleto}' encontrado")
            
            _selectedUser.value = response
            response
        } catch (e: Exception) {
            Log.e(TAG, "Error getUsuarioPorId($id)", e)
            val cached = _allUsers.value.find { it.id == id }
            if (cached != null) {
                Log.d(TAG, "Usuario recuperado de caché local")
                _selectedUser.value = cached
            }
            cached
        }
    }

    /**
     * Fetches extended security account details.
     * Updates [_selectedUserAccount] upon success.
     * 
     * @param id The user ID to query.
     * @return [CuentaResponse] detailing specific account security metrics.
     */
    suspend fun getCuentaUsuario(id: Long): CuentaResponse? {
        return try {
            Log.d(TAG, "GET /api/v1/usuarios/$id/cuenta")
            val cuenta = apiService.getCuentaUsuario(id)
            Log.d(TAG, "Cuenta recuperada exitosamente")
            _selectedUserAccount.value = cuenta
            cuenta
        } catch (e: Exception) {
            Log.e(TAG, "Error getCuenta($id): ${e.message}", e)
            null
        }
    }

    /**
     * Registers a new user within the system.
     */
    suspend fun registrarUsuario(request: UserRequest): Result<Usuario> {
        return try {
            Log.d(TAG, "POST /api/v1/usuarios")
            val response = apiService.registrarUsuario(request)
            if (response.isSuccessful && response.body() != null) {
                Log.d(TAG, "Registro exitoso")
                getUsuarios()
                Result.success(response.body()!!)
            } else {
                val err = "Error de servidor (${response.code()})"
                Log.e(TAG, err)
                Result.failure(Exception(err))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Excepción al registrar: ${e.message}", e)
            Result.failure(e)
        }
    }

    /**
     * Updates an existing user record.
     */
    suspend fun actualizarUsuario(id: Long, request: UserRequest): Result<Usuario> {
        return try {
            Log.d(TAG, "PUT /api/v1/usuarios/$id")
            val response = apiService.actualizarUsuario(id, request)
            if (response.isSuccessful && response.body() != null) {
                Log.d(TAG, "Actualización exitosa")
                getUsuarios()
                Result.success(response.body()!!)
            } else {
                val err = "Error de servidor (${response.code()})"
                Log.e(TAG, err)
                Result.failure(Exception(err))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Excepción al actualizar: ${e.message}", e)
            Result.failure(e)
        }
    }

    /**
     * Toggles the active status of a user account.
     */
    suspend fun toggleEstadoUsuario(id: Long): Result<EstadoCuentaResponse> {
        return try {
            Log.d(TAG, "PATCH /api/v1/usuarios/$id/estado")
            val response = apiService.toggleEstadoUsuario(id)
            if (response.isSuccessful && response.body() != null) {
                Log.d(TAG, "Cambio de estado exitoso")
                getUsuarios()
                Result.success(response.body()!!)
            } else {
                val err = "Error de servidor (${response.code()})"
                Log.e(TAG, err)
                Result.failure(Exception(err))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Excepción al toggle estado: ${e.message}", e)
            Result.failure(e)
        }
    }

    /**
     * Resets the selected user flows.
     */
    fun clearSelectedUser() {
        Log.d(TAG, "Limpiando estados del usuario seleccionado")
        _selectedUser.value = null
        _selectedUserAccount.value = null
    }
}
