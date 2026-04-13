package mx.edu.utez.jyps.data.repository

import com.google.gson.Gson
import mx.edu.utez.jyps.data.model.PassRequest
import mx.edu.utez.jyps.data.model.PassResponse
import mx.edu.utez.jyps.data.network.ApiService
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
        return try {
            Timber.d("GET /api/v1/pases/empleado?empleadoId=$empleadoId")
            val response = api.getPasesPorEmpleado(empleadoId)
            Result.success(response)
        } catch (e: Exception) {
            Timber.e(e, "Error during GET /api/v1/pases/empleado")
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
        return try {
            Timber.d("GET /api/v1/pases/$id/detalles")
            val response = api.getPaseDetalles(id)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                throw Exception("Failed to fetch pass details: ${response.code()}")
            }
        } catch (e: Exception) {
            Timber.e(e, "Error during GET /api/v1/pases/$id/detalles")
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
        return try {
            Timber.d("POST /api/v1/pases")
            val json = Gson().toJson(request)
            val dataPart = json.toRequestBody("application/json".toMediaTypeOrNull())
            
            val response = api.crearPase(dataPart, null)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                throw Exception("Failed to create pass: ${response.code()}")
            }
        } catch (e: Exception) {
            Timber.e(e, "Error during POST /api/v1/pases")
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
        return try {
            Timber.d("DELETE /api/v1/pases/$id")
            val response = api.eliminarPase(id)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                throw Exception("Delete operation failed: ${response.code()}")
            }
        } catch (e: Exception) {
            Timber.e(e, "Error during DELETE /api/v1/pases/$id")
            Result.failure(e)
        }
    }
}
