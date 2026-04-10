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
 * Sealed class representing the different states of a data loading operation.
 * Used to communicate progress and results between the repository and the UI layers.
 */
sealed class LoadResult<out T> {
    /** Operation completed successfully with data [T]. */
    data class Success<T>(val data: T) : LoadResult<T>()
    
    /** Operation failed with an error [message]. */
    data class Error(val message: String) : LoadResult<Nothing>()
    
    /** Operation is currently in progress. */
    data object Loading : LoadResult<Nothing>()
}

/**
 * Repository responsible for managing user data and account security states.
 * Orchestrates network calls via [ApiService] and maintains local state flows for the UI.
 *
 * @property apiService Gateway for making HTTP calls to the backend.
 */
class UsuarioRepository(
    private val apiService: ApiService
) {
    private val TAG = "UsuarioRepo"
    
    /** Observable list of all users retrieved from the server. */
    private val _allUsers = MutableStateFlow<List<Usuario>>(emptyList())
    val allUsers: Flow<List<Usuario>> = _allUsers.asStateFlow()

    /** Currently selected user for detailed view or modification. */
    private val _selectedUser = MutableStateFlow<Usuario?>(null)
    val selectedUser: Flow<Usuario?> = _selectedUser.asStateFlow()

    /** Current loading status for general user operations. */
    private val _loadState = MutableStateFlow<LoadResult<Unit>>(LoadResult.Loading)
    val loadState: Flow<LoadResult<Unit>> = _loadState.asStateFlow()

    /** Map associating user IDs with their respective account security responses. */
    private val _accountStatuses = MutableStateFlow<Map<Long, CuentaResponse>>(emptyMap())
    val accountStatuses: Flow<Map<Long, CuentaResponse>> = _accountStatuses.asStateFlow()

    /**
     * Fetches the complete list of registered users.
     * Updates the local [_allUsers] cache and [_loadState] upon completion.
     * 
     * @return List of all retrieved [Usuario] objects.
     */
    suspend fun getUsuarios(): List<Usuario> {
        _loadState.value = LoadResult.Loading
        return try {
            Log.d(TAG, "GET /api/v1/usuarios")
            val response = apiService.getUsuarios()
            Log.d(TAG, "${response.size} usuarios recibidos del servidor")
            
            _allUsers.value = response
            Log.d(TAG, "Lista global de usuarios actualizada en StateFlow")
            
            _loadState.value = LoadResult.Success(Unit)
            response
        } catch (e: Exception) {
            Log.e(TAG, "Error al obtener usuarios: ${e.message}", e)
            _loadState.value = LoadResult.Error(e.localizedMessage ?: "Error desconocido")
            emptyList()
        }
    }

    /**
     * Retrieves detailed information for a specific user.
     * Searches via network first; falls back to local cache if offline or on error.
     *
     * @param id The unique identifier of the user to fetch.
     * @return The [Usuario] object if found, null otherwise.
     */
    suspend fun getUsuarioPorId(id: Long): Usuario? {
        return try {
            Log.d(TAG, "GET /api/v1/usuarios/$id")
            val response = apiService.getUsuarioPorId(id)
            Log.d(TAG, "Usuario '${response.nombreCompleto}' encontrado")
            
            _selectedUser.value = response
            Log.d(TAG, "Usuario seleccionado actualizado en StateFlow")
            response
        } catch (e: Exception) {
            Log.e(TAG, "Error getUsuarioPorId($id)", e)
            Log.d(TAG, "Buscando usuario en el caché local...")
            val cached = _allUsers.value.find { it.id == id }
            if (cached != null) {
                Log.d(TAG, "Usuario encontrado en caché: ${cached.nombreCompleto}")
                _selectedUser.value = cached
            } else {
                Log.w(TAG, "No se encontró el usuario ni en red ni en caché")
            }
            cached
        }
    }

    /**
     * Fetches the security account metadata for a specific user.
     * 
     * @param id Unique identifier for the target user.
     * @return [CuentaResponse] detailing roles and active status.
     */
    suspend fun getCuentaUsuario(id: Long): CuentaResponse? {
        return try {
            Log.d(TAG, "GET /api/v1/usuarios/$id/cuenta")
            val cuenta = apiService.getCuentaUsuario(id)
            Log.d(TAG, "Cuenta de usuario recuperada (Activo: ${cuenta.activa})")
            
            val updated = _accountStatuses.value.toMutableMap()
            updated[id] = cuenta
            _accountStatuses.value = updated
            Log.d(TAG, "Mapa de estados de cuenta actualizado localmente")
            
            cuenta
        } catch (e: Exception) {
            Log.e(TAG, "Error getCuenta($id): ${e.message}", e)
            null
        }
    }

    /**
     * Executes a bulk update of account statuses for all currently cached users.
     * Iterates through all users in [_allUsers] and fetches their [CuentaResponse].
     */
    suspend fun fetchAllAccountStatuses() {
        val users = _allUsers.value
        Log.d(TAG, "Iniciando mapeo masivo de estados de cuenta para ${users.size} usuarios")
        val statusMap = mutableMapOf<Long, CuentaResponse>()
        for (user in users) {
            try {
                val cuenta = apiService.getCuentaUsuario(user.id)
                statusMap[user.id] = cuenta
                Log.d(TAG, "Cuenta recuperada para usuario ${user.id} (${user.nombreCompleto})")
            } catch (e: Exception) {
                Log.w(TAG, "No se pudo obtener cuenta para usuario ${user.id}")
            }
        }
        _accountStatuses.value = statusMap
        Log.d(TAG, "Finalizado: Mapa masivo de cuentas actualizado")
    }

    /**
     * Registers a new user within the system.
     *
     * @param request Data package containing the new user's information.
     * @return [Result] encapsulating the created [Usuario] or an [Exception].
     */
    suspend fun registrarUsuario(request: UserRequest): Result<Usuario> {
        return try {
            Log.d(TAG, "POST /api/v1/usuarios - Solicitud de registro enviada")
            val response = apiService.registrarUsuario(request)
            if (response.isSuccessful && response.body() != null) {
                Log.d(TAG, "Registro exitoso en servidor")
                getUsuarios() // Automate list refresh after creation
                Result.success(response.body()!!)
            } else {
                Log.e(TAG, "Servidor rechazó el registro (${response.code()})")
                Result.failure(Exception("Error ${response.code()}: ${response.message()}"))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Excepción al registrar: ${e.message}", e)
            Result.failure(e)
        }
    }

    /**
     * Updates an existing user's profile information.
     *
     * @param id Unique database ID of the user to update.
     * @param request Updated data for the target user.
     * @return [Result] containing the updated [Usuario] or an [Exception].
     */
    suspend fun actualizarUsuario(id: Long, request: UserRequest): Result<Usuario> {
        return try {
            Log.d(TAG, "PUT /api/v1/usuarios/$id - Solicitud de actualización enviada")
            val response = apiService.actualizarUsuario(id, request)
            if (response.isSuccessful && response.body() != null) {
                Log.d(TAG, "Actualización exitosa en servidor")
                getUsuarios() // Refresh cache to reflect changes
                Result.success(response.body()!!)
            } else {
                Log.e(TAG, "Servidor rechazó la actualización (${response.code()})")
                Result.failure(Exception("Error ${response.code()}: ${response.message()}"))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Excepción al actualizar: ${e.message}", e)
            Result.failure(e)
        }
    }

    /**
     * Toggles the active/inactive state of a user account.
     *
     * @param id Identifier for the account to toggle.
     * @return [Result] encapsulating the new account state response.
     */
    suspend fun toggleEstadoUsuario(id: Long): Result<EstadoCuentaResponse> {
        return try {
            Log.d(TAG, "PATCH /api/v1/usuarios/$id/estado - Cambiando estado de cuenta")
            val response = apiService.toggleEstadoUsuario(id)
            if (response.isSuccessful && response.body() != null) {
                Log.d(TAG, "Cambio de estado confirmado por servidor")
                getCuentaUsuario(id) // Sync local account status
                getUsuarios()       // Sync general user info
                Result.success(response.body()!!)
            } else {
                Log.e(TAG, "Servidor rechazó el cambio de estado (${response.code()})")
                Result.failure(Exception("Error ${response.code()}: ${response.message()}"))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Excepción al toggle estado: ${e.message}", e)
            Result.failure(e)
        }
    }

    /**
     * Resets the selected user state to null.
     */
    fun clearSelectedUser() {
        Log.d(TAG, "Limpiando usuario seleccionado del caché")
        _selectedUser.value = null
    }
}
