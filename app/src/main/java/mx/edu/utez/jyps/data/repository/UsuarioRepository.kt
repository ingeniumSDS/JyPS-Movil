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
     * 
     * @return [List] of [Usuario] objects retrieved from the server.
     */
    suspend fun getUsuarios(): List<Usuario> {
        _loadState.value = LoadResult.Loading
        return try {
            Log.d(TAG, "GET /api/v1/usuarios")
            val response = apiService.getUsuarios()
            
            Log.d(TAG, "Mapping ${response.size} users from remote response")
            _allUsers.value = response
            
            Log.d(TAG, "Local cache synchronized successfully")
            _loadState.value = LoadResult.Success(Unit)
            response
        } catch (e: Exception) {
            Log.e(TAG, "Fetch failure at /api/v1/usuarios: ${e.message}", e)
            _loadState.value = LoadResult.Error(e.localizedMessage ?: "Error desconocido")
            emptyList()
        }
    }

    /**
     * Fetches a specific user by their unique database identifier.
     * 
     * @param id The primary key of the user to fetch.
     * @return [Usuario] object if found, null otherwise.
     */
    suspend fun getUsuarioPorId(id: Long): Usuario? {
        return try {
            Log.d(TAG, "GET /api/v1/usuarios/$id")
            val response = apiService.getUsuarioPorId(id)
            
            Log.d(TAG, "Retrieved profile for '${response.nombreCompleto}'")
            _selectedUser.value = response
            response
        } catch (e: Exception) {
            Log.e(TAG, "Lookup failure at /api/v1/usuarios/$id", e)
            val cached = _allUsers.value.find { it.id == id }
            if (cached != null) {
                Log.d(TAG, "Falling back to local cache for user ID $id")
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
            
            Log.d(TAG, "Account security metrics retrieved for UID $id")
            _selectedUserAccount.value = cuenta
            cuenta
        } catch (e: Exception) {
            Log.e(TAG, "Security lookup failure at /api/v1/usuarios/$id/cuenta: ${e.message}", e)
            null
        }
    }

    /**
     * Registers a new user within the system.
     * 
     * @param request [UserRequest] with the new user's metadata.
     * @return [Result] wrapping the created [Usuario] or an exception.
     */
    suspend fun registrarUsuario(request: UserRequest): Result<Usuario> {
        return try {
            Log.d(TAG, "POST /api/v1/usuarios")
            val response = apiService.registrarUsuario(request)
            
            if (response.isSuccessful && response.body() != null) {
                Log.d(TAG, "Identity record created successfully")
                getUsuarios()
                Result.success(response.body()!!)
            } else {
                val err = "Server rejected registration (HTTP ${response.code()})"
                Log.e(TAG, err)
                Result.failure(Exception(err))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Resource creation failure at /api/v1/usuarios: ${e.message}", e)
            Result.failure(e)
        }
    }

    /**
     * Updates an existing user record.
     * 
     * @param id The primary key of the user to update.
     * @param request [UserRequest] containing the updated payload.
     * @return [Result] wrapping the updated [Usuario] or an exception.
     */
    suspend fun actualizarUsuario(id: Long, request: UserRequest): Result<Usuario> {
        return try {
            Log.d(TAG, "PUT /api/v1/usuarios/$id")
            val response = apiService.actualizarUsuario(id, request)
            
            if (response.isSuccessful && response.body() != null) {
                Log.d(TAG, "Profile update applied successfully")
                getUsuarios()
                Result.success(response.body()!!)
            } else {
                val err = "Server rejected update (HTTP ${response.code()})"
                Log.e(TAG, err)
                Result.failure(Exception(err))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Update failure at /api/v1/usuarios/$id: ${e.message}", e)
            Result.failure(e)
        }
    }

    /**
     * Toggles the active status of a user account.
     * 
     * @param id The primary key of the target account.
     * @return [Result] wrapping the new [EstadoCuentaResponse].
     */
    suspend fun toggleEstadoUsuario(id: Long): Result<EstadoCuentaResponse> {
        return try {
            Log.d(TAG, "PATCH /api/v1/usuarios/$id/estado")
            val response = apiService.toggleEstadoUsuario(id)
            
            if (response.isSuccessful && response.body() != null) {
                Log.d(TAG, "Account status toggled successfully")
                getUsuarios()
                Result.success(response.body()!!)
            } else {
                val err = "Status transition rejected (HTTP ${response.code()})"
                Log.e(TAG, err)
                Result.failure(Exception(err))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Status transition failure at /api/v1/usuarios/$id/estado: ${e.message}", e)
            Result.failure(e)
        }
    }

    /**
     * Resets the selected user and account security flows to clear the UI state.
     */
    fun clearSelectedUser() {
        Log.d(TAG, "Clearing transient UI states for selected user")
        _selectedUser.value = null
        _selectedUserAccount.value = null
    }
}
