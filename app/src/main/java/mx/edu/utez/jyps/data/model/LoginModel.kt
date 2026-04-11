package mx.edu.utez.jyps.data.model

import com.google.gson.annotations.SerializedName

/**
 * Data Transfer Object (DTO) for authenticating user credentials.
 */
data class LoginRequest(
    val correo: String,
    val password: String
)

/**
 * Decoded payload returning from a successful authentication challenge.
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
