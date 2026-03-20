package mx.edu.utez.jyps.data.model

import com.google.gson.annotations.SerializedName

/**
 * Data Transfer Object for creating a new user.
 * Matches the backend's RegistrarUsuarioCommand.
 */
data class UserRequest(
    val nombre: String,
    val apellidoPaterno: String,
    val apellidoMaterno: String,
    val correo: String,
    val telefono: String,
    val horaEntrada: String, // Format HH:mm:ss
    val horaSalida: String,  // Format HH:mm:ss
    val roles: List<String>, // [ADMINISTRADOR, EMPLEADO, JEFE_DE_DEPARTAMENTO, GUARDIA, AUDITOR]
    @SerializedName("departamentoId")
    val departamentoId: Long
)
