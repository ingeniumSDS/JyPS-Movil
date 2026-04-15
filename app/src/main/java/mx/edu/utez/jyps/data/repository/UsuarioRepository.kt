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
import mx.edu.utez.jyps.utils.CrashlyticsHelper

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
        val endpoint = "/api/v1/usuarios"
        _loadState.value = LoadResult.Loading
        return try {
            CrashlyticsHelper.logApiCall("GET", endpoint)
            Log.d(TAG, "GET $endpoint")
            val response = apiService.getUsuarios()
            
            Log.d(TAG, "Mapping ${response.size} users from remote response")
            _allUsers.value = response
            
            CrashlyticsHelper.logApiSuccess("GET", endpoint)
            _loadState.value = LoadResult.Success(Unit)
            response
        } catch (e: Exception) {
            Log.e(TAG, "Fetch failure at $endpoint: ${e.message}", e)
            CrashlyticsHelper.logApiError("GET", endpoint, e)
            _loadState.value = LoadResult.Error(e.localizedMessage ?: "Error desconocido")
            emptyList()
        }
    }

    /**
     * Retrieves all users associated with a specific department.
     * 
     * @param departamentoId The target department identifier.
     * @return [List] of [Usuario] objects belonging to the specified department.
     */
    suspend fun getUsuariosByDepartamento(departamentoId: Long): List<Usuario> {
        val endpoint = "/api/v1/$departamentoId/usuarios"
        _loadState.value = LoadResult.Loading
        return try {
            CrashlyticsHelper.logApiCall("GET", endpoint)
            Log.d(TAG, "GET $endpoint")
            val response = apiService.getUsuariosByDepartamento(departamentoId)
            
            Log.d(TAG, "Synchronizing local state with ${response.size} departmental employees")
            _allUsers.value = response
            CrashlyticsHelper.logApiSuccess("GET", endpoint)
            _loadState.value = LoadResult.Success(Unit)
            response
        } catch (e: Exception) {
            Log.e(TAG, "Departmental fetch failure at $endpoint: ${e.message}", e)
            CrashlyticsHelper.logApiError("GET", endpoint, e)
            _loadState.value = LoadResult.Error(e.localizedMessage ?: "Error de comunicación")
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
        val endpoint = "/api/v1/usuarios"
        return try {
            CrashlyticsHelper.logApiCall("POST", endpoint)
            Log.d(TAG, "POST $endpoint")
            val response = apiService.registrarUsuario(request)
            
            if (response.isSuccessful && response.body() != null) {
                CrashlyticsHelper.logApiSuccess("POST", endpoint, response.code())
                Log.d(TAG, "Identity record created successfully")
                getUsuarios()
                Result.success(response.body()!!)
            } else {
                val err = "Server rejected registration (HTTP ${response.code()})"
                Log.e(TAG, err)
                Result.failure(Exception(err))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Resource creation failure at $endpoint: ${e.message}", e)
            CrashlyticsHelper.logApiError("POST", endpoint, e)
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
        val endpoint = "/api/v1/usuarios/$id"
        return try {
            CrashlyticsHelper.logApiCall("PUT", endpoint)
            Log.d(TAG, "PUT $endpoint")
            val response = apiService.actualizarUsuario(id, request)
            
            if (response.isSuccessful && response.body() != null) {
                CrashlyticsHelper.logApiSuccess("PUT", endpoint, response.code())
                Log.d(TAG, "Profile update applied successfully")
                getUsuarios()
                Result.success(response.body()!!)
            } else {
                val err = "Server rejected update (HTTP ${response.code()})"
                Log.e(TAG, err)
                Result.failure(Exception(err))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Update failure at $endpoint: ${e.message}", e)
            CrashlyticsHelper.logApiError("PUT", endpoint, e)
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
        val endpoint = "/api/v1/usuarios/$id/estado"
        return try {
            CrashlyticsHelper.logApiCall("PATCH", endpoint)
            Log.d(TAG, "PATCH $endpoint")
            val response = apiService.toggleEstadoUsuario(id)
            
            if (response.isSuccessful && response.body() != null) {
                CrashlyticsHelper.logApiSuccess("PATCH", endpoint, response.code())
                Log.d(TAG, "Account status toggled successfully")
                getUsuarios()
                Result.success(response.body()!!)
            } else {
                val err = "Status transition rejected (HTTP ${response.code()})"
                Log.e(TAG, err)
                Result.failure(Exception(err))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Status transition failure at $endpoint: ${e.message}", e)
            CrashlyticsHelper.logApiError("PATCH", endpoint, e)
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
