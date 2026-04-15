package mx.edu.utez.jyps.data.repository

import android.util.Log
import mx.edu.utez.jyps.data.model.PassResponse
import mx.edu.utez.jyps.data.network.ApiService
import timber.log.Timber

/**
 * Handles all security scanner related data operations.
 * Communicates with the backend to officialize pass check-ins/check-outs.
 *
 * @property api The Retrofit-based network interface.
 */
class ScannerRepository(private val api: ApiService) {

    /**
     * Officializes the exit or return of a personnel pass by its QR code.
     *
     * @param qr The unique 6-character alphanumeric code extracted from the scanned pass.
     * @return [Result] wrapping the updated [PassResponse] on success, or an [Exception] on failure.
     */
    suspend fun processPassCheckout(qr: String): Result<PassResponse> {
        return try {
            Timber.d("PATCH /api/v1/pases/$qr")
            val response = api.processPassCheckout(qr)
            if (response.isSuccessful && response.body() != null) {
                val body = response.body()!!
                Timber.d("Mapeando respuesta del pase $qr con estado ${body.estado}")
                Timber.d("Sincronización con el servidor completada con éxito")
                Result.success(body)
            } else {
                val errorBody = response.errorBody()?.string() ?: "Error desconocido"
                Timber.e("Fallo al procesar /api/v1/pases/$qr: ${response.code()} - $errorBody")
                // Extract human-readable message from backend JSON error body
                val friendlyMessage = try {
                    val json = org.json.JSONObject(errorBody)
                    val mensaje = json.optString("mensaje", "")
                    when {
                        mensaje.contains("horaSalidaReal") ->
                            "Este pase aún no ha registrado su salida. El empleado debe escanear primero al salir."
                        mensaje.isNotBlank() -> mensaje
                        else -> "Error del servidor (${response.code()})"
                    }
                } catch (e: Exception) {
                    "Error al procesar la respuesta del servidor"
                }
                Result.failure(Exception(friendlyMessage))
            }
        } catch (e: Exception) {
            Timber.e("Error al intentar PATCH /api/v1/pases/$qr: ${e.message}", e)
            Result.failure(Exception("Error de conexión: Verifica tu internet"))
        }
    }
}
