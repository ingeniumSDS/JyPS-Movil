package mx.edu.utez.jyps.data.model

/**
 * Data Transfer Object (DTO) for authenticating user credentials.
 * Ensures the payload strictly matches the backend's expected JSON schema for the login endpoint.
 *
 * @property correo The institutional email address.
 * @property password The raw string password provided by the user.
 */
data class LoginRequest(
    val correo: String,
    val password: String
)

/**
 * Decoded payload returning from a successful authentication challenge.
 * Contains both user identity details and the cryptographic JWT required for subsequent requests.
 *
 * @property id Unique identifier of the authenticated user.
 * @property nombreCompleto The user's full name.
 * @property correo The institutional email of the user.
 * @property telefono The contact phone number.
 * @property roles A list of granted authorities mapped from the backend.
 * @property departamentoId The ID of the department the user belongs to.
 * @property nombreDepartamento The resolved name of the user's department.
 * @property tokenJwt Cryptographically signed token required for authorized route access.
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
