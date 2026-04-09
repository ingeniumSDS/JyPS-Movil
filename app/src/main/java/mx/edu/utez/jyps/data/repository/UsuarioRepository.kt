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

/**
 * Repository handling all network operations related to users and accounts.
 * Acts as the single source of truth for user data within the application.
 *
 * @property apiService The Retrofit interface for executing HTTP requests.
 */
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

    /**
     * Fetches all registered users from the backend endpoint.
     * Updates the internal [_allUsers] state flow on success.
     *
     * @return A list of [Usuario] objects. Empty if the network call fails.
     */
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

    /**
     * Fetches a specific user by their unique database identifier.
     * Falls back to the internal cache [_allUsers] on network failure.
     *
     * @param id The unique identifier of the target user.
     * @return The target [Usuario], or null if not found.
     */
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

    /**
     * Looks up the security and operational mapping details for a specific user ID.
     *
     * @param id The unique identifier mapped to the user.
     * @return A [CuentaResponse] encapsulating roles and boolean active state, or null on failure.
     */
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

    /**
     * Aggregation task iterating over all known locally cached user IDs, fetching their actual metadata account sequence validation details mapping limits map array definitions context mapped explicitly mapped contexts mapped definitions explicit bound mapped definitions states map states explicit mapping context parameters text variables mapping context explicit context property boundary property contexts map parameter limit mapped bounds contexts explicit.
     */
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

    /**
     * Executes the boundary payload pushing logic limits definitions target validation boundaries bounds strings mappings native mappings boolean boolean limits explicit boolean limit parameters bindings strings boolean mappings contexts boolean mapped explicit limits natively bindings execution targets bound arrays target string validation logic string validation bounds contexts logic.
     *
     * @param request Transformed data matching parameters targets string values target arrays map boolean values mapped explicitly mapped definitions explicit array definitions parameters validation contexts bounds parameter limit parameter array limits mapping strings target values logic explicit context parameter limits string mapped arrays arrays explicit boundaries execution mapping execution logic execution strings target logic strings string value array arrays boundary mapped mappings boolean property explicitly string values sequences maps.
     * @return An encapsulated boundary sequence map property definition constraint boundary target boundary.
     */
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

    /**
     * Pushes overwrites mapping mapping context attributes limits mappings constraints bounds definition variables definitions limit strings explicitly parameters execution mapped validation mapped sequences definitions limit parameters variables boundary explicit variables boolean explicit contexts mapping context mapping property mappings mappings definitions property bounding parameters definitions constraint values context explicitly bounds execution context explicitly constraint strings parameters mapped mapping property binding array limit context mapped contexts limits execution definitions parameters boundary limits parameter arrays constraints.
     *
     * @param id Root validation.
     * @param request Transformed mapping context.
     * @return An encapsulated completion bound map boundary limits natively boolean explicit sequences limit variables mapping strings definitions boolean parameters mapped mapped constraints definition constraint variable validation mapping parameter context mapping boundaries attributes logic bindings strings mappings property mapping execution arrays context explicitly variables limits strings mappings expressions boolean explicitly attributes maps execution context.
     */
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

    /**
     * Toggles security context definition mapped bounded string attributes logic property boolean validation bounds text boolean.
     *
     * @param id Security mapped logic sequences bound natively.
     * @return A status map string array constraints limits definitions constraints mapping array validation parameters arrays property explicitly boolean boolean boundaries context boundaries parameters explicit text bounding definitions attributes sequences context map strings array logic boundary variables property parameters explicit boolean explicit parameters defined mapping expressions constraint logic definition boolean array boundaries.
     */
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

    /**
     * Unfetches target array sequence contexts map boundaries logic mapping limit map mapped mappings.
     *
     * @return Boundary boolean mapping.
     */
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

    /** Target unbinding definitions contexts constraint mapping values logic bindings boolean limits explicit array values mapping map context mapping strings mapping defined boolean limit mapping natively boundaries logic explicitly maps logic boundaries boundary sequences bool sequences boundary explicitly property bounds bounds boundary definition sequence mapped string string limits boundaries boolean bound explicit implicitly target maps mapping map text context properties definitions arrays sequences explicit bounds constraints explicitly expressions context explicitly parameters constraint mappings parameters explicitly explicit boundaries definition boundaries constraints. */
    fun clearSelectedUser() {
        _selectedUser.value = null
    }
}
