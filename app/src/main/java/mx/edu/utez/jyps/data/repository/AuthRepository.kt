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
     * @param correo The institutional email address used as the unique identifier.
     * @param pass The plaintext password to be validated against the security provider.
     * @return [Result] containing the [LoginResponse] on success or an [Exception] on failure.
     */
    suspend fun login(correo: String, pass: String): Result<LoginResponse> {
        Log.d("AuthRepo", "POST /api/v1/usuarios/login")

        /* Work In Progress - Future Features - No Delete */
        // FALLBACK MOCKS: Maintained for development continuity as requested
        if (correo == "maria.gonzalez@utez.edu.mx") {
            val mock = LoginResponse(999, "María González Hernández", correo, "N/A", listOf("GUARDIA"), null, null, "MOCK_GUARD_TOKEN")
            preferencesManager.saveSession(
                token = mock.tokenJwt,
                roles = mock.roles ?: listOf("GUARDIA"),
                name = "María",
                email = correo,
                phone = "777-111-2233",
                paternal = "González",
                maternal = "Hernández",
                userId = 999
            )
            return Result.success(mock)
        }
        if (correo == "juan.perez@utez.edu.mx") {
            val mock = LoginResponse(100, "Juan Pérez", correo, "777123", listOf("EMPLEADO"), 1, "Sistemas", "MOCK_EMP_TOKEN")
            preferencesManager.saveSession(
                token = mock.tokenJwt,
                roles = mock.roles ?: listOf("EMPLEADO"),
                name = "Juan",
                email = correo,
                phone = "7771234567",
                paternal = "Pérez",
                userId = 100,
                deptName = "Sistemas",
                deptId = 1
            )
            return Result.success(mock)
        }
        if (correo == "root@jyps.com") {
            val mock = LoginResponse(1, "Administrador Root", correo, "0000", listOf("ADMINISTRADOR"), null, null, "MOCK_ADMIN_TOKEN")
            preferencesManager.saveSession(mock.tokenJwt, mock.roles ?: listOf("ADMINISTRADOR"), "Administrador Root", correo, "000-000-0000", userId = 1L)
            return Result.success(mock)
        }

        return try {
            val response = api.login(LoginRequest(correo, pass))
            if (response.isSuccessful && response.body() != null) {
                var data = response.body()!!
                
                Log.d("AuthRepo", "Decoding cryptographic session token")
                val decoded = decodeJwtPayload(data.tokenJwt)
                
                Log.d("AuthRepo", "Resolving profile attributes from identity provider")
                val finalName = decoded.optString("nombre", data.nombreCompleto ?: "Usuario")
                val finalPaternal = decoded.optString("apellidoPaterno", "")
                val finalMaternal = decoded.optString("apellidoMaterno", "")
                val finalUserId = if (decoded.has("id")) decoded.getLong("id") else (data.id ?: 0L)
                val finalDeptName = decoded.optString("nombreDepartamento", data.nombreDepartamento ?: "")
                val finalDeptId = if (decoded.has("departamentoId")) decoded.getLong("departamentoId") else (data.departamentoId ?: 0L)
                
                Log.d("AuthRepo", "Resolved Identity: ID=$finalUserId, DeptID=$finalDeptId, DeptName='$finalDeptName'")
                val finalEmail = data.correo ?: decoded.optString("sub", correo)
                val finalPhone = data.telefono?.takeIf { it.isNotBlank() } ?: decoded.optString("telefono", decoded.optString("phone", "No disponible"))
                val finalRoles = data.roles ?: decoded.optJSONArray("authorities")?.let { arr ->
                    List(arr.length()) { i -> arr.getString(i) }
                } ?: decoded.optJSONArray("roles")?.let { arr ->
                    List(arr.length()) { i -> arr.getString(i) }
                } ?: listOf("USUARIO")

                Log.d("AuthRepo", "Synchronizing resilient profile for '$finalName $finalPaternal $finalMaternal'")
                
                preferencesManager.saveSession(
                    token = data.tokenJwt,
                    roles = finalRoles,
                    name = finalName,
                    email = finalEmail,
                    phone = finalPhone,
                    paternal = finalPaternal,
                    maternal = finalMaternal,
                    userId = finalUserId,
                    deptName = finalDeptName,
                    deptId = finalDeptId
                )
                
                Result.success(data)
            } else {
                val errorMsg = parseError(response.errorBody()?.string(), response.code())
                Log.e("AuthRepo", "Auth REJECTED at /api/v1/usuarios/login - Status: ${response.code()}")
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Log.e("AuthRepo", "Resource UNREACHABLE at /api/v1/usuarios/login: ${e.message}")
            Result.failure(Exception("Error de conexión: Verifica tu internet"))
        }
    }

    /**
     * Extracts safely the informational payload from a JWT without signature verification.
     * 
     * @param token The raw JWT string to decode.
     * @return [JSONObject] containing the decrypted claims.
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
            Log.e("AuthRepo", "Security Violation: JWT payload is malformed")
            JSONObject()
        }
    }

    /**
     * Requests a one-time password recovery token via email.
     * 
     * @param correo Target institutional email for recovery instructions.
     * @return [Result] wrapping the [GenericMessageResponse] from the provider.
     */
    suspend fun requestPasswordToken(correo: String): Result<GenericMessageResponse> {
        return try {
            Log.d("AuthRepo", "POST /api/v1/usuarios/token")
            val response = api.generarRecuperacionToken(PasswordTokenRequest(correo))
            if (response.isSuccessful && response.body() != null) {
                Log.d("AuthRepo", "Token generation triggered successfully")
                Result.success(response.body()!!)
            } else {
                val error = parseError(response.errorBody()?.string(), response.code())
                Log.e("AuthRepo", "Token generation FAILED at /api/v1/usuarios/token")
                Result.failure(Exception(error))
            }
        } catch (e: Exception) {
            Log.e("AuthRepo", "Resource UNREACHABLE at /api/v1/usuarios/token: ${e.message}")
            Result.failure(Exception("Error al solicitar token: ${e.message}"))
        }
    }

    /**
     * Validates if a provided setup/recovery token is still active and valid.
     * 
     * @param token String containing the UUID-based security token.
     * @return [Result] wrapping the validation message or an exception.
     */
    suspend fun verifySetupToken(token: String): Result<String> {
        return try {
            Log.d("AuthRepo", "GET /api/v1/usuarios/setup/validar")
            val response = api.validarSetupToken(token)
            if (response.isSuccessful && response.body() != null) {
                Log.d("AuthRepo", "Token confirmed valid by identity provider")
                Result.success(response.body()!!.string())
            } else {
                Log.e("AuthRepo", "Token verification REJECTED at /api/v1/usuarios/setup/validar")
                Result.failure(Exception("Token inválido o expirado"))
            }
        } catch (e: Exception) {
            Log.e("AuthRepo", "Resource UNREACHABLE at /api/v1/usuarios/setup/validar: ${e.message}")
            Result.failure(Exception("Error de red al validar token"))
        }
    }

    /**
     * Finalizes the password setup or reset flow.
     * 
     * @param request [PasswordSetupRequest] containing new credential and token.
     * @return [Result] wrapping [CuentaResponse] detailing final account state.
     */
    suspend fun setupPassword(request: PasswordSetupRequest): Result<CuentaResponse> {
        return try {
            Log.d("AuthRepo", "POST /api/v1/usuarios/setup")
            val response = api.establecerPassword(request)
            if (response.isSuccessful && response.body() != null) {
                Log.d("AuthRepo", "Credential reconciliation completed successfully")
                Result.success(response.body()!!)
            } else {
                val error = parseError(response.errorBody()?.string(), response.code())
                Log.e("AuthRepo", "Credential setup REJECTED at /api/v1/usuarios/setup")
                Result.failure(Exception(error))
            }
        } catch (e: Exception) {
            Log.e("AuthRepo", "Resource UNREACHABLE at /api/v1/usuarios/setup: ${e.message}")
            Result.failure(Exception("Error al establecer contraseña: ${e.message}"))
        }
    }

    /**
     * Executes an explicit revocation of the local cryptographic session.
     */
    suspend fun logout() {
        Log.d("AuthRepo", "Revoking active session and clearing DataStore")
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
