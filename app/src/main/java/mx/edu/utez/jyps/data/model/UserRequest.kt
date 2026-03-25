package mx.edu.utez.jyps.data.model

/**
 * DTO for creating/updating a user.
 * Matches the backend's RegistrarUsuarioCommand.
 * Uses LocalTimeInfo objects for time fields.
 */
data class UserRequest(
    val nombre: String,
    val apellidoPaterno: String,
    val apellidoMaterno: String,
    val correo: String,
    val telefono: String,
    val horaEntrada: LocalTimeInfo,
    val horaSalida: LocalTimeInfo,
    val roles: List<String>,
    val departamentoId: Long
)
