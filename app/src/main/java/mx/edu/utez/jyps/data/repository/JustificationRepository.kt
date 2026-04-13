package mx.edu.utez.jyps.data.repository

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import mx.edu.utez.jyps.data.model.JustificationResponse
import mx.edu.utez.jyps.data.network.ApiService
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.ResponseBody
import retrofit2.Response
import timber.log.Timber
import java.io.InputStream

/**
 * Repository orchestrating the data access for user justifications.
 * Handles both the remote API calls and the transformation of media types for multipart requests.
 * 
 * Follows a manual instantiation pattern, injecting dependencies through the constructor
 * to align with the project's architecture without third-party DI frameworks.
 *
 * @property api The [ApiService] instance for network operations.
 * @property context Application context required for resolve [Uri] inputs and content stream access.
 */
class JustificationRepository(
    private val api: ApiService,
    private val context: Context
) {

    /**
     * Retrieves the complete list of justifications for a specific employee.
     * 
     * @param empleadoId The unique database identifier of the employee.
     * @return [Result] wrapping the [List] of [JustificationResponse] on success.
     */
    suspend fun getJustificantesPorEmpleado(empleadoId: Long): Result<List<JustificationResponse>> {
        return try {
            Timber.d("Iniciando petición GET /api/v1/justificantes/empleado para el ID: $empleadoId")
            val response = api.getJustificantesPorEmpleado(empleadoId)
            Result.success(response)
        } catch (e: Exception) {
            Timber.e(e, "Fallo al recuperar justificantes reales palpa el empleado $empleadoId")
            Result.failure(e)
        }
    }

    /**
     * Retrieves the structural details of a single justification request.
     *
     * @param id The justification unique ID.
     * @return [Result] containing the [JustificationResponse] if successful.
     */
    suspend fun getJustificanteDetalles(id: Long): Result<JustificationResponse> {
        return try {
            Timber.d("Petición GET /api/v1/justificantes/$id/detalles enviada")
            val response = api.getJustificanteDetalles(id)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Hubo un problema al obtener los detalles del servidor."))
            }
        } catch (e: Exception) {
            Timber.e(e, "Error crítico durante la recuperación de detalles del justificante $id")
            Result.failure(e)
        }
    }

    /**
     * Downloads an attachment associated with a justification.
     * 
     * @param empleadoId Owner of the file.
     * @param fileName Name of the file to download.
     * @return [Result] wrapping the [ResponseBody] containing raw file bytes.
     */
    suspend fun downloadJustificanteFile(empleadoId: Long, fileName: String): Result<ResponseBody> {
        return try {
            Timber.d("GET /api/v1/justificantes/$empleadoId/$fileName")
            val response = api.descargarArchivoJustificante(empleadoId, fileName)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Error al descargar archivo: ${response.code()}"))
            }
        } catch (e: Exception) {
            Timber.e(e, "Fallo al descargar el archivo $fileName para el empleado $empleadoId")
            Result.failure(e)
        }
    }

    /**
     * Submits a new justification request including optional data and file attachments.
     * Manages the conversion of primitive types and [Uri] into [RequestBody] and [MultipartBody.Part].
     * 
     * @param empleadoId Identifer of the requester.
     * @param fechaSolicitada Target date to justify.
     * @param descripcion Explanatory text for the incident.
     * @param fileUris Optional list of Android [Uri] pointing to evidence files.
     * @return [Result] wrapping the created [JustificationResponse].
     */
    suspend fun crearJustificante(
        empleadoId: Long,
        jefeId: Long,
        fechaSolicitada: String,
        descripcion: String,
        fileUris: List<Uri>
    ): Result<JustificationResponse> {
        return try {
            Timber.d("POST /api/v1/justificantes")
            
            // 1. Convert metadata fields to a single JSON RequestBody (part name "data")
            val json = """
                {
                    "empleadoId": $empleadoId,
                    "jefeId": $jefeId,
                    "fechaSolicitada": "$fechaSolicitada",
                    "descripcion": "$descripcion"
                }
            """.trimIndent()
            
            val dataBody = json.toRequestBody("application/json".toMediaTypeOrNull())

            // 2. Convert Uri list to MultipartBody.Part list
            val fileParts = fileUris.mapNotNull { uri ->
                prepareFilePart(uri)
            }

            // 3. Execute the network request
            val response = api.crearJustificante(
                data = dataBody,
                archivos = fileParts.takeIf { it.isNotEmpty() }
            )

            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                val errorRaw = response.errorBody()?.string() ?: "Error de servidor desconocido"
                Timber.e("FALLÓ la creación del justificante: $errorRaw")
                
                // Try to extract "mensaje" from JSON error body
                val friendlyMessage = try {
                    val jsonObj = org.json.JSONObject(errorRaw)
                    jsonObj.optString("mensaje", "Ocurrió un error en el servidor")
                } catch (e: Exception) {
                    "Error al procesar la solicitud ($errorRaw)"
                }
                
                Result.failure(Exception(friendlyMessage))
            }
        } catch (e: Exception) {
            Timber.e(e, "Excepción durante la creación del justificante")
            Result.failure(e)
        }
    }

    /**
     * Resolves a [Uri] into a [MultipartBody.Part] for backend transmission.
     * Extracts MIME types and original filenames from [OpenableColumns].
     * 
     * @param uri The source file pointer.
     * @return [MultipartBody.Part] or null if the file cannot be accessed or read.
     */
    private fun prepareFilePart(uri: Uri): MultipartBody.Part? {
        return try {
            val contentResolver = context.contentResolver
            val mimeType = contentResolver.getType(uri) ?: "application/octet-stream"
            
            // Extract original filename
            var fileName = "attachment"
            contentResolver.query(uri, null, null, null, null)?.use { cursor ->
                val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                if (nameIndex != -1 && cursor.moveToFirst()) {
                    fileName = cursor.getString(nameIndex)
                }
            }

            val inputStream: InputStream? = contentResolver.openInputStream(uri)
            val bytes = inputStream?.readBytes()
            inputStream?.close()

            if (bytes != null) {
                val requestFile = bytes.toRequestBody(mimeType.toMediaTypeOrNull())
                // 'archivos' matches the parameter name expected by the @Part annotation in ApiService
                MultipartBody.Part.createFormData("archivos", fileName, requestFile)
            } else {
                null
            }
        } catch (e: Exception) {
            Timber.e(e, "Fallo al preparar la parte del archivo para el URI: $uri")
            null
        }
    }
}
