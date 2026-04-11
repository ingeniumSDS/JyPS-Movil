package mx.edu.utez.jyps.data.repository

import android.util.Log
import mx.edu.utez.jyps.data.model.CuentaResponse
import mx.edu.utez.jyps.data.model.GenericMessageResponse
import mx.edu.utez.jyps.data.model.LoginRequest
import mx.edu.utez.jyps.data.model.LoginResponse
import mx.edu.utez.jyps.data.model.PasswordSetupRequest
import mx.edu.utez.jyps.data.model.PasswordTokenRequest
import mx.edu.utez.jyps.data.network.ApiService
import org.json.JSONObject
import android.util.Base64
import org.json.JSONArray
import java.nio.charset.Charset

/**
 * Orchestrates authentication strategies between the remote API surface and local secured storage.
 * 
 * Centralizes the authentication flow and security lifecycle management, ensuring 
 * cryptographic materials and user roles are handled strictly in isolation from UI states.
 */
class AuthRepository(
    private val api: ApiService,
    private val preferencesManager: PreferencesManager
) {
    /** Reactive stream exposing the current token validity. */
    val tokenFlow = preferencesManager.tokenFlow
    
    /** Reactive stream exposing the current user roles. */
    val rolesFlow = preferencesManager.rolesFlow

    /**
     * Attempts a credential exchange against the backend service.
     * 
     * @param correo Institutional email.
     * @param pass Plaintext password.
     * @return Result containing [LoginResponse] or a detailed error message.
     */
    suspend fun login(correo: String, pass: String): Result<LoginResponse> {
        Log.d("AuthRepo", "POST /api/v1/usuarios/login - Attempting auth for: $correo")

        // FALLBACK MOCKS: Maintained for development continuity as requested
        if (correo == "maria.gonzalez@utez.edu.mx") {
            val mock = LoginResponse(999, "María González", correo, "N/A", listOf("GUARDIA"), null, null, "MOCK_GUARD_TOKEN")
            preferencesManager.saveSession(mock.tokenJwt, mock.roles ?: listOf("GUARDIA"), "María González", correo, "777-111-2233")
            return Result.success(mock)
        }
        if (correo == "juan.perez@utez.edu.mx") {
            val mock = LoginResponse(100, "Juan Pérez", correo, "777123", listOf("EMPLEADO"), 1, "Sistemas", "MOCK_EMP_TOKEN")
            preferencesManager.saveSession(mock.tokenJwt, mock.roles ?: listOf("EMPLEADO"), "Juan Pérez", correo, "7771234567")
            return Result.success(mock)
        }
        if (correo == "root@jyps.com") {
            val mock = LoginResponse(1, "Administrador Root", correo, "0000", listOf("ADMINISTRADOR"), null, null, "MOCK_ADMIN_TOKEN")
            preferencesManager.saveSession(mock.tokenJwt, mock.roles ?: listOf("ADMINISTRADOR"), "Administrador Root", correo, "000-000-0000")
            return Result.success(mock)
        }

        return try {
            val response = api.login(LoginRequest(correo, pass))
            if (response.isSuccessful && response.body() != null) {
                var data = response.body()!!
                
                // ESTRATEGIA RESILIENTE (Estilo Web): Extraer base del JWT primero
                val decoded = decodeJwtPayload(data.tokenJwt)
                
                // Mapeo inteligente con fallbacks
                val finalName = data.nombreCompleto ?: decoded.optString("nombre", decoded.optString("name", "Usuario"))
                val finalEmail = data.correo ?: decoded.optString("sub", correo)
                val finalPhone = data.telefono?.takeIf { it.isNotBlank() } ?: decoded.optString("telefono", decoded.optString("phone", "No disponible"))
                val finalRoles = data.roles ?: decoded.optJSONArray("authorities")?.let { arr ->
                    List(arr.length()) { i -> arr.getString(i) }
                } ?: decoded.optJSONArray("roles")?.let { arr ->
                    List(arr.length()) { i -> arr.getString(i) }
                } ?: listOf("USUARIO")

                Log.d("AuthRepo", "SUCCESS /login - Syncing resilient profile: $finalName")
                
                // Persistencia atómica
                preferencesManager.saveSession(
                    token = data.tokenJwt,
                    roles = finalRoles,
                    name = finalName,
                    email = finalEmail,
                    phone = finalPhone
                )
                
                Result.success(data)
            } else {
                val errorMsg = parseError(response.errorBody()?.string(), response.code())
                Log.d("AuthRepo", "FAILURE /login - Code: ${response.code()} Body: $errorMsg")
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Log.d("AuthRepo", "ERROR /login - Connection failed: ${e.message}")
            Result.failure(Exception("Error de conexión: Verifica tu internet"))
        }
    }

    /**
     * Extrae de forma segura el payload informativo de un JWT sin verificar la firma.
     * Utilizado exclusivamente para sincronización de UI (Nombre, Roles).
     */
    private fun decodeJwtPayload(token: String): JSONObject {
        return try {
            val parts = token.split(".")
            if (parts.size < 2) return JSONObject()
            
            val payloadBase64 = parts[1]
            val decodedBytes = Base64.decode(payloadBase64, Base64.URL_SAFE)
            val decodedString = String(decodedBytes, Charset.forName("UTF-8"))
            JSONObject(decodedString)
        } catch (e: Exception) {
            Log.e("AuthRepo", "Security Exception during JWT decoding: ${e.message}")
            JSONObject()
        }
    }

    /**
     * Requests a one-time password recovery token via email.
     */
    suspend fun requestPasswordToken(correo: String): Result<GenericMessageResponse> {
        Log.d("AuthRepo", "POST /api/v1/usuarios/token - Requesting token for: $correo")
        return try {
            val response = api.generarRecuperacionToken(PasswordTokenRequest(correo))
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                val error = parseError(response.errorBody()?.string(), response.code())
                Result.failure(Exception(error))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Error al solicitar token: ${e.message}"))
        }
    }

    /**
     * Validates if a provided setup/recovery token is still active and valid.
     */
    suspend fun verifySetupToken(token: String): Result<String> {
        Log.d("AuthRepo", "GET /api/v1/usuarios/setup/validar - Validating token: $token")
        return try {
            val response = api.validarSetupToken(token)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.string())
            } else {
                Result.failure(Exception("Token inválido o expirado"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Error de red al validar token"))
        }
    }

    /**
     * Finalizes the password setup or reset flow.
     */
    suspend fun setupPassword(request: PasswordSetupRequest): Result<CuentaResponse> {
        Log.d("AuthRepo", "POST /api/v1/usuarios/setup - Completing password setup")
        return try {
            val response = api.establecerPassword(request)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                val error = parseError(response.errorBody()?.string(), response.code())
                Result.failure(Exception(error))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Error al establecer contraseña: ${e.message}"))
        }
    }

    /**
     * Executes an explicit revocation of the local cryptographic session.
     */
    suspend fun logout() {
        Log.d("AuthRepo", "LOGOUT - Purging DataStore session")
        preferencesManager.clearSession()
    }

    /**
     * Helper to extract human-readable error messages from raw JSON response bodies.
     * 
     * Uses HTTP status codes as fallbacks to provide accurate context even when 
     * the backend suppresses detailed exception messages in production.
     */
    private fun parseError(json: String?, code: Int): String {
        val serverMsg = try {
            if (json.isNullOrBlank()) null
            else {
                val obj = JSONObject(json)
                val msg = obj.optString("message", "")
                if (msg.isNotEmpty()) msg else obj.optString("mensaje", null)
            }
        } catch (e: Exception) {
            null
        }

        return serverMsg ?: when (code) {
            401, 403 -> "Credenciales incorrectas"
            423 -> "La cuenta está bloqueada temporalmente"
            500 -> "Error interno del servidor. Inténtalo más tarde."
            else -> "Error del servidor HTTP $code"
        }
    }
}
