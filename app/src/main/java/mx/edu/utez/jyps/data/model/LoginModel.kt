package mx.edu.utez.jyps.data.model

/**
 * Data Transfer Object (DTO) for authenticating user credentials.
 * Ensures the payload strictly matches the backend's expected JSON schema for the login endpoint.
 */
data class LoginRequest(
    val correo: String,
    val password: String
)

/**
 * Decoded payload returning from a successful authentication challenge.
 * Contains both user identity details and the cryptographic JWT required for subsequent requests.
 */
data class LoginResponse(
    val id: Long,
    val nombreCompleto: String,
    val correo: String,
    val telefono: String,
    val roles: List<String>,
    val departamentoId: Long?,
    val nombreDepartamento: String?,
    val tokenJwt: String
)
