package mx.edu.utez.jyps.data.repository

import com.google.gson.Gson
import mx.edu.utez.jyps.data.model.PassRequest
import mx.edu.utez.jyps.data.model.PassResponse
import mx.edu.utez.jyps.data.network.ApiService
import mx.edu.utez.jyps.utils.CrashlyticsHelper
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import timber.log.Timber

/**
 * Repository in charge of managing all Exit Pass operations.
 * Communicates with [ApiService] and handles error transparency via Result pattern.
 *
 * @property api The Retrofit service interface defining endpoints.
 */
class PassRepository(private val api: ApiService) {

    /**
     * Fetches all passes for a given employee from the backend.
     *
     * @param empleadoId Unique identifier of the user to filter.
     * @return [Result] wrapping the list of [PassResponse] objects.
     */
    suspend fun getPasesPorEmpleado(empleadoId: Long): Result<List<PassResponse>> {
        val endpoint = "/api/v1/pases/empleado"
        return try {
            CrashlyticsHelper.logApiCall("GET", endpoint)
            Timber.d("GET $endpoint?empleadoId=$empleadoId")
            val response = api.getPasesPorEmpleado(empleadoId)
            CrashlyticsHelper.logApiSuccess("GET", endpoint)
            Result.success(response)
        } catch (e: Exception) {
            Timber.e(e, "Error during GET $endpoint")
            CrashlyticsHelper.logApiError("GET", endpoint, e)
            Result.failure(e)
        }
    }

    /**
     * Fetches all passes associated with a specific manager from the backend.
     *
     * @param jefeId Unique identifier of the manager.
     * @return [Result] wrapping the list of [PassResponse] objects.
     */
    suspend fun getPasesPorJefe(jefeId: Long): Result<List<PassResponse>> {
        val endpoint = "/api/v1/pases/jefe"
        return try {
            CrashlyticsHelper.logApiCall("GET", endpoint)
            Timber.d("GET $endpoint?jefeId=$jefeId")
            val response = api.getPasesPorJefe(jefeId)
            CrashlyticsHelper.logApiSuccess("GET", endpoint)
            Result.success(response)
        } catch (e: Exception) {
            Timber.e(e, "Error during GET $endpoint")
            CrashlyticsHelper.logApiError("GET", endpoint, e)
            Result.failure(e)
        }
    }

    /**
     * Allows a manager to review a pending exit pass.
     *
     * @param paseDeSalidaId ID of the pass.
     * @param estado The new status (e.g., APROBADO, RECHAZADO).
     * @param comentario Manager's observation.
     * @return [Result] wrapping the updated [PassResponse].
     */
    suspend fun revisarPase(paseDeSalidaId: Long, estado: String, comentario: String?): Result<PassResponse> {
        val endpoint = "/api/v1/pases/revisar"
        return try {
            CrashlyticsHelper.logApiCall("PUT", endpoint)
            CrashlyticsHelper.logAction("PassRepository", "review_pass",
                mapOf("paseId" to paseDeSalidaId.toString(), "estado" to estado))
            Timber.d("PUT $endpoint")
            val request = mx.edu.utez.jyps.data.model.ReviewPassRequest(
                paseDeSalidaId = paseDeSalidaId,
                estado = estado,
                comentario = comentario
            )
            val response = api.revisarPase(request)
            if (response.isSuccessful && response.body() != null) {
                CrashlyticsHelper.logApiSuccess("PUT", endpoint, response.code())
                Result.success(response.body()!!)
            } else {
                throw Exception("Failed to review pass: ${response.code()}")
            }
        } catch (e: Exception) {
            Timber.e(e, "Error during PUT $endpoint")
            CrashlyticsHelper.logApiError("PUT", endpoint, e)
            Result.failure(e)
        }
    }

    /**
     * Retrieves full granular details for a specific exit pass.
     * 
     * @param id The primary key identifier of the pass.
     * @return [Result] wrapping the [PassResponse] data.
     */
    suspend fun getPaseDetalles(id: Long): Result<PassResponse> {
        val endpoint = "/api/v1/pases/$id/detalles"
        return try {
            CrashlyticsHelper.logApiCall("GET", endpoint)
            Timber.d("GET $endpoint")
            val response = api.getPaseDetalles(id)
            if (response.isSuccessful && response.body() != null) {
                CrashlyticsHelper.logApiSuccess("GET", endpoint, response.code())
                Result.success(response.body()!!)
            } else {
                throw Exception("Failed to fetch pass details: ${response.code()}")
            }
        } catch (e: Exception) {
            Timber.e(e, "Error during GET $endpoint")
            CrashlyticsHelper.logApiError("GET", endpoint, e)
            Result.failure(e)
        }
    }

    /**
     * Sends a new exit pass request to the server using Multipart encoding.
     *
     * @param request Data containing the pass specifics.
     * @return [Result] wrapping the created [PassResponse].
     */
    suspend fun crearPase(request: PassRequest): Result<PassResponse> {
        val endpoint = "/api/v1/pases"
        return try {
            CrashlyticsHelper.logApiCall("POST", endpoint)
            CrashlyticsHelper.logAction("PassRepository", "create_pass",
                mapOf("jefeId" to (request.jefeId?.toString() ?: "null"),
                      "empleadoId" to (request.empleadoId?.toString() ?: "null")))
            Timber.d("POST $endpoint")
            val json = Gson().toJson(request)
            val dataPart = json.toRequestBody("application/json".toMediaTypeOrNull())
            
            val response = api.crearPase(dataPart, null)
            if (response.isSuccessful && response.body() != null) {
                CrashlyticsHelper.logApiSuccess("POST", endpoint, response.code())
                Result.success(response.body()!!)
            } else {
                throw Exception("Failed to create pass: ${response.code()}")
            }
        } catch (e: Exception) {
            Timber.e(e, "Error during POST $endpoint")
            CrashlyticsHelper.logApiError("POST", endpoint, e)
            Result.failure(e)
        }
    }

    /**
     * Removes a pending pass from the database.
     *
     * @param id Unique identifier of the pass to delete.
     * @return [Result] indicating success or failure of the deletion.
     */
    suspend fun eliminarPase(id: Long): Result<Unit> {
        val endpoint = "/api/v1/pases/$id"
        return try {
            CrashlyticsHelper.logApiCall("DELETE", endpoint)
            Timber.d("DELETE $endpoint")
            val response = api.eliminarPase(id)
            if (response.isSuccessful) {
                CrashlyticsHelper.logApiSuccess("DELETE", endpoint, response.code())
                Result.success(Unit)
            } else {
                throw Exception("Delete operation failed: ${response.code()}")
            }
        } catch (e: Exception) {
            Timber.e(e, "Error during DELETE $endpoint")
            CrashlyticsHelper.logApiError("DELETE", endpoint, e)
            Result.failure(e)
        }
    }
}
