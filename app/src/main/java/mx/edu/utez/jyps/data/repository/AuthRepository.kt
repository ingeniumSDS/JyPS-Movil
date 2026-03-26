package mx.edu.utez.jyps.data.repository

import mx.edu.utez.jyps.data.model.LoginRequest
import mx.edu.utez.jyps.data.model.LoginResponse
import mx.edu.utez.jyps.data.network.ApiService

/**
 * Orchestrates authentication strategies between the remote API surface and local secured storage.
 * 
 * Centralizes the authentication flow to decouple ViewModels from directly manipulating network 
 * responses and primitive storage keys, adhering to strict architectural separation of concerns.
 */
class AuthRepository(
    private val api: ApiService,
    private val preferencesManager: PreferencesManager
) {
    /**
     * Exposes the continuous state of the user's session validity.
     * ViewModels subscribe here to react globally whenever the interceptor or explicit actions clear the token.
     */
    val tokenFlow = preferencesManager.tokenFlow

    /**
     * Attempts a credential exchange against the backend service.
     * Upon success, delegates the resulting JWT strictly to the [PreferencesManager] 
     * before passing the user data upstream, ensuring no tokens are leaked into memory UI states.
     */
    suspend fun login(correo: String, pass: String): Result<LoginResponse> {
        return try {
            val response = api.login(LoginRequest(correo, pass))
            if (response.isSuccessful && response.body() != null) {
                val loginResponse = response.body()!!
                // Guardar el JWT directamente de forma encriptada en el DataStore
                preferencesManager.saveToken(loginResponse.tokenJwt)
                Result.success(loginResponse)
            } else {
                Result.failure(Exception(if (response.code() == 401 || response.code() == 403) "Credenciales inválidas" else "Error del servidor HTTP ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Fallo en la conexión: ${e.message}"))
        }
    }

    /**
     * Executes an explicit revocation of the local cryptographic session.
     */
    suspend fun logout() {
        preferencesManager.clearSession()
    }
}
