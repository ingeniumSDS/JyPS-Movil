package mx.edu.utez.jyps.data.model

import com.google.gson.annotations.SerializedName

/**
 * Data Transfer Object (DTO) for authenticating user credentials.
 * 
 * @property correo The institutional email address used as the primary identifier.
 * @property password The plain-text password to be validated against the security provider.
 */
data class LoginRequest(
    val correo: String,
    val password: String
)

/**
 * Decoded payload returning from a successful authentication challenge.
 * 
 * @property id The unique database identifier for the authenticated user.
 * @property nombreCompleto The full display name formatted for the UI.
 * @property correo The confirmed email address associated with the session.
 * @property telefono The contact phone number for the active profile.
 * @property roles List of authorized access roles assigned to the account.
 * @property departamentoId Reference to the department the user belongs to.
 * @property nombreDepartamento Human-readable name of the associated department.
 * @property tokenJwt String representation of the JSON Web Token used for subsequent requests.
 */
data class LoginResponse(
    val id: Long?,
    @SerializedName("nombreCompleto")
    val nombreCompleto: String?,
    @SerializedName("correo")
    val correo: String?,
    @SerializedName("telefono")
    val telefono: String?,
    val roles: List<String>?,
    val departamentoId: Long?,
    val nombreDepartamento: String?,
    val tokenJwt: String
)
