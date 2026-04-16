package mx.edu.utez.jyps.data.repository

import android.util.Log
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import mx.edu.utez.jyps.data.model.CreateDepartmentRequest
import mx.edu.utez.jyps.data.model.DepartamentoResponse
import mx.edu.utez.jyps.data.model.UpdateDepartmentRequest
import mx.edu.utez.jyps.data.model.ToggleStatusRequest
import mx.edu.utez.jyps.data.model.Usuario
import mx.edu.utez.jyps.data.network.ApiService
import mx.edu.utez.jyps.data.network.NetworkErrorParser
import mx.edu.utez.jyps.utils.CrashlyticsHelper

/**
 * Repository responsible for managing department operations within the institution.
 * Handles CRUD actions, status toggles, and head-of-department assignments via [ApiService].
 * 
 * @property apiService gateway used to perform HTTP operations on department resources.
 */
class DepartmentRepository(private val apiService: ApiService) {
    
    private val TAG = "DepartmentRepo"

    /** Observable flow containing the list of all institutions departments. */
    private val _allDepartments = MutableStateFlow<List<DepartamentoResponse>>(emptyList())
    val allDepartments: StateFlow<List<DepartamentoResponse>> = _allDepartments.asStateFlow()

    private var isFetchingDepts = false

    /**
     * Fetches all registered departments from the backend repository.
     * Updates the local [_allDepartments] state flow upon success.
     * 
     * @return List of retrieved [DepartamentoResponse] objects.
     */
    suspend fun getDepartamentos(): List<DepartamentoResponse> {
        val endpoint = "/api/v1/departamentos"
        if (isFetchingDepts) return _allDepartments.value
        return try {
            isFetchingDepts = true
            CrashlyticsHelper.logApiCall("GET", endpoint)
            Log.d(TAG, "GET $endpoint")
            val response = apiService.getDepartamentos()
            Log.d(TAG, "${response.size} departamentos recibidos")
            
            _allDepartments.value = response
            CrashlyticsHelper.logApiSuccess("GET", endpoint)
            response
        } catch (e: Exception) {
            Log.e(TAG, "Error getDepartamentos: ${e.message}")
            CrashlyticsHelper.logApiError("GET", endpoint, e)
            _allDepartments.value
        } finally {
            isFetchingDepts = false
        }
    }

    /**
     * Retrieves users who are eligible to be assigned as department heads.
     * 
     * @return List of [Usuario] objects with the required roles and no prior head-of-dept assignment.
     */
    suspend fun getJefesDisponibles(): List<Usuario> {
        return try {
            Log.d(TAG, "GET /api/v1/usuarios/jefes")
            val response = apiService.getJefesDisponibles()
            Log.d(TAG, "${response.size} jefes potenciales encontrados")
            response
        } catch (e: retrofit2.HttpException) {
            if (e.code() == 403) {
                Log.w(TAG, "Endpoint /jefes inaccesible (403 Forbidden). Ignorando silenciosamente.")
            } else {
                Log.e(TAG, "Error HTTP en getJefesDisponibles: ${e.code()}")
            }
            emptyList()
        } catch (e: Exception) {
            Log.e(TAG, "Error getJefesDisponibles: ${e.message}")
            emptyList()
        }
    }

    /**
     * Fetches the list of all active users belonging to a specific department.
     * Useful for deactivation checks and linked-user visualization.
     *
     * @param id The unique identifier of the target department.
     * @return List of [Usuario] objects linked to the department.
     */
    suspend fun getUsuariosByDepartamento(id: Long): List<Usuario> {
        val endpoint = "/api/v1/$id/usuarios"
        return try {
            CrashlyticsHelper.logApiCall("GET", endpoint)
            Log.d(TAG, "GET $endpoint")
            val response = apiService.getUsuariosByDepartamento(id)
            CrashlyticsHelper.logApiSuccess("GET", endpoint)
            Log.d(TAG, "${response.size} usuarios recuperados para depto $id")
            response
        } catch (e: Exception) {
            Log.e(TAG, "Error getUsuariosByDepartamento ($id): ${e.message}", e)
            CrashlyticsHelper.logApiError("GET", endpoint, e)
            emptyList()
        }
    }

    /**
     * Directly assigns a user as the head of a specific department via PATCH.
     *
     * @param id The unique ID of the target department.
     * @param jefeId The unique ID of the user to be appointed as head.
     * @return [Result] containing the updated [DepartamentoResponse] or an error.
     */
    suspend fun asignarJefe(id: Long, jefeId: Long): Result<DepartamentoResponse> {
        val endpoint = "/api/v1/departamentos/$id/asignar-jefe"
        return try {
            CrashlyticsHelper.logApiCall("PATCH", endpoint)
            CrashlyticsHelper.logAction("DepartmentRepo", "assign_head",
                mapOf("deptId" to id.toString(), "jefeId" to jefeId.toString()))
            Log.d(TAG, "PATCH $endpoint?jefeId=$jefeId")
            val response = apiService.asignarJefe(id, jefeId)
            if (response.isSuccessful && response.body() != null) {
                CrashlyticsHelper.logApiSuccess("PATCH", endpoint)
                Log.d(TAG, "Jefe asignado correctamente en el servidor")
                Result.success(response.body()!!)
            } else {
                val error = NetworkErrorParser.parseError(response.errorBody())
                Log.w(TAG, "Server rejection: $error")
                Result.failure(Exception(error))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Excepción en asignarJefe: ${e.message}", e)
            CrashlyticsHelper.logApiError("PATCH", endpoint, e)
            Result.failure(e)
        }
    }

    /**
     * Toggles the active status of a department.
     * Note: Backend validation prevents deactivating departments with active users.
     *
     * @param id Unique ID of the department to toggle.
     * @return [Result] encapsulating the server response.
     */
    suspend fun toggleEstado(id: Long): Result<DepartamentoResponse> {
        return try {
            Log.d(TAG, "PATCH /api/v1/departamentos/estado (ID: $id)")
            val response = apiService.toggleEstado(ToggleStatusRequest(id))
            if (response.isSuccessful && response.body() != null) {
                Log.d(TAG, "El servidor confirmó el cambio de estado")
                Result.success(response.body()!!)
            } else {
                val error = "Error de servidor (${response.code()})"
                Log.w(TAG, error)
                Result.failure(Exception(error))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Excepción en toggleEstado: ${e.message}", e)
            Result.failure(e)
        }
    }

    /**
     * Registers a new department in the institution's database.
     *
     * @param request Data transfer object containing department details.
     * @return [Result] containing the created [DepartamentoResponse] or an error.
     */
    suspend fun crearDepartamento(request: CreateDepartmentRequest): Result<DepartamentoResponse> {
        return try {
            Log.d(TAG, "POST /api/v1/departamentos - Nombre: ${request.nombre}")
            val response = apiService.crearDepartamento(request)
            if (response.isSuccessful && response.body() != null) {
                Log.d(TAG, "Departamento '${request.nombre}' creado exitosamente")
                Result.success(response.body()!!)
            } else {
                val error = NetworkErrorParser.parseError(response.errorBody())
                Log.w(TAG, "Creación fallida: $error")
                Result.failure(Exception(error))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Excepción al crear departamento: ${e.message}", e)
            Result.failure(e)
        }
    }

    /**
     * Updates an existing department record.
     * Ensures all fields, including 'activo', are persisted correctly.
     *
     * @param request Object containing the updated department information.
     * @return [Result] containing the updated record or an error.
     */
    suspend fun actualizarDepartamento(request: UpdateDepartmentRequest): Result<DepartamentoResponse> {
        return try {
            Log.d(TAG, "PUT /api/v1/departamentos - ID: ${request.id}, Nombre: ${request.nombre}")
            val response = apiService.actualizarDepartamento(request)
            if (response.isSuccessful && response.body() != null) {
                Log.d(TAG, "Actualización procesada exitosamente")
                Result.success(response.body()!!)
            } else {
                val error = NetworkErrorParser.parseError(response.errorBody())
                Log.w(TAG, "Actualización fallida: $error")
                Result.failure(Exception(error))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Excepción al actualizar departamento: ${e.message}", e)
            Result.failure(e)
        }
    }
}
